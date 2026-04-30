import React, {useCallback, useEffect, useRef, useState} from "react";
import {useLocation} from "react-router-dom";
import SearchBar from "../components/SearchBar";
import MovieCard from "../components/MovieCard";
import LoadingMessage from "../components/LoadingMessage";
import {searchMovies} from "../api/movieApi";

const Main = () => {
    const [query, setQuery] = useState("");
    const [movies, setMovies] = useState([]);
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(null);
    const [loading, setLoading] = useState(false);
    const [selectedMovie, setSelectedMovie] = useState(null);
    const [hasSearched, setHasSearched] = useState(false);
    const observer = useRef(null);
    const location = useLocation();

    // 마지막 카드에 연결될 IntersectionObserver
    const lastMovieRef = useCallback(
        (node) => {
            if (loading || !node) return;
            if (observer.current) observer.current.disconnect();

            observer.current = new IntersectionObserver(([entry]) => {
                if (entry.isIntersecting && page < totalPages) {
                    setPage((prev) => prev + 1);
                }
            }, {rootMargin: "100px"});

            observer.current.observe(node);
        },
        [loading, page, totalPages]
    );

    // 검색 실행
    const handleSearch = () => {
        if (!query.trim()) return;

        setPage(1);
        setMovies([]);
        setHasSearched(true);
        setSelectedMovie(null);

        fetchMovies(query, 1);
    };

    // TMDB에서 영화 검색 결과 가져오기
    const fetchMovies = async (q, pageNum) => {
        setLoading(true);
        try {
            const data = await searchMovies(q, pageNum);
            const newMovies = data.results;
            const total = data.total_pages;

            if (pageNum === 1) {
                setMovies(newMovies);
            } else {
                setMovies((prev) => [...prev, ...newMovies]);
            }

            setTotalPages(total);
        } catch (error) {
            console.error("영화 검색 실패:", error);
        } finally {
            setLoading(false);
        }
    };


    // 검색 모드 선택 1. 실시간 검색 모드 2. 버튼 기반 검색 모드

    /*
    // 1. 실시간 검색 모드 (검색어 변경 또는 페이지 변경 시 자동 요청)
    useEffect(() => {
        if (!query) return;
        fetchMovies(query, page);
    }, [query, page]);
    */

    // 2. 버튼 기반 검색 모드 (검색 버튼(또는 Enter)을 눌렀을 때만 요청)
    useEffect(() => {
        if (hasSearched && page > 1 && query) {
            fetchMovies(query, page);
        }
    }, [hasSearched, page, query]);

    // 메인 이동 시 상태 초기화
    useEffect(() => {
        if (location.pathname === "/") {
            setQuery("");
            setMovies([]);
            setPage(1);
            setHasSearched(false);
            setSelectedMovie(null);
            setLoading(false);
        }
    }, [location.pathname]);

    return (
        <main className="app-page-bg min-h-screen px-4 py-16 text-center">
            <h1 className="text-4xl font-bold mb-8">영화 (재)개봉 알림</h1>

            <div className="flex flex-col items-center">
                <SearchBar query={query} setQuery={setQuery} onSearch={handleSearch}/>

                <div
                    className="mt-10 w-full max-w-5xl grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6 px-2">
                    {hasSearched && loading && movies.length === 0 && (
                        <LoadingMessage message="검색 중입니다..."/>
                    )}

                    {hasSearched && !loading && movies.length === 0 && (
                        <p className="text-gray-500 text-sm col-span-full">검색 결과가 없습니다.</p>
                    )}

                    {movies.map((movie, index) => (
                        <MovieCard
                            key={movie.id}
                            movie={movie}
                            isSelected={selectedMovie?.id === movie.id}
                            onSelect={(selected) => setSelectedMovie(selected)}
                            ref={index === movies.length - 1 ? lastMovieRef : null}
                        />
                    ))}
                </div>

                {loading && movies.length > 0 && (
                    <LoadingMessage message="추가 로딩 중..."/>
                )}
            </div>
        </main>
    );
};

export default Main;

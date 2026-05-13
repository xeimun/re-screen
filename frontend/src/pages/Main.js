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

    const handleSearch = () => {
        if (!query.trim()) return;

        setPage(1);
        setMovies([]);
        setHasSearched(true);
        setSelectedMovie(null);

        fetchMovies(query, 1);
    };

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

    useEffect(() => {
        if (hasSearched && page > 1 && query) {
            fetchMovies(query, page);
        }
    }, [hasSearched, page, query]);

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

import React, {useCallback, useEffect, useRef, useState} from "react";
import UpcomingCard from "../components/UpcomingCard";
import MovieModal from "../components/MovieModal";
import LoadingMessage from "../components/LoadingMessage";
import {getUpcomingMovies} from "../api/movieApi";

const UpcomingPage = () => {
    const [movies, setMovies] = useState([]);
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [selectedMovie, setSelectedMovie] = useState(null);
    const observer = useRef(null);

    const lastMovieRef = useCallback(
        (node) => {
            if (loading || !node) return;

            if (observer.current) observer.current.disconnect();

            observer.current = new IntersectionObserver(
                ([entry]) => {
                    if (entry.isIntersecting && page < totalPages) {
                        setPage((prev) => prev + 1);
                    }
                },
                {rootMargin: "100px"}
            );

            observer.current.observe(node);
        },
        [loading, page, totalPages]
    );

    const fetchMovies = async (pageNum) => {
        setLoading(true);
        setError("");

        try {
            const data = await getUpcomingMovies(pageNum);
            const newMovies = data.results;
            const total = data.total_pages;

            if (newMovies) {
                setMovies((prev) => [...prev, ...newMovies]);
                setTotalPages(total);
            } else {
                setError("데이터를 불러올 수 없습니다.");
            }
        } catch (err) {
            setError("서버 요청 중 문제가 발생했습니다.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchMovies(page);
    }, [page]);

    return (
        <main className="app-page-bg min-h-screen px-4 py-16 text-center">
            <h1 className="text-4xl font-bold mb-8">개봉 예정 영화</h1>

            {error && <p className="text-red-500 text-sm mb-4">{error}</p>}

            <div
                className="w-full max-w-5xl mx-auto grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6 px-2">
                {movies.length === 0 && !loading && (
                    <p className="text-gray-500 text-sm col-span-full">
                        현재 개봉 예정인 영화가 없습니다.
                    </p>
                )}

                {movies.map((movie, index) => {
                    const movieData = {
                        title: movie.title,
                        posterPath: movie.poster_path,
                        releaseDate: movie.release_date,
                        voteAverage: movie.vote_average ?? 0.0,
                        overview: movie.overview,
                    };

                    return (
                        <UpcomingCard
                            key={movie.id}
                            ref={index === movies.length - 1 ? lastMovieRef : null}
                            movie={movieData}
                            onClick={() => setSelectedMovie(movie)}
                        />
                    );
                })}
            </div>

            {loading && (
                <LoadingMessage message="로딩 중입니다..."/>
            )}

            <MovieModal
                isOpen={!!selectedMovie}
                onClose={() => setSelectedMovie(null)}
                movie={selectedMovie}
            />
        </main>
    );
};

export default UpcomingPage;

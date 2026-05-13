import React, {useContext, useState, forwardRef} from "react";
import {AuthContext} from "../context/AuthContext";
import {useNavigate} from "react-router-dom";
import {registerMovieAlert} from "../api/alertApi";

const MovieCard = forwardRef(({movie, isSelected, onSelect}, ref) => {
    const {isAuthenticated} = useContext(AuthContext);
    const navigate = useNavigate();

    const [successMessage, setSuccessMessage] = useState("");
    const [errorMessage, setErrorMessage] = useState("");

    const handleCardClick = (e) => {
        if (e.target.closest("button")) return;
        onSelect(isSelected ? null : movie);
    };

    const handleRegister = async (e) => {
        e.stopPropagation();

        if (!isAuthenticated) {
            alert("로그인이 필요합니다.");
            navigate("/login");
            return;
        }

        try {
            await registerMovieAlert({
                tmdbId: movie.id,
                title: movie.title,
                posterPath: movie.poster_path,
            });

            setSuccessMessage("✅ 등록 완료되었습니다.");
            setErrorMessage("");
            setTimeout(() => setSuccessMessage(""), 3000);
        } catch (error) {
            setSuccessMessage("");
            if (error.response) {
                switch (error.response.status) {
                    case 409:
                        setErrorMessage("⚠️ 이미 등록된 영화입니다.");
                        break;
                    case 401:
                        setErrorMessage("⚠️ 로그인이 필요합니다.");
                        break;
                    default:
                        setErrorMessage("⚠️ 등록 실패. 다시 시도해주세요.");
                }
            } else {
                setErrorMessage("⚠️ 서버 연결 오류입니다.");
            }
            setTimeout(() => setErrorMessage(""), 3000);
        }
    };

    return (
        <div
            ref={ref}
            className="app-card-surface cursor-pointer overflow-hidden rounded-xl transition hover:-translate-y-0.5 hover:shadow-[0_22px_48px_rgba(4,10,20,0.20)]"
            onClick={handleCardClick}
        >
            <div className="relative aspect-[1/1.4] bg-black">
                {movie.poster_path ? (
                    <img
                        src={`https://image.tmdb.org/t/p/w500${movie.poster_path}`}
                        alt={movie.title}
                        className={`w-full h-full object-cover transition ${isSelected ? "brightness-75" : ""}`}
                    />
                ) : (
                    <div
                        className={`w-full h-full flex items-center justify-center bg-gray-200 text-gray-600 font-bold text-lg tracking-wide transition ${isSelected ? "brightness-75" : ""}`}>
                        NO IMAGE
                    </div>
                )}

                {isSelected && (
                    <div
                        className="absolute inset-0 flex flex-col items-center justify-center bg-black bg-opacity-50 px-2">
                        <button
                            onClick={handleRegister}
                            className="px-6 py-2 bg-amber-400 hover:bg-amber-500 text-white font-semibold rounded-full shadow-md transition"
                        >
                            🔔 알림 등록
                        </button>

                        {successMessage && (
                            <div
                                className="mt-3 px-4 py-2 rounded text-sm font-semibold shadow-md bg-green-100 text-green-800">
                                {successMessage}
                            </div>
                        )}

                        {errorMessage && (
                            <div
                                className="mt-3 px-4 py-2 rounded text-sm font-semibold shadow-md bg-red-100 text-red-800">
                                {errorMessage}
                            </div>
                        )}
                    </div>
                )}
            </div>

            <div className="p-3 text-left">
                <h3 className="truncate text-base font-semibold text-[#122033]" title={movie.title}>
                    {movie.title}
                </h3>
                <p className="text-sm text-[#62748a]">
                    개봉일: {movie.release_date?.split("-")[0]}
                </p>
            </div>
        </div>
    );
});

MovieCard.displayName = "MovieCard";
export default MovieCard;

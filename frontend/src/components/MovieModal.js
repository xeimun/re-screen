import React from "react";

const MovieModal = ({isOpen, onClose, movie}) => {
    if (!isOpen || !movie) return null;

    return (
        <div
            className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
            onClick={onClose}
        >
            <div
                className="app-surface relative w-11/12 max-w-md rounded-lg p-6 text-left"
                onClick={(e) => e.stopPropagation()} // 내부 클릭 시 닫힘 방지
            >
                {/* 닫기 버튼 */}
                <button
                    onClick={onClose}
                    className="absolute right-2 top-2 text-lg text-[#62748a] transition hover:text-[#122033]"
                >
                    ✖
                </button>

                <h2 className="mb-4 text-xl font-bold text-[#122033]">{movie.title}</h2>
                <p className="whitespace-pre-line text-sm text-[#41556f]">
                    {movie.overview || "줄거리 정보가 없습니다."}
                </p>
            </div>
        </div>
    );
};

export default MovieModal;

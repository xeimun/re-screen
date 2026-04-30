import React from "react";

const AlertCard = ({alert, isSelected, onSelect, onDelete}) => {
    const handleCardClick = (e) => {
        if (e.target.closest("button")) return;
        onSelect(isSelected ? null : alert);
    };

    return (
        <div
            className="app-card-surface cursor-pointer overflow-hidden rounded-xl transition hover:-translate-y-0.5 hover:shadow-[0_22px_48px_rgba(4,10,20,0.20)]"
            onClick={handleCardClick}
        >
            {/* 포스터 이미지 */}
            <div className="relative aspect-[1/1.4] bg-black">
                {alert.posterPath ? (
                    <img
                        src={`https://image.tmdb.org/t/p/w500${alert.posterPath}`}
                        alt={alert.movieTitle}
                        className={`w-full h-full object-cover transition ${
                            isSelected ? "brightness-75" : ""
                        }`}
                    />
                ) : (
                    <div
                        className={`w-full h-full flex items-center justify-center bg-gray-200 text-gray-600 font-bold text-lg tracking-wide transition ${
                            isSelected ? "brightness-75" : ""
                        }`}
                    >
                        NO IMAGE
                    </div>
                )}

                {/* 선택된 경우 삭제 버튼 */}
                {isSelected && (
                    <div
                        className="absolute inset-0 flex flex-col items-center justify-center bg-black bg-opacity-40 px-2">
                        <button
                            onClick={() => onDelete(alert.userMovieAlertId)}
                            className="px-6 py-2 bg-red-500 hover:bg-red-600 text-white font-semibold rounded-full shadow-md transition"
                        >
                            🗑️ 삭제
                        </button>
                    </div>
                )}
            </div>

            {/* 텍스트 정보 */}
            <div className="p-3 text-left">
                <h3 className="truncate text-base font-semibold text-[#122033]" title={alert.movieTitle}>
                    {alert.movieTitle}
                </h3>
                <p className="text-sm text-[#62748a]">
                    등록일: {new Date(alert.registeredAt).toLocaleDateString()}
                </p>
            </div>
        </div>
    );
};

export default AlertCard;

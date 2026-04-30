import React, {forwardRef} from "react";

const UpcomingCard = forwardRef(({movie, onClick}, ref) => {
    const {
        title,
        posterPath,
        releaseDate,
        voteAverage,
    } = movie;

    const formattedRating = voteAverage ? voteAverage.toFixed(1) : "0.0";
    const formattedDate = releaseDate || "미정";

    return (
        <div
            ref={ref}
            onClick={onClick}
            className="app-card-surface cursor-pointer overflow-hidden rounded-xl transition hover:-translate-y-0.5 hover:shadow-[0_22px_48px_rgba(4,10,20,0.20)]"
        >
            {/* 포스터 이미지 */}
            <div className="relative aspect-[1/1.4] bg-black">
                {posterPath ? (
                    <img
                        src={`https://image.tmdb.org/t/p/w500${posterPath}`}
                        alt={`${title} 포스터`}
                        className="w-full h-full object-cover"
                    />
                ) : (
                    <div
                        className="w-full h-full flex items-center justify-center bg-gray-200 text-gray-600 font-bold text-lg"
                    >
                        NO IMAGE
                    </div>
                )}
            </div>

            {/* 텍스트 정보 */}
            <div className="p-3 text-left">
                <h3
                    className="truncate text-base font-semibold text-[#122033]"
                    title={title}
                >
                    {title}
                </h3>
                <p className="text-sm text-[#62748a]">개봉일: {formattedDate}</p>
                <p className="text-sm font-semibold text-[#9a6a10]">
                    ⭐ {formattedRating}
                </p>
            </div>
        </div>
    );
});

UpcomingCard.displayName = "UpcomingCard";
export default UpcomingCard;

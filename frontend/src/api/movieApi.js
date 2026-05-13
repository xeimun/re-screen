import axios from "./axiosInstance";

export const searchMovies = async (query, page = 1) => {
    try {
        const response = await axios.get("/api/tmdb/search", {
            params: {query, page}
        });
        return response.data;
    } catch (error) {
        console.error("영화 검색 오류", error);
        return {results: [], total_pages: 1};
    }
};

export const getUpcomingMovies = async (page = 1) => {
    const response = await axios.get("/api/tmdb/upcoming", {
        params: {page}
    });
    return response.data;
};

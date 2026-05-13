import axios from "./axiosInstance";

export const registerMovieAlert = async ({tmdbId, title, posterPath}) => {
    const response = await axios.post("/api/alerts/register", {tmdbId, title, posterPath});
    return response.data;
};

export const getUserAlerts = async () => {
    const response = await axios.get("/api/alerts/my-alerts");
    return response.data;
};

export const deleteUserAlert = async (alertId) => {
    const response = await axios.delete(`/api/alerts/${alertId}`);
    return response.data;
};

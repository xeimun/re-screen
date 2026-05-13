import axios from "./axiosInstance";

export const loginUser = async ({email, password}) => {
    const response = await axios.post("/api/auth/login", {email, password});
    return response.data;
};

export const signupUser = async ({email, password, nickname}) => {
    const response = await axios.post("/api/auth/signup", {email, password, nickname});
    return response.data;
};

export const getCurrentUser = async () => {
    const response = await axios.get("/api/auth/me");
    return response.data;
};

import {createContext, useState, useEffect, useContext, useCallback} from "react";
import {useNavigate} from "react-router-dom";

export const AuthContext = createContext();

export const AuthProvider = ({children}) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const navigate = useNavigate();

    const logout = useCallback(() => {
        localStorage.removeItem("token");
        setIsAuthenticated(false);
        navigate("/login");
    }, [navigate]);

    useEffect(() => {
        const token = localStorage.getItem("token");

        if (token) {
            try {
                const payload = JSON.parse(atob(token.split('.')[1]));
                const now = Math.floor(Date.now() / 1000);

                if (payload.exp && payload.exp < now) {
                    logout();
                } else {
                    setIsAuthenticated(true);
                }
            } catch (e) {
                console.error("토큰 파싱 오류:", e);
                logout();
            }
        }
    }, [logout]);

    const login = (token) => {
        localStorage.setItem("token", token);
        setIsAuthenticated(true);
        navigate("/");
    };

    return (
        <AuthContext.Provider value={{isAuthenticated, login, logout}}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);

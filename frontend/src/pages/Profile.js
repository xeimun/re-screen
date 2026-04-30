import {useState, useEffect} from "react";
import axios from "axios";

const Profile = () => {
    const [user, setUser] = useState(null);
    const [message, setMessage] = useState("");

    useEffect(() => {
        const fetchUser = async () => {
            const token = localStorage.getItem("token");
            if (!token) {
                setMessage("로그인이 필요합니다.");
                return;
            }

            try {
                const response = await axios.get("http://localhost:8080/api/auth/me", {
                    headers: {Authorization: `Bearer ${token}`},
                });
                setUser(response.data);
            } catch (error) {
                setMessage("사용자 정보를 불러올 수 없습니다.");
            }
        };

        fetchUser();
    }, []);

    return (
        <main className="app-page-bg min-h-screen px-4 py-16">
            <h2>내 정보</h2>
            {message && <p>{message}</p>}
            {user && (
                <div>
                    <p>이메일: {user.email}</p>
                    <p>닉네임: {user.nickname}</p>
                </div>
            )}
        </main>
    );
};

export default Profile;

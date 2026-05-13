import {useState, useEffect} from "react";
import {getCurrentUser} from "../api/authApi";

const Profile = () => {
    const [user, setUser] = useState(null);
    const [message, setMessage] = useState("");

    useEffect(() => {
        const fetchUser = async () => {
            try {
                const user = await getCurrentUser();
                setUser(user);
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

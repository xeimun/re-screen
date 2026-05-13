import {useState} from "react";
import {useNavigate} from "react-router-dom";
import {signupUser} from "../api/authApi";

const Signup = () => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [nickname, setNickname] = useState("");
    const [message, setMessage] = useState("");
    const navigate = useNavigate();

    const handleSignup = async (e) => {
        e.preventDefault();
        try {
            await signupUser({email, password, nickname});
            setMessage("회원가입 성공:) 로그인 페이지로 이동합니다.");
            setTimeout(() => navigate("/login"), 1500);
        } catch (error) {
            setMessage(error.response?.data?.message || "회원가입 실패");
        }
    };

    return (
        <div className="app-page-bg flex min-h-screen items-center justify-center">
            <div className="w-96 rounded-lg bg-white p-8 text-gray-900 shadow-md">
                <h2 className="mb-4 text-center text-2xl font-bold text-gray-900">회원가입</h2>
                <form onSubmit={handleSignup} className="flex flex-col space-y-4">
                    <input
                        type="email"
                        placeholder="이메일"
                        className="w-full rounded border p-2 text-gray-900 placeholder:text-gray-400"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                    <input
                        type="password"
                        placeholder="비밀번호"
                        className="w-full rounded border p-2 text-gray-900 placeholder:text-gray-400"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                    <input
                        type="text"
                        placeholder="닉네임"
                        className="w-full rounded border p-2 text-gray-900 placeholder:text-gray-400"
                        value={nickname}
                        onChange={(e) => setNickname(e.target.value)}
                        required
                    />
                    <button
                        type="submit"
                        className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-700 w-full"
                    >
                        회원가입
                    </button>
                </form>
                {message && <p className="text-red-500 mt-2 text-center">{message}</p>}
            </div>
        </div>
    );
};

export default Signup;

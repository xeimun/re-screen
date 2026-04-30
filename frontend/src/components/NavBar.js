import {useContext} from "react";
import {Link, useNavigate} from "react-router-dom";
import {AuthContext} from "../context/AuthContext";

const NavBar = () => {
    const {isAuthenticated, logout} = useContext(AuthContext);
    const navigate = useNavigate();

    const goHome = () => {
        navigate("/", {replace: true});
    };

    const handleLogout = () => {
        logout();
        navigate("/");
    };

    const renderMenu = () => {
        return (
            <>
                {/* 항상 표시되는 개봉 예정 메뉴 */}
                <Link to="/upcoming" className="transition hover:text-[#b7d7ff]">
                    개봉 예정
                </Link>

                {!isAuthenticated ? (
                    <>
                        <Link to="/login" className="transition hover:text-[#b7d7ff]">
                            로그인
                        </Link>
                        <Link to="/signup" className="transition hover:text-[#b7d7ff]">
                            회원가입
                        </Link>
                    </>
                ) : (
                    <>
                        <Link to="/alerts/manage" className="transition hover:text-[#b7d7ff]">
                            알림 관리
                        </Link>
                        <Link to="/me" className="transition hover:text-[#b7d7ff]">
                            내 정보
                        </Link>
                        <button onClick={handleLogout} className="transition hover:text-[#b7d7ff]">
                            로그아웃
                        </button>
                    </>
                )}
            </>
        );
    };

    return (
        <nav className="app-nav-bg py-3 text-[#edf4ff] shadow-[0_16px_40px_rgba(3,8,18,0.34)]">
            <div className="max-w-7xl mx-auto px-6 flex flex-col sm:flex-row justify-between items-center">
                {/* 좌측 로고 */}
                <button
                    onClick={goHome}
                    className="mb-2 text-lg font-bold transition hover:text-white sm:mb-0"
                >
                    ReScreen
                </button>

                {/* 우측 메뉴 */}
                <div className="flex space-x-4 text-sm font-semibold">
                    {renderMenu()}
                </div>
            </div>
        </nav>
    );
};

export default NavBar;

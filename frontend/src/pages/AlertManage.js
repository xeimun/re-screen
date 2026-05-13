import React, {useEffect, useState, useRef} from "react";
import {getUserAlerts, deleteUserAlert} from "../api/alertApi";
import {useAuth} from "../context/AuthContext";
import AlertCard from "../components/AlertCard";

const AlertManage = () => {
    const {isAuthenticated} = useAuth();
    const [alerts, setAlerts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedAlert, setSelectedAlert] = useState(null);
    const cardAreaRef = useRef(null);

    const fetchAlerts = async () => {
        try {
            const data = await getUserAlerts();
            setAlerts(data || []);
        } catch (error) {
            console.error("알림 목록 불러오기 실패:", error);
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (alertId) => {
        if (window.confirm("이 알림을 삭제하시겠습니까?")) {
            try {
                await deleteUserAlert(alertId);
                fetchAlerts();
                setSelectedAlert(null); // 삭제 시 선택 해제
            } catch (error) {
                console.error("알림 삭제 실패:", error);
                alert("삭제 중 오류가 발생했습니다.");
            }
        }
    };

    // 초기 알림 목록 불러오기
    useEffect(() => {
        if (isAuthenticated) {
            fetchAlerts();
        }
    }, [isAuthenticated]);

    // 카드 외부 클릭 시 선택 해제
    useEffect(() => {
        const handleClickOutside = (e) => {
            if (
                cardAreaRef.current &&
                !cardAreaRef.current.contains(e.target)
            ) {
                setSelectedAlert(null);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    if (!isAuthenticated) {
        return (
            <main className="app-page-bg min-h-screen flex items-center justify-center text-lg">
                로그인이 필요합니다.
            </main>
        );
    }

    if (loading) {
        return (
            <main className="app-page-bg min-h-screen flex items-center justify-center text-gray-500">
                불러오는 중...
            </main>
        );
    }

    if (alerts.length === 0) {
        return (
            <main className="app-page-bg min-h-screen flex items-center justify-center text-gray-600">
                등록된 알림이 없습니다.
            </main>
        );
    }

    return (
        <main className="app-page-bg min-h-screen px-4 py-16 text-center">
            <h1 className="text-4xl font-bold text-center mb-8">등록한 알림 관리</h1>

            <div
                ref={cardAreaRef}
                className="w-full max-w-5xl mx-auto grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6 px-2"
            >
                {alerts.map((alert) => (
                    <AlertCard
                        key={alert.userMovieAlertId}
                        alert={alert}
                        isSelected={selectedAlert?.userMovieAlertId === alert.userMovieAlertId}
                        onSelect={(selected) => setSelectedAlert(selected)}
                        onDelete={handleDelete}
                    />
                ))}
            </div>
        </main>
    );
};

export default AlertManage;

import { useEffect } from "react";
import { requestFCMToken, registerServiceWorker, onMessageListener } from "../firebase/firebaseConfig";
import {registerFCMToken} from "../api/fcmAPI.ts";

const useFCM = (email: string | null) => {
    useEffect(() => {
        const initializeFCM = async () => {
            try {
                await registerServiceWorker(); // Service Worker 등록
                const token = await requestFCMToken(); // FCM 토큰 요청
                if (token) {
                    if (email) {
                        // 서버에 FCM 토큰 등록 API 호출
                        await registerFCMToken(token, email, true); // isUser = true로 설정
                    }
                }
            } catch (error) {
                console.error("FCM 초기화 오류:", error);
            }
        };


        const listenToMessages = () => {
            onMessageListener()
                .then(() => {})
                .catch((err) => console.error("알림 수신 오류:", err));
        };

        if (email) {
            initializeFCM();
            listenToMessages();
        }
    }, [email]); // email이 변경될 때마다 실행
};

export default useFCM;

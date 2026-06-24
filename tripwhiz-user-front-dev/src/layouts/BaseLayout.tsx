import HeaderLayout from "./HeaderLayout.tsx";
import {ReactNode, useEffect} from "react";
import {getToken, onMessage} from "firebase/messaging";
import {messaging} from "../firebase/firebaseConfig.ts";

function BaseLayout({ children }: { children: ReactNode }) {

    async function requestPermission() {
        //requesting permission using Notification API
        const permission = await Notification.requestPermission();

        if (permission === "granted") {

            alert("Notification granted!")
            await getToken(messaging, {

                vapidKey: import.meta.env.VITE_FIREBASE_VAPID_KEY,

            });

        } else if (permission === "denied") {

            //notifications are blocked
            alert("You denied for the notification");

        }
    }


    useEffect(() => {

        requestPermission();

    }, []);


    onMessage(messaging, () => {
        alert("On Message ")
    });

    return (
        <div className="bg-white min-h-screen"> {/* 전체 배경을 흰색으로 설정 */}
            <HeaderLayout/>
            <main style={{ marginTop: "130px" }}>{children}</main>
        </div>
    );
}

export default BaseLayout;

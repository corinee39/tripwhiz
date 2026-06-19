importScripts("https://www.gstatic.com/firebasejs/8.10.0/firebase-app.js");
importScripts("https://www.gstatic.com/firebasejs/8.10.0/firebase-messaging.js");

const loadFirebaseConfig = async () => {
    const response = await fetch("/firebase-config.json", { cache: "no-store" });

    if (!response.ok) {
        throw new Error("firebase-config.json was not found");
    }

    return response.json();
};

const showBackgroundNotification = (payload) => {
    const notification = payload.notification || {};
    const notificationTitle = notification.title || "TripWhiz";
    const notificationOptions = {
        body: notification.body || "",
        icon: notification.image || "/192.png",
    };

    self.registration.showNotification(notificationTitle, notificationOptions);
};

loadFirebaseConfig()
    .then((firebaseConfig) => {
        if (!firebaseConfig.apiKey) {
            throw new Error("Firebase apiKey is missing");
        }

        firebase.initializeApp(firebaseConfig);
        const messaging = firebase.messaging();
        messaging.onBackgroundMessage(showBackgroundNotification);
    })
    .catch((error) => {
        console.warn("Firebase background messaging disabled:", error.message);
    });

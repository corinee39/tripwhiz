import { createRoot } from 'react-dom/client';
import './index.css';
import './App.css';
import { GoogleOAuthProvider } from '@react-oauth/google';
import axios from 'axios';
import App from './App.tsx';

axios.interceptors.request.use((config) => {
    const accessToken = sessionStorage.getItem("accessToken");

    if (accessToken) {
        config.headers.Authorization = `Bearer ${accessToken}`;
    }

    return config;
});

const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;

createRoot(document.getElementById('root')!).render(
    <GoogleOAuthProvider clientId={clientId}>
        <App />
    </GoogleOAuthProvider>
);

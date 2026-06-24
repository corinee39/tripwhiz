import axios from "axios";

const restApiKey = import.meta.env.VITE_KAKAO_REST_API_KEY;
const redirectUri = import.meta.env.VITE_KAKAO_REDIRECT_URI || `${window.location.origin}/member/kakao`;
const authCodePath = "https://kauth.kakao.com/oauth/authorize";
const accessTokenUrl = "https://kauth.kakao.com/oauth/token";
const host = `${import.meta.env.VITE_USER_SERVER_URL || ""}/api/member/kakao`;

type LoginResponse = {
    name: string;
    email: string;
    accessToken: string;
    refreshToken?: string;
};

export const getKakaoWithAccessToken = async (
    oauthAccessToken: string,
    setUser: (name: string, email: string, accessToken: string) => void
) => {
    const res = await axios.get<LoginResponse>(host, {
        headers: {
            Authorization: `Bearer ${oauthAccessToken}`,
        },
    });

    const { name, email, accessToken } = res.data;

    if (name && email && accessToken) {
        setUser(name, email, accessToken);
    }

    return res.data;
};

export const getKakaoLoginLink = () => {
    const params = new URLSearchParams({
        client_id: restApiKey,
        redirect_uri: redirectUri,
        response_type: "code",
    });

    return `${authCodePath}?${params.toString()}`;
};

export const getAccessToken = async (authCode: string) => {
    const params = new URLSearchParams({
        grant_type: "authorization_code",
        client_id: restApiKey,
        redirect_uri: redirectUri,
        code: authCode,
    });

    const res = await axios.post(accessTokenUrl, params, {
        headers: {
            "Content-Type": "application/x-www-form-urlencoded",
        },
    });

    return res.data.access_token;
};

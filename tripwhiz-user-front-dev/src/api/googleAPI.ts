import axios from "axios";

const host = `${import.meta.env.VITE_USER_SERVER_URL || ""}/api/member/google`;

type LoginResponse = {
    name: string;
    email: string;
    accessToken: string;
    refreshToken?: string;
};

export const getGoogleWithAccessToken = async (
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

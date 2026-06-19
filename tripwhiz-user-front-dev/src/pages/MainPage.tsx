import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import useAuthStore from "../store/AuthStore.ts";
import MainProductListComponent from "../components/main/MainProductListComponent.tsx";
import { getCategories } from "../api/categoryAPI.ts";
import MainCarouselComponent from "../components/main/MainCarouselComponent.tsx";
import MainServiceComponent from "../components/main/MainServiceComponent.tsx";
import SampleChatUI from "../components/chatbot/SampleChatUI.tsx";

interface Category {
    cno: number;
    cname: string;
}

const MainPage = () => {
    const [categories, setCategories] = useState<Category[]>([]);
    const { name } = useAuthStore();
    const navigate = useNavigate();

    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const data: Category[] = await getCategories();
                setCategories(data);
            } catch (error) {
                console.error("Error fetching categories:", error);
                setCategories([]);
            }
        };

        fetchCategories();
    }, []);

    return (
        <div className="flex flex-col bg-white h-screen">
            <MainCarouselComponent />
            <MainServiceComponent />

            <div className="mt-6 px-4 pt-4">
                <div className="flex justify-between items-center mb-2">
                    <h2 className="flex items-center ml-2 text-lg font-bold text-gray-700">
                        {name ? `${name}'s recommended picks` : "Recommended picks"}
                    </h2>
                    <button
                        type="button"
                        className="text-sm text-gray-500 cursor-pointer"
                        onClick={() => navigate("/product/list")}
                    >
                        View all &gt;
                    </button>
                </div>

                <MainProductListComponent categories={categories} />
                <SampleChatUI />
            </div>
        </div>
    );
};

export default MainPage;

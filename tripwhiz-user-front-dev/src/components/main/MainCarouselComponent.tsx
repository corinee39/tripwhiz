import { useState } from "react";
import { Swiper, SwiperSlide } from "swiper/react";
import { Pagination } from "swiper/modules";
import "swiper/css";
import "swiper/css/pagination";
import { Swiper as SwiperClass } from "swiper/types";

const slides = [
    {
        image: "/images/main/m4.jpg",
        title: "BELLROY",
        description: "Smart travel goods for lighter trips.",
    },
    {
        image: "/images/main/m5.jpg",
        title: "BRAND NEW",
        description: "Fresh picks for your next destination.",
    },
    {
        image: "/images/main/m6.jpg",
        title: "TRAVEL IN STYLE",
        description: "Curated essentials for smoother journeys.",
    },
    {
        image: "/images/main/m7.jpg",
        title: "DURABLE AND LIGHT",
        description: "Reliable gear that keeps your day moving.",
    },
    {
        image: "/images/main/m8.jpg",
        title: "ECO FRIENDLY",
        description: "Thoughtful products for conscious travel.",
    },
];

const MainCarouselComponent: React.FC = () => {
    const [slideIndex, setSlideIndex] = useState<number>(0);

    const handleSlideChange = (swiper: SwiperClass) => {
        setSlideIndex(swiper.realIndex);
    };

    return (
        <div className="relative z-30 bg-gray-200 h-[50vh]">
            <Swiper
                className="w-full h-full object-cover"
                onSlideChange={handleSlideChange}
                modules={[Pagination]}
                loop={true}
                pagination={{
                    el: ".swiper-pagination",
                    type: "custom",
                    renderCustom: (_, current, total) => {
                        return `<span class="block text-right mr-4 font-bold text-base text-white">${current} / ${total}</span>`;
                    },
                }}
            >
                {slides.map((slide, index) => (
                    <SwiperSlide key={slide.title}>
                        <img
                            src={slide.image}
                            alt={slide.title}
                            className="w-full h-full object-cover"
                        />
                        <div
                            className={`absolute top-3/4 left-0 w-full text-left text-white transform transition-all duration-1000 ${
                                slideIndex === index ? "translate-y-0 opacity-100" : "translate-y-20 opacity-0"
                            }`}
                            style={{ zIndex: 10 }}
                        >
                            <div className="pl-4">
                                <h1 className="text-3xl font-bold">{slide.title}</h1>
                                <p className="mt-2 text-md">{slide.description}</p>
                            </div>
                        </div>
                    </SwiperSlide>
                ))}
                <div className="swiper-pagination"></div>
            </Swiper>
        </div>
    );
};

export default MainCarouselComponent;

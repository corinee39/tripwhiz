import { useNavigate } from "react-router-dom";

const services = [
    {
        label: "Luggage",
        icon: "/images/main/free-icon-luggage-5793226.png",
        iconClassName: "w-14 h-14",
        path: "/luggage",
    },
    {
        label: "Store Finder",
        icon: "/images/main/free-icon-search-1916736.png",
        iconClassName: "w-10 h-10",
        path: "/destination",
    },
    {
        label: "Themes",
        icon: "/images/main/free-icon-mountain-12136492.png",
        iconClassName: "w-14 h-14",
        path: "/theme",
    },
    {
        label: "Products",
        icon: "/images/main/free-icon-online-shopping-3081648.png",
        iconClassName: "w-12 h-12",
        path: "/product/list",
    },
];

const MainServiceComponent: React.FC = () => {
    const navigate = useNavigate();

    return (
        <div>
            <div className="flex items-center ml-6 mt-8 text-lg font-bold text-gray-700 mb-2">
                <span>Explore services</span>
                <img
                    src="/images/main/free-icon-eyes-7835667.png"
                    alt="Explore"
                    className="w-5 h-5 ml-2"
                />
            </div>

            <div className="flex justify-center mt-4 space-x-4">
                {services.map((service) => (
                    <button
                        key={service.label}
                        type="button"
                        className="flex flex-col items-center justify-center cursor-pointer"
                        onClick={() => navigate(service.path)}
                    >
                        <span className="w-20 h-20 flex items-center justify-center rounded-lg shadow-lg">
                            <img
                                src={service.icon}
                                alt={service.label}
                                className={`${service.iconClassName} object-cover rounded-lg`}
                            />
                        </span>
                        <span className="text-sm text-gray-600 mt-2">{service.label}</span>
                    </button>
                ))}
            </div>
        </div>
    );
};

export default MainServiceComponent;

import { useEffect, useState } from "react";
import { getList } from "../../api/productAPI.ts";

interface Product {
    pno: number;
    pname: string;
    price: number;
    cno: number;
    attachFiles: { file_name: string }[];
}

interface Category {
    cno: number;
    cname: string;
}

interface MainProductListProps {
    categories: Category[];
}

const IMAGE_BASE_URL = "/images/product";

const MainProductListComponent: React.FC<MainProductListProps> = ({ categories }) => {
    const [selectedCategory, setSelectedCategory] = useState<number | null>(null);
    const [products, setProducts] = useState<Product[]>([]);
    const [filteredProducts, setFilteredProducts] = useState<Product[]>([]);

    useEffect(() => {
        if (categories.length === 0) {
            return;
        }

        setSelectedCategory(categories[0].cno);
    }, [categories]);

    useEffect(() => {
        const fetchProducts = async () => {
            if (selectedCategory === null) {
                return;
            }

            try {
                const productData = await getList(1, null, selectedCategory, null);
                setProducts(productData);
            } catch (error) {
                console.error("Error fetching products:", error);
                setProducts([]);
            }
        };

        fetchProducts();
    }, [selectedCategory]);

    useEffect(() => {
        if (selectedCategory === null) {
            setFilteredProducts([]);
            return;
        }

        const filtered = products.filter((product) => product.cno === selectedCategory);
        setFilteredProducts(filtered.slice(0, 4));
    }, [selectedCategory, products]);

    return (
        <div>
            <div className="bg-white-200 px-4 py-2 pt-4">
                <div className="flex space-x-4 overflow-x-auto scrollbar-hide w-full">
                    {categories.map((category) => (
                        <button
                            key={category.cno}
                            type="button"
                            onClick={() => setSelectedCategory(category.cno)}
                            className={`text-gray-500 text-base cursor-pointer transition-all duration-300 relative whitespace-nowrap ${
                                selectedCategory === category.cno ? "text-gray-800" : "hover:text-gray-800"
                            }`}
                        >
                            {category.cname}
                            {selectedCategory === category.cno && (
                                <span className="block w-full h-[2px] bg-yellow-400 mt-1"></span>
                            )}
                        </button>
                    ))}
                </div>
            </div>

            <div className="grid grid-cols-2 gap-4 mt-4">
                {filteredProducts.map((product) => (
                    <div
                        key={product.pno}
                        className="bg-white border rounded-lg shadow-sm overflow-hidden"
                    >
                        <div className="relative">
                            {product.attachFiles && product.attachFiles.length > 0 ? (
                                <img
                                    src={`${IMAGE_BASE_URL}/${product.attachFiles[0]?.file_name}`}
                                    alt={product.pname}
                                    className="w-full h-[100px] object-cover"
                                />
                            ) : (
                                <div className="w-full h-[100px] bg-gray-200 flex items-center justify-center text-sm text-gray-500">
                                    No image
                                </div>
                            )}
                        </div>
                        <div className="p-2">
                            <h3 className="text-sm text-gray-800 font-medium">{product.pname}</h3>
                            <div className="text-base font-bold text-gray-800">
                                {new Intl.NumberFormat("ko-KR").format(product.price)} KRW
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default MainProductListComponent;

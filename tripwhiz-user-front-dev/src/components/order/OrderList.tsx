import React, { useCallback, useEffect, useState } from "react";
import { fetchOrderList } from "../../api/orderAPI";
import { OrderListDTO } from "../../types/ordertype";

const OrderList: React.FC = () => {
    const [orders, setOrders] = useState<OrderListDTO[]>([]);
    const [email, setEmail] = useState("");
    const [loading, setLoading] = useState(false);
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);

    const loadOrders = useCallback(async () => {
        setLoading(true);
        try {
            const data = await fetchOrderList(page, 10);
            setOrders(data.dtoList);
            setTotalPages(Math.ceil(data.totalElements / 10));
        } catch (error) {
            console.error("Failed to fetch order list:", error);
        } finally {
            setLoading(false);
        }
    }, [page]);

    useEffect(() => {
        if (email) {
            loadOrders();
        }
    }, [email, loadOrders]);

    const handlePrevPage = () => {
        if (page > 1) {
            setPage((prevPage) => prevPage - 1);
        }
    };

    const handleNextPage = () => {
        if (page < totalPages) {
            setPage((prevPage) => prevPage + 1);
        }
    };

    return (
        <div>
            <h1>Order List</h1>
            <input
                type="email"
                placeholder="Enter your email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
            />
            <button onClick={loadOrders}>Load Orders</button>
            {loading ? (
                <p>Loading...</p>
            ) : (
                <>
                    <ul>
                        {orders.map((order) => (
                            <li key={order.ono}>
                                Order #{order.ono}: {order.status} - {order.totalPrice} USD
                            </li>
                        ))}
                    </ul>
                    <div>
                        <button onClick={handlePrevPage} disabled={page === 1}>
                            Previous
                        </button>
                        <span>
                            Page {page} of {totalPages}
                        </span>
                        <button onClick={handleNextPage} disabled={page === totalPages}>
                            Next
                        </button>
                    </div>
                </>
            )}
        </div>
    );
};

export default OrderList;

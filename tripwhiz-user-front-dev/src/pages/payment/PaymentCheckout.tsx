import { loadTossPayments, TossPaymentsPayment } from "@tosspayments/tosspayments-sdk";
import { useEffect, useMemo, useState } from "react";
import { cartStore } from "../../store/CartStore.ts";

const clientKey = "test_ck_D5GePWvyJnrK0W0k6q8gLzN97Eoq";

function PaymentCheckout() {
    const [customerKey] = useState(() => generateRandomString());
    const [payment, setPayment] = useState<TossPaymentsPayment | null>(null);
    const [selectedPaymentMethod, setSelectedPaymentMethod] = useState<string | null>("CARD");
    const cartItems = cartStore((state) => state.cartItems);

    const amount = useMemo(() => ({
        currency: "KRW",
        value: cartItems.reduce((acc, item) => acc + item.price * item.qty, 0),
    }), [cartItems]);

    const orderName = useMemo(() => {
        if (cartItems.length === 0) {
            return "TripWhiz demo order";
        }

        return cartItems
            .map((item) => `${item.pname} (${item.qty})`)
            .join(", ");
    }, [cartItems]);

    useEffect(() => {
        async function fetchPayment() {
            try {
                const tossPayments = await loadTossPayments(clientKey);
                const paymentInstance = tossPayments.payment({ customerKey });
                setPayment(paymentInstance);
            } catch (error) {
                console.error("Error fetching payment:", error);
            }
        }

        fetchPayment();
    }, [customerKey]);

    function selectPaymentMethod(method: string) {
        setSelectedPaymentMethod(method);
    }

    async function requestPayment() {
        if (!payment) {
            console.error("Payment is not initialized.");
            return;
        }

        payment.requestPayment({
            method: "CARD",
            amount,
            orderId: generateRandomString(),
            orderName,
            successUrl: window.location.origin + "/payment/success",
            failUrl: window.location.origin + "/fail",
            customerEmail: "customer123@gmail.com",
            customerName: "TripWhiz Customer",
            card: {
                useEscrow: false,
                flowMode: "DEFAULT",
                useCardPoint: false,
                useAppCardOnly: false,
            },
        });
    }

    return (
        <div className="flex flex-col items-center justify-center h-screen w-full p-6 bg-white rounded-lg shadow-md">
            <h2 className="text-lg font-semibold mb-6">Payment</h2>

            <div className="grid grid-cols-2 gap-6 mb-6">
                {[
                    ["CARD", "Card"],
                    ["TRANSFER", "Bank transfer"],
                    ["VIRTUAL_ACCOUNT", "Virtual account"],
                    ["MOBILE_PHONE", "Mobile phone"],
                    ["CULTURE_GIFT_CERTIFICATE", "Gift certificate"],
                    ["FOREIGN_EASY_PAY", "Easy pay"],
                ].map(([method, label]) => (
                    <button
                        key={method}
                        className={`bg-gray-100 text-gray-800 border border-gray-300 rounded-lg px-4 py-2 text-lg font-medium transition-all duration-300 ease-in-out ${
                            selectedPaymentMethod === method ? "bg-gray-300 text-blue-500 border-blue-500" : ""
                        }`}
                        onClick={() => selectPaymentMethod(method)}
                    >
                        {label}
                    </button>
                ))}
            </div>

            <button
                className="w-full py-2 bg-blue-600 text-white text-md font-semibold rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-300 mt-6"
                onClick={() => requestPayment()}
            >
                Pay
            </button>
        </div>
    );
}

function generateRandomString() {
    return window.btoa(Math.random().toString()).slice(0, 20);
}

export default PaymentCheckout;

import { lazy, Suspense } from "react";
import { createBrowserRouter, Outlet } from "react-router-dom";
import BaseLayout from "../layouts/BaseLayout";
import LoadingPage from "../pages/LoadingPage";
import cartRouter from "./cartRouter";
import luggageRouter from "./luggageRouter";
import memberRouter from "./memberRouter";
import orderRouter from "./orderRouter";
import paymentRouter from "./paymentRouter";
import productRouter from "./productRouter";
import sidebarRouter from "./sidebarRouter";

const PickupPage = lazy(() => import("../pages/pickup/PickupPage"));
const DestinationPage = lazy(() => import("../pages/destination/DestinationPage"));
const MainPage = lazy(() => import("../pages/MainPage"));
const ThemePage = lazy(() => import("../pages/theme/ThemePage"));

const Loading = <LoadingPage />;

const mainRouter = createBrowserRouter([
    {
        element: (
            <BaseLayout>
                <Outlet />
            </BaseLayout>
        ),
        children: [
            {
                path: "/main",
                element: <Suspense fallback={Loading}><MainPage /></Suspense>,
            },
            {
                path: "/pickup",
                element: <Suspense fallback={Loading}><PickupPage /></Suspense>,
            },
            productRouter,
            memberRouter,
            cartRouter,
            paymentRouter,
            sidebarRouter,
            orderRouter,
            luggageRouter,
        ],
    },
    {
        path: "/",
        element: <Suspense fallback={Loading}><DestinationPage /></Suspense>,
    },
    {
        path: "/theme",
        element: <Suspense fallback={Loading}><ThemePage /></Suspense>,
    },
]);

export default mainRouter;

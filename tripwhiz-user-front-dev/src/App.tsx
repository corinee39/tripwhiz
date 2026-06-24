import { RouterProvider } from "react-router-dom";
import mainRouter from "./routes/mainRouter";

function App() {
    return <RouterProvider router={mainRouter} future={{ v7_startTransition: true }} />;
}

export default App;

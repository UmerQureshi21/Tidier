import {
  createBrowserRouter,
  createRoutesFromElements,
  Route,
  RouterProvider,
} from "react-router";
import RootLayout from "./layouts/RootLayout";
import UploadPage from "./pages/UploadPage";
import MontagePage from "./pages/MontagePage";

export default function App() {
  const router = createBrowserRouter(
    createRoutesFromElements(
      <Route path="/" element={<RootLayout />}>
        <Route index element={<UploadPage />} />
        <Route path="view-montage" element={<MontagePage />} />
      </Route>
    )
  );

  return <RouterProvider router={router} />;
}

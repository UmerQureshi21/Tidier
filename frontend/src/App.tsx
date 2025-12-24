import {
  createBrowserRouter,
  createRoutesFromElements,
  Route,
  RouterProvider,
} from "react-router";

import RootLayout from "./layouts/RootLayout";
import MontagePage from "./pages/MontageLibraryPage";
import HomePage from "./pages/HomePage";
import LogInPage from "./pages/LogInPage";
import VideoLibraryPage from "./pages/VideoLibraryPage";
import DashBoardPage from "./pages/DashBoardPage";
import CreateMontagePage from "./pages/CreateMontagePage";
import NotFoundPage from "./pages/NotFoundPage";

export default function App() {
  const router = createBrowserRouter(
    createRoutesFromElements(
      <>
        <Route path="/" element={<HomePage />} />
        <Route path="/account" element={<LogInPage />} />

        <Route path="/app" element={<RootLayout />}>
          <Route index path="dashboard" element={<DashBoardPage />} />
          <Route path="upload" element={<CreateMontagePage />} />
          <Route path="montages" element={<MontagePage />} />
          <Route path="videos" element={<VideoLibraryPage />} />
        </Route>

        {/* Catch-all route for 404 */}
        <Route path="*" element={<NotFoundPage />} />
      </>
    )
  );

  return <RouterProvider router={router} />;
}

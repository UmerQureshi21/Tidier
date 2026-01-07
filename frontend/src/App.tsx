import { useEffect, useState } from "react";
import {
  createBrowserRouter,
  createRoutesFromElements,
  Route,
  RouterProvider,
  Navigate,
} from "react-router";

import RootLayout from "./layouts/RootLayout";
import MontagePage from "./pages/MontageLibraryPage";
import HomePage from "./pages/HomePage";
import LogInPage from "./pages/LogInPage";
import VideoLibraryPage from "./pages/VideoLibraryPage";
import DashBoardPage from "./pages/DashBoardPage";
import CreateMontagePage from "./pages/CreateMontagePage";
import NotFoundPage from "./pages/NotFoundPage";
import { restoreSession } from "./services/userService";
import { tokenManager } from "./services/tokenManager";
import AccountPage from "./pages/AccountPage";

export default function App() {
  const [isInitialized, setIsInitialized] = useState(false);

  useEffect(() => {
    const initializeApp = async () => {
      await restoreSession();
      setIsInitialized(true);
    };

    initializeApp();
  }, []);

  // Helper component for protected routes
  const ProtectedLayout = () => {
    if (!tokenManager.hasToken()) {
      return <Navigate to="/account" replace />;
    }
    return <RootLayout />;
  };

  const router = createBrowserRouter(
    createRoutesFromElements(
      <>
        <Route path="/" element={<HomePage />} />
        <Route path="/account" element={<LogInPage />} />

        <Route path="/app" element={<ProtectedLayout />}>
          <Route index path="dashboard" element={<DashBoardPage />} />
          <Route path="create" element={<CreateMontagePage />} />
          <Route path="montages" element={<MontagePage />} />
          <Route path="videos" element={<VideoLibraryPage />} />
          <Route path="account" element={<AccountPage />} />
        </Route>

        {/* Catch-all route for 404 */}
        <Route path="*" element={<NotFoundPage />} />
      </>
    )
  );

  if (!isInitialized) {
    return (
      <div className="poppins-font text-white w-full min-h-screen bg-black font-bold">
        Loading...
      </div>
    );
  }

  return <RouterProvider router={router} />;
}

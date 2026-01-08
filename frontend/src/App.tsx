import { useEffect, useState } from "react";
import {
  createBrowserRouter,
  createRoutesFromElements,
  Route,
  RouterProvider,
  Navigate,
  useLocation,
  Routes,
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
import { clearVideoCache } from "./services/videoService";
import { clearMontageCache } from "./services/montageService";

// Wrapper component that uses useLocation
function AppContent() {
  const location = useLocation();

  useEffect(() => {
    if (location.pathname === "/account" || location.pathname === "/") {
      clearVideoCache();
      clearMontageCache();
    }
  }, [location.pathname]);

  // Helper component for protected routes
  const ProtectedLayout = () => {
    if (!tokenManager.hasToken()) {
      return <Navigate to="/account" replace />;
    }
    return <RootLayout />;
  };

  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/account" element={<LogInPage />} />

      <Route path="/app" element={<ProtectedLayout />}>
        <Route path="/app/" element={<DashBoardPage />} />
        <Route path="dashboard" element={<DashBoardPage />} />
        <Route path="create" element={<CreateMontagePage />} />
        <Route path="montages" element={<MontagePage />} />
        <Route path="videos" element={<VideoLibraryPage />} />
        <Route path="account" element={<AccountPage />} />
      </Route>

      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}

export default function App() {
  const [isInitialized, setIsInitialized] = useState(false);

  useEffect(() => {
    const initializeApp = async () => {
      await restoreSession();
      setIsInitialized(true);
    };

    initializeApp();
  }, []);

  if (!isInitialized) {
    return (
      <div className="poppins-font text-white w-full min-h-screen bg-black font-bold">
        Loading...
      </div>
    );
  }

  const router = createBrowserRouter([
    {
      path: "*",
      element: <AppContent />,
    },
  ]);

  return <RouterProvider router={router} />;
}

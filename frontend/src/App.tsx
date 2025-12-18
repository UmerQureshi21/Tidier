import {
  createBrowserRouter,
  createRoutesFromElements,
  Route,
  RouterProvider,
} from "react-router";

import RootLayout from "./layouts/RootLayout";
import UploadPage from "./pages/UploadPage";
import MontagePage from "./pages/MontagePage";
import HomePage from "./pages/HomePage";
import LogInPage from "./pages/LogInPage";
import VideoLibraryPage from "./pages/VideoLibraryPage";

export default function App() {
  const router = createBrowserRouter(
    createRoutesFromElements(
      <>
        <Route path="/" element={<HomePage />} />
        <Route path="/account" element={<LogInPage />} />

        <Route path="/app" element={<RootLayout />}>
          <Route path="upload" element={<UploadPage />} />
          <Route path="viewing" element={<MontagePage />} />
          <Route path="videos" element={<VideoLibraryPage/>} />

        </Route>
      </>
    )
  );

  return <RouterProvider router={router} />;
}

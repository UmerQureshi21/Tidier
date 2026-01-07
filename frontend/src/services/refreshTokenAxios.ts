import axios from "axios";
import { tokenManager } from "./tokenManager";

const backendURL = import.meta.env.VITE_BACKEND_URL;

// Create axios instance
const axiosInstance = axios.create({
  baseURL: `${backendURL}`,
});

// Add token to every request
axiosInstance.interceptors.request.use((config) => {
  const token = tokenManager.getToken();
  if (token) {
    config.headers.Authorization = token;
  }
  return config;
});

// Handle 401/403 errors - refresh token and retry
axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // If we get 401 or 403 and haven't already retried
    if (
      (error.response?.status === 401 || error.response?.status === 403) &&
      !originalRequest._retry
    ) {
      originalRequest._retry = true;

      try {
        // Call refresh endpoint
        const refreshResponse = await axios.post(
          `${backendURL}/refresh-token`,
          {},
          { withCredentials: true } // Send refresh token cookie
        );

        // Get new access token from response header
        const newToken = refreshResponse.headers.authorization;
        tokenManager.setToken(newToken);

        // Retry original request with new token
        originalRequest.headers.Authorization = newToken;
        return axiosInstance(originalRequest);
      } catch (refreshError) {
        // Refresh failed, clear token and redirect to login
        tokenManager.clearToken();
        window.location.href = "/login";
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;
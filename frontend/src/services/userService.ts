import axios from "axios";
import { tokenManager } from "./tokenManager";
import type { UserRequestDTO } from "../Types";
import { clearVideoCache } from "./videoService";
import { clearMontageCache } from "./montageService";

const backendURL = import.meta.env.VITE_BACKEND_URL;

export async function logInOrSignUp(
  isLogin: boolean,
  email: string,
  password: string,
  userName: string
): Promise<string> {
  let errorMessage = "";
  const request: UserRequestDTO = {
    username: email,
    password: password,
    displayedName: userName,
  };

  if (isLogin) {
    try {
      console.log("LOGIN REQUEST:", request);

      const tokenResponse = await axios.post(
        `${backendURL}/generate-token`,
        {
          username: email,
          password: password,
        },
        {
          withCredentials: true, // Send/receive cookies
        }
      );

      // Extract and store access token in memory
      const accessToken = tokenResponse.headers.authorization;
      tokenManager.setToken(accessToken);
      console.log("ACCESS TOKEN SET IN MEMORY");
    } catch (err: any) {
      if (axios.isAxiosError(err) && err.response) {
        if (err.response.status === 401) {
          errorMessage = "Invalid username or password.";
        } else {
          errorMessage = `Server error: ${err.response.status}`;
        }
        console.error("REQUEST FAILED:", err.response);
      } else {
        errorMessage = "Network error. Please try again.";
        console.error("REQUEST FAILED (no response):", err);
      }
    }
  } else {
    console.log("REGISTER REQUEST:", request);
    const response = await axios.post(`${backendURL}/register`, request);
    console.log("REGISTER SUCCESS:", response.data);

    if (response.data["username"] != null) {
      const tokenResponse = await axios.post(
        `${backendURL}/generate-token`,
        {
          username: email,
          password: password,
        },
        {
          withCredentials: true,
        }
      );

      // Extract and store access token in memory
      const accessToken = tokenResponse.headers.authorization;
      tokenManager.setToken(accessToken);
      console.log("ACCESS TOKEN SET IN MEMORY AFTER SIGNUP");
    } else {
      errorMessage = "email or username already used";
    }
  }

  return errorMessage;
}

// New function to restore session on app startup
export async function restoreSession(): Promise<boolean> {
  try {
    const tokenResponse = await axios.post(
      `${backendURL}/refresh-token`,
      {},
      { withCredentials: true } // Sends refresh token cookie
    );

    const accessToken = tokenResponse.headers.authorization;
    if (accessToken) {
      tokenManager.setToken(accessToken);
      console.log("SESSION RESTORED FROM REFRESH TOKEN");
      return true;
    }
    return false;
  } catch (err) {
    console.log("Failed to restore session:", err);
    return false;
  }
}

export function logout(): void {
  tokenManager.clearToken();
  clearVideoCache();
  clearMontageCache();
}

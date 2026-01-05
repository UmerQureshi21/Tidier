import axios from "axios";
import type { UserRequestDTO } from "../Types";

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
        `http://${backendURL}/generate-token`,
        {
          username: email,
          password: password,
        }
      );
      // If we reach here, login was successful
      // console.log("LOGIN SUCCESS: \n\n " + tokenResponse);
      const accessToken = tokenResponse.headers.authorization;
      localStorage.setItem("accessToken", accessToken);
      console.log("ACCESS TOKEN: " + localStorage.getItem("accessToken"));
    } catch (err: any) {
      if (axios.isAxiosError(err) && err.response) {
        if (err.response.status === 401) {
          // invalid username/password
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
    const response = await axios.post(`http://${backendURL}/register`, request);
    console.log("REGISTER SUCCESS:", response.data);

    if (response.data["username"] != null) {
      const tokenResponse = await axios.post(
        `http://${backendURL}/generate-token`,
        {
          username: email,
          password: password,
        }
      );
      const accessToken = tokenResponse.headers.authorization; // or response.headers['authorization']

      // Since we're calling a filter endpoint, no controler was made for this endpoint so we didnt
      // return a user response DTO meaning i dont have the user's displayed name in the response,
      // so other call might be needed

      // Store it somewhere (localStorage, state, context, etc.)
      console.log("HEADERS:\n\n" + tokenResponse.headers);
      localStorage.setItem("accessToken", accessToken);

      console.log(localStorage);
    } else {
      errorMessage = "email or username already used";
    }

    /**
         * Refresh Token (in response cookie):
           The refresh token comes back as an HttpOnly cookie. 
           The browser automatically stores it in cookies because it's marked as HttpOnly. 
           You don't need to do anything—the browser handles it automatically.

          On subsequent requests:
          Include the access token manually in the header: Authorization: Bearer <token>
          The browser automatically includes the refresh token cookie
         */
  }
  return errorMessage;
}

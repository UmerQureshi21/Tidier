import { useState } from "react";
import type { UserRequestDTO } from "../Types";
import axios from "axios";
import { useNavigate } from "react-router";

export default function LogIn() {
  const [isLogin, setIsLogin] = useState(true);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [userName, setUserName] = useState("");
  const [error, setError] = useState("");
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const navigate = useNavigate()

  const backendURL = import.meta.env.VITE_BACKEND_URL;

  const validateFields = () => {
    const errors: Record<string, string> = {};

    if (!userName.trim()) {
      errors.userName = "Name is required";
    }
    if (!email.trim()) {
      errors.email = "Email is required";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      errors.email = "Please enter a valid email";
    }
    if (!password) {
      errors.password = "Password is required";
    } else if (password.length < 6) {
      errors.password = "Password must be at least 6 characters";
    }

    if (!isLogin) {
      if (!confirmPassword) {
        errors.confirmPassword = "Please confirm your password";
      } else if (password !== confirmPassword) {
        errors.confirmPassword = "Passwords do not match";
      }
    }

    return errors;
  };

  const handleSubmit = async () => {
    try {
      setError("");
      const errors = validateFields();

      if (Object.keys(errors).length > 0) {
        setFieldErrors(errors);
        return;
      }

      setFieldErrors({});

      const request: UserRequestDTO = {
        username: userName,
        password: password,
        email: email,
      };

      if (isLogin) {
        console.log("LOGIN REQUEST:", request);
        // await axios.post(`${backendURL}/generate-token`, request);
      } else {
        console.log("REGISTER REQUEST:", request);
        const response = await axios.post(
          `http://${backendURL}/register`,
          request
        );
        console.log("REGISTER SUCCESS:", response.data);

        if (response.data["username"] != null) {
          const tokenResponse = await axios.post(
            `http://${backendURL}/generate-token`,
            {
              username: userName,
              password: password,
            }
          );
          const accessToken = tokenResponse.headers.authorization; // or response.headers['authorization']
          // Store it somewhere (localStorage, state, context, etc.)
          console.log("HEADERS:\n\n"+tokenResponse.headers)
          localStorage.setItem("accessToken", accessToken);
          console.log(localStorage);
          navigate("/app/dashboard")
        } else {
          console.log("email or username already used");
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
    } catch (err) {
      console.error("REQUEST FAILED:", err);
      setError("Something went wrong. Please try again.");
    }
  };

  const clearFieldError = (field: string) => {
    setFieldErrors((prev) => {
      const updated = { ...prev };
      const { [field]: _, ...rest } = updated;
      return rest;
    });
  };

  return (
    <div className="w-full poppins-font relative">
      <div className="relative">
        <div className="absolute -inset-1 bg-gradient-to-r from-purple-600 via-pink-600 to-blue-600 rounded-2xl blur-xl opacity-75 animate-pulse"></div>

        <div className="relative bg-black rounded-2xl p-8 border border-purple-500/30">
          <div className="text-center mb-8 flex flex-col justify-center items-center">
            <h1 className="text-[50px] font-bold text-white">Trip Slice</h1>
            <p className="text-white text-sm">
              {isLogin ? "Welcome back" : "Create your account"}
            </p>
          </div>

          <div className="flex gap-2 mb-6 p-1 bg-gray-800/50 rounded-xl">
            <button
              onClick={() => {
                setIsLogin(true);
                setFieldErrors({});
                setError("");
              }}
              className={`flex-1 py-2 px-4 rounded-lg font-medium transition-all duration-300 ${
                isLogin
                  ? "bg-purple-600 text-white shadow-lg shadow-purple-500/50"
                  : "text-gray-400 hover:text-white"
              }`}
            >
              Login
            </button>
            <button
              onClick={() => {
                setIsLogin(false);
                setFieldErrors({});
                setError("");
              }}
              className={`flex-1 py-2 px-4 rounded-lg font-medium transition-all duration-300 ${
                !isLogin
                  ? "bg-purple-600 text-white shadow-lg shadow-purple-500/50"
                  : "text-gray-400 hover:text-white"
              }`}
            >
              Sign Up
            </button>
          </div>

          {error && (
            <div className="mb-4 p-3 bg-red-500/20 border border-red-500/50 rounded-lg">
              <p className="text-red-400 text-sm">{error}</p>
            </div>
          )}

          <div className="space-y-4">
            <div>
              <label className="block text-gray-300 text-sm font-medium mb-2">
                Email
              </label>
              <input
                type="email"
                value={email}
                onChange={(e) => {
                  setEmail(e.target.value);
                  clearFieldError("email");
                }}
                className={`w-full px-4 py-3 bg-gray-800/50 border rounded-xl text-white placeholder-gray-500 focus:outline-none focus:ring-2 transition-all ${
                  fieldErrors.email
                    ? "border-red-500/50 focus:border-red-500 focus:ring-red-500/20"
                    : "border-purple-500/30 focus:border-purple-500 focus:ring-purple-500/20"
                }`}
                placeholder="you@example.com"
              />
              {fieldErrors.email && (
                <p className="text-red-400 text-sm mt-1">{fieldErrors.email}</p>
              )}
            </div>

            <div>
              <label className="block text-gray-300 text-sm font-medium mb-2">
                Name
              </label>
              <input
                type="text"
                value={userName}
                onChange={(e) => {
                  setUserName(e.target.value);
                  clearFieldError("userName");
                }}
                className={`w-full px-4 py-3 bg-gray-800/50 border rounded-xl text-white placeholder-gray-500 focus:outline-none focus:ring-2 transition-all ${
                  fieldErrors.userName
                    ? "border-red-500/50 focus:border-red-500 focus:ring-red-500/20"
                    : "border-purple-500/30 focus:border-purple-500 focus:ring-purple-500/20"
                }`}
                placeholder="Goofy name here"
              />
              {fieldErrors.userName && (
                <p className="text-red-400 text-sm mt-1">
                  {fieldErrors.userName}
                </p>
              )}
            </div>

            <div>
              <label className="block text-gray-300 text-sm font-medium mb-2">
                Password
              </label>
              <input
                type="password"
                value={password}
                onChange={(e) => {
                  setPassword(e.target.value);
                  clearFieldError("password");
                }}
                className={`w-full px-4 py-3 bg-gray-800/50 border rounded-xl text-white placeholder-gray-500 focus:outline-none focus:ring-2 transition-all ${
                  fieldErrors.password
                    ? "border-red-500/50 focus:border-red-500 focus:ring-red-500/20"
                    : "border-purple-500/30 focus:border-purple-500 focus:ring-purple-500/20"
                }`}
                placeholder="••••••••"
              />
              {fieldErrors.password && (
                <p className="text-red-400 text-sm mt-1">
                  {fieldErrors.password}
                </p>
              )}
            </div>

            {!isLogin && (
              <div>
                <label className="block text-gray-300 text-sm font-medium mb-2">
                  Confirm Password
                </label>
                <input
                  type="password"
                  value={confirmPassword}
                  onChange={(e) => {
                    setConfirmPassword(e.target.value);
                    clearFieldError("confirmPassword");
                  }}
                  className={`w-full px-4 py-3 bg-gray-800/50 border rounded-xl text-white placeholder-gray-500 focus:outline-none focus:ring-2 transition-all ${
                    fieldErrors.confirmPassword
                      ? "border-red-500/50 focus:border-red-500 focus:ring-red-500/20"
                      : "border-purple-500/30 focus:border-purple-500 focus:ring-purple-500/20"
                  }`}
                  placeholder="••••••••"
                />
                {fieldErrors.confirmPassword && (
                  <p className="text-red-400 text-sm mt-1">
                    {fieldErrors.confirmPassword}
                  </p>
                )}
              </div>
            )}

            {isLogin && (
              <div className="text-right">
                <button
                  type="button"
                  className="text-sm text-purple-400 hover:text-purple-300 transition-colors"
                >
                  Forgot password?
                </button>
              </div>
            )}

            <button
              onClick={handleSubmit}
              className="w-full py-3 px-4 bg-gradient-to-r from-purple-600 to-pink-600 text-white font-semibold rounded-xl shadow-lg shadow-purple-500/50 hover:shadow-purple-500/70 hover:scale-[1.02] active:scale-[0.98] transition-all duration-200"
            >
              {isLogin ? "Login" : "Sign Up"}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

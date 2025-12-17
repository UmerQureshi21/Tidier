import { useState } from "react";

export default function LogIn() {
  const [isLogin, setIsLogin] = useState(true);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const handleSubmit = () => {
    // Handle login/signup logic here
    console.log(isLogin ? "Logging in..." : "Signing up...");
  };

  return (
    <div className="w-full poppins-font  relative">
      {/* Neon glow container */}
      <div className="relative">
        {/* Purple glow effect */}
        <div className="absolute -inset-1 bg-gradient-to-r from-purple-600 via-pink-600 to-blue-600 rounded-2xl blur-xl opacity-75 animate-pulse"></div>

        {/* Main card */}
        <div className="relative  bg-black rounded-2xl p-8 border border-purple-500/30">
          {/* Logo/Title */}
          <div className="text-center mb-8">
            {/* <h1 className="text-5xl font-bold  text-transparent bg-clip-text bg-gradient-to-r from-purple-400 to-pink-600 mb-2"> */}
            <h1 className="text-[50px] font-bold text-white">Tidier</h1>
            <p className="text-white text-sm ">
              {isLogin ? "Welcome back" : "Create your account"}
            </p>
          </div>

          {/* Toggle between Login/Signup */}
          <div className="flex gap-2 mb-6 p-1 bg-gray-800/50 rounded-xl">
            <button
              onClick={() => setIsLogin(true)}
              className={`flex-1 py-2 px-4 rounded-lg font-medium transition-all duration-300  ${
                isLogin
                  ? "bg-purple-600 text-white shadow-lg shadow-purple-500/50"
                  : "text-gray-400 hover:text-white"
              }`}
            >
              Login
            </button>
            <button
              onClick={() => setIsLogin(false)}
              className={`flex-1 py-2 px-4 rounded-lg font-medium transition-all duration-300  ${
                !isLogin
                  ? "bg-purple-600 text-white shadow-lg shadow-purple-500/50"
                  : "text-gray-400 hover:text-white"
              }`}
            >
              Sign Up
            </button>
          </div>

          {/* Form Fields */}
          <div className="space-y-4 ">
            {/* Email Input */}
            <div>
              <label className="block text-gray-300 text-sm font-medium mb-2">
                Email
              </label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full px-4 py-3 bg-gray-800/50 border border-purple-500/30 rounded-xl text-white placeholder-gray-500 focus:outline-none focus:border-purple-500 focus:ring-2 focus:ring-purple-500/20 transition-all"
                placeholder="you@example.com"
              />
            </div>

            {/* Password Input */}
            <div>
              <label className="block text-gray-300 text-sm font-medium mb-2">
                Password
              </label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full px-4 py-3 bg-gray-800/50 border border-purple-500/30 rounded-xl text-white placeholder-gray-500 focus:outline-none focus:border-purple-500 focus:ring-2 focus:ring-purple-500/20 transition-all"
                placeholder="••••••••"
              />
            </div>

            {/* Confirm Password (only for signup) */}
            {!isLogin && (
              <div>
                <label className="block text-gray-300 text-sm font-medium mb-2">
                  Confirm Password
                </label>
                <input
                  type="password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  className="w-full px-4 py-3 bg-gray-800/50 border border-purple-500/30 rounded-xl text-white placeholder-gray-500 focus:outline-none focus:border-purple-500 focus:ring-2 focus:ring-purple-500/20 transition-all"
                  placeholder="••••••••"
                />
              </div>
            )}

            {/* Forgot Password (only for login) */}
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

            {/* Submit Button */}
            <button
              onClick={handleSubmit}
              className="w-full py-3 px-4 bg-gradient-to-r from-purple-600 to-pink-600 text-white font-semibold rounded-xl shadow-lg shadow-purple-500/50 hover:shadow-purple-500/70 hover:scale-[1.02] active:scale-[0.98] transition-all duration-200"
            >
              {isLogin ? "Login" : "Sign Up"}
            </button>
          </div>

          {/* Divider */}
          <div className="flex items-center gap-4 my-6">
            <div className="flex-1 h-px bg-gray-700"></div>
            <span className="text-gray-500 text-sm">or</span>
            <div className="flex-1 h-px bg-gray-700"></div>
          </div>

          {/* Social Login */}
          <div className="space-y-3">
            <button className="w-full py-3 px-4 bg-gray-800/50 border border-gray-700 rounded-xl text-white font-medium hover:bg-gray-800 hover:border-purple-500/30 transition-all duration-200 flex items-center justify-center gap-3">
              <svg className="w-5 h-5" viewBox="0 0 24 24" fill="currentColor">
                <path
                  d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                  fill="#4285F4"
                />
                <path
                  d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                  fill="#34A853"
                />
                <path
                  d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                  fill="#FBBC05"
                />
                <path
                  d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                  fill="#EA4335"
                />
              </svg>
              Continue with Google
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

import { NavLink } from "react-router";

export default function NotFoundPage() {
  return (
    <div className="poppins-font min-h-screen bg-black flex items-center justify-center p-4 relative overflow-hidden">
      {/* Animated background elements */}
      <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-[#925CFE]/10 rounded-full blur-3xl animate-pulse"></div>
      <div className="absolute bottom-1/4 right-1/4 w-64 h-64 bg-[#925CFE]/10 rounded-full blur-3xl animate-pulse delay-700"></div>

      <div className="relative z-10 text-center max-w-2xl">
        {/* 404 Number */}
        <div className="mb-8">
          <h1 className="text-[180px] md:text-[250px] font-bold leading-none">
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-[#925CFE] via-purple-500 to-[#925CFE] bg-[length:200%_auto] animate-[gradient_3s_ease_infinite]">
              404
            </span>
          </h1>
        </div>

        {/* Message */}
        <div className="mb-12">
          <h2 className="text-3xl md:text-5xl font-bold text-white mb-4">
            Oops! Page Not Found
          </h2>
          <p className="text-gray-400 text-lg md:text-xl">
            Looks like this montage got lost in the footage...
          </p>
        </div>

        {/* Button */}
        <NavLink
          to="/app/dashboard"
          className="inline-flex items-center gap-3 px-8 py-4 bg-gradient-to-r from-[#925CFE] to-purple-600 text-white text-lg font-semibold rounded-full shadow-lg shadow-[#925CFE]/50 hover:shadow-[#925CFE]/70 hover:scale-105 transition-all duration-300 group"
        >
          <svg
            className="w-5 h-5 group-hover:-translate-x-1 transition-transform"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M10 19l-7-7m0 0l7-7m-7 7h18"
            />
          </svg>
          Back to Home
        </NavLink>

        {/* Decorative line */}
        <div className="mt-16 flex items-center justify-center gap-4">
          <div className="h-px w-16 bg-gradient-to-r from-transparent to-[#925CFE]"></div>
          <span className="text-[#925CFE] text-sm font-medium">Tidier</span>
          <div className="h-px w-16 bg-gradient-to-l from-transparent to-[#925CFE]"></div>
        </div>
      </div>

      <style>{`
        @keyframes gradient {
          0%, 100% {
            background-position: 0% 50%;
          }
          50% {
            background-position: 100% 50%;
          }
        }
      `}</style>
    </div>
  );
}

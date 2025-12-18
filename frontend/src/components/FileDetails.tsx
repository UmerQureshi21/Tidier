interface FileDetailsProps {
  thumbnail: string;
  name: string;
  size: number;
  type: string;
  isClicked: boolean;
}

export default function FileDetails({
  thumbnail,
  name,
  size,
  type,
  isClicked,
}: FileDetailsProps) {
  // Format file size
  const formatSize = (bytes: number) => {
    if (bytes === 0) return "0 Bytes";
    const k = 1024;
    const sizes = ["Bytes", "KB", "MB", "GB"];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + " " + sizes[i];
  };

  // Get file extension
  const getExtension = (filename: string) => {
    return filename.split(".").pop()?.toUpperCase() || "FILE";
  };

  return (
    <div
      className={`relative w-full h-full rounded-xl border-2 transition-all duration-300 overflow-hidden group ${
        isClicked
          ? "bg-[#925CFE]/20 border-[#925CFE] shadow-lg shadow-[#925CFE]/30"
          : "bg-gray-900/50 border-gray-800 hover:border-[#925CFE]/50 hover:bg-gray-900/70"
      }`}
    >
      {/* Selection indicator */}
      <div
        className={`absolute top-3 right-3 w-6 h-6 rounded-full border-2 flex items-center justify-center transition-all duration-300 ${
          isClicked
            ? "bg-[#925CFE] border-[#925CFE]"
            : "bg-transparent border-gray-600 group-hover:border-[#925CFE]/50"
        }`}
      >
        {isClicked && (
          <svg
            className="w-4 h-4 text-white"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={3}
              d="M5 13l4 4L19 7"
            />
          </svg>
        )}
      </div>

      {/* Content */}
      <div className="flex items-center gap-4 p-4">
        {/* Thumbnail / File Icon */}
        <div className="flex-shrink-0 w-16 h-16 rounded-lg bg-gradient-to-br from-[#925CFE] to-purple-700 flex items-center justify-center overflow-hidden">
          {thumbnail ? (
            <img
              src={thumbnail}
              alt={name}
              className="w-full h-full object-cover"
            />
          ) : (
            <div className="text-white text-xs font-bold">
              {getExtension(name)}
            </div>
          )}
        </div>

        {/* File Info */}
        <div className="flex-1 min-w-0">
          <h3
            className={`font-semibold text-base truncate transition-colors ${
              isClicked ? "text-white" : "text-gray-200 group-hover:text-white"
            }`}
          >
            {name}
          </h3>
          <div className="flex items-center gap-3 mt-1">
            <span className="text-xs text-gray-400">{formatSize(size)}</span>
            {type && (
              <>
                <span className="text-gray-600">•</span>
                <span className="text-xs text-gray-400">{type}</span>
              </>
            )}
          </div>
        </div>

        {/* Hover indicator */}
        <div
          className={`flex-shrink-0 transition-all duration-300 ${
            isClicked ? "opacity-100" : "opacity-0 group-hover:opacity-100"
          }`}
        >
          <svg
            className="w-5 h-5 text-[#925CFE]"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M9 5l7 7-7 7"
            />
          </svg>
        </div>
      </div>

      {/* Animated border glow on hover */}
      <div
        className={`absolute inset-0 rounded-xl opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none ${
          !isClicked &&
          "bg-gradient-to-r from-transparent via-[#925CFE]/10 to-transparent"
        }`}
      ></div>
    </div>
  );
}

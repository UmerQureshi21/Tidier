import React from "react";

interface SelectableFileDetailsProps {
    name: string;
    size: number;
    type: string;
    videoSrc: string;
    isSelected: boolean;
    onToggle: () => void;
  }

  export default function SelectableFileDetails({
    name,
    videoSrc,
    isSelected,
    onToggle,
  }: SelectableFileDetailsProps) {
  const videoRef = React.useRef<HTMLVideoElement>(null);
  const [duration, setDuration] = React.useState(0);
  const [isHovering, setIsHovering] = React.useState(false);

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins}:${secs.toString().padStart(2, "0")}`;
  };

  React.useEffect(() => {
    if (isHovering) {
      videoRef.current?.play();
    } else {
      videoRef.current?.pause();
    }
  }, [isHovering]);

  return (
    <div
      className={`flex flex-col w-full h-[450px] rounded-xl border-2 transition-all duration-300 overflow-hidden group ${
        isSelected
          ? "bg-[#925CFE]/10 border-[#925CFE] shadow-lg shadow-[#925CFE]/20"
          : "bg-[#222222] border-[#222222] hover:border-[#925CFE]/50 hover:bg-[#222222]/90"
      }`}
      onMouseEnter={() => setIsHovering(true)}
      onMouseLeave={() => setIsHovering(false)}
      onClick={onToggle}
    >
      {/* Video */}
      <div className="flex-1 bg-[#000000] overflow-hidden">
        <video
          ref={videoRef}
          src={videoSrc}
          controls
          muted
          crossOrigin="anonymous"
          className="w-full h-full object-cover"
          onLoadedMetadata={() => setDuration(videoRef.current?.duration || 0)}
        />
      </div>

      {/* File Info */}
      <div className="p-4 flex flex-col gap-3">
        <h3 className="font-semibold text-base truncate text-gray-200 group-hover:text-white transition-colors">
          {name}
        </h3>
        <div className="flex items-center gap-2 text-xs text-gray-400">
          <span>{formatTime(duration)}</span>
        </div>
        <summary className="text-xs text-gray-500 line-clamp-2"></summary>

        {/* Selection Indicator */}
        <div className="flex items-center gap-2 pt-2 border-t border-gray-700">
          <div
            className={`w-5 h-5 rounded-md border-2 flex items-center justify-center transition-all ${
              isSelected
                ? "bg-[#925CFE] border-[#925CFE]"
                : "border-gray-500 hover:border-[#925CFE]"
            }`}
          >
            {isSelected && (
              <svg
                className="w-3 h-3 text-white"
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
          <span className={`text-xs font-medium ${
            isSelected ? "text-[#925CFE]" : "text-gray-400"
          }`}>
            {isSelected ? "Selected" : "Select"}
          </span>
        </div>
      </div>
    </div>
  );
}
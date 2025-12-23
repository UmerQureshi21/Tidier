import React from "react";

interface FileDetailsProps {
  name: string;
  size: number;
  type: string;
  videoSrc: string;
}

export default function FileDetails({
  name,
  size,
  type,
  videoSrc,
}: FileDetailsProps) {
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
      className="flex flex-col w-full h-[400px] rounded-xl border-2 transition-all  duration-300 overflow-hidden group bg-[#222222] border-[#222222] hover:border-[#925CFE]/50 hover:bg-[#222222]/90"
      onMouseEnter={() => setIsHovering(true)}
      onMouseLeave={() => setIsHovering(false)}
    >
      {/* Video */}
      <div className="flex-1 bg-[#000000] overflow-hidden">
        <video
          ref={videoRef}
          src={videoSrc}
          controls
          muted
          className="w-full h-full object-cover"
          onLoadedMetadata={() => setDuration(videoRef.current?.duration || 0)}
        />
      </div>

      {/* File Info */}
      <div className="p-4 flex flex-col gap-2">
        <h3 className="font-semibold text-base truncate text-gray-200 group-hover:text-white transition-colors">
          {name}
        </h3>
        <div className="flex items-center gap-2 text-xs text-gray-400">
          <span>{formatTime(duration)}</span>
        </div>
        <summary className="text-xs text-gray-500 line-clamp-2"></summary>
      </div>
    </div>
  );
}

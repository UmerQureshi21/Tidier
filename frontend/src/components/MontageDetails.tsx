import { useState } from "react";

export default function MontageDetails({
  title,
  topic,
  photos,
  videoSrc,
}: {
  title: string;
  topic: string;
  photos: string[];
  videoSrc: string;
}) {
  const [isPhotosExpanded, setIsPhotosExpanded] = useState(false);
  const [isVideoHovering, setIsVideoHovering] = useState(false);

  return (
    <div
      className={`
      bg-[#222222] rounded-xl shadow-md cursor-pointer
      transition-all duration-500 ease-in-out
      hover:shadow-xl border-2
      flex flex-col h-[full]
      ${
        isPhotosExpanded
          ? "border-[#925CFE] shadow-2xl"
          : "border-[#222222] hover:border-[#925CFE]/50"
      }
    `}
    >
      <div className="p-4 md:p-6 bg-[#222222] flex flex-col h-full rounded-xl">
        <div className="flex flex-col gap-4 flex-1">
          <div className="flex-1">
            <h3 className="text-xl md:text-2xl font-bold text-[#fff] mb-2">
              {title}
            </h3>
            <p className="text-sm md:text-base text-[#925CFE] mb-4">
              <span className="font-bold">Topic: </span>
              {topic}
            </p>

            {/* Video Section */}
            <div
              className="w-full h-[400px] bg-[#111] rounded-lg overflow-hidden mb-4"
              onMouseEnter={() => setIsVideoHovering(true)}
              onMouseLeave={() => setIsVideoHovering(false)}
            >
              <video
                src={videoSrc}
                crossOrigin="anonymous"
                muted
                controls
                autoPlay={isVideoHovering}
                className="w-full h-48 md:h-[400px] object-cover"
              />
            </div>
          </div>

          <div
            className={`
              overflow-hidden transition-all duration-500
              ${
                isPhotosExpanded
                  ? "max-h-[500px] opacity-100 mt-4"
                  : "max-h-0 opacity-0"
              }
            `}
          >
            <div className="border-t-2 border-[#fff] border-opacity-20 pt-4">
              <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
                {photos?.map((photo, idx) => (
                  <video
                    key={idx}
                    src={photo}
                    autoPlay
                    muted
                    className="w-full h-32 md:h-40 object-cover rounded-lg shadow-md hover:shadow-lg transition-shadow"
                  />
                ))}
              </div>
            </div>
          </div>
        </div>

        <button
          onClick={() => setIsPhotosExpanded(!isPhotosExpanded)}
          className="text-[#fff] hover:text-[#925CFE] font-semibold text-sm md:text-base transition-colors border-t-2 border-[#fff] border-opacity-20 pt-4 mt-4 flex items-center justify-between"
        >
          <span>Videos that this montage parsed from</span>
          <svg
            className={`w-5 h-5 text-[#fff] transition-transform duration-500 ${
              isPhotosExpanded ? "rotate-180" : ""
            }`}
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M19 14l-7 7m0 0l-7-7m7 7V3"
            />
          </svg>
        </button>
      </div>
    </div>
  );
}

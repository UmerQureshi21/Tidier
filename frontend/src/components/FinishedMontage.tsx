import { useEffect, useState } from "react";

interface Props {
  preSignedUrl: string;
  progressMessage: string;
}

export default function FinishedMontage({
  preSignedUrl,
  progressMessage,
}: Props) {
  const [reloadKey, setReloadKey] = useState(0);
  const [videoReady, setVideoReady] = useState(false);


  // Only reload video until it successfully plays
  useEffect(() => {
    if (!preSignedUrl || videoReady) return; // Stop if ready
    const interval = setInterval(() => {
      setReloadKey((k) => k + 1);
    }, 2000);

    return () => clearInterval(interval);
  }, [preSignedUrl, videoReady]);

  return (
    <>
      <div className="relative  w-full bg-black h-[60px] flex items-center justify-center prompt-section">
        <h1 className="w-[70%] text-[white] text-[20px] text-center overflow-x-scroll whitespace-nowrap">
          {progressMessage}
        </h1>
      </div>
      <div className="w-full bg-black pb-[100px] flex flex-col items-center text-white">
        <div className="w-[80%] h-[300px] bg-[rgb(20,20,20)] flex items-center justify-center rounded-[20px]">
          <video
            key={`${preSignedUrl}-${reloadKey}`} // changing key forces React to reload
            src={preSignedUrl}
            className="w-[80%] h-[250px] rounded-[40px]"
            controls
            autoPlay
            onPlay={() => {
              console.log("Video started playing");
              setVideoReady(true); // stop reloading
            }}
            onError={() => {
              console.log("Video failed to load, retrying...");
              setVideoReady(false); // keep trying
            }}
          />
        </div>
      </div>
    </>
  );
}

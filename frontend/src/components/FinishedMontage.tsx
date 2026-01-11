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
      <div className="relative  w-full bg-black flex items-center justify-center ">
        <h1 className="w-[70%] text-[white] text-[40px] font-bold text-center ">
          {progressMessage}
        </h1>
      </div>
      <div className="w-full bg-black py-[20px] flex flex-col items-center text-white">
          <video
            key={`${preSignedUrl}-${reloadKey}`} // changing key forces React to reload
            src={preSignedUrl}
            className="w-[90%] max-w-[400px] rounded-[40px] border-[2px] border-white"
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
    </>
  );
}

import { useEffect, useState } from "react";
import type { MontageResponseDTO } from "../Types";
import MontageDetails from "./MontageDetails";
import { getAllMontages } from "../services/montageService";

export default function Montages() {
  const [montages, setMontages] = useState<MontageResponseDTO[]>([]);

  async function getMontages() {
    try {
      const videos = await getAllMontages();

      setMontages(videos);
    } catch (err) {
      console.error("Error loading videos:", err);
    }
  }

  useEffect(() => {
    getMontages();
  }, []);

  return (
    <div className="w-full bg-black flex justify-center pb-[100px]">
      {montages.length > 0 ? (
        <div className="bg-[rgb(20,20,20)] w-[95%] min-h-[800px] overflow-y-scroll rounded-[10px] grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 p-4">
          {montages?.map((montage, index) => (
            <MontageDetails
              key={`montage ${index}`}
              title={montage.name}
              topic={montage.prompt}
              createdAt={montage.createdAt}
              duration={montage.duration}
              photos={montage.videos.map((video, vi) => video.previewUrl)}
              videoSrc={montage.preSignedUrl}
            />
          ))}
        </div>
      ) : (
        <div className="text-white min-h-screen mt-[100px] poppins-font text-[40px] w-[50%] text-center">
          No montages have been made yet. Go on the{" "}
          <span className="text-[#925cfe]">Create Montage</span> page to relive
          your first vacation!
        </div>
      )}
    </div>
  );
}

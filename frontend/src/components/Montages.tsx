import axios from "axios";
import { useEffect, useState } from "react";
import type { MontageResponseDTO, VideoRequestDTO } from "../Types";
import MontageDetails from "./MontageDetails";

// Cache variables (persist across component mounts)
let cachedMontages: MontageResponseDTO[] | null = null;
let cacheTime: number = 0;
const CACHE_DURATION = 10 * 60 * 1000; // 10 minutes

export default function Montages() {
  const backendURL = import.meta.env.VITE_BACKEND_URL;
  const [montages, setMontages] = useState<MontageResponseDTO[]>([]);

  async function getMontages() {
    try {
      const now = Date.now();

      // Check if cache exists and is still valid
      if (cachedMontages && now - cacheTime < CACHE_DURATION) {
        console.log("Using cached Montages");
        setMontages(cachedMontages);
        return;
      }

      // Fetch new data
      console.log("Fetching fresh montages from API");
      const res = await axios.get(`http://${backendURL}/montages`);
      const data: MontageResponseDTO[] = res.data;

      for (let idk of data) {
        console.log(idk);
      }

      // Store in cache and update state
      cachedMontages = data;
      cacheTime = now;
      setMontages(data);
    } catch (err) {
      console.error("Upload failed bro:", err);
    }
  }

  useEffect(() => {
    getMontages();
  }, []);

  return (
    <div className="w-full bg-black flex justify-center">
      <div className="bg-[rgb(20,20,20)] w-[95%] min-h-[800px] overflow-y-scroll rounded-[10px] grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 p-4">
        {montages?.map((montage, index) => (
          <MontageDetails
            key={`montage ${index}`}
            title={montage.name}
            topic={montage.prompt}
            photos={[]}
            videoSrc={montage.preSignedUrl}
          />
        ))}
      </div>
    </div>
  );
}

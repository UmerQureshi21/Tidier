import { useEffect, useState } from "react";
import axiosInstance from "../services/refreshTokenAxios";
import type { UserResponseDTO, MontageResponseDTO } from "../Types";
import DashboardStats from "./DashboardStats";
import PromptSuggestion from "./PromptSuggestions";
import MontageDetails from "./MontageDetails";

const CACHE_DURATION = 10 * 60 * 1000; // 10 minutes
let cachedMontages: MontageResponseDTO[] | null = null;
let cacheTime: number = 0;

export default function DashboardHero() {
  const [displayedName, setDisplayedName] = useState<string>("");
  const [mostRecentMontage, setMostRecentMontage] =
    useState<MontageResponseDTO | null>(null);
  const [mostVideosMontage, setMostVideosMontage] =
    useState<MontageResponseDTO | null>(null);

  async function getUser() {
    const res = await axiosInstance.get(`/user`);
    const userData: UserResponseDTO = res.data;
    setDisplayedName(userData.displayedName);
  }

  async function getMontages() {
    try {
      const now = Date.now();

      if (cachedMontages && now - cacheTime < CACHE_DURATION) {
        console.log("Using cached montage highlights");
        setMostRecentMontage(cachedMontages[0]);
        setMostVideosMontage(cachedMontages[1]);
      }

      console.log("Fetching fresh montage highlights from API");

      const longest = await axiosInstance.get("/montages/longest");
      setMostRecentMontage(longest.data);
      const mostVids = await axiosInstance.get("montages/most-vids");
      setMostVideosMontage(mostVids.data);

      cachedMontages = [longest.data, mostVids.data];
      cacheTime = now;
    } catch (err) {
      console.error("Failed to fetch montages:", err);
    }
  }

  useEffect(() => {
    getUser();
   // getMontages();
  }, []);

  return (
    <div className="w-full  poppins-font text-white bg-black flex flex-col items-center gap-8 p-8">
      {/* Hero Section */}
      <div className=" w-[95%]">
        <h1 className="text-[40px] text-center sm:text-left sm:text-[80px] font-bold">
          Hi,&nbsp;
          <span style={{ color: "#925cfe" }}>{displayedName}</span>
        </h1>
      </div>

      {/* Main Content */}
      <div className="w-[95%]  flex flex-col sm:flex-row gap-8 bg-[#222222] p-[15px] rounded-[15px]">
        {/* Left Side */}
        <div className="w-full sm:w-[50%]  flex flex-col ">
          <DashboardStats />
        </div>
        {/* Right Side */}
        <div className="w-full sm:w-[50%]  flex flex-col ">
          <PromptSuggestion />
        </div>
      </div>
      <h1 className="text-[30px] text-center sm:text-left w-[95%] sm:text-[70px] poppins-font font-bold">
        Montages that stand out:
      </h1>
      <div className="flex flex-col sm:flex-row w-full justify-around">
        {/* Most Recent Montage */}
        {mostRecentMontage && (
          <div className="w-full sm:w-[40%]">
            <h2 className="text-[20px] sm:text-[35px]  my-4">Longest Time!</h2>
            <MontageDetails
              title={mostRecentMontage.name}
              topic={mostRecentMontage.prompt}
              photos={mostRecentMontage.videos?.map((v) => v.previewUrl) || []}
              videoSrc={mostRecentMontage.preSignedUrl}
            />
          </div>
        )}
        {/* Most Videos Montage */}
        {mostVideosMontage && (
          <div className="w-full sm:w-[40%]">
            <h2 className="text-[20px] sm:text-[35px]  my-4">Most Videos!</h2>
            <MontageDetails
              title={mostVideosMontage.name}
              topic={mostVideosMontage.prompt}
              photos={mostVideosMontage.videos?.map((v) => v.previewUrl) || []}
              videoSrc={mostVideosMontage.preSignedUrl}
            />
          </div>
        )}
      </div>
    </div>
  );
}

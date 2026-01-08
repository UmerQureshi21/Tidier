import { useEffect, useState } from "react";
import axiosInstance from "../services/refreshTokenAxios";
import type { UserResponseDTO, MontageResponseDTO } from "../Types";
import DashboardStats from "./DashboardStats";
import PromptSuggestion from "./PromptSuggestions";
import MontageDetails from "./MontageDetails";

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
      const res = await axiosInstance.get(`/montages`);
      const montages: MontageResponseDTO[] = res.data;

      if (montages.length > 0) {
        // Most recent - assuming sorted by date, first one is most recent
        setMostRecentMontage(montages[0]); // will change and get montage from backend
        setMostVideosMontage(montages[1]); // will change and get montage from backend
      }
    } catch (err) {
      console.error("Failed to fetch montages:", err);
    }
  }

  useEffect(() => {
    getUser();
    getMontages();
  }, []);

  return (
    <div className="w-full poppins-font text-white bg-black flex flex-col items-center gap-8 p-8">
      {/* Hero Section */}
      <div className=" w-[95%]">
        <h1 className="text-[80px] font-bold">
          Hi,&nbsp;
          <span style={{ color: "#925cfe" }}>{displayedName}</span>
        </h1>
      </div>

      {/* Main Content */}
      <div className="w-[95%] flex gap-8 bg-[#222222] p-[15px] rounded-[15px]">
        {/* Left Side */}
        <div className="w-[50%] flex flex-col ">
          <DashboardStats />
        </div>
        {/* Right Side */}
        <div className="w-[50%] flex flex-col ">
          <PromptSuggestion />
        </div>
      </div>
      <div className="flex w-full justify-around">
        {/* Most Recent Montage */}
        {mostRecentMontage && (
          <div className="w-[40%]">
            <h2 className="text-2xl font-bold mb-4">Most Recent</h2>
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
          <div className="w-[40%]">
            <h2 className="text-2xl font-bold mb-4">Most Videos</h2>
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

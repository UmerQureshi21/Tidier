import { useEffect, useState } from "react";
import axiosInstance from "../services/refreshTokenAxios";
import type { UserResponseDTO } from "../Types";

export default function DashboardHero() {
  const [displayedName, setDisplayedName] = useState<String>("");
  async function getUser() {
    const res = await axiosInstance.get(`/user`);
    const userData: UserResponseDTO = res.data;
    setDisplayedName(userData.displayedName);
  }

  useEffect(() => {
    getUser();
  }, []);

  return (
    <div className=" w-full text-white font-bold flex">
      <h1 className="text-[60px]">
        Hi,
        <span className="bg-gradient-to-b from-white to-[#925cfe] bg-clip-text text-transparent ">
          {displayedName}
        </span>
      </h1>{" "}
    </div>
  );
}

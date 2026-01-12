import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
//import type { MontageResponseDTO } from "../Types";
import axiosInstance from "../services/refreshTokenAxios";

export default function DashboardStats() {
  const navigate = useNavigate();
  const [videoCount, setVideoCount] = useState(30);
  const [montageCount, setMontageCount] = useState(7);
  // const [recentMontages, setRecentMontages] = useState<MontageResponseDTO[]>(
  //   []
  // );
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        // Fetch all videos
        const videosRes = await axiosInstance.get("/videos");
        setVideoCount(videosRes.data.length);
        // Fetch all montages
        const montagesRes = await axiosInstance.get("/montages");
        setMontageCount(montagesRes.data.length);
        // Get 3 most recent montages (assuming they're sorted by date)
        // setRecentMontages(montagesRes.data.slice(0, 3));
      } catch (err) {
        console.error("Failed to fetch dashboard stats:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, []);

  if (loading) {
    return <div className="text-white">Loading...</div>;
  }

  return (
    <div className="">
      {/* Stats Section */}
      <div className="grid grid-cols-2 gap-6 mb-12">
        <div className="bg-gradient-to-br from-[#111] to-[#111] p-6 rounded-lg">
          <p className="text-gray-300 text-sm mb-2">Total Videos</p>
          <p className="text-4xl font-thin">{videoCount}</p>
        </div>
        <div className="bg-gradient-to-br from-[#111] to-[#111] p-6 rounded-lg">
          <p className="text-gray-300 text-sm mb-2">Total Montages</p>
          <p className="text-4xl font-thin">{montageCount}</p>
        </div>
      </div>

      {/* Quick Action Buttons */}
      <div className="flex flex-col gap-4 ">
        <h1 className=" text-[30px] font-normal">Ready to Create?</h1>
        <button
          onClick={() => navigate("/app/create")}
          className="w-[100%] bg-[#925cfe] hover:bg-[#7a4dd1] text-white text-[30px] font-bold h-[80px]  rounded-lg transition"
        >
          Create Montage!
        </button>
      </div>

      {/* Recent Montages Section */}
      {/* <div>
        <h2 className="text-2xl font-bold mb-6">Recent Montages</h2>
        {recentMontages.length === 0 ? (
          <p className="text-gray-400">No montages created yet. Start by creating one!</p>
        ) : (
          <div className="space-y-4">
            {recentMontages.map((montage) => (
              <div
                key={montage.id}
                className="bg-gray-900 p-4 rounded-lg hover:bg-gray-800 transition cursor-pointer"
                onClick={() => navigate("/app/montages")}
              >
                <h3 className="font-bold text-lg">{montage.name}</h3>
                <p className="text-gray-400 text-sm">Created: {new Date(montage.createdAt).toLocaleDateString()}</p>
              </div>
            ))}
          </div>
        )}
      </div> */}
    </div>
  );
}

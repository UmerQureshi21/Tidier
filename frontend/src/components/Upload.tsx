import { useEffect, useState } from "react";
import FileDetails from "./VideoDetails";
import type { VideoRequestDTO } from "../Types";
import axios from "axios";

// Cache variables (persist across component mounts)
let cachedVideos: VideoRequestDTO[] | null = null;
let cacheTime: number = 0;
const CACHE_DURATION = 10 * 60 * 1000; // 10 minutes
const token = localStorage.getItem("accessToken");

// I logged in, loaded some videos, then went back and signed up,
// but videos from the prev user which i was, loaded, until i
// reloaded the site and it worked again (no montages were shown
// which is good because i jsut signed up)

export default function Upload() {
  const backendURL = import.meta.env.VITE_BACKEND_URL;
  let [prevFiles, setPrevFiles] = useState<VideoRequestDTO[]>([]);

  async function getAllFiles() {
    try {
      const now = Date.now();
      // Check if cache exists and is still valid
      if (cachedVideos && now - cacheTime < CACHE_DURATION) {
        console.log("Using cached videos");
        setPrevFiles(cachedVideos);
        return;
      }
      // Fetch new data
      console.log("Fetching fresh videos from API");
      console.log("TOKEN: " + token);
      const res = await axios.get(`http://${backendURL}/videos`, {
        headers: {
          Authorization: token,
        },
      });
      const data = res.data;
      let fileDetails = [];
      for (let file of data) {
        fileDetails.push(file);
        console.log(file.previewUrl);
      }
      setPrevFiles(fileDetails);
      // Store in cache
      cachedVideos = fileDetails;
      cacheTime = now;
    } catch (err) {
      console.error("Upload failed bro:", err);
    }
  }

  useEffect(() => {
    getAllFiles();
  }, []);

  async function handleFileInput(e: React.ChangeEvent<HTMLInputElement>) {
    const request = new FormData();
    if (e.target.files) {
      for (let [index, file] of Object.entries(e.target.files)) {
        console.log(`File ${index}: ${file}`);
        request.append("files", file);
      }
      try {
        const res = await axios.post(`http://${backendURL}/videos`, request, {
          headers: {
            Authorization: token,
          },
        });
        console.log("Upload successful:", res.data);
        getAllFiles();
      } catch (err) {
        console.error("Upload failed:", err);
      }
    }
  }

  return (
    <div className="w-full ">
      <div className="relative w-full h-[100px] bg-black prompt-section flex flex-col items-center">
        <div className="w-full pt-[40px] bg-black flex flex-col items-center">
          <button
            onMouseEnter={() => {
              console.log("Button got hovered!");
            }}
            className="hover:cursor-pointer mt-[-20px] hover:shadow-[0_0_10px_white] shadow-[0_0_0_white] transition duration-350 ease relative w-[60%] bg-[#925CFE] px-[30px] py-[15px] rounded-[20px] poppins-font text-white text-[20px]"
          >
            <input
              type="file"
              multiple // Always makes the request a FileList even though you put in one file
              accept="video/*"
              onChange={handleFileInput} // Pass the reference
              className="absolute w-full h-full bg-red-300 left-0 top-0 rounded-[20px] opacity-0 hover:cursor-pointer z-[100]"
            />
            Upload Videos
          </button>
        </div>
        <div className="text-white poppins w-[90%] flex flex-col items-center justify-center">
          <div className="flex flex-col items-center w-full "></div>
        </div>
      </div>
      <div>
        <div className="w-full bg-black flex flex-col items-center pb-[100px] ">
          <div className="bg-[rgb(20,20,20)] w-[95%] min-h-[800px] overflow-y-scroll rounded-[10px] grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 p-4">
            {prevFiles.map((file, index) => (
              <div
                className="flex items-center justify-center hover:cursor-pointer"
                key={`Prev Video ${index}`}
              >
                <FileDetails
                  name={file.name}
                  size={0}
                  type={""}
                  videoSrc={file.previewUrl}
                />
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

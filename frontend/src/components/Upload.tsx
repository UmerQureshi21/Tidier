import { useEffect, useState } from "react";
import FileDetails from "./FileDetails";
import type { VideoRequestDTO, MontageRequestDTO } from "../Types";
import { useNavigate } from "react-router";
import axios from "axios";

// Cache variables (persist across component mounts)
let cachedVideos: VideoRequestDTO[] | null = null;
let cacheTime: number = 0;
const CACHE_DURATION = 10 * 60 * 1000; // 10 minutes

export default function Upload() {
  const backendURL = import.meta.env.VITE_BACKEND_URL;
  const navigate = useNavigate();
  let [prevFiles, setPrevFiles] = useState<VideoRequestDTO[]>([]);
  let [sentence, setSentence] = useState<string>("");
  let [title, setTitle] = useState<string>("");
  let [videosSelected, setVideosSelected] = useState<boolean>(false);

  // async function handleSubmit() {
  //   let videos: VideoRequestDTO[] = [];
  //   for (let i = 0; i < clicks.length; i++) {
  //     if (clicks[i]) {
  //       videos.push(prevFiles[i]);
  //       videosSelected = true;
  //     }
  //   }

  //   let request: MontageRequestDTO = {
  //     name: `${title}.mp4`,
  //     sentence: `Give all time intervals of ${sentence}, only tell me the intervals, nothing else, and in this format: 00:00-00:06, 01:02-01:09, ... If there are no such time intervals, only return 00:00-00:00`,
  //     prompt: sentence,
  //     videoRequestDTOs: videos,
  //   };

  //   navigate("/view-montage");

  //   await fetch(`http://${backendURL}/montages`, {
  //     method: "POST",
  //     headers: {
  //       "Content-Type": "application/json", // tell backend to expect JSON
  //     },
  //     body: JSON.stringify(request), // stringify the object
  //   })
  //     .then((res) => {
  //       if (!res.ok) {
  //         throw new Error(`Server responded with status ${res.status}`);
  //       }
  //       return res.json();
  //     })
  //     .then((data) => {
  //       console.log(data);
  //     })
  //     .catch((err) => {
  //       console.error("Montage upload failed my bro:", err);
  //     });

  //   console.log(videos);
  // }

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
      const res = await axios.get(`http://${backendURL}/videos`);
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
        //console.log(`File ${index}: ${file}`);
        request.append("files", file);
      }

      try {
        const res = await axios.post(`http://${backendURL}/videos`, request);

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
          <div className="flex flex-col items-center w-full ">
            {/* <div className="flex w-full justify-between items-center">
              <h1 className="w-[20%] text-[25px]">Topic: </h1>
              <input
                type="text"
                onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                  setSentence(e.target.value);
                }} // react onChange is same as vanilla input event listener
                autoFocus
                className="w-[77%] mt-[10px] outline-none border-b-[1px] border-b-[#6600FF] pb-[3px]"
                style={{ caretColor: "#6600FF" }}
              />
            </div> */}
            {/* <div className="flex w-full justify-between items-center">
              <h1 className="w-[20%] text-[25px]">Title: </h1>
              <input
                type="text"
                onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                  setTitle(e.target.value);
                }} // react onChange is same as vanilla input event listener
                autoFocus
                className="w-[77%] mt-[10px] outline-none border-b-[1px] border-b-[#6600FF] pb-[3px]"
                style={{ caretColor: "#6600FF" }}
              />
            </div> */}
          </div>
          {/* <button
            className="hover:cursor-pointer mt-[15px] hover:shadow-[0_0_10px_white] shadow-[0_0_0_white] transition duration-150 ease relative w-[80%] bg-[#925CFE] px-[30px] py-[15px] rounded-[20px] poppins-font text-white text-[20px]"
            onClick={handleSubmit}
            disabled={sentence == "" || title == "" ? true : false}
            style={{
              backgroundColor:
                sentence == "" || title == "" ? "#222222" : "#925CFE",
            }}
          >
            Generate Montage!
          </button> */}
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

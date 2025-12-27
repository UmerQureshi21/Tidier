import { useEffect, useState } from "react";
import type {
  VideoRequestDTO,
  MontageRequestDTO,
  MontageResponseDTO,
} from "../Types";
import axios from "axios";
import SelectableFileDetails from "./VideoDetailsSelectable";
import MontageProgressWebSocket from "./MontageProcessWebSocket";
import FinishedMontage from "./FinishedMontage";

// Cache variables (persist across component mounts)
let cachedVideos: VideoRequestDTO[] | null = null;
let cacheTime: number = 0;
const CACHE_DURATION = 10 * 60 * 1000; // 10 minutes

export default function UploadCopy() {
  const backendURL = import.meta.env.VITE_BACKEND_URL;
  let [montageSubmitted, isMontageSubmitted] = useState<boolean>(false);
  let [montageFinished, isMontageFinished] = useState<boolean>(false);
  let [prevFiles, setPrevFiles] = useState<VideoRequestDTO[]>([]);
  let [clicks, setClicks] = useState<boolean[]>([]);
  let [sentence, setSentence] = useState<string>("");
  let [title, setTitle] = useState<string>("");
  const [preSignedUrl, setPreSignedUrl] = useState<string>("");
  const [montageName, setMontageName] = useState<string>("");

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

  async function handleSubmit() {
    let videos: VideoRequestDTO[] = [];
    for (let i = 0; i < clicks.length; i++) {
      if (clicks[i]) {
        videos.push(prevFiles[i]);
      }
    }

    let request: MontageRequestDTO = {
      name: title,
      videoRequestDTOs: videos,
      prompt: sentence,
      sentence: `Give all time intervals of ${sentence}, only tell me the intervals, nothing else, and in this format: 00:00-00:06, 01:02-01:09, ... If there are no such time intervals, only return 00:00-00:00`,
    };


   // Find timestamps containing FOOD-related visuals in a STREET environment at NIGHT.


    try {
      isMontageSubmitted(true);
      const res = await axios.post(`http://${backendURL}/montages`, request);
      let data: MontageResponseDTO = res.data;
      setPreSignedUrl(data.preSignedUrl);
      setMontageName(data.name);
      isMontageFinished(true);
      console.log(data);
    } catch (err) {
      console.error("Montage upload failed my bro:", err);
    }

    // console.log(request);
  }

  const handleToggleSelect = (index: number) => {
    const newClicks = [...clicks];
    newClicks[index] = !newClicks[index];
    setClicks(newClicks);

    // Log selected videos
    const selectedVideos = prevFiles.filter((_, i) => newClicks[i]);
    console.log("Selected videos:", selectedVideos);
  };

  useEffect(() => {
    getAllFiles();
    setClicks(new Array(prevFiles.length).fill(false));
  }, []);

  return (
    <div className="w-full bg-black flex flex-col items-center">
      {montageSubmitted && montageFinished ? (
        <>
          <FinishedMontage
            preSignedUrl={preSignedUrl}
            progressMessage={`${montageName} has been created!`}
          />
          <button
            className="hover:cursor-pointer mt-[15px] hover:shadow-[0_0_10px_white] shadow-[0_0_0_white] transition duration-150 ease relative w-[80%] bg-[#925CFE] px-[30px] py-[15px] rounded-[20px] poppins-font text-white text-[20px]"
            onClick={() => {
              isMontageFinished(false);
              isMontageSubmitted(false);
            }}
          >
            Create Another Montage!
          </button>
        </>
      ) : montageSubmitted ? (
        <div className="w-full bg-black">
          <MontageProgressWebSocket />
        </div>
      ) : (
        <div className="w-full bg-black flex flex-col items-center gap-[50px]">
          {/* Desktop Layout */}
          <div className="hidden md:flex text-white bg-[rgb(20,20,20)] pt-[50px] poppins w-[65%] rounded-[20px] flex-col items-center justify-center pb-[50px]">
            <div className="flex flex-col items-center w-full gap-[20px]">
              <div className="flex w-[60%] justify-between items-center gap-[20px]">
                <h1 className="text-[35px] text-[#6600FF] whitespace-nowrap">Topic:</h1>
                <input
                  type="text"
                  onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                    setSentence(e.target.value);
                  }}
                  autoFocus
                  className="flex-1 outline-none border-b-[1px] border-b-[#6600FF] pb-[3px] bg-transparent"
                  style={{ caretColor: "#6600FF" }}
                />
              </div>
              <div className="flex w-[60%] justify-between items-center gap-[20px]">
                <h1 className="text-[35px] text-[#6600FF] whitespace-nowrap">Title:</h1>
                <input
                  type="text"
                  onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                    setTitle(e.target.value);
                  }}
                  autoFocus
                  className="flex-1 outline-none border-b-[1px] border-b-[#6600FF] pb-[3px] bg-transparent"
                  style={{ caretColor: "#6600FF" }}
                />
              </div>
            </div>
            <button
              className={`mt-[25px] px-[30px] py-[15px] rounded-[20px] poppins-font text-white text-[20px] font-medium transition duration-150 ease w-[65%] ${
                sentence === "" ||
                title === "" ||
                !clicks.some((selected) => selected)
                  ? "hover:cursor-not-allowed"
                  : "hover:cursor-pointer hover:shadow-[0_0_10px_white] shadow-[0_0_0_white]"
              }`}
              onClick={handleSubmit}
              disabled={
                sentence === "" ||
                title === "" ||
                !clicks.some((selected) => selected)
              }
              style={{
                backgroundColor:
                  sentence === "" ||
                  title === "" ||
                  !clicks.some((selected) => selected)
                    ? "#222222"
                    : "#925CFE",
              }}
            >
              Generate Montage!
            </button>
          </div>

          {/* Mobile Layout */}
          <div className="flex md:hidden text-white bg-[rgb(20,20,20)] pt-[30px] poppins w-[90%] rounded-[20px] flex-col items-center justify-center pb-[30px] gap-[20px]">
            <div className="flex flex-col w-full items-center gap-[20px] px-[20px]">
              <div className="flex flex-col w-full gap-[8px]">
                <h1 className="text-[24px] text-[#6600FF]">Topic:</h1>
                <input
                  type="text"
                  onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                    setSentence(e.target.value);
                  }}
                  autoFocus
                  className="outline-none border-b-[1px] border-b-[#6600FF] pb-[8px] bg-transparent text-white"
                  style={{ caretColor: "#6600FF" }}
                />
              </div>
              <div className="flex flex-col w-full gap-[8px]">
                <h1 className="text-[24px] text-[#6600FF]">Title:</h1>
                <input
                  type="text"
                  onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                    setTitle(e.target.value);
                  }}
                  autoFocus
                  className="outline-none border-b-[1px] border-b-[#6600FF] pb-[8px] bg-transparent text-white"
                  style={{ caretColor: "#6600FF" }}
                />
              </div>
            </div>
            <button
              className={`w-[80%] px-[30px] py-[15px] rounded-[20px] poppins-font text-white text-[18px] font-medium transition duration-150 ease ${
                sentence === "" ||
                title === "" ||
                !clicks.some((selected) => selected)
                  ? "hover:cursor-not-allowed"
                  : "hover:cursor-pointer hover:shadow-[0_0_10px_white] shadow-[0_0_0_white]"
              }`}
              onClick={handleSubmit}
              disabled={
                sentence === "" ||
                title === "" ||
                !clicks.some((selected) => selected)
              }
              style={{
                backgroundColor:
                  sentence === "" ||
                  title === "" ||
                  !clicks.some((selected) => selected)
                    ? "#222222"
                    : "#925CFE",
              }}
            >
              Generate Montage!
            </button>
          </div>
          <div className="bg-[rgb(20,20,20)] w-[95%] min-h-[800px] overflow-y-scroll rounded-[10px] grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 p-4">
            {prevFiles.map((file, index) => (
              <div
                className="flex items-center justify-center"
                key={`Prev Video ${index}`}
              >
                <SelectableFileDetails
                  name={file.name}
                  size={0}
                  type={""}
                  videoSrc={file.previewUrl}
                  isSelected={clicks[index] || false}
                  onToggle={() => handleToggleSelect(index)}
                />
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

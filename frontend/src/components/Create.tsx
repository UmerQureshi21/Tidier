import { useEffect, useState } from "react";
import type { VideoRequestDTO, MontageResponseDTO } from "../Types";
import SelectableFileDetails from "./VideoDetailsSelectable";
import MontageProgressWebSocket from "./MontageProcessWebSocket";
import FinishedMontage from "./FinishedMontage";
import { getAllVideos } from "../services/videoService";
import { createMontage } from "../services/montageService";

export default function UploadCopy() {
  let [montageSubmitted, isMontageSubmitted] = useState<boolean>(false);
  let [montageFinished, isMontageFinished] = useState<boolean>(false);
  let [prevFiles, setPrevFiles] = useState<VideoRequestDTO[]>([]);
  let [clicks, setClicks] = useState<boolean[]>([]);
  let [sentence, setSentence] = useState<string>("");
  let [title, setTitle] = useState<string>("");
  const [preSignedUrl, setPreSignedUrl] = useState<string>("");
  const [montageName, setMontageName] = useState<string>("");

  async function loadVideos() {
    try {
      const videos = await getAllVideos();
      setPrevFiles(videos);
    } catch (err) {
      console.error("Error loading videos:", err);
    }
  }

  async function handleSubmit() {
    try {
      isMontageSubmitted(true);
      let montageData: MontageResponseDTO = await createMontage(
        prevFiles,
        clicks,
        title,
        sentence
      );
      setPreSignedUrl(montageData.preSignedUrl);
      setMontageName(montageData.name);
      isMontageFinished(true);
    } catch (err) {
      console.error("Montage upload failed my bro:", err);
    }
  }

  const handleToggleSelect = (index: number) => {
    const newClicks = [...clicks];
    newClicks[index] = !newClicks[index];
    setClicks(newClicks);
  };

  useEffect(() => {
    loadVideos();
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
            className="hover:cursor-pointer mb-[100px] mt-[15px] hover:shadow-[0_0_10px_white] shadow-[0_0_0_white] transition duration-150 ease relative w-[80%] bg-[#925CFE] px-[30px] py-[15px] rounded-[20px] poppins-font text-white text-[20px]"
            onClick={() => {
              isMontageFinished(false);
              isMontageSubmitted(false);
            }}
          >
            Create Another Montage!
          </button>
        </>
      ) : montageSubmitted ? (
        <div className="w-full bg-black pb-[50px]">
          <MontageProgressWebSocket />
        </div>
      ) : (
        <div className="w-full bg-black flex flex-col items-center gap-[50px]">
          {/* Desktop Layout */}
          <div className="hidden md:flex text-white bg-[rgb(20,20,20)] pt-[50px] poppins w-[65%] rounded-[20px] flex-col items-center justify-center pb-[50px]">
            <div className="flex flex-col items-center w-full gap-[20px]">
              <div className="flex w-[60%] justify-between items-center gap-[20px]">
                <h1 className="text-[35px] text-[#925cfe] whitespace-nowrap">
                  Topic:
                </h1>
                <input
                  type="text"
                  onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                    setSentence(e.target.value);
                  }}
                  autoFocus
                  className="flex-1 outline-none border-b-[1px] border-b-[#925cfe] pb-[3px] bg-transparent"
                  style={{ caretColor: "#925cfe" }}
                />
              </div>
              <div className="flex w-[60%] justify-between items-center gap-[20px]">
                <h1 className="text-[35px] text-[#925cfe] whitespace-nowrap">
                  Title:
                </h1>
                <input
                  type="text"
                  onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                    setTitle(e.target.value);
                  }}
                  autoFocus
                  className="flex-1 outline-none border-b-[1px] border-b-[#925cfe] pb-[3px] bg-transparent"
                  style={{ caretColor: "#925cfe" }}
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
                <h1 className="text-[24px] text-[#925cfe]">Topic:</h1>
                <input
                  type="text"
                  onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                    setSentence(e.target.value);
                  }}
                  autoFocus
                  className="outline-none border-b-[1px] border-b-[#925cfe] pb-[8px] bg-transparent text-white"
                  style={{ caretColor: "#925cfe" }}
                />
              </div>
              <div className="flex flex-col w-full gap-[8px]">
                <h1 className="text-[24px] text-[#925cfe]">Title:</h1>
                <input
                  type="text"
                  onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                    setTitle(e.target.value);
                  }}
                  autoFocus
                  className="outline-none border-b-[1px] border-b-[#925cfe] pb-[8px] bg-transparent text-white"
                  style={{ caretColor: "#925cfe" }}
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
          {prevFiles.length > 0 ? (
            <div className="bg-[rgb(20,20,20)] w-[95%] mb-[100px] max-h-[700px] overflow-y-scroll rounded-[10px] grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 p-4">
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
          ) : (
            <div className="text-white min-h-screen mt-[100px] poppins-font sm:text-[40px] text-[30px] w-[95%] sm:w-[50%] text-center">
              No videos have been uploaded yet. Go on the{" "}
              <span className="text-[#925cfe]">Video Library</span> page to
              upload videos that you can use later!
            </div>
          )}
        </div>
      )}
    </div>
  );
}

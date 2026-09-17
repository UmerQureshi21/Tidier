import { useEffect, useRef, useState } from "react";
import axios from "axios";
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
  const [errorMessage, setErrorMessage] = useState<string>("");
  const [showingError, setShowingError] = useState<boolean>(false);
  const redirectTimer = useRef<ReturnType<typeof setTimeout> | null>(null);

  // Clear the pending redirect if the page is left
  useEffect(() => {
    return () => {
      if (redirectTimer.current) clearTimeout(redirectTimer.current);
    };
  }, []);

  // Show the error on the progress screen, then go back to the form (the error stays visible there)
  function failMontage(message: string) {
    setErrorMessage(message);
    setShowingError(true);
    redirectTimer.current = setTimeout(() => {
      setShowingError(false);
      isMontageSubmitted(false);
    }, 3000);
  }

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
      setErrorMessage("");
      isMontageSubmitted(true);
      let montageData: MontageResponseDTO = await createMontage(
        prevFiles,
        clicks,
        title,
        sentence
      );
      if (montageData) {
        setPreSignedUrl(montageData.preSignedUrl);
        setMontageName(montageData.name);
        isMontageFinished(true);
      } else {
        failMontage("Something went wrong while creating the montage.");
      }
    } catch (err) {
      console.error("Montage upload failed my bro:", err);
      // The backend sends { error: "..." } when a montage can't be made, e.g. no matching moments
      const message =
        axios.isAxiosError(err) && err.response?.data?.error
          ? err.response.data.error
          : "Something went wrong while creating the montage. Please try again.";
      failMontage(message);
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
            className="hover:cursor-pointer mb-[100px] mt-[15px] hover:shadow-[0_0_10px_white] shadow-[0_0_0_white] transition duration-150 ease relative w-[80%] max-w-[400px] bg-[#925CFE] px-[30px] py-[15px] rounded-[20px] poppins-font text-white text-[20px]"
            onClick={() => {
              isMontageFinished(false);
              isMontageSubmitted(false);
            }}
          >
            Create Another Montage!
          </button>
        </>
      ) : montageSubmitted && showingError ? (
        <div className="w-full bg-black min-h-[700px] py-16 flex flex-col items-center justify-center gap-6 poppins-font text-center">
          <div className="w-12 h-12 rounded-full bg-red-500 flex items-center justify-center text-white text-[28px] font-bold">
            !
          </div>
          <p className="text-red-400 text-[22px] w-[80%] max-w-[500px]">
            {errorMessage}
          </p>
          <p className="text-gray-400 text-sm">
            Taking you back to the create page...
          </p>
        </div>
      ) : montageSubmitted ? (
        <div className="w-full bg-black pb-[50px]">
          <MontageProgressWebSocket />
        </div>
      ) : (
        <div className="w-full bg-black flex flex-col items-center gap-[50px]">
          {errorMessage !== "" && (
            <div className="w-[90%] md:w-[65%] rounded-[20px] border border-red-500 bg-red-500/10 px-[20px] py-[15px] poppins-font text-red-400 text-center">
              {errorMessage}
            </div>
          )}
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
                  value={sentence}
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
                  value={title}
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
                  value={sentence}
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
                  value={title}
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
            <div className="text-white min-h-screen mt-[100px] poppins-font sm:text-[40px] text-[30px] w-[75%] sm:w-[50%] text-center">
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

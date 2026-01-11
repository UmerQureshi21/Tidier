import { useEffect, useState } from "react";
import FileDetails from "./VideoDetails";
import type { VideoRequestDTO } from "../Types";
import {
  clearVideoCache,
  getAllVideos,
  uploadVideos,
} from "../services/videoService";

export default function Upload() {
  const [prevFiles, setPrevFiles] = useState<VideoRequestDTO[]>([]);
  const [exceededUploads, setExceededUploads] = useState<boolean>(false);
  const [rateLimit, setRateLimit] = useState<boolean>(false);

  async function loadVideos() {
    try {
      const videos = await getAllVideos();
      setPrevFiles(videos);
    } catch (err) {
      console.error("Error loading videos:", err);
    }
  }

  useEffect(() => {
    loadVideos();
  }, []);

  async function handleFileInput(e: React.ChangeEvent<HTMLInputElement>) {
    if (e.target.files) {
      try {
        if (e.target.files.length <= 5) {
          setExceededUploads(false);
          const uploadSuccessful: boolean = await uploadVideos(e.target.files);
          if (uploadSuccessful) {
            setRateLimit(false);
            clearVideoCache(); // Clear cache so fresh data is fetched
            await loadVideos();
          } else {
            setRateLimit(true);
          }
        } else {
          setExceededUploads(true);
        }

        // do errror handling for limit exceeded
      } catch (err) {
        console.error("Error uploading videos:", err);
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
          <h1 className="py-[15px] w-[90%] text-white font-normal sm:w-full text-center">
            Before you can use parts of your video for a montage, you must
            upload it first, for proper analyzing capabilities
          </h1>
          {exceededUploads ? (
            <h1 className="py-[15px] text-red-500 font-bold w-full text-center">
              Can only upload up to 5 videos at a time.
            </h1>
          ) : null}
          {rateLimit ? (
            <h1 className="py-[15px] w-[90%] text-red-500 font-bold sm:w-full text-center">
              This is a message from the creator of this app. If you are reading
              this, then that means I have hit my rate limit on minutes of
              videos that I can upload to the service that I'm using. Come back
              when I'm not broke and can afford a better plan. Sorry for the
              inconvenience.
            </h1>
          ) : null}
        </div>
        <div className="text-white poppins w-[90%] flex flex-col items-center justify-center">
          <div className="flex flex-col items-center w-full "></div>
        </div>
      </div>
      <div>
        <div className="w-full   bg-black  flex flex-col items-center  ">
          {prevFiles.length > 0 ? (
            <div className="bg-[rgb(20,20,20)] w-[95%] mb-[100px] max-h-[700px] overflow-y-scroll rounded-[10px] grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 p-4">
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
          ) : (
            <div className=" text-white min-h-screen mt-[100px] poppins-font w-[75%] text-[25px] sm:text-[40px] sm:w-[50%] text-center">
              No videos have been uploaded yet. Click on the{" "}
              <span className="text-[#925cfe]"> Upload</span> button to upload
              videos that you can use later!
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

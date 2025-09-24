import { useEffect, useState } from "react";
import FileDetails from "./FileDetails";
import type { VideoRequestDTO, MontageRequestDTO } from "../Types";
import { useNavigate } from "react-router";

export default function Upload() {
  const navigate = useNavigate();
  let [prevFiles, setPrevFiles] = useState<VideoRequestDTO[]>([]);
  let [clicks, setClicks] = useState<boolean[]>([]);
  let [sentence, setSentence] = useState<string>("");

  async function handleSubmit() {
    /**
     * This will eventually upload a montage to the montages folder. When the montages folder updates,
     * i.e when this function is called, I want to also call the route to get all montages, which will
     * be used for updating the MontageArea, displaying all the montages.
     *
     * I am aware that I can call that route in here, so that everytime the montage folder updates, I
     * have all the montages and can pass it through via context, but I want to use a websocket to
     * have a persistent connection between that component and the montages route.
     */
    let videos: VideoRequestDTO[] = [];
    for (let i = 0; i < clicks.length; i++) {
      if (clicks[i]) {
        videos.push(prevFiles[i]);
      }
    }

    let request: MontageRequestDTO = {
      name: `montage-of-${sentence}.mp4`,
      sentence: `Give all time intervals of ${sentence}, only tell me the intervals, nothing else, and in this format: 00:00-00:06, 01:02:01:09, ...`,
      prompt: sentence,
      videoRequestDTOs: videos,
    };

    navigate("/view-montage");

    await fetch("http://localhost:8080/montages", {
      method: "POST",
      headers: {
        "Content-Type": "application/json", // tell backend to expect JSON
      },
      body: JSON.stringify(request), // stringify the object
    })
      .then((res) => {
        if (!res.ok) {
          throw new Error(`Server responded with status ${res.status}`);
        }
        return res.json();
      })
      .then((data) => {
        console.log(data);
      })
      .catch((err) => {
        console.error("Montage upload failed my bro:", err);
      });

    console.log(videos);
  }

  async function getAllFiles() {
    await fetch("http://localhost:8080/videos")
      .then((res) => {
        if (!res.ok) {
          throw new Error(`Server responded with status ${res.status}`);
        }
        return res.json();
      })
      .then((data) => {
        let fileDetails = [];
        let clicksCopy = [];
        for (let file of data) {
          fileDetails.push(file);
          clicksCopy.push(false);
        }

        setClicks(clicksCopy);
        console.log("Upload successful:", fileDetails); // data is like of VideoDTOs
        setPrevFiles(fileDetails);
      })
      .catch((err) => {
        console.error("Upload failed bro:", err);
      });
  }

  useEffect(() => {
    getAllFiles();
  }, []);

  useEffect(() => {
    console.log(clicks);
  }, [clicks]);

  async function handleFileInput(e: React.ChangeEvent<HTMLInputElement>) {
    let request = new FormData();
    if (e.target.files) {
      for (let [index, file] of Object.entries(e.target.files)) {
        console.log(`File ${index}: ${file}`);
        request.append("files", file);
      }
      fetch("http://localhost:8080/videos", {
        method: "POST",
        body: request,
      })
        .then((res) => {
          if (!res.ok) {
            throw new Error(`Server responded with status ${res.status}`);
          }
          return res.json();
        })
        .then((data) => {
          console.log("Upload successful:", data); // data is like of VideoDTOs
          getAllFiles();
        })
        .catch((err) => {
          console.error("Upload failed:", err);
        });
    }
  }

  return (
    <div className="w-full ">
      <div className="relative w-full h-[100px] bg-black prompt-section flex flex-col items-center">
        <div className="text-white poppins w-[90%] flex flex-col items-center justify-center">
          <h1 className="w-full text-[25px] text-center">
            Give me a montage of{" "}
          </h1>
          <div className="flex justify-around w-full ">
            <input
              type="text"
              onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                setSentence(e.target.value);
              }} // react onChange is same as vanilla input event listener
              autoFocus
              className="w-[70%] mt-[10px] outline-none border-b-[1px] border-b-[#6600FF] pb-[3px]"
              style={{ caretColor: "#6600FF" }}
            />
            <button
              className="w-[20%] hover:cursor-pointer h-[60%] mt-[10px] text-[12px] rounded-[5px] text-center transition-ease duration-[250ms]"
              onClick={handleSubmit}
              disabled={sentence == "" ? true : false}
              style={{
                backgroundColor: sentence == "" ? "#222222" : "#925CFE",
              }}
            >
              Generate!
            </button>
          </div>
        </div>
      </div>
      <div>
        <div className="w-full bg-black flex flex-col items-center h-[250px]">
          <h1 className="w-full text-center text-white poppins-font">
            Your Videos
          </h1>
          <div className="bg-[rgb(20,20,20)] w-[85%] h-[200px] overflow-y-scroll mt-[20px]  flex flex-col items-center rounded-[10px]">
            {prevFiles.map((file, index) => (
              <div
                className="w-full h-[50px] flex items-center justify-center mt-[20px] hover:cursor-pointer"
                key={`Prev Video ${index}`}
                onClick={() => {
                  let clicksCopy = [];
                  for (let click of clicks) {
                    clicksCopy.push(click);
                  }
                  clicksCopy[index] = !clicksCopy[index];
                  setClicks(clicksCopy);
                }}
              >
                <FileDetails
                  thumbnail={""}
                  name={file.name}
                  size={0}
                  type={""}
                  isClicked={clicks[index]}
                />
              </div>
            ))}
          </div>
        </div>
        <div className="w-full pt-[30px] bg-black flex flex-col items-center">
          <button
            onMouseEnter={() => {
              console.log("Button got hovered!");
            }}
            className="hover:cursor-pointer hover:shadow-[0_0_10px_white] shadow-[0_0_0_white] transition duration-150 ease relative w-[60%] bg-[#925CFE] px-[30px] py-[15px] rounded-[20px] poppins-font text-white text-[20px]"
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
          <h1 className=" mt-[20px] text-white w-[80%] text-center">
            Upload your trip clips here, and{" "}
            <span className="font-bold text-[#6600FF]">Tidier</span> will
            display your{" "}
            <span className="font-bold text-[#6600FF]">personalized </span>{" "}
            montage below!
          </h1>
        </div>
      </div>
    </div>
  );
}

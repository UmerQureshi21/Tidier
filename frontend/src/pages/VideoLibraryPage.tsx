import { useEffect, useState } from "react";
import type { VideoRequestDTO, MontageRequestDTO } from "../Types";
import { useNavigate } from "react-router";
import FileDetails from "../components/FileDetails";

export default function VideoLibraryPage() {
  const backendURL = import.meta.env.VITE_BACKEND_URL;
  const navigate = useNavigate();
  let [prevFiles, setPrevFiles] = useState<VideoRequestDTO[]>([]);
  let [clicks, setClicks] = useState<boolean[]>([]);
  let [sentence, setSentence] = useState<string>("");
  let [title, setTitle] = useState<string>("");
  let [videosSelected, setVideosSelected] = useState<boolean>(false);

  //   async function handleSubmit() {
  //     let videos: VideoRequestDTO[] = [];
  //     for (let i = 0; i < clicks.length; i++) {
  //       if (clicks[i]) {
  //         videos.push(prevFiles[i]);
  //         videosSelected = true;
  //       }
  //     }

  //     let request: MontageRequestDTO = {
  //       name: `${title}.mp4`,
  //       sentence: `Give all time intervals of ${sentence}, only tell me the intervals, nothing else, and in this format: 00:00-00:06, 01:02:01:09, ...`,
  //       prompt: sentence,
  //       videoRequestDTOs: videos,
  //     };

  //     navigate("/view-montage");

  //     await fetch(`http://${backendURL}/montages`, {
  //       method: "POST",
  //       headers: {
  //         "Content-Type": "application/json", // tell backend to expect JSON
  //       },
  //       body: JSON.stringify(request), // stringify the object
  //     })
  //       .then((res) => {
  //         if (!res.ok) {
  //           throw new Error(`Server responded with status ${res.status}`);
  //         }
  //         return res.json();
  //       })
  //       .then((data) => {
  //         console.log(data);
  //       })
  //       .catch((err) => {
  //         console.error("Montage upload failed my bro:", err);
  //       });

  //     console.log(videos);
  //   }

  async function getAllFiles() {
    await fetch(`http://${backendURL}/videos`)
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
      fetch(`http://${backendURL}/videos`, {
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
    <div className="min-h-screen bg-black w-full">
      <div className="bg-[rgb(20,20,20)] w-[85%] p-[30px] overflow-y-scroll   flex flex-col items-center rounded-[10px]">
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
  );
}

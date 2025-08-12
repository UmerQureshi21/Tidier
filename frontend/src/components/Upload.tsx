import { useEffect, useState } from "react";
import FileDetails from "./FileDetails";

export default function Upload() {
  let [files, setFiles] = useState<FileList | null>(null);
  let [prevFiles, setPrevFiles] = useState<Object[]>([]);

  async function getAllFiles() {
    fetch("http://localhost:8080/videos")
      .then((res) => {
        if (!res.ok) {
          throw new Error(`Server responded with status ${res.status}`);
        }
        return res.json();
      })
      .then((data) => {
        console.log("Upload successful:", data); // data is like of VideoResponseDTOs
        setPrevFiles(data);
      })
      .catch((err) => {
        console.error("Upload failed bro:", err);
      });
  }

  useEffect(()=>{
    getAllFiles();
  },[]);

  async function handleFileInput(e: React.ChangeEvent<HTMLInputElement>) {
    setFiles(e.target.files);

    let request = new FormData();
    if (e.target.files) {
      for (let [index, file] of Object.entries(e.target.files)) {
        console.log(`File ${index}: ${file}`);
        request.append("files", file);
      }
      fetch("http://localhost:8080/upload", {
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
          console.log("Upload successful:", data); // data is like of VideoResponseDTOs
        })
        .catch((err) => {
          console.error("Upload failed:", err);
        });
    }
  }

  return (
    <div>
      <div className="w-full bg-black flex flex-col items-center h-[250px]">
        <h1 className="w-full text-center text-white poppins-font">
          Your Videos
        </h1>
        <div className="bg-[rgb(20,20,20)] w-[85%] h-[200px] overflow-y-scroll flex flex-col items-center rounded-[10px] mt-[10px]">
          {prevFiles.map((file, index) => (
            <FileDetails
              key={`Prev Video ${index}`}
              thumbnail={""}
              name={file.name}
              size={0}
              type={""}
            />
          ))}
          {files
            ? Object.entries(files).map(([numString, file]) => (
                <FileDetails
                  key={`Video ${numString}`}
                  thumbnail={URL.createObjectURL(file)}
                  name={file.name}
                  size={file.size}
                  type={file.type}
                />
              ))
            : null}
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
        <h1 className="mt-[20px] text-white w-[80%] text-center">
          Upload your trip clips here, and{" "}
          <span className="font-bold text-[#6600FF]">Tidier</span> will combine
          the parts that <span className="font-bold text-[#6600FF]">you</span>{" "}
          want!
        </h1>
      </div>
    </div>
  );
}

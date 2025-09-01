import { useEffect, useState } from "react";

export default function MontageArea() {
  let [montagePaths, setMontagePaths] = useState<string[]>([]);

  useEffect(() => {
    async function getAllMontages() {
      await fetch("http://localhost:8080/montages")
        .then((res) => {
          if (!res.ok) {
            throw new Error(`Server responded with status ${res.status}`);
          }
          return res.json();
        })
        .then((data) => {
          setMontagePaths(data);
          console.log(data);
        })
        .catch((err) => {
          console.error("Upload failed bro:", err);
        });
    }

    getAllMontages();
  }, []);

  return (
    <div className="w-full bg-black flex flex-col items-center pt-[20px] text-white">
      <div className="w-[80%] h-[400px] bg-[rgb(20,20,20)] flex items-center justify-center rounded-[20px]">
        <video
          src={montagePaths[8]}
          className="w-[80%] h-[350px] rounded-[40px]"
          controls
          autoPlay
        ></video>
      </div>
    </div>
  );
}

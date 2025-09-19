import { useState } from "react";

export default function MontageArea() {
  let [montagePaths, setMontagePaths] = useState<string[]>([]);

  return (
    <div className="w-full bg-black flex flex-col items-center text-white">
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

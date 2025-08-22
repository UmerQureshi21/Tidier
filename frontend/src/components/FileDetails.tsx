import { useState } from "react";

interface Props {
  name: string;
  type: string;
  size: number;
  thumbnail: string;
  isClicked: boolean;
}

export default function FileDetails({
  name,
  type,
  size,
  thumbnail,
  isClicked,
}: Props) {
  return (
    <div className="relative rounded-[10px] transition duration-300 w-[90%] bg-[rgb(35,35,35)] h-full flex justify-between items-center p-[17px]">
      <div className="w-[85%]">
        <h1
          className="text-white text-[10px] w-full text-left truncate poppins-font font-thin"
          title={name}
        >
          <span className="font-bold">Name:</span> {name}
        </h1>
        {/* <h1 className="text-white text-[10px]">Type: {type}</h1>
        <h1 className="text-white text-[10px]">Size: {size} KB</h1> */}
      </div>

      {/* <video
        src={thumbnail}
        className="w-[25%] object-cover"
        controls={false}
        muted
      /> */}
      {
        //thumbbails wpould only appear for vieos that the user uploaded in the current session
        //so vidoes loaded from DB wont have thumbnail unless I actually store the video in some
        //S3 thing, but the videos are stores in twelvelabs under the Tidier index so maybe I can
        //do something to do that
      }
      <input
        readOnly
        type="checkbox"
        checked={isClicked}
        style={{ accentColor: "#925CFE" }} // native box + white checkmark
        className="w-[15px] h-[15px] hover:cursor-pointer"
      />
    </div>
  );
}

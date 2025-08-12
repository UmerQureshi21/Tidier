interface Props {
  name: string;
  type: string;
  size: number;
  thumbnail: string;
}

export default function FileDetails({ name, type, size, thumbnail }: Props) {
  return (
    <div className="relative transition duration-300 w-[90%] bg-[rgb(25,25,25)] flex justify-center items-center p-[17px] mt-[20px]">
      <div className="w-[65%]">
        <h1 className="text-white text-[10px]">Name: {name}</h1>
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

      <button className="absolute bg-white w-[15px] h-full  top-0 right-0 hover:cursor-pointer"></button>
    </div>
  );
}

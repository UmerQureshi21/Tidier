import { useEffect, useState } from "react";
import Carousel from "./Carousel";

interface props {
  height: number;
  isHome: boolean;
}

export default function Header({ height, isHome }: props) {
  const MEDIUM_WIDTH_SIZE = 768;
  const [windowWidth, setWindowWidth] = useState<number>(window.innerWidth);

  useEffect(() => {
    const handleResize = () => {
      setWindowWidth(window.innerWidth);
    };

    window.addEventListener("resize", handleResize);

    return () => {
      window.removeEventListener("resize", handleResize);
    };
  }, []);

  return (
    <div
      className="relative w-full flex flex-col justify-center"
      style={{
        height: windowWidth >= MEDIUM_WIDTH_SIZE ? height : 320,
      }}
    >
      {windowWidth >= MEDIUM_WIDTH_SIZE ? (
        <div
          className="w-full"
          style={{
            height: height,
          }}
        >
          <Carousel
            images={[
              "/times-square.jpg",
              "/paris-evening.png",
              "/london-bridge.jpeg",
              "/time-square-2.jpeg",
              "/paris-night.jpeg",
              "/river.jpeg",
              "/building.jpeg",
              "/london-bridge.jpeg",
              "/time-square-2.jpeg",
            ]}
            flowSpeed={50}
            carouselHeight={height}
            height={height}
            width={`${height}px`}
          />
        </div>
      ) : (
        <div className="absolute top-0 right-0 w-[200%] h-full bg-cover bg-center flex">
          <img
            src="/times-square.jpg"
            alt=""
            className="w-[50%] h-full object-cover"
          />
          <img
            src="/paris-evening.png"
            alt=""
            className="w-[50%] h-full object-cover"
          />
        </div>
      )}

      {windowWidth >= MEDIUM_WIDTH_SIZE ? (
        !isHome ?(<img
          src="/trip-slice-logo-gradient.png"
          className="absolute z-[50] top-[25px] w-[20%] left-[40%] lg:w-[15%] lg:left-[42.5%]"
          alt=""
        />) : (
          <img
          src="/trip-slice-logo-gradient.png"
          className="absolute z-[50] top-[55px] w-[50%] left-[25%] lg:w-[30%] lg:left-[35%]"
          alt=""
        />
        )
      ) : (
        <h1
          className="absolute z-[50] poppins-font top-[40px] left-[20px] md:top-[80px] 
                     md:left-1/2 md:-translate-x-1/2 text-[40px] md:text-[120px] md:font-bold 
                     text-white md:bg-gradient-to-r md:from-purple-400 md:via-white 
                     md:via-purple-600 md:to-blue-500 md:bg-clip-text md:text-transparent 
                     md:animate-[gradient_5s_ease-in-out_infinite] md:bg-[length:300%_auto]"
        >
          TripSlice
        </h1>
      )}
      {isHome || windowWidth < MEDIUM_WIDTH_SIZE ? (
        <h1
          className="absolute w-[85%] font-thin top-[100px] lg:top-[320px] md:top-[370px] left-[20px] 
               z-[50] text-white text-[25px] poppins-font md:left-1/2 md:-translate-x-1/2 md:text-center md:w-auto"
        >
          Your AI-powered
          <span className="font-normal ml-[5px] ">Vacation Montage </span>Maker
        </h1>
      ) : null}

      <div className="absolute top-0 left-0 right-0 bottom-0 bg-gradient-to-b from-[rgb(5,5,5)] to-transparent"></div>
    </div>
  );
}

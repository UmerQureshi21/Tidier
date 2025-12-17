import { useEffect, useState } from "react";
import Carousel from "../components/Carousel";
import LogIn from "../components/LogIn";

export default function LogInPage() {
  const IMAGE_HEIGHT = 350;
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
    <div className="relative min-h-screen bg-gray-950 flex poppins-font">
      {windowWidth >= 1024 ? (
        <>
          <div className="absolute w-[50%] top-[90px] left-[100px] z-20">
            <LogIn />
          </div>
          <div
            className={`w-full  z-10 h-[${IMAGE_HEIGHT}]  absolute bottom-[300px]`}
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
              ]}
              flowSpeed={50}
              carouselHeight={IMAGE_HEIGHT}
              height={IMAGE_HEIGHT}
              width={`${IMAGE_HEIGHT}px`}
            />
            {/* Black fade overlay */}
            <div className="absolute inset-0 bg-gradient-to-b from-black/70 via-transparent to-black/70 pointer-events-none"></div>
          </div>
          <h1 className="text-white text-[50px] font-thin  absolute bottom-[100px] right-[0] w-[40%]">
            Find the moments that <span className="font-bold">matter.</span>
          </h1>
        </>
      ) : (
        <div className="w-full flex items-center justify-center">
          <div className="w-[80%]">
            <LogIn />
          </div>
        </div>
      )}
    </div>
  );
}

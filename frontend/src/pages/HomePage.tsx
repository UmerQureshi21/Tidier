import { useEffect, useState, useRef } from "react";
import Header from "../components/Header";
import { NavLink } from "react-router";

export default function HomePage() {
  const [windowWidth, setWindowWidth] = useState<number>(window.innerWidth);

  useEffect(() => {
    const handleResize = () => {
      setWindowWidth(window.innerWidth);
    };

    // MIGHT AFFECT OTHER PAGES' BACKGROUNDS
    document.body.style.backgroundColor = "#000000";

    window.addEventListener("resize", handleResize);

    return () => {
      window.removeEventListener("resize", handleResize);
    };
  }, []);

  return (
    <div className="bg-black">
      <div>
        <Header height={600} />
      </div>

      <div className="w-full relative poppins-font">
        <div className="relative w-full h-[100px] bg-black prompt-section flex flex-col items-center"></div>

        {windowWidth >= 768 ? (
          <NavLink
            to={"/account"}
            className={`absolute w-[20%] left-[40%] top-[-300px]  bg-[#000000] text-white text-[30px] rounded-[100px] h-[100px]
                      overflow-hidden group flex items-center justify-center
                     transition-all duration-1000 ease-out delay-500
                     hover:scale-110 hover:shadow-2xl
                     scale-100 opacity-100`}
          >
            <span
              className="absolute inset-0 bg-[#925CFE] translate-x-[-100%] 
                   group-hover:translate-x-0 transition-transform duration-500"
            ></span>
            <span className="relative z-10 group-hover:text-[#000000] text-[#925CFE] transition-colors duration-300">
              Start Creating!
            </span>
          </NavLink>
        ) : null}

        <Example />
        <div className="pt-[200px] bg-black">
          <Procedure />
        </div>
      </div>
    </div>
  );
}

function Example() {
  const [isVisible, setIsVisible] = useState(false);
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setIsVisible(true);
        }
      },
      { threshold: 0.2 }
    );

    if (ref.current) {
      observer.observe(ref.current);
    }

    return () => {
      if (ref.current) {
        observer.unobserve(ref.current);
      }
    };
  }, []);

  return (
    <div
      ref={ref}
      className={`w-full flex transition-all duration-1000 ${
        isVisible ? "opacity-100 translate-y-0" : "opacity-0 translate-y-20"
      }`}
    >
      <div className="w-[50%] bg-black font-thin flex justify-center">
        <h1 className="text-white text-[80px] w-[80%]">
          From <span className="font-bold text-[#925CFE]">cluttered</span> hours
          of footage to minutes of{" "}
          <span className="text-[#925CFE] font-bold">magic</span>
        </h1>
      </div>
      <div className="w-[50%] bg-black flex justify-center">
        <video
          className="w-[45%] border-[1px] border-white rounded-[20px]"
          src="/tidier-example.mov"
          autoPlay
          muted
          playsInline
        ></video>
      </div>
    </div>
  );
}

function Procedure() {
  const [isVisible, setIsVisible] = useState(false);
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setIsVisible(true);
        }
      },
      { threshold: 0.2 }
    );

    if (ref.current) {
      observer.observe(ref.current);
    }

    return () => {
      if (ref.current) {
        observer.unobserve(ref.current);
      }
    };
  }, []);

  return (
    <div
      ref={ref}
      className={`h-[600px] w-full mx-auto py-12 px-6 bg-black transition-all duration-1000 ${
        isVisible ? "opacity-100 translate-y-0" : "opacity-0 translate-y-20"
      }`}
    >
      <h2 className="text-3xl md:text-[80px] font-bold text-center mb-12 md:mb-20 text-[#925CFE]">
        How It Works
      </h2>

      <div className="flex flex-col sm:flex-row justify-around">
        {/* Step 1 */}
        <div className="flex flex-col w-[25%] md:gap-12 items-center">
          <div className="flex-shrink-0 w-16 h-16 md:w-24 md:h-24 rounded-full bg-[#925CFE] flex items-center justify-center text-white font-bold text-2xl md:text-[50px]">
            1
          </div>
          <div className="flex-1">
            <h3 className="text-xl text-center md:text-3xl font-thin text-white mb-2">
              Select all the videos you want to search from
            </h3>
          </div>
        </div>

        {/* Step 2 */}
        <div className="flex flex-col w-[25%] md:gap-12 items-center">
          <div className="flex-shrink-0 w-16 h-16 md:w-24 md:h-24 rounded-full bg-[#925CFE] flex items-center justify-center text-white font-bold text-2xl md:text-[50px]">
            2
          </div>
          <div className="flex-1">
            <h3 className="text-xl text-center md:text-3xl font-thin text-white mb-2">
              Describe what topic of your montage
            </h3>
          </div>
        </div>

        {/* Step 3 */}
        <div className="flex flex-col w-[25%] md:gap-12 items-center">
          <div className="flex-shrink-0 w-16 h-16 md:w-24 md:h-24 rounded-full bg-[#925CFE] flex items-center justify-center text-white font-bold text-2xl md:text-[50px]">
            3
          </div>
          <div className="flex-1">
            <h3 className="text-xl text-center md:text-3xl font-thin text-white mb-2">
              Generate your montage by the click of a button!
            </h3>
          </div>
        </div>
      </div>
    </div>
  );
}

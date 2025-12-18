import { useEffect, useState } from "react";
import { NavLink } from "react-router";
import PopUp from "./PopUp";
import NavBarLink from "./NavBarLink";

export default function Navbar() {
  const [clientWidth, setClientWidth] = useState<number>(window.innerWidth);

  useEffect(() => {
    function handleResize() {
      setClientWidth(window.innerWidth);
    }

    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  return (
    <>
      <div className="poppins-font fixed w-full border-b-[1px] border-b-black h-[75px] z-[500] bg-[#000] flex items-center justify-between ">
        {/* Logo Section */}
        <div
          className={`flex items-center justify-around group
                        transition-all duration-700 delay-200 ml-[20px]
                        `}
        >
          <NavLink className="relative" to={"/"}>
            <img
              src="/montage-icon-white.png"
              alt=""
              className="w-[45px] h-[45px]  rounded-lg transition-all duration-300 
                         group-hover:scale-110 group-hover:rotate-3 group-hover:shadow-lg"
            />
            {/* Glow effect on hover */}
            <div
              className="absolute inset-0 rounded-lg bg-[#925cfe]/0 group-hover:bg-[#925cfe]/20 
                            transition-all duration-300 blur-sm"
            ></div>
          </NavLink>
          {/* <h2 className="text-[30px] text-[#925cfe] font-thin relative overflow-hidden">
            Tidier
            <span
              className="absolute bottom-0 left-0 w-0 h-[2px] bg-[#fff] 
                           group-hover:w-full transition-all duration-500 ease-out"
            ></span>
          </h2> */}
        </div>
        {clientWidth < 900 ? (
          <PopUp />
        ) : (
          <div className="w-[55%]  relative right-[20px] flex items-center justify-between">
            <NavBarLink
              page="/"
              text="Dashboard"
              widthPercent={20}
              fontSize={15}
              alignment="center"
              colour="#925cfe"
            />
            <NavBarLink
              page="/"
              text="Video Library"
              widthPercent={20}
              fontSize={15}
              alignment="center"
              colour="#925cfe"
            />
            <NavBarLink
              page="/our-team"
              text="Create Montage"
              widthPercent={20}
              fontSize={15}
              alignment="center"
              colour="#925cfe"
            />
            <NavBarLink
              page="/past-events"
              text="Montage Library"
              widthPercent={20}
              fontSize={15}
              alignment="center"
              colour="#925cfe"
            />
            <NavBarLink
              page="/past-events"
              text="Account"
              widthPercent={20}
              fontSize={15}
              alignment="center"
              colour="#925cfe"
            />
          </div>
        )}
      </div>
    </>
  );
}

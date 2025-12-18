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

  const linkWidth = 15;
  const linkFont = 12;
  const allignment = "center";
  const colour = "#925cfe";
  const pages = [
    {
      page: "/dashboard",
      text: "Dashboard",
    },
    {
      page: "/videos",
      text: "Video Library",
    },
    {
      page: "/create",
      text: "Create Montage",
    },
    {
      page: "/montages",
      text: "Montage Library",
    },
    {
      page: "/account",
      text: "Account",
    },
  ];

  return (
    <>
      <div className="poppins-font fixed w-full border-b-[1px] border-b-black h-[75px] z-[500] bg-[#000] flex items-center justify-between ">
        {/* Logo Section */}
        <div
          className={`flex items-center justify-around group
                        transition-all duration-700 delay-200 ml-[20px]
                        `}
        >
          <NavLink className="relative" to="/app/dashboard">
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
        </div>
        {clientWidth < 900 ? (
          <PopUp />
        ) : (
          <div className="w-[55%]  relative right-[20px] flex items-center justify-between">
            {pages.map((page, index) => (
              <NavBarLink
                key={`nav bar link ${index}`}
                page={`/app${page.page}`}
                text={page.text}
                widthPercent={linkWidth}
                fontSize={linkFont}
                alignment={allignment}
                colour={colour}
              />
            ))}
          </div>
        )}
      </div>
    </>
  );
}

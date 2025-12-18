import { NavLink } from "react-router";

interface Props {
  text: string;
  widthPercent: number;
  fontSize: number;
  marginTop?: number;
  alignment?: string;
  colour: string;
  page: string;
}

export default function NavBarLink({
  text,
  widthPercent,
  fontSize,
  marginTop = 0,
  alignment = "left",
  colour,
  page,
}: Props) {
  return (
    <NavLink
      to={page}
      className={({ isActive }) =>
        `relative h-fit inline-block group transition-all duration-300 ${
          isActive ? "active" : ""
        }`
      }
      style={{
        width: `${widthPercent}%`,
        fontSize: `${fontSize}px`,
        marginTop: `${marginTop}px`,
      }}
    >
      {({ isActive }) => (
        <>
          <p
            className={`text-${alignment} font-medium transition-all duration-300 group-hover:tracking-wide`}
            style={{ color: colour }}
          >
            {text}
          </p>

          {/* Animated underline */}
          <span
            className={`absolute bottom-0 left-1/2 -translate-x-1/2 h-[2px] transition-all duration-300 ease-out ${
              isActive ? "w-full" : "w-0 group-hover:w-full"
            }`}
            style={{ backgroundColor: colour }}
          ></span>

          {/* Active indicator dot
          <span
            className={`absolute -bottom-2 left-1/2 -translate-x-1/2 w-1 h-1 rounded-full transition-opacity duration-300 ${
              isActive ? "opacity-100" : "opacity-0 group-hover:opacity-100"
            }`}
            style={{ backgroundColor: colour }}
          ></span> */}
        </>
      )}
    </NavLink>
  );
}

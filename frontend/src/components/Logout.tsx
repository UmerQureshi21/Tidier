import { useNavigate } from "react-router";
import { logout } from "../services/userService";

interface Props {
  fontSize?: number;
  colour?: string;
  onLoggedOut?: () => void;
}

// Styled to match NavBarLink so it sits next to the nav links
export default function Logout({
  fontSize = 12,
  colour = "#925cfe",
  onLoggedOut,
}: Props) {
  const navigate = useNavigate();

  return (
    <button
      onClick={async () => {
        await logout();
        onLoggedOut?.();
        navigate("/");
      }}
      className="relative h-fit inline-block group transition-all duration-300 hover:cursor-pointer text-left"
      style={{ fontSize: `${fontSize}px` }}
    >
      <p
        className="font-medium transition-all duration-300 group-hover:tracking-wide"
        style={{ color: colour }}
      >
        Log Out
      </p>
      <span
        className="absolute bottom-0 left-1/2 -translate-x-1/2 h-[2px] w-0 group-hover:w-full transition-all duration-300 ease-out"
        style={{ backgroundColor: colour }}
      ></span>
    </button>
  );
}

import { useNavigate } from "react-router";
import { logout } from "../services/userService";

export default function Logout() {
  const navigate = useNavigate();

  return (
    <button
      onClick={async () => {
        await logout();
        navigate("/");
      }}
      className="bg-white"
    >
      log out
    </button>
  );
}

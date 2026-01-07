import { useNavigate } from "react-router";
import { tokenManager } from "../services/tokenManager";

export default function Logout() {
  const navigate = useNavigate();

  return (
    <button
      onClick={() => {
        tokenManager.clearToken();
        navigate("/");
      }}
      className="bg-white"
    >
      log out
    </button>
  );
}

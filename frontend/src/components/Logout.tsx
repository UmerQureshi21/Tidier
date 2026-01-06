import { useNavigate } from "react-router";

export default function Logout() {
  return (
    <div>
      <InsaneLogoutButton />
    </div>
  );
}

function InsaneLogoutButton() {
  const navigate = useNavigate();

  function handleLogout() {
    //stopTokenRefreshInterval();
    localStorage.removeItem("accessToken");
    navigate("/login"); // Add this line - redirect to your login page
  }

  return (
    <div className=" bg-black flex items-center justify-center overflow-hidden">
      <button className="bg-blue-500" onClick={handleLogout}>
        <span>LOG OUT IF YOU ARE A BABY</span>
      </button>
    </div>
  );
}

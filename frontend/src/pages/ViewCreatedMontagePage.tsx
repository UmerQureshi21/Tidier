import { useEffect } from "react";
import { useLocation, useNavigate } from "react-router";
import MontageProgressWebSocket from "../components/MontageProcessWebSocket";

export default function ViewCreatedMontage() {
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    if (!location.state?.montageData) {
      navigate("/app/create");
      return;
    }
  }, [location, navigate]);

  if (!location.state?.montageData) {
    return null;
  }

  return (
    <div>
      {/* <MontageProgressWebSocket /> */}
      HELLO THIS PAGE SHOULDNT BE HERE
    </div>
  );
}

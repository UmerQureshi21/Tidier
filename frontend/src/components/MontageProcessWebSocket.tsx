import { useState, useRef, useEffect } from "react";
import * as StompJs from "@stomp/stompjs";
import { useNavigate } from "react-router";



export default function MontageProgressWebSocket() {
  //const backendURL = import.meta.env.VITE_BACKEND_URL;
  let navigate = useNavigate();
  const dummyMontagePath = "/montages/null.mp4";
  const [progressMessage, setProgressMessage] = useState<string>(
    "Starting Montage Creation..."
  );
  const clientRef = useRef<StompJs.Client | null>(null);
  const [montagePath, setMontagePath] = useState<string>(dummyMontagePath);

  // for forcing re-renders of <video>
  const [reloadKey, setReloadKey] = useState(0);
  const [isPlaying, setIsPlaying] = useState(false);

  // WebSocket setup
  useEffect(() => {
    const client = new StompJs.Client({
      brokerURL: `ws://localhost:8080/gs-guide-websocket`,
      reconnectDelay: 2000,
      debug: (str) => console.log(str),
      onConnect: (frame) => {
        console.log("We have connected to this frame: " + frame);

        client.subscribe("/topic/montage-progress", (message) => {
          const body = JSON.parse(message.body);
          setProgressMessage(body.content);

          if (body.montagePath) {
            clientRef.current?.deactivate();
            setMontagePath(body.montagePath);
            console.log("Montage path: " +body.montagePath)
            setIsPlaying(false); // reset for retry loop
          }
        });
      },
      onWebSocketError: (error) => {
        console.error("Error with websocket", error);
      },
      onStompError: (frame) => {
        console.error("Broker error: " + frame.headers["message"]);
        console.error("Details: " + frame.body);
      },
    });

    clientRef.current = client;
    clientRef.current.activate();
  }, []);

  // Retry until video starts playing
  useEffect(() => {
    if (!isPlaying && montagePath !== "/montages/null.mp4") {
      const interval = setInterval(() => {
        console.log("Retrying video load...");
        setReloadKey((k) => k + 1); // force re-render of <video> in order to reload so that the montage gets played
      }, 2000);

      return () => clearInterval(interval);
    }
  }, [isPlaying, montagePath]);

  return (
    <>
      <div className="relative w-full bg-black h-[60px] py-[100px] flex items-center justify-center prompt-section">
        <h1 className="w-[70%] text-[white] text-[20px] text-center overflow-x-scroll whitespace-nowrap">
          {progressMessage}
        </h1>
        {montagePath != dummyMontagePath ? (
          <div></div>
        ) : (
          <svg
            className="w-[10%]"
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 100 100"
            preserveAspectRatio="xMidYMid"
            width="50"
            height="50"
            style={{ shapeRendering: "auto", display: "block" }}
          >
            <circle
              strokeDasharray="164.93361431346415 56.97787143782138"
              r="35"
              strokeWidth="10"
              stroke="#6600ff"
              fill="none"
              cy="50"
              cx="50"
            >
              <animateTransform
                attributeName="transform"
                type="rotate"
                keyTimes="0;1"
                values="0 50 50;360 50 50"
                dur="1s"
                repeatCount="indefinite"
              />
            </circle>
          </svg>
        )}
      </div>
      <div className="w-full bg-black pb-[100px] flex flex-col items-center text-white">
        <div className="w-[80%] h-[300px] bg-[rgb(20,20,20)] flex items-center justify-center rounded-[20px]">
          <video
            key={`${montagePath}-${reloadKey}`} // new key = re-render
            src={montagePath}
            className="w-[80%] h-[250px] rounded-[40px]"
            controls
            autoPlay
            onPlay={() => {
              console.log("Video started playing");
              setIsPlaying(true);
            }}
            onError={() => {
              console.log("Video failed to load");
            }}
          />
        </div>
        <button
          onClick={() => {
            navigate("/app/create");
          }}
          className="hover:cursor-pointer mt-[15px] hover:shadow-[0_0_10px_white] shadow-[0_0_0_white] transition duration-150 ease relative w-[80%] bg-[#925CFE] px-[30px] py-[15px] rounded-[20px] poppins-font text-white text-[20px]"
        >
          Create New Montage
        </button>
      </div>
    </>
  );
}

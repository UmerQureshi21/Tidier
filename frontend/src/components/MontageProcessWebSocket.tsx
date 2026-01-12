import { useState, useRef, useEffect } from "react";
import * as StompJs from "@stomp/stompjs";

export default function MontageProgressWebSocket() {
  const full = import.meta.env.VITE_BACKEND_URL;
  const host = full.replace(/^https?:\/\//, "");
  const [progressMessage, setProgressMessage] = useState<string>(
    "Starting Montage Creation..."
  );
  const clientRef = useRef<StompJs.Client | null>(null);

  // WebSocket setup
  useEffect(() => {
    const client = new StompJs.Client({
      brokerURL: `ws://${host}/gs-guide-websocket`,
      reconnectDelay: 2000,
      debug: (str) => console.log(str),
      onConnect: (frame) => {
        console.log("We have connected to this frame: " + frame);

        client.subscribe("/topic/montage-progress", (message) => {
          const body = JSON.parse(message.body);
          setProgressMessage(body.content);

          if (body.montagePath) {
            clientRef.current?.deactivate();
            console.log("Montage path: " + body.montagePath);
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

  return (
    <>
      <div className="relative w-full bg-black h-[700px] py-[100px] flex items-center justify-center ">
        <h1 className="w-[70%] text-[white] text-[20px] text-center ">
          {progressMessage}
        </h1>
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
      </div>
    </>
  );
}

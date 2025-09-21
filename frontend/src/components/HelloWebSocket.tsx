import React, { useState, useRef, useEffect } from "react";
import * as StompJs from "@stomp/stompjs";

export default function MontageProgressWebSocket() {
  const [connected, setConnected] = useState(false);
  const [progressMessages, setProgressMessages] = useState<string[]>([]);
  const clientRef = useRef<StompJs.Client | null>(null);

  useEffect(() => {
    const client = new StompJs.Client({
      brokerURL: "ws://localhost:8080/gs-guide-websocket",
      reconnectDelay: 2000,
      debug: (str) => console.log(str),
      onConnect: (frame) => {
        console.log("Connected: " + frame);
        setConnected(true);

        // Subscribe to the montage progress topic
        client.subscribe("/topic/montage-progress", (message) => {
          const body = JSON.parse(message.body);
          if (body?.content) {
            setProgressMessages((prev) => [...prev, body.content]);
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

    return () => {
      client.deactivate();
      setConnected(false);
    };
  }, []);

  const handleConnect = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
    if (clientRef.current && !connected) {
      clientRef.current.activate();
    }
  };

  const handleDisconnect = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
    if (clientRef.current && connected) {
      clientRef.current.deactivate();
      setConnected(false);
    }
  };

  return (
    <div style={{ padding: "20px", maxWidth: "800px", margin: "0 auto" }}>
      <section style={{ marginBottom: "20px" }}>
        <form style={{ display: "flex", gap: "10px", alignItems: "center" }}>
          <label>WebSocket connection:</label>
          <button onClick={handleConnect} disabled={connected}>
            Connect
          </button>
          <button onClick={handleDisconnect} disabled={!connected}>
            Disconnect
          </button>
        </form>
      </section>

      <section>
        <h3>Montage Progress</h3>
        <ul>
          {progressMessages.map((msg, index) => (
            <li key={index}>{msg}</li>
          ))}
        </ul>
      </section>
    </div>
  );
}

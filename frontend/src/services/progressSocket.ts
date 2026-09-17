import * as StompJs from "@stomp/stompjs";
import axiosInstance from "./refreshTokenAxios";
import { tokenManager } from "./tokenManager";

export type ProgressMessage = {
  content: string;
  montagePath: string | null;
};

const backendURL: string = import.meta.env.VITE_BACKEND_URL;
// http -> ws and https -> wss, browsers block ws:// from https pages
const brokerURL = `${backendURL.replace(/^http/, "ws")}/gs-guide-websocket`;

// Connects with the user's access token and subscribes to their private queue, e.g. "montage-progress"
export function createProgressClient(
  queue: string,
  onMessage: (message: ProgressMessage) => void
): StompJs.Client {
  const client = new StompJs.Client({
    brokerURL,
    reconnectDelay: 2000,
    debug: (str) => console.log(str),
    beforeConnect: async () => {
      // Any authenticated request renews the access token first if it has expired
      try {
        await axiosInstance.get("/user");
      } catch (err) {
        console.error("Could not refresh token before connecting:", err);
      }
      client.connectHeaders = { Authorization: tokenManager.getToken() ?? "" };
    },
    onConnect: () => {
      client.subscribe(`/user/queue/${queue}`, (message) => {
        onMessage(JSON.parse(message.body));
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
  return client;
}

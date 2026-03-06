import { useState, useRef, useEffect } from "react";
import * as StompJs from "@stomp/stompjs";

interface VideoProgress {
  uploading: "pending" | "active" | "completed";
  indexing: "pending" | "active" | "completed";
  saved: "pending" | "active" | "completed";
}

const STAGES = ["uploading", "indexing", "saved"] as const;

const STAGE_LABELS: Record<string, string> = {
  uploading: "Uploading to API",
  indexing: "Indexing video",
  saved: "Saved",
};

function StepIcon({ status }: { status: "pending" | "active" | "completed" }) {
  if (status === "completed") {
    return (
      <div className="w-6 h-6 rounded-full bg-green-500 flex items-center justify-center">
        <svg
          className="w-3 h-3 text-white"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          strokeWidth={3}
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M5 13l4 4L19 7"
          />
        </svg>
      </div>
    );
  }
  if (status === "active") {
    return (
      <div className="w-6 h-6 rounded-full border-2 border-[#6600ff] flex items-center justify-center">
        <svg
          className="w-4 h-4 animate-spin text-[#6600ff]"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
        >
          <circle
            className="opacity-25"
            cx="12"
            cy="12"
            r="10"
            stroke="currentColor"
            strokeWidth="4"
          />
          <path
            className="opacity-75"
            fill="currentColor"
            d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"
          />
        </svg>
      </div>
    );
  }
  return <div className="w-6 h-6 rounded-full border-2 border-gray-600" />;
}

export default function UploadProgressWebSocket({
  fileNames,
}: {
  fileNames: string[];
}) {
  const full = import.meta.env.VITE_BACKEND_URL;
  const host = full.replace(/^https?:\/\//, "");
  const [progress, setProgress] = useState<Record<string, VideoProgress>>(() => {
    const initial: Record<string, VideoProgress> = {};
    for (const name of fileNames) {
      initial[name] = {
        uploading: "active",
        indexing: "pending",
        saved: "pending",
      };
    }
    return initial;
  });
  const clientRef = useRef<StompJs.Client | null>(null);

  useEffect(() => {
    const client = new StompJs.Client({
      brokerURL: `ws://${host}/gs-guide-websocket`,
      reconnectDelay: 2000,
      debug: (str) => console.log(str),
      onConnect: () => {
        client.subscribe("/topic/upload-progress", (message) => {
          const body = JSON.parse(message.body);
          const videoName: string = body.content;
          const stage: string = body.montagePath;

          setProgress((prev) => {
            const video = prev[videoName];
            if (!video) return prev;

            const updated: VideoProgress = { ...video };
            for (const s of STAGES) {
              if (s === stage) {
                updated[s] = s === "saved" ? "completed" : "active";
              } else if (STAGES.indexOf(s) < STAGES.indexOf(stage as typeof STAGES[number])) {
                updated[s] = "completed";
              }
            }
            // If saved, mark all completed
            if (stage === "saved") {
              updated.uploading = "completed";
              updated.indexing = "completed";
              updated.saved = "completed";
            }
            return { ...prev, [videoName]: updated };
          });
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

    return () => {
      clientRef.current?.deactivate();
    };
  }, []);

  const totalSteps = fileNames.length * 3;
  const completedSteps = Object.values(progress).reduce((sum, vp) => {
    return sum + STAGES.filter((s) => vp[s] === "completed").length;
  }, 0);
  const progressPercent = (completedSteps / totalSteps) * 100;

  return (
    <div className="w-full bg-black min-h-[500px] py-12 flex flex-col items-center gap-8">
      {/* Overall progress bar */}
      <div className="w-[80%] max-w-[600px]">
        <div className="flex justify-between mb-2">
          <span className="text-sm text-gray-400">Upload Progress</span>
          <span className="text-sm text-gray-400">
            {Math.round(progressPercent)}%
          </span>
        </div>
        <div className="w-full h-3 bg-gray-800 rounded-full overflow-hidden">
          <div
            className="h-full rounded-full transition-all duration-700 ease-out"
            style={{
              width: `${progressPercent}%`,
              background:
                progressPercent === 100
                  ? "#22c55e"
                  : "linear-gradient(90deg, #6600ff, #9945ff)",
            }}
          />
        </div>
      </div>

      {/* Per-video progress */}
      <div className="w-[80%] max-w-[600px] flex flex-col gap-6">
        {fileNames.map((name) => {
          const vp = progress[name];
          const allDone = STAGES.every((s) => vp[s] === "completed");
          return (
            <div
              key={name}
              className={`border rounded-lg p-4 transition-colors duration-500 ${
                allDone
                  ? "border-green-500/30 bg-green-500/5"
                  : "border-gray-700 bg-gray-900/50"
              }`}
            >
              <h3 className="text-white text-sm font-medium mb-3 truncate">
                {name}
              </h3>
              <div className="flex items-center gap-2">
                {STAGES.map((stage, i) => (
                  <div key={stage} className="flex items-center gap-2 flex-1">
                    <StepIcon status={vp[stage]} />
                    <span
                      className={`text-xs whitespace-nowrap ${
                        vp[stage] === "completed"
                          ? "text-green-400"
                          : vp[stage] === "active"
                          ? "text-white font-medium"
                          : "text-gray-500"
                      }`}
                    >
                      {STAGE_LABELS[stage]}
                    </span>
                    {i < STAGES.length - 1 && (
                      <div
                        className={`flex-1 h-[2px] ml-2 ${
                          vp[STAGES[i + 1]] === "completed" ||
                          vp[STAGES[i + 1]] === "active"
                            ? "bg-[#6600ff]"
                            : "bg-gray-700"
                        }`}
                      />
                    )}
                  </div>
                ))}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

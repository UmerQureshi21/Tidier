import { useState } from "react";
import { useRef, useEffect } from "react";

import * as StompJs from "@stomp/stompjs";

interface Step {
  label: string;
  status: "pending" | "active" | "completed";
}

const INITIAL_STEPS: Step[] = [
  { label: "Analyzing videos with prompt", status: "active" },
  { label: "Trimming clips", status: "pending" },
  { label: "Combining videos", status: "pending" },
  { label: "Montage complete", status: "pending" },
];

function getStepIndex(message: string): number {
  if (message.includes("created!")) return 3;
  if (message.includes("Combining")) return 2;
  if (message.includes("Finished trimming") || message.includes("trimming")) return 1;
  if (message.includes("extracted")) return 0;
  return -1;
}

export default function MontageProgressWebSocket() {
  const full = import.meta.env.VITE_BACKEND_URL;
  const host = full.replace(/^https?:\/\//, "");
  const [steps, setSteps] = useState<Step[]>(INITIAL_STEPS);
  const [logs, setLogs] = useState<string[]>([]);
  const clientRef = useRef<StompJs.Client | null>(null);

  useEffect(() => {
    const client = new StompJs.Client({
      brokerURL: `ws://${host}/gs-guide-websocket`,
      reconnectDelay: 2000,
      debug: (str) => console.log(str),
      onConnect: (frame) => {
        console.log("We have connected to this frame: " + frame);

        client.subscribe("/topic/montage-progress", (message) => {
          const body = JSON.parse(message.body);
          const msg: string = body.content;

          setLogs((prev) => [...prev, msg]);

          const stepIdx = getStepIndex(msg);
          if (stepIdx >= 0) {
            setSteps((prev) =>
              prev.map((step, i) => {
                if (i < stepIdx) return { ...step, status: "completed" };
                if (i === stepIdx) {
                  // Final step becomes completed immediately
                  if (stepIdx === 3) return { ...step, status: "completed" };
                  return { ...step, status: "active" };
                }
                return step;
              })
            );
          }

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

  const completedCount = steps.filter((s) => s.status === "completed").length;
  const progressPercent = (completedCount / steps.length) * 100;

  return (
    <div className="w-full bg-black min-h-[700px] py-16 flex flex-col items-center justify-center gap-10">
      {/* Progress bar */}
      <div className="w-[80%] max-w-[500px]">
        <div className="flex justify-between mb-2">
          <span className="text-sm text-gray-400">Progress</span>
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

      {/* Steps */}
      <div className="w-[80%] max-w-[500px] flex flex-col gap-4">
        {steps.map((step, i) => (
          <div key={i} className="flex items-center gap-4">
            {/* Icon */}
            <div className="flex-shrink-0">
              {step.status === "completed" ? (
                <div className="w-8 h-8 rounded-full bg-green-500 flex items-center justify-center">
                  <svg
                    className="w-4 h-4 text-white"
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
              ) : step.status === "active" ? (
                <div className="w-8 h-8 rounded-full border-2 border-[#6600ff] flex items-center justify-center">
                  <svg
                    className="w-5 h-5 animate-spin text-[#6600ff]"
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
              ) : (
                <div className="w-8 h-8 rounded-full border-2 border-gray-600" />
              )}
            </div>

            {/* Label */}
            <span
              className={`text-base ${
                step.status === "completed"
                  ? "text-green-400"
                  : step.status === "active"
                  ? "text-white font-medium"
                  : "text-gray-500"
              }`}
            >
              {step.label}
            </span>
          </div>
        ))}
      </div>

      {/* Live log */}
      {logs.length > 0 && (
        <div className="w-[80%] max-w-[500px] mt-4 border border-gray-800 rounded-lg p-4 max-h-[150px] overflow-y-auto">
          {logs.map((log, i) => (
            <p key={i} className="text-xs text-gray-400 font-mono leading-6">
              {log}
            </p>
          ))}
        </div>
      )}
    </div>
  );
}

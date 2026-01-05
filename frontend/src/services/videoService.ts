import axios from "axios";
import type { VideoRequestDTO } from "../Types";

const CACHE_DURATION = 10 * 60 * 1000; // 10 minutes

let cachedVideos: VideoRequestDTO[] | null = null;
let cacheTime: number = 0;

const getBackendURL = () => import.meta.env.VITE_BACKEND_URL;
const getToken = () => localStorage.getItem("accessToken");

export async function getAllVideos(): Promise<VideoRequestDTO[]> {
  try {
    const now = Date.now();
    const token = getToken();

    // Check if cache exists and is still valid
    if (cachedVideos && now - cacheTime < CACHE_DURATION) {
      console.log("Using cached videos");
      return cachedVideos;
    }

    // Fetch new data
    console.log("Fetching fresh videos from API");
    console.log("TOKEN: " + token);

    const backendURL = getBackendURL();
    const res = await axios.get(`http://${backendURL}/videos`, {
      headers: {
        Authorization: token,
      },
    });

    const fileDetails = res.data;
    fileDetails.forEach((file: VideoRequestDTO) => {
      console.log(file.previewUrl);
    });

    // Store in cache
    cachedVideos = fileDetails;
    cacheTime = now;

    return fileDetails;
  } catch (err) {
    console.error("Failed to fetch videos:", err);
    throw err;
  }
}

export async function uploadVideos(files: FileList): Promise<void> {
  try {
    const token = getToken();
    const backendURL = getBackendURL();
    const request = new FormData();

    for (let [index, file] of Object.entries(files)) {
      console.log(`File ${index}: ${file.name}`);
      request.append("files", file);
    }

    const res = await axios.post(`http://${backendURL}/videos`, request, {
      headers: {
        Authorization: token,
      },
    });

    console.log("Upload successful:", res.data);
  } catch (err) {
    console.error("Upload failed:", err);
    throw err;
  }
}

export function clearVideoCache(): void {
  cachedVideos = null;
  cacheTime = 0;
}
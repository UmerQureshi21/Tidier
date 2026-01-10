import axiosInstance from "./refreshTokenAxios";
import type { VideoRequestDTO } from "../Types";

const CACHE_DURATION = 10 * 60 * 1000; // 10 minutes

let cachedVideos: VideoRequestDTO[] | null = null;
let cacheTime: number = 0;

export async function getAllVideos(): Promise<VideoRequestDTO[]> {
  try {
    const now = Date.now();

    // Check if cache exists and is still valid
    if (cachedVideos && now - cacheTime < CACHE_DURATION) {
      console.log("Using cached videos");
      return cachedVideos;
    }

    // Fetch new data
    console.log("Fetching fresh videos from API");

    const res = await axiosInstance.get("/videos");

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

export async function uploadVideos(files: FileList): Promise<boolean> {
  try {
    const request = new FormData();

    for (let [index, file] of Object.entries(files)) {
      console.log(`File ${index}: ${file.name}`);
      request.append("files", file);
    }

    const res = await axiosInstance.post("/videos", request);
    const data: VideoRequestDTO[] = res.data;
    if (data.length == 0) {
      console.log("rate limit");
      return false;
    } else {
      console.log("Upload successful:", res.data);
      return true;
    }
  } catch (err) {
    console.error("Upload failed:", err);
    throw err;
  }
}

export function clearVideoCache(): void {
  cachedVideos = null;
  cacheTime = 0;
}

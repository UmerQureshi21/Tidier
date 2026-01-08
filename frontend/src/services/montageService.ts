import axiosInstance from "./refreshTokenAxios";
import type {
  MontageRequestDTO,
  MontageResponseDTO,
  VideoRequestDTO,
} from "../Types";

const CACHE_DURATION = 10 * 60 * 1000; // 10 minutes

let cachedVideos: MontageResponseDTO[] | null = null;
let cacheTime: number = 0;

export async function createMontage(
  files: VideoRequestDTO[],
  clicks: boolean[],
  title: string,
  sentence: string
): Promise<MontageResponseDTO> {
  let videosInMontage: VideoRequestDTO[] = [];
  for (let i = 0; i < clicks.length; i++) {
    if (clicks[i]) {
      videosInMontage.push(files[i]);
    }
  }

  let request: MontageRequestDTO = {
    name: title,
    videoRequestDTOs: videosInMontage,
    prompt: sentence,
    sentence: `Give all time intervals of ${sentence}, only tell me the intervals, nothing else, and in this format: 00:00-00:06, 01:02-01:09, ... If there are no such time intervals, only return 00:00-00:00`,
  };

  // Find timestamps containing FOOD-related visuals in a STREET environment at NIGHT.

  const res = await axiosInstance.post(`/montages`, request);
  clearMontageCache();
  let data: MontageResponseDTO = res.data;
  return data;
}

export async function getAllMontages(): Promise<MontageResponseDTO[]> {
  try {
    const now = Date.now();

    // Check if cache exists and is still valid
    if (cachedVideos && now - cacheTime < CACHE_DURATION) {
      console.log("Using cached videos");
      return cachedVideos;
    }

    // Fetch new data
    console.log("Fetching fresh videos from API");

    const res = await axiosInstance.get(`/montages`);

    const fileDetails = res.data;

    // Store in cache
    cachedVideos = fileDetails;
    cacheTime = now;

    return fileDetails;
  } catch (err) {
    console.error("Failed to fetch videos:", err);
    throw err;
  }
}

export function clearMontageCache(): void {
  cachedVideos = null;
  cacheTime = 0;
}

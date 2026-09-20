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
  };

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

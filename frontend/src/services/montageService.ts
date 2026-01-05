import axios from "axios";
import type {
  MontageRequestDTO,
  MontageResponseDTO,
  VideoRequestDTO,
} from "../Types";

const CACHE_DURATION = 10 * 60 * 1000; // 10 minutes

let cachedVideos: VideoRequestDTO[] | null = null;
let cacheTime: number = 0;

const backendURL = import.meta.env.VITE_BACKEND_URL;
const getToken = () => localStorage.getItem("accessToken");

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

  const token = getToken();
  const res = await axios.post(`http://${backendURL}/montages`, request, {
    headers: {
      Authorization: token,
    },
  });
  let data: MontageResponseDTO = res.data;
  return data;
}

export async function getAllmontages(){
    
}
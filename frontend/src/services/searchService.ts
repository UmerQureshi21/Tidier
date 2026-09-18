import axiosInstance from "./refreshTokenAxios";
import type { SearchResultDTO } from "../Types";

export type SearchType = "videos" | "montages";

export async function search(
  query: string,
  type: SearchType
): Promise<SearchResultDTO[]> {
  const res = await axiosInstance.get("/search", {
    params: { q: query, type },
  });
  return res.data;
}

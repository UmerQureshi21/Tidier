export type VideoRequestDTO = {
  name: string;
  videoId: string;
  previewUrl: string;
  searchable: boolean; // has a summary + embedding, so the search page can find it
};

export type MontageRequestDTO = {
  name: string;
  videoRequestDTOs: VideoRequestDTO[];
  prompt: string; // the topic the user typed, the AI service turns it into the model prompt
};

export type MontageResponseDTO = {
  name: string;
  preSignedUrl: string;
  prompt: string;
  createdAt: Date;
  duration: number;
  videos: VideoRequestDTO[];
  searchable: boolean;
};

export type SearchResultDTO = {
  kind: "VIDEO" | "MONTAGE";
  id: number;
  name: string;
  url: string;
  summary: string;
  prompt: string | null; // montages only
  score: number;
};

export type UserRequestDTO = {
  displayedName: string;
  password: string;
  username: string;
};

export type UserResponseDTO = {
  username: string;
  displayedName: string;
};

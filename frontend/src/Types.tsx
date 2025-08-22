export type VideoDTO = {
    name: string;
    videoId: string;
  };

export type PromptRequestDTO = {
    sentence: string;
    videoRequestDTOs: VideoDTO[]
}       
export type VideoRequestDTO = {
  name: string;
  videoId: string;
  previewUrl: string;
};

export type MontageRequestDTO = {
  name: string;
  videoRequestDTOs: VideoRequestDTO[];
  prompt: string;
  sentence: string; //the senetence that tells twelvelabs how to format, we dont wanna store this in DB, only prompt
};

export type MontageResponseDTO = {
  name: string;
  preSignedUrl: string;
  prompt: string;
  videos: VideoRequestDTO[];
};

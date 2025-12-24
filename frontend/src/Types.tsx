export type VideoRequestDTO = {
  name: string;
  videoId: string;
  previewUrl: string;
};

export type MontageRequestDTO = {
  name: string;
  prompt: string;
  sentence: string; //the senetence that tells twelvelabs how to format, we dont wanna store this in DB, only prompt
  videoRequestDTOs: VideoRequestDTO[];
};

export type MontageResponseDTO = {
  name: string;
  preSignedUrl: string;
  prompt: string;
  videos: VideoRequestDTO[];
};

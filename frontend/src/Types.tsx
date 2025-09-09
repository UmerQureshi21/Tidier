export type VideoRequestDTO = {
  name: string;
  videoId: string
}

export type MontageRequestDTO = {
  name:string;
  prompt: string;
  sentence: string; //the senetence that tells twelvelabs how to format, we dont wanna store this in DB, only prompt
  videoRequestDTOs: VideoRequestDTO[]
}
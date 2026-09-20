from typing import Literal, Optional

from pydantic import BaseModel, Field

Kind = Literal["VIDEO", "MONTAGE"]


class IndexVideoRequest(BaseModel):
    video_url: str  # presigned S3 url, TwelveLabs downloads it from there


class IndexVideoResponse(BaseModel):
    video_id: str


class AssetResponse(BaseModel):
    asset_id: Optional[str]  # null while the video is still being indexed


class SummaryResponse(BaseModel):
    summary: Optional[str]


class IntervalsRequest(BaseModel):
    topic: str  # what the user typed, e.g. "eiffel tower"


class IntervalsResponse(BaseModel):
    data: Optional[str]  # raw interval text, e.g. "00:00-00:06, 01:02-01:09"


class DocumentRequest(BaseModel):
    owner_id: int
    kind: Kind
    ref_id: int
    text: str


class DocumentResponse(BaseModel):
    embedded: bool
    chunks: int = 0
    dim: int = 0


class SearchRequest(BaseModel):
    owner_id: int
    kind: Kind
    query: str
    limit: int = Field(default=10, ge=1, le=50)


class Hit(BaseModel):
    ref_id: int
    text: str
    score: float


class SearchResponse(BaseModel):
    hits: list[Hit]

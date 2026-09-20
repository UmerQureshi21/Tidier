"""AI service: owns every TwelveLabs call and the retrieval side of search.

The Spring Boot backend owns users, videos, montages, S3 and FFmpeg, and calls this service
over HTTP. This service is the only writer of the embeddings table.
"""

import logging
from contextlib import asynccontextmanager

from fastapi import Depends, FastAPI, Header, HTTPException

from . import rag, store, twelvelabs_client
from .config import settings
from .schemas import (
    AssetResponse,
    DocumentRequest,
    DocumentResponse,
    Hit,
    IndexVideoRequest,
    IndexVideoResponse,
    IntervalsRequest,
    IntervalsResponse,
    SearchRequest,
    SearchResponse,
    SummaryResponse,
)

logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(name)s: %(message)s")


@asynccontextmanager
async def lifespan(app: FastAPI):
    store.ensure_schema()
    yield


app = FastAPI(title="Tidier AI service", lifespan=lifespan)


def require_service_key(x_service_key: str = Header(default="")) -> None:
    """Only the backend may call this service, it is not exposed to browsers."""
    if x_service_key != settings.service_key:
        raise HTTPException(status_code=401, detail="Invalid service key")


@app.get("/health")
def health() -> dict:
    return {"status": "ok"}


# --- TwelveLabs ---

@app.post("/videos/index", response_model=IndexVideoResponse, dependencies=[Depends(require_service_key)])
def index_video(request: IndexVideoRequest) -> IndexVideoResponse:
    video_id = twelvelabs_client.index_video(request.video_url)
    if video_id is None:
        raise HTTPException(status_code=502, detail="Could not index the video")
    return IndexVideoResponse(video_id=video_id)


@app.get("/videos/{video_id}/asset", response_model=AssetResponse, dependencies=[Depends(require_service_key)])
def get_asset(video_id: str) -> AssetResponse:
    return AssetResponse(asset_id=twelvelabs_client.get_asset_id(video_id))


@app.delete("/videos/{video_id}", dependencies=[Depends(require_service_key)])
def delete_video(video_id: str) -> dict:
    return {"deleted": twelvelabs_client.delete_video(video_id)}


@app.post("/videos/{asset_id}/summary", response_model=SummaryResponse, dependencies=[Depends(require_service_key)])
def summarize(asset_id: str) -> SummaryResponse:
    return SummaryResponse(summary=twelvelabs_client.summarize(asset_id))


@app.post("/videos/{asset_id}/intervals", response_model=IntervalsResponse, dependencies=[Depends(require_service_key)])
def intervals(asset_id: str, request: IntervalsRequest) -> IntervalsResponse:
    return IntervalsResponse(data=twelvelabs_client.find_intervals(asset_id, request.topic))


# --- Retrieval ---

@app.post("/rag/documents", response_model=DocumentResponse, dependencies=[Depends(require_service_key)])
def index_document(request: DocumentRequest) -> DocumentResponse:
    chunks, dim = rag.index_document(request.owner_id, request.kind, request.ref_id, request.text)
    return DocumentResponse(embedded=chunks > 0, chunks=chunks, dim=dim)


@app.delete("/rag/documents/{kind}/{ref_id}", dependencies=[Depends(require_service_key)])
def delete_document(kind: str, ref_id: int) -> dict:
    return {"deleted": store.delete_document(kind, ref_id)}


@app.post("/rag/search", response_model=SearchResponse, dependencies=[Depends(require_service_key)])
def search(request: SearchRequest) -> SearchResponse:
    if not request.query.strip():
        return SearchResponse(hits=[])
    hits = rag.search(request.owner_id, request.kind, request.query.strip(), request.limit)
    return SearchResponse(hits=[Hit(ref_id=r, text=t, score=s) for r, t, s in hits])

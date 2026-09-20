"""Every TwelveLabs call the product makes lives here."""

import logging

from twelvelabs import TwelveLabs
from twelvelabs.types.text_input_request import TextInputRequest
from twelvelabs.types.video_context import VideoContext_AssetId

from .config import settings

log = logging.getLogger(__name__)

client = TwelveLabs(api_key=settings.twelvelabs_api_key)

SUMMARY_PROMPT = (
    "Summarize this video in about 120 words. Describe the scenery, setting, time of day, "
    "weather, mood and the activities people are doing. Use plain descriptive sentences."
)

# The montage pipeline parses this answer, so the format matters
INTERVALS_PROMPT = (
    "Give all time intervals of {topic}, only tell me the intervals, nothing else, and in "
    "this format: 00:00-00:06, 01:02-01:09, ... If there are no such time intervals, only "
    "return 00:00-00:00"
)


def index_video(video_url: str) -> str | None:
    """Upload a video to the index. Indexing continues in the background at TwelveLabs."""
    try:
        task = client.tasks.create(index_id=settings.twelvelabs_index_id, video_url=video_url)
        return task.video_id or task.id
    except Exception:
        log.exception("Failed to index video")
        return None


def get_asset_id(video_id: str) -> str | None:
    """The analyze endpoint needs an asset id, which only exists once indexing has finished."""
    try:
        video = client.indexes.videos.retrieve(settings.twelvelabs_index_id, video_id)
        return getattr(video, "asset_id", None)
    except Exception as e:
        log.info("No asset id for video %s yet: %s", video_id, e)
        return None


def delete_video(video_id: str) -> bool:
    try:
        client.indexes.videos.delete(settings.twelvelabs_index_id, video_id)
        return True
    except Exception:
        log.exception("Failed to delete video %s", video_id)
        return False


def _analyze(asset_id: str, prompt: str) -> str | None:
    try:
        response = client.analyze(
            model_name=settings.analyze_model,
            video=VideoContext_AssetId(asset_id=asset_id),
            prompt=prompt,
            temperature=0.2,
        )
        data = getattr(response, "data", None)
        return data.strip() if data else None
    except Exception:
        log.exception("Analyze failed for asset %s", asset_id)
        return None


def summarize(asset_id: str) -> str | None:
    """The description that gets embedded for search."""
    return _analyze(asset_id, SUMMARY_PROMPT)


def find_intervals(asset_id: str, topic: str) -> str | None:
    """Timestamps of a topic, used to cut the montage."""
    return _analyze(asset_id, INTERVALS_PROMPT.format(topic=topic))


def embed_text(text: str) -> list[float] | None:
    """Queries and stored text must use the same model or the vectors aren't comparable."""
    try:
        response = client.embed.v_2.create(
            input_type="text",
            model_name=settings.embedding_model,
            text=TextInputRequest(input_text=text),
        )
        if not response.data:
            log.error("No embedding in response")
            return None
        return list(response.data[0].embedding)
    except Exception:
        log.exception("Failed to embed text")
        return None

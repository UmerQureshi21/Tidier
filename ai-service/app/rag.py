"""Chunk, embed, store, retrieve."""

import logging
import re

import numpy as np

from . import store, twelvelabs_client
from .config import settings

log = logging.getLogger(__name__)


def chunk(text: str) -> list[str]:
    """Split on sentence boundaries into pieces the embedding endpoint accepts.

    Summaries are one chunk today. Longer text (per-segment descriptions, transcripts)
    would otherwise be truncated and lose whatever came after the cut.
    """
    text = text.strip()
    if len(text) <= settings.max_chunk_chars:
        return [text] if text else []

    chunks: list[str] = []
    current = ""
    for sentence in re.split(r"(?<=[.!?])\s+", text):
        if len(current) + len(sentence) + 1 > settings.max_chunk_chars and current:
            chunks.append(current.strip())
            current = ""
        # A single sentence longer than the limit still has to be cut somewhere
        while len(sentence) > settings.max_chunk_chars:
            chunks.append(sentence[: settings.max_chunk_chars])
            sentence = sentence[settings.max_chunk_chars :]
        current += sentence + " "
    if current.strip():
        chunks.append(current.strip())
    return chunks


def index_document(owner_id: int, kind: str, ref_id: int, text: str) -> tuple[int, int]:
    """Embed a video or montage description. Returns (chunks stored, vector dimension)."""
    pieces = chunk(text)
    if not pieces:
        return 0, 0

    embedded: list[tuple[str, list[float]]] = []
    for piece in pieces:
        vector = twelvelabs_client.embed_text(piece)
        if vector is None:
            return 0, 0  # keep whatever was stored before rather than a partial document
        embedded.append((piece, vector))

    store.replace_document(owner_id, kind, ref_id, embedded)
    return len(embedded), len(embedded[0][1])


def search(owner_id: int, kind: str, query: str, limit: int) -> list[tuple[int, str, float]]:
    """Rank this user's videos or montages against the query, best first."""
    query_vector = twelvelabs_client.embed_text(query)
    if query_vector is None:
        return []

    rows = store.load_for_owner(owner_id, kind)
    if not rows:
        return []

    query_array = np.array(query_vector, dtype=float)
    query_norm = np.linalg.norm(query_array)
    if query_norm == 0:
        return []

    best: dict[int, tuple[str, float]] = {}
    for ref_id, text, vector in rows:
        candidate = np.array(vector, dtype=float)
        if candidate.shape != query_array.shape:
            continue  # embedded with a different model, ignore rather than compare nonsense
        norm = np.linalg.norm(candidate)
        if norm == 0:
            continue
        score = float(np.dot(query_array, candidate) / (query_norm * norm))
        # A document scores as well as its best chunk
        if score >= settings.min_score and (ref_id not in best or score > best[ref_id][1]):
            best[ref_id] = (text, score)

    ranked = sorted(best.items(), key=lambda item: item[1][1], reverse=True)
    return [(ref_id, text, score) for ref_id, (text, score) in ranked[:limit]]

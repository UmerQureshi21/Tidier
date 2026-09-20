"""Storage for embeddings. This service is the only writer of the embeddings table."""

import logging

import psycopg

from .config import settings

log = logging.getLogger(__name__)

CREATE_SQL = """
CREATE SEQUENCE IF NOT EXISTS embeddingseq;
CREATE TABLE IF NOT EXISTS embeddings (
    id bigint PRIMARY KEY DEFAULT nextval('embeddingseq'),
    owner_id bigint NOT NULL,
    kind varchar(16) NOT NULL,
    ref_id bigint NOT NULL,
    chunk_index integer NOT NULL DEFAULT 0,
    text text,
    vector double precision[] NOT NULL,
    dim integer NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_embeddings_owner_kind ON embeddings (owner_id, kind);
CREATE INDEX IF NOT EXISTS idx_embeddings_kind_ref ON embeddings (kind, ref_id);
"""


def connect() -> psycopg.Connection:
    return psycopg.connect(settings.database_url)


def ensure_schema() -> None:
    with connect() as conn:
        conn.execute(CREATE_SQL)
        # The table may predate this service, when Hibernate owned it
        conn.execute("ALTER TABLE embeddings ALTER COLUMN id SET DEFAULT nextval('embeddingseq')")
    log.info("Embeddings table ready")


def replace_document(owner_id: int, kind: str, ref_id: int, chunks: list[tuple[str, list[float]]]) -> None:
    """Replaces every chunk of one video or montage, so re-embedding can't leave duplicates."""
    with connect() as conn:
        with conn.transaction():
            conn.execute("DELETE FROM embeddings WHERE kind = %s AND ref_id = %s", (kind, ref_id))
            for chunk_index, (text, vector) in enumerate(chunks):
                conn.execute(
                    "INSERT INTO embeddings (owner_id, kind, ref_id, chunk_index, text, vector, dim)"
                    " VALUES (%s, %s, %s, %s, %s, %s, %s)",
                    (owner_id, kind, ref_id, chunk_index, text, vector, len(vector)),
                )


def delete_document(kind: str, ref_id: int) -> int:
    with connect() as conn:
        cursor = conn.execute("DELETE FROM embeddings WHERE kind = %s AND ref_id = %s", (kind, ref_id))
        return cursor.rowcount


def load_for_owner(owner_id: int, kind: str) -> list[tuple[int, str, list[float]]]:
    with connect() as conn:
        rows = conn.execute(
            "SELECT ref_id, text, vector FROM embeddings WHERE owner_id = %s AND kind = %s",
            (owner_id, kind),
        ).fetchall()
    return [(row[0], row[1], row[2]) for row in rows]

# Tidier AI service

Owns every TwelveLabs call and the retrieval side of search. The Spring Boot backend calls it
over HTTP; it is not exposed to browsers. It is the only writer of the `embeddings` table.

## Run it

```bash
cd ai-service
python3 -m venv .venv
./.venv/bin/pip install -r requirements.txt
cp .env.example .env        # fill in the TwelveLabs key, index id and a shared secret
set -a && . ./.env && set +a
./.venv/bin/uvicorn app.main:app --reload --port 8000
```

The backend needs the same secret as `ai.service.key` (in `application-secrets.properties`
or the `AI_SERVICE_KEY` environment variable) and `ai.service.url` pointing here.

Interactive API docs while it runs: http://localhost:8000/docs

## Endpoints

| Endpoint | Purpose |
|---|---|
| `POST /videos/index` | Index a video from a presigned S3 url |
| `GET /videos/{video_id}/asset` | Asset id, null while still indexing |
| `DELETE /videos/{video_id}` | Remove the video from the index |
| `POST /videos/{asset_id}/summary` | Description used for search |
| `POST /videos/{asset_id}/intervals` | Timestamps of a topic, for the montage cut |
| `POST /rag/documents` | Chunk, embed and store a description |
| `DELETE /rag/documents/{kind}/{ref_id}` | Forget a video or montage |
| `POST /rag/search` | Rank one user's videos or montages against a query |

## Layout

- `app/twelvelabs_client.py` — TwelveLabs SDK calls and the prompts
- `app/rag.py` — chunking, embedding, cosine ranking
- `app/store.py` — the embeddings table
- `app/main.py` — the HTTP API

import os


class Settings:
    """Configuration read from the environment (see .env.example)."""

    def __init__(self) -> None:
        self.twelvelabs_api_key = os.environ["TWELVELABS_API_KEY"]
        self.twelvelabs_index_id = os.environ["TWELVELABS_INDEX_ID"]
        self.database_url = os.environ["DATABASE_URL"]
        self.service_key = os.environ["SERVICE_KEY"]

    # Models
    analyze_model = "pegasus1.5"
    embedding_model = "marengo3.0"

    # Retrieval
    max_chunk_chars = 1500   # the embedding endpoint caps input at 500 tokens
    min_score = 0.30         # below this a match is closer to noise than to an answer


settings = Settings()

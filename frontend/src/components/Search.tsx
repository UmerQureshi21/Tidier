import { useState } from "react";
import FileDetails from "./VideoDetails";
import type { SearchResultDTO } from "../Types";
import { search, type SearchType } from "../services/searchService";

export default function Search() {
  const [query, setQuery] = useState<string>("");
  const [type, setType] = useState<SearchType>("videos");
  const [results, setResults] = useState<SearchResultDTO[] | null>(null);
  const [searchedFor, setSearchedFor] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string>("");

  async function runSearch(searchType: SearchType) {
    if (query.trim() === "") return;
    try {
      setLoading(true);
      setErrorMessage("");
      const found = await search(query.trim(), searchType);
      setResults(found);
      setSearchedFor(query.trim());
    } catch (err) {
      console.error("Search failed:", err);
      setErrorMessage("Search failed. Please try again.");
      setResults(null);
    } finally {
      setLoading(false);
    }
  }

  // Switching the filter re-runs the current query so the results always match the toggle
  function handleTypeChange(newType: SearchType) {
    setType(newType);
    if (results !== null) {
      runSearch(newType);
    }
  }

  return (
    <div className="w-full bg-black flex flex-col items-center gap-[30px] pb-[100px]">
      <div className="w-[90%] md:w-[65%] bg-[rgb(20,20,20)] rounded-[20px] p-[25px] md:p-[40px] flex flex-col gap-[20px]">
        <h1 className="poppins-font text-white text-[26px] md:text-[35px]">
          Search by <span className="text-[#925cfe]">description</span>
        </h1>
        <p className="poppins-font text-gray-400 text-sm">
          Describe what you remember seeing, like "warm beach" or "scenic
          sunset", and we'll find the closest matches.
        </p>

        <form
          className="flex flex-col sm:flex-row gap-[15px]"
          onSubmit={(e) => {
            e.preventDefault();
            runSearch(type);
          }}
        >
          <input
            type="text"
            value={query}
            autoFocus
            placeholder="warm beach at sunset"
            onChange={(e) => setQuery(e.target.value)}
            className="flex-1 outline-none border-b-[1px] border-b-[#925cfe] pb-[8px] bg-transparent text-white poppins-font"
            style={{ caretColor: "#925cfe" }}
          />
          <button
            type="submit"
            disabled={query.trim() === "" || loading}
            className={`px-[30px] py-[12px] rounded-[20px] poppins-font text-white text-[18px] transition duration-150 ease ${
              query.trim() === "" || loading
                ? "bg-[#222222] hover:cursor-not-allowed"
                : "bg-[#925CFE] hover:cursor-pointer hover:shadow-[0_0_10px_white]"
            }`}
          >
            {loading ? "Searching..." : "Search"}
          </button>
        </form>

        {/* Videos / montages filter */}
        <div className="flex gap-[10px]">
          {(["videos", "montages"] as SearchType[]).map((option) => (
            <button
              key={option}
              onClick={() => handleTypeChange(option)}
              className={`px-[20px] py-[8px] rounded-[20px] poppins-font text-sm capitalize transition duration-150 hover:cursor-pointer ${
                type === option
                  ? "bg-[#925CFE] text-white"
                  : "bg-[#222222] text-gray-400 hover:text-white"
              }`}
            >
              {option}
            </button>
          ))}
        </div>
      </div>

      {errorMessage !== "" && (
        <div className="w-[90%] md:w-[65%] rounded-[20px] border border-red-500 bg-red-500/10 px-[20px] py-[15px] poppins-font text-red-400 text-center">
          {errorMessage}
        </div>
      )}

      {results !== null && results.length === 0 && !loading && (
        <div className="text-white poppins-font text-center text-[22px] md:text-[28px] w-[80%] md:w-[50%] mt-[40px]">
          No {type} matched{" "}
          <span className="text-[#925cfe]">"{searchedFor}"</span>. Try
          describing the scene differently.
        </div>
      )}

      {results !== null && results.length > 0 && (
        <div className="bg-[rgb(20,20,20)] w-[95%] rounded-[10px] grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 p-4">
          {results.map((result) => (
            <div key={`${result.kind}-${result.id}`} className="flex flex-col gap-[8px]">
              <FileDetails
                name={result.name}
                size={0}
                type={""}
                videoSrc={result.url}
              />
              <div className="px-[6px] flex flex-col gap-[4px]">
                <span className="poppins-font text-[#925cfe] text-xs">
                  {Math.round(result.score * 100)}% match
                  {result.prompt ? ` · "${result.prompt}"` : ""}
                </span>
                <p className="poppins-font text-gray-400 text-xs line-clamp-3">
                  {result.summary}
                </p>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

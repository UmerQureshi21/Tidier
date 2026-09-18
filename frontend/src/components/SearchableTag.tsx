// Small pill saying whether the search page can find this video or montage yet
export default function SearchableTag({
  searchable,
  className = "",
}: {
  searchable: boolean;
  className?: string;
}) {
  return (
    <span
      title={
        searchable
          ? "Found by description on the Search page"
          : "Still being described, this takes a minute or two after uploading"
      }
      className={`inline-flex items-center gap-[6px] self-start rounded-full border px-[10px] py-[3px] poppins-font text-[11px] transition-colors ${
        searchable
          ? "border-green-500/30 bg-green-500/10 text-green-400"
          : "border-white/10 bg-white/5 text-gray-500"
      } ${className}`}
    >
      {searchable ? (
        <svg
          className="w-[11px] h-[11px]"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          strokeWidth={3}
        >
          <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
        </svg>
      ) : (
        <svg
          className="w-[11px] h-[11px]"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          strokeWidth={3}
        >
          <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
        </svg>
      )}
      {searchable ? "Searchable" : "Not searchable yet"}
    </span>
  );
}

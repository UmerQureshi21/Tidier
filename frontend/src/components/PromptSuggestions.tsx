import { useState } from "react";

const PROMPT_SUGGESTIONS = [
  "the sunset",
  "dad laughing",
  "food carts",
  "the lakes and rivers",
  "hot air balloons",
  "beautiful nature",
  "times square in the night light",
  "smiley faces",
  "girraffes in the zoo",
  "any mercedes benz coupe",
  "waterfalls",
  "london bridge",
];

export default function PromptSuggestion() {
  const [currentPrompt, setCurrentPrompt] = useState<string | null>(null);
  const [isGenerating, setIsGenerating] = useState(false);

  const generatePrompt = () => {
    setIsGenerating(true);
    // Simulate a small delay for better UX
    setTimeout(() => {
      const randomPrompt =
        PROMPT_SUGGESTIONS[
          Math.floor(Math.random() * PROMPT_SUGGESTIONS.length)
        ];
      setCurrentPrompt(randomPrompt);
      setIsGenerating(false);
    }, 300);
  };

  const copyPrompt = () => {
    if (currentPrompt) {
      navigator.clipboard.writeText(currentPrompt);
    }
  };

  return (
    <div className="p-6 bg-gradient-to-br from-[#111] to-[#111] h-full border border-[#925cfe] rounded-lg">
      <div className="flex items-center gap-2 mb-4">
        <span className="text-[#925cfe] text-2xl">✨</span>
        <h3 className="text-xl  text-white">Need Inspiration?</h3>
      </div>

      <p className="text-gray-400 text-sm mb-6">
        Not sure what to create? Get AI-powered suggestions based on your uplaods, to spark your
        creativity.
      </p>

      {currentPrompt && (
        <div className="bg-[#222] p-4 rounded-lg mb-4 border border-[#925cfe] border-opacity-30">
          <p className="text-white  mb-3">{currentPrompt}</p>
          <button
            onClick={copyPrompt}
            className="text-sm bg-[#925cfe] hover:bg-[#7a4dd1] text-white px-3 py-1 rounded transition"
          >
            Copy Prompt
          </button>
        </div>
      )}

      <button
        onClick={generatePrompt}
        disabled={isGenerating}
        className="w-full bg-[#925cfe] hover:bg-[#7a4dd1] disabled:opacity-50 disabled:cursor-not-allowed text-white  py-3 px-4 rounded-lg transition flex items-center justify-center gap-2"
      >
        <span className="text-lg">✨</span>
        {isGenerating ? "Generating..." : "Generate Suggestion (BETA)"}
      </button>
    </div>
  );
}

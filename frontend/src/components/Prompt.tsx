export default function Prompt() {
  return (
    <div className="relative w-full h-[100px] bg-black prompt-section flex flex-col items-center">
      <div className="text-white poppins w-[90%] flex flex-col items-center justify-center">
        <h1 className=" text-[25px] text-center">Give me a montage of </h1>
        <input
          type="text"
          autoFocus
          className="w-[90%] mt-[10px] outline-none border-b-[1px] border-b-[#6600FF] pb-[3px]"
          style={{ caretColor: '#6600FF' }}
        />
      </div>
    </div>
  );
}

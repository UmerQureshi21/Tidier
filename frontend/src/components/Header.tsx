export default function Header() {
  return (
    <div className="relative w-full h-[350px]">
      <div className="absolute top-0 right-0 w-[200%] h-full bg-cover bg-center flex">
        <img src="/times-square.jpg" alt="" className="w-[50%] h-full object-cover" />
        <img src="/paris-evening.png" alt="" className="w-[50%] h-full object-cover" />
        {/*I just put two to include a carousel type feature in the future, put left-0 to get the other image*/}
      </div>
      <h1 className="absolute z-[50] poppins-font top-[40px] left-[20px] text-[30px] text-white">
        Tidier
      </h1>
      <h1 className="absolute w-[80%] font-thin top-[100px] left-[20px] z-[50] text-white text-[20px] poppins-font">
        Your AI-powered Video
        <span className="font-normal ml-[5px]">Montage Maker</span>
      </h1>

      {/* Black gradient overlay */}
      <div className="absolute top-0 left-0 right-0 bottom-0 bg-gradient-to-b from-[rgb(5,5,5)] to-transparent"></div>
    </div>
  );
}

import { Outlet } from "react-router";
import Header from "../components/Header";
import Navbar from "../components/NavBar";

export default function RootLayout() {
  return (
    <div className="flex flex-col">
      <Navbar />
      <div className="mt-[75px]">
        <Header height={320} />
        <Outlet />
      </div>
    </div>
  );
}

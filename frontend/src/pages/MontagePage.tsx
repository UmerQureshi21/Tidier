import HelloWebSocket from "../components/HelloWebSocket";
import MontageArea from "../components/MontageArea";
import MontageCompleted from "../components/MontageCompleted";

 export default function MontagePage(){
    return (
        <div>
            <MontageCompleted/>
            <MontageArea/>
            <HelloWebSocket/>
        </div>
    )
 }
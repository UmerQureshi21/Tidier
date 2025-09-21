package com.umerqureshicodes.tidier.Utilities;

import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.util.HtmlUtils;

@Controller
public class MessageController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    /*
     * The @SendTo + @MessageMapping combo is for receiving a message from a client and then broadcasting a response.
     * In my case, I already know the progress inside the server (during trimming/combining videos).
     * Instead, I can directly push updates to the topic using SimpMessagingTemplate from your MontageService.
     */
    public WebSocketServiceMessage  greeting(StompMessage  message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return new WebSocketServiceMessage("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }


}

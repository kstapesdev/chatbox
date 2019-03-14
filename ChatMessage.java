package com.scatterform.chatabox;

public class ChatMessage {

    String chatSender;
    String chatSendTime;
    String chatText;

    public ChatMessage(){}

    String getChatSender(){ return chatSender; }
    String getChatSendTime(){ return chatSendTime; }
    String getChatText(){ return chatText; }

    public String toString(){
        String result="Chat Message : Sender ["+ chatSender + "] Time [" + chatSendTime + "]" + "] Text [" + chatText + "]";
        return result;
    }

}

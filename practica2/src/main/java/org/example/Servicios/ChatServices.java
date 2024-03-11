package org.example.Servicios;

import org.example.Entidades.Chat;

import java.util.ArrayList;
import java.util.List;

public class ChatServices {
    private static ChatServices instance = null;
    private static int id = 1;
    private final List<Chat> chats;

    private ChatServices() {
        this.chats = new ArrayList<>();
    }

    public static ChatServices getInstance() {
        if (instance == null) {
            instance = new ChatServices();
        }
        return instance;
    }

    public void addChat(Chat chat){
        this.chats.add(chat);
        id++;
    }

    public List<Chat> getChats(){
        return this.chats;
    }

    public Chat getChat(int id){
        for(Chat c : this.chats){
            if(c.getId() == id){
                return c;
            }
        }
        return null;
    }

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        ChatServices.id = id;
    }
}

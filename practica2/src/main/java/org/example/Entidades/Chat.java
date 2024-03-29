package org.example.Entidades;

import org.eclipse.jetty.websocket.api.Session;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Chat {
    private int id;
    private String usuario;
    private String destinatario;
    private List<Mensaje> mensajes;
    private final List<Session> conectedUsers;

    public Chat(int id, String usuario, String destinatario) {
        this.id = id;
        this.usuario = usuario;
        this.destinatario = destinatario;
        this.conectedUsers = new ArrayList<>();
        this.mensajes = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public List<Mensaje> getMensajes() {
        return mensajes;
    }

    public void setMensajes(List<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }

    public void addMensaje(Mensaje mensaje){
        mensajes.add(mensaje);
    }

    public List<Session> getConectedUsers(){
        return conectedUsers;
    }
}

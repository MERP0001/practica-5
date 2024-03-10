package org.example.Servicios;
import org.example.Entidades.Articulo;
import org.example.Entidades.Comentario;
import org.example.Entidades.Etiqueta;
import org.example.Entidades.Usuario;

import java.util.ArrayList;

public class Blog {
    private static Blog instance = null;
    private ArrayList<Articulo> articulos;
    private ArrayList<Usuario> usuarios;
    private ArrayList<Comentario> comentarios;
    private ArrayList<Etiqueta> etiquetas;
    private static long genArt = 1;
    private static long genCom = 1;
    private static long genEt = 1;

    private Blog() {
        this.articulos = new ArrayList<>();
        this.usuarios = new ArrayList<>();
        this.comentarios = new ArrayList<>();
        this.etiquetas = new ArrayList<>();
    }

    public static Blog getInstance() {
        if (instance == null) {
            instance = new Blog();
        }
        return instance;
    }

    public void agregarUsuario(Usuario usuario) {
        if(findUserByUsername(usuario.getUsername()) != null){
            return;
        }
        this.usuarios.add(usuario);
    }

    public void agregarArticulo(Articulo articulo) {
        this.articulos.add(articulo);
        genArt++;
    }

    public void agregarComentario(Comentario comentario) {
        this.comentarios.add(comentario);
        genCom++;
    }

    public void agregarEtiqueta(Etiqueta etiqueta) {
        this.etiquetas.add(etiqueta);
        genEt++;
    }

    public ArrayList<Usuario> getUsuarios() {
        return this.usuarios;
    }

    public ArrayList<Articulo> getArticulos() {
        return this.articulos;
    }

    public ArrayList<Comentario> getComentarios() {
        return this.comentarios;
    }

    public ArrayList<Etiqueta> getEtiquetas() {
        return this.etiquetas;
    }

    public static long getGenArt() {
        return genArt;
    }

    public static long getGenCom() {
        return genCom;
    }

    public static long getGenEt() {
        return genEt;
    }

    public Usuario findUserByUsername(String username){
        Usuario found = null;

        for (Usuario user : usuarios) {
            if(user.getUsername().equalsIgnoreCase(username)){
                found = user;
                break;
            }
        }

        return found;
    }

    public Articulo findArticuloById(long id){
        Articulo found = null;

        for(Articulo art : articulos){
            if (art.getId() == id){
                found = art;
                break;
            }
        }

        return found;
    }

    public Etiqueta findEtiquetaById(long id){
        Etiqueta found = null;

        for(Etiqueta etq : etiquetas){
            if (etq.getId() == id){
                found = etq;
                break;
            }
        }

        return found;
    }

    public Comentario findComentarioById(long id){
        Comentario found = null;

        for(Comentario com : comentarios){
            if (com.getId() == id){
                found = com;
                break;
            }
        }

        return found;
    }

    public void eliminarArticulo(long id) {
        Articulo articulo = findArticuloById(id);
        if(articulo != null){
            articulos.remove(articulo);
        }
    }
}

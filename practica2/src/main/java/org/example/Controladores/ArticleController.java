package org.example.Controladores;

import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.example.Entidades.*;
import org.example.Servicios.*;
import org.hibernate.Hibernate;

import java.util.*;

public class ArticleController extends BaseController{

    public ArticleController(Javalin app) {
        super(app);
        registerTemplates();
    }

    public void registerTemplates(){
        JavalinRenderer.register(new JavalinThymeleaf(), ".html");
    }
    @Override
    public void aplicarRutas() {
        app.before("/articulo/crear", ctx -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null) {
                ctx.redirect("/user/login");
            }
        });

        app.get("/articulo/crear", ctx -> {
            List<Etiqueta> etiquetas = EtiquetaServices.getInstance().findAll();
            Map<String, Object> model = new HashMap<>();

            model.put("titulo", "Crear");
            model.put("etiquetas", etiquetas);
            ctx.render("/public/templates/crear-articulo.html", model);
        });

        app.post("/articulo/crear", ctx -> {
            String titulo = ctx.formParam("titulo");
            String cuerpo = ctx.formParam("cuerpo");
            List<Etiqueta> etiq = new ArrayList<>();
            Usuario usuario = ctx.sessionAttribute("username");
            String idsEtiquetasExistentesStr = ctx.formParam("etiquetasExistentes");
            String nuevasEtiquetasStr = ctx.formParam("nuevasEtiquetas");
            List<String> idsEtiquetasExistentes = idsEtiquetasExistentesStr != null ? new ArrayList<>(Arrays.asList(idsEtiquetasExistentesStr.split(","))) : new ArrayList<>();
            List<String> nuevasEtiquetas = nuevasEtiquetasStr != null ? new ArrayList<>(Arrays.asList(nuevasEtiquetasStr.split(","))) : new ArrayList<>();
            for (String id : idsEtiquetasExistentes) {
                Etiqueta etiquetaExistente = EtiquetaServices.getInstance().findAll().stream()
                        .filter(e -> e.getId() == Long.parseLong(id))
                        .findFirst()
                        .orElse(null);
                if (etiquetaExistente != null) {
                    etiq.add(etiquetaExistente);
                }
            }

            for (String etiquetaStr : nuevasEtiquetas) {
                if (etiquetaStr.isEmpty()) {
                    continue;
                }
                Etiqueta nuevaEtiqueta = new Etiqueta(etiquetaStr);
                EtiquetaServices.getInstance().create(nuevaEtiqueta);
                etiq.add(nuevaEtiqueta);
            }
            Articulo temp = new Articulo(titulo, cuerpo, usuario, new Date(), etiq);
            ArticleServices.getInstance().create(temp);
            ctx.redirect("/");
        });

        app.get("/articulo/detalle/{id}", ctx -> {
            long id = Long.parseLong(ctx.pathParam("id"));
            List<Comentario> listaComents  = ComentarioServices.getInstance().findAllByArticulo(id);
            Articulo articulo = ArticleServices.getInstance().buscar(id);
            //ArticuloDTO articuloDTO = ArticuloDTO.fromEntity(articulo);
            Map<String, Object> model = new HashMap<>();
            model.put("articulo", articulo);
            model.put("listaComentarios", listaComents);
            ctx.render("/public/templates/mostrar-articulo.html", model);
        });

        app.post("/articulo/comentar", ctx -> {
            String comentario = ctx.formParam("comentario");
            Usuario usuario = ctx.sessionAttribute("username");
            long idArticulo = Long.parseLong(ctx.formParam("id"));
            Articulo articulo = ArticleServices.getInstance().find(idArticulo);
            Hibernate.initialize(articulo.getListaComentarios()); // Initialize the collection
            Comentario temp = new Comentario(comentario, UserServices.getInstance().find(usuario.getUsername()), articulo);
            articulo.agregarComentario(temp);
            ComentarioServices.getInstance().create(temp);
            ArticleServices.getInstance().edit(articulo);
            ctx.redirect("/articulo/detalle/" + idArticulo);
        });

        app.post("/articulo/borrar", ctx -> {
            long id = Long.parseLong(ctx.formParam("id"));
            Articulo articulo = ArticleServices.getInstance().find(id);
            ArticleServices.getInstance().remove(articulo);
            ctx.redirect("/");
        });

        app.before("/articulo/editar/lista", ctx -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null) {
                ctx.redirect("/user/login");
            }
        });

        app.get("/articulo/editar/lista", ctx -> {
            Map<String, Object> model = new HashMap<>();
            Usuario aux = ctx.sessionAttribute("username");
            ArrayList<Articulo> articulosUser = new ArrayList<>();

            for(Articulo articulo : ArticleServices.getInstance().findAllRecent()){
                if(articulo.getAutor().getUsername().equals(aux.getUsername())){
                    articulosUser.add(articulo);
                }
            }
            model.put("titulo", "Lista de Articulos del Usuario");
            model.put("articulos", articulosUser);
            ctx.render("/public/templates/lista-articulo-user.html", model);
        });

        app.before("/articulo/editar/{id}", ctx -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null) {
                ctx.redirect("/user/login");
            }
        });

        app.get("/articulo/editar/{id}", ctx -> {
            long id = Long.parseLong(ctx.pathParam("id"));
            Articulo articulo = ArticleServices.getInstance().find(id);
            List<Etiqueta> etiquetas = EtiquetaServices.getInstance().findAll();
            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Editar Articulo");
            model.put("articulo", articulo);
            model.put("etiquetas", etiquetas);
            ctx.render("/public/templates/modificar-articulo.html", model);
        });

        app.post("articulo/editar", ctx -> {
            long idArticulo = Long.parseLong(ctx.formParam("id"));
            Articulo articulo = ArticleServices.getInstance().find(idArticulo);
            List<Etiqueta> etiq = new ArrayList<>();
            String idsEtiquetasExistentesStr = ctx.formParam("etiquetasExistentes");
            String nuevasEtiquetasStr = ctx.formParam("nuevasEtiquetas");
            List<String> idsEtiquetasExistentes = idsEtiquetasExistentesStr != null ? new ArrayList<>(Arrays.asList(idsEtiquetasExistentesStr.split(","))) : new ArrayList<>();
            List<String> nuevasEtiquetas = nuevasEtiquetasStr != null ? new ArrayList<>(Arrays.asList(nuevasEtiquetasStr.split(","))) : new ArrayList<>();
            for (String id : idsEtiquetasExistentes) {
                Etiqueta etiquetaExistente = EtiquetaServices.getInstance().findAll().stream()
                        .filter(e -> e.getId() == Long.parseLong(id))
                        .findFirst()
                        .orElse(null);
                if (etiquetaExistente != null) {
                    etiq.add(etiquetaExistente);
                }
            }

            for (String etiquetaStr : nuevasEtiquetas) {
                if (etiquetaStr.isEmpty()) {
                    continue;
                }
                Etiqueta nuevaEtiqueta = new Etiqueta(etiquetaStr);
                EtiquetaServices.getInstance().create(nuevaEtiqueta);
                etiq.add(nuevaEtiqueta);
            }
            articulo.setCuerpo(ctx.formParam("cuerpo"));
            articulo.setTitulo(ctx.formParam("titulo"));
            articulo.setListaEtiquetas(etiq);
            ArticleServices.getInstance().edit(articulo);
            ctx.redirect("/");
        });
    }
}

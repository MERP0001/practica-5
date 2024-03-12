package org.example.Controladores;

import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.example.Entidades.Usuario;
import org.example.Servicios.ArticleServices;
import org.example.Entidades.Articulo;
import org.example.Entidades.Etiqueta;
import org.example.Servicios.EtiquetaServices;
import org.example.Servicios.UserServices;

import java.util.*;

public class HomeController extends BaseController{

    public HomeController(Javalin app) {
        super(app);
        registerTemplates();
    }

    public void registerTemplates(){
        JavalinRenderer.register(new JavalinThymeleaf(), ".html");
    }

    @Override
    public void aplicarRutas() {

        app.before("/", ctx -> {
            if(ctx.cookie("rememberedUser") != null){
                Usuario aux = UserServices.getInstance().find(ctx.cookie("rememberedUser"));
                ctx.sessionAttribute("username", aux);
            }
        });

        app.get("/", ctx -> {
            String pageParam = ctx.queryParam("page");
            int page = (pageParam != null) ? Integer.parseInt(pageParam) : 1;
            List<Articulo> articulos = ArticleServices.getInstance().findAllRecentPag(page, 5);
            List<Etiqueta> etiquetas = EtiquetaServices.getInstance().findAll();
            int totalArticles = ArticleServices.getInstance().findAll().size();
            int totalPages = (int) Math.ceil((double) totalArticles / 5);
            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Inicio");
            model.put("articulos", articulos);
            model.put("totalPages", totalPages);
            model.put("currentPage", page);
            model.put("etiquetas", etiquetas);
            ctx.render("/public/templates/index.html", model);
        });

        app.get("/tag", ctx -> {
            String pageParam = ctx.queryParam("page");
            int page = (pageParam != null) ? Integer.parseInt(pageParam) : 1;
            String tag = ctx.queryParam("tag");
            List<Articulo> articulos = ArticleServices.getInstance().findAllByTagPag(tag, page, 5);
            List<Etiqueta> etiquetas = EtiquetaServices.getInstance().findAll();
            int totalArticles = ArticleServices.getInstance().findAllByTag(tag).size();
            int totalPages = (int) Math.ceil((double) totalArticles / 5);
            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Inicio");
            model.put("articulos", articulos);
            model.put("totalPages", totalPages);
            model.put("currentPage", page);
            model.put("etiquetas", etiquetas);
            ctx.render("/public/templates/index.html", model);
        });
    }
}

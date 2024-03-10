package org.example;
import io.javalin.Javalin;
import org.example.Controladores.ChatControler;
import org.example.Entidades.Articulo;
import org.example.Entidades.Etiqueta;
import org.example.Servicios.*;
import org.example.Controladores.ArticleController;
import org.example.Controladores.HomeController;
import org.example.Controladores.UserController;
import org.example.Entidades.Usuario;
import jakarta.persistence.Persistence;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        StartServices.getInstance().startDb();
        if (UserServices.getInstance().find("admin") == null) {
            UserServices.getInstance().create(new Usuario("admin", "admin", "admin", true, false));
        }

        Javalin app = Javalin.create(javalinConfig -> {

        }).start(7000);

        new UserController(app).aplicarRutas();
        new ArticleController(app).aplicarRutas();
        new HomeController(app).aplicarRutas();
        new ChatControler(app).aplicarRutas();
    }
}
package org.example.Controladores;

import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.example.Entidades.Foto;
import org.example.Entidades.Usuario;
import org.example.Servicios.FotoServices;
import org.example.Servicios.UserServices;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class UserController extends BaseController{
    public UserController(Javalin app){
        super(app);
        registerTemplates();
    }

    public void registerTemplates(){
        JavalinRenderer.register(new JavalinThymeleaf(), ".html");
    }

    @Override
    public void aplicarRutas() {

        app.get("/user/register", ctx -> {
            Map<String, Object> model = new HashMap<>();

            model.put("titulo", "Registrar");
            ctx.render("/public/templates/register.html", model);
        });

        app.post("/user/register", ctx -> {
            String user = ctx.formParam("usuario");
            String name = ctx.formParam("nombre");
            String pass = ctx.formParam("password");

            Usuario existingUser = UserServices.getInstance().find(user);
            if (existingUser != null) {
                ctx.render("/public/templates/register.html", Map.of("error", "El nombre de usuario ya existe"));
            }
            else{
                Usuario temp = new Usuario(user, name, pass, false, true);
                UserServices.getInstance().create(temp);
                ctx.sessionAttribute("username", temp);
                ctx.redirect("/");
            }
        });

        app.get("/user/login", ctx -> {
            Map<String, Object> model = new HashMap<>();

            model.put("titulo", "Log in");
            ctx.render("/public/templates/login.html", model);
        });

        app.post("/user/login", ctx -> {
            String user = ctx.formParam("usuario");
            String pass = ctx.formParam("password");

            Usuario aux = UserServices.getInstance().find(user);
            if(aux != null){
                if(aux.getPassword().equalsIgnoreCase(pass)){
                    if (ctx.formParam("rememberMe") != null) {
                        ctx.cookie("rememberedUser", aux.getUsername(),600);
                    }
                    ctx.sessionAttribute("username", aux);
                    try {
                        Class.forName("org.postgresql.Driver");
                        String dbUrl = System.getenv("JDBC_DATABASE_URL");
                        Connection connection = DriverManager.getConnection(dbUrl);
                        String sql = "INSERT INTO user_authentications (username, authentication_time) VALUES (?, ?)";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, aux.getUsername());
                        statement.setObject(2, LocalDateTime.now());
                        statement.executeUpdate();
                    } catch (ClassNotFoundException | SQLException e) {
                        e.printStackTrace();
                    }
                    ctx.redirect("/1");
                }

                else{
                    ctx.render("/public/templates/login.html", Map.of("error", "Usuario o contraseña incorrectos"));
                }
            }
            else{
                ctx.render("/public/templates/login.html", Map.of("error", "Usuario no existe"));
            }
        });

        app.before("/user/list", ctx -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null || !usuario.isAdministrator()) {
                ctx.redirect("/");
            }
        });

        app.get("/user/list/{page}", ctx -> {
            int page = Integer.parseInt(ctx.pathParam("page"));
            List<Usuario> usuarios = UserServices.getInstance().findAll(page, 5);
            int totalUsers = UserServices.getInstance().findAll().size();
            int totalPages = (int) Math.ceil((double) totalUsers / 5);
            ctx.render("/public/templates/user-list.html", Map.of("usuarios", usuarios, "totalPages", totalPages, "currentPage", page));
        });

        app.before("/user/crear", ctx -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null || !usuario.isAdministrator()) {
                ctx.redirect("/");
            }
        });

        app.get("/user/crear", ctx -> {
            Map<String, Object> model = new HashMap<>();

            model.put("titulo", "Crear");
            ctx.render("/public/templates/create-user.html", model);
        });

        app.post("/user/crear", ctx -> {
            String user = ctx.formParam("usuario");
            String name = ctx.formParam("nombre");
            String pass = ctx.formParam("password");
            boolean autor = ctx.formParam("Autor") != null;
            boolean admin = ctx.formParam("Admin") != null;

            Usuario existingUser = UserServices.getInstance().find(user);
            if (existingUser != null) {
                ctx.render("/public/templates/register.html", Map.of("error", "El nombre de usuario ya existe"));
            } else {
                Foto foto = null;
                if (ctx.uploadedFile("foto") != null) {
                    try {
                        byte[] bytes = ctx.uploadedFile("foto").content().readAllBytes();
                        String encodedString = Base64.getEncoder().encodeToString(bytes);
                        String mimeType = ctx.uploadedFile("foto").contentType();
                        foto = new Foto(ctx.uploadedFile("foto").filename(), mimeType, encodedString);
                        FotoServices.getInstance().create(foto);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Usuario temp = new Usuario(user, name, pass, admin, autor);
                temp.setFoto(foto);
                UserServices.getInstance().create(temp);
                ctx.redirect("/user/list");
            }
        });

        app.before("/user/editar", ctx -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null || !usuario.isAdministrator()) {
                ctx.redirect("/");
            }
        });

        app.get("/user/editar/{username}", ctx -> {
            Usuario usuario = UserServices.getInstance().find(ctx.pathParam("username"));
            if (usuario == null) {
                ctx.redirect("/user/list/1");
            } else {
                Map<String, Object> model = new HashMap<>();
                model.put("usuario", usuario);
                model.put("titulo", "Editar");
                ctx.render("/public/templates/modificar-usuario.html", model);
            }
        });

        app.post("/user/editar/{username}", ctx -> {
            Usuario usuario = UserServices.getInstance().find(ctx.pathParam("username"));
            if (usuario == null) {
                ctx.redirect("/user/list");
            } else {
                String name = ctx.formParam("nombre");
                String pass = ctx.formParam("password");
                boolean autor = ctx.formParam("Autor") != null;
                boolean admin = ctx.formParam("Admin") != null;
                Foto foto = usuario.getFoto();
                if (ctx.uploadedFile("foto") != null) {
                    try {
                        byte[] bytes = ctx.uploadedFile("foto").content().readAllBytes();
                        String encodedString = Base64.getEncoder().encodeToString(bytes);
                        String mimeType = ctx.uploadedFile("foto").contentType();
                        foto = new Foto(ctx.uploadedFile("foto").filename(), mimeType, encodedString);
                        FotoServices.getInstance().create(foto);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                usuario.setNombre(name);
                usuario.setPassword(pass);
                usuario.setAutor(autor);
                usuario.setAdministrator(admin);
                usuario.setFoto(foto);
                UserServices.getInstance().edit(usuario);
                ctx.redirect("/user/list/1");
            }
        });

        app.get("/user/close", ctx -> {
            Map<String, Object> model = new HashMap<>();

            model.put("titulo", "Cerrar Sesion");
            ctx.render("/public/templates/cerrar-sesion.html", model);
        });

        app.post("/user/close", ctx -> {
            ctx.removeCookie("rememberedUser");
            ctx.req().getSession().invalidate();
            ctx.redirect("/");
        });
    }
}

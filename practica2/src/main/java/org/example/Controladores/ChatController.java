package org.example.Controladores;

import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.eclipse.jetty.websocket.api.Session;
import org.example.Entidades.Chat;
import org.example.Entidades.Mensaje;
import org.example.Entidades.Usuario;
import org.example.Servicios.ChatServices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatController extends BaseController{

    private final List<Session> conectedUsers;

    public ChatController(Javalin app, List<Session> conectedUsers) {
        super(app);
        this.conectedUsers = conectedUsers;
        registerTemplates();
    }

    public void registerTemplates(){
        JavalinRenderer.register(new JavalinThymeleaf(), ".html");
    }

    @Override
    public void aplicarRutas() {
/*
        app.before("/chats", ctx -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null) {
                ctx.redirect("/user/login");
            }
        });
 */
        app.get("/chats", ctx -> {
            List<Chat> chats = ChatServices.getInstance().getChats();
            Map<String, Object> model = new HashMap<>();
            model.put("chats", chats);
            ctx.render("/public/templates/listar-chats.html", model);
        });
/*
        app.before("/chat/:id", ctx -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null) {
                ctx.redirect("/user/login");
            }
        });
*/
        app.get("/chat/{id}", ctx -> {
            Usuario usuario = ctx.sessionAttribute("username");
            int id = Integer.parseInt(ctx.pathParam("id"));
            Chat chat = ChatServices.getInstance().getChat(id);
            if (!usuario.getUsername().equalsIgnoreCase(chat.getUsuario())) {
                chat.setDestinatario(usuario.getUsername());
            }
            Map<String, Object> model = new HashMap<>();
            model.put("chat", chat);
            ctx.render("/public/templates/chat.html", model);
        });

        app.post("/chat/crear", ctx -> {
            String usuario = ctx.formParam("usuario");
            Chat chat = new Chat(ChatServices.getId(), usuario, "");
            ChatServices.getInstance().addChat(chat);
            ctx.redirect("/chat/" + chat.getId());
        });

        app.ws("/chat/{id}", ws -> {
            ws.onConnect(ctx -> {
                conectedUsers.add(ctx.session);
                List<Mensaje> mensajes = ChatServices.getInstance().getChat(Integer.parseInt(ctx.pathParam("id"))).getMensajes();
                for (Mensaje m : mensajes) {
                    ctx.session.getRemote().sendString(m.getUsuario() + ": " + m.getMensaje());
                }
            });

            ws.onMessage(ctx -> {
                Usuario usuario = ctx.sessionAttribute("username");
                String mensaje = ctx.message();
                Mensaje m = new Mensaje(usuario.getUsername(), mensaje);
                ChatServices.getInstance().getChat(Integer.parseInt(ctx.pathParam("id"))).getMensajes().add(m);
                for (Session s : conectedUsers) {
                    s.getRemote().sendString(m.getUsuario() + ": " + m.getMensaje());
                }
            });

            ws.onBinaryMessage(ctx -> {
                System.out.println("Jorgen von Strangle");
            });

            ws.onClose(ctx -> {
                conectedUsers.remove(ctx.session);
            });

            ws.onError(ctx -> {
                System.out.println("Error en WS");
            });
        });
    }
}

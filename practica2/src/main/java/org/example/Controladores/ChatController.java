package org.example.Controladores;
import io.javalin.Javalin;
import io.javalin.http.sse.SseClient;
import org.eclipse.jetty.websocket.api.Session;
import org.example.Entidades.Usuario;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatController  extends BaseController{
    public static List<Session> usuariosConectados = new ArrayList<>();
    public static List<SseClient> listaSseUsuario = new ArrayList<>();
    public ChatController(Javalin app) {
        super(app);
    }

    public void aplicarRutas() {
        app.get("/enviarMensaje", ctx -> {
            System.out.println("Se APLICA EN EL CHAT");
            Usuario usuario = ctx.sessionAttribute("username");
            String mensaje = ctx.queryParam("mensaje");
            enviarMensajeAClientesConectados(mensaje, usuario);
            ctx.result("Enviando mensaje: "+mensaje);
        });

        /**
         * Filtro para activarse antes de la llamadas al contexto.
         */
        app.wsBefore("/mensajeServidor", wsHandler -> {
            System.out.println("Filtro para WS antes de la llamada ws");
            //ejecutar cualquier evento antes...
        });

        /**
         * Definición del WS en Javalin en contexto
         */
        app.ws("/mensajeServidor", ws -> {
            System.out.println("Se APLICA EN EL CHAT");

            ws.onConnect(ctx -> {
                Usuario usuario = ctx.sessionAttribute("username");
                if (usuario != null) {
                    System.out.println("Conexión Iniciada - "+usuario.getUsername());
                }
                System.out.println("Conexión Iniciada - "+ctx.getSessionId());
                usuariosConectados.add(ctx.session);
            });

//            ws.onMessage(ctx -> {
//                //Puedo leer los header, parametros entre otros.
//                ctx.headerMap();
//                ctx.pathParamMap();
//                ctx.queryParamMap();
//                //
//                System.out.println("Mensaje Recibido de "+ctx.getSessionId()+" ====== ");
//                System.out.println("Mensaje: "+ctx.message());
//                System.out.println("================================");
//                //
//                enviarMensajeAClientesConectados(ctx.message(), ctx.sessionAttribute("username"));
//            });
//
//            ws.onBinaryMessage(ctx -> {
//                System.out.println("Mensaje Recibido Binario "+ctx.getSessionId()+" ====== ");
//                System.out.println("Mensaje: "+ctx.data().length);
//                System.out.println("================================");
//            });

            ws.onClose(ctx -> {
                Usuario usuario = ctx.sessionAttribute("username");
                if (usuario != null) {
                    System.out.println("Conexión Cerrada -- "+usuario.getUsername());
                }
                System.out.println("Conexión Cerrada - "+ctx.getSessionId());
                usuariosConectados.remove(ctx.session);
            });

            ws.onError(ctx -> {
                System.out.println("Ocurrió un error en el WS");
            });
        });

        /**
         * Filtro para activarse despues de la llamadas al contexto.
         */
        app.wsAfter("/mensajeServidor", wsHandler -> {
            System.out.println("Se APLICA EN EL CHAT");
            System.out.println("Filtro para WS despues de la llamada al WS");
            //ejecutar cualquier evento antes...
        });

    }
    public static void enviarMensajeAClientesConectados(String mensaje, Usuario remitente) {
        for (Session sesionConectada : usuariosConectados) {
//            try {
//                sesionConectada.getRemote().sendString(p(mensaje).withClass(remitente.getUsername()).render());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }




//==============================================================================
}

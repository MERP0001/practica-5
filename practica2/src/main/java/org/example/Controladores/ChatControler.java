package org.example.Controladores;
import io.javalin.Javalin;
import io.javalin.http.sse.SseClient;
import io.javalin.websocket.WsConnectContext;
import org.example.Entidades.Usuario;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatControler  extends BaseController{
    public static List<WsConnectContext> usuariosConectados = new ArrayList<>();
    public static List<SseClient> listaSseUsuario = new ArrayList<>();

    public ChatControler(Javalin app) {
        super(app);
    }

    @Override
    public void aplicarRutas() {
        app.get("/enviarMensaje", ctx -> {
            ctx.sessionAttribute("usuario");
            System.out.println("Se APLICA EN EL CHAT");
            String mensaje = ctx.queryParam("mensaje");
            enviarMensajeAClientesConectados(mensaje, "rojo");
            ctx.result("Enviando mensaje: "+mensaje);
            ctx.render("/templates/ejemploWebSocket.html");
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

            ws.onConnect(ctx -> {
                Usuario usuario = ctx.sessionAttribute("username");
                if(usuario != null){
                    System.out.println("Conexión Iniciada - "+ usuario.getUsername());
                }
                //System.out.println("Conexión Iniciada - "+ usuario.getUsername());
               // usuariosConectados.add(ctx.session);
            });

            ws.onMessage(ctx -> {
                //Puedo leer los header, parametros entre otros.
                ctx.headerMap();
                ctx.pathParamMap();
                ctx.queryParamMap();
                //
                System.out.println("Mensaje Recibido de "+ctx.getSessionId()+" ====== ");
                System.out.println("Mensaje: "+ctx.message());
                System.out.println("================================");
                //
                enviarMensajeAClientesConectados(ctx.message(), "azul");
            });

            ws.onBinaryMessage(ctx -> {
                System.out.println("Mensaje Recibido Binario "+ctx.getSessionId()+" ====== ");
                System.out.println("Mensaje: "+ctx.data().length);
                System.out.println("================================");
            });

            ws.onClose(ctx -> {
                Usuario usuario = ctx.sessionAttribute("username");
                if(usuario != null){
                    System.out.println("Conexión Cerrada - "+ usuario.getUsername());
                }
                System.out.println("Conexión Cerrada - "+ ctx.getSessionId());
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

        //Caso de los Server-sent Events.
        app.sse("/evento-servidor", sseClient -> {
            System.out.println("Se APLICA EN EL CHAT");
            System.out.println("Agregando cliente para evento del servidor: ");
            sseClient.keepAlive();
            sseClient.sendEvent("conectado","Conectando ");
            listaSseUsuario.add(sseClient);
            sseClient.onClose(() -> {
                listaSseUsuario.remove(sseClient);
            });
        });
        new Thread(() -> {
            while(true){
                List<SseClient> listaTmp = new CopyOnWriteArrayList<>(listaSseUsuario);
                listaTmp.forEach(sseClient -> {
                    System.out.println("Enviando informacion...");
                    sseClient.sendEvent("ping", ""+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    private static void enviarMensajeAClientesConectados(String mensaje, String color) {
        for (WsConnectContext ctx : usuariosConectados) {
            try {
                ctx.send(mensaje);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




//===================================================================================================
}

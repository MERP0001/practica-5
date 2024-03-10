package org.example.Servicios;

import org.h2.tools.Server;

import java.sql.SQLException;

public class StartServices {
    private static StartServices instance = null;

    private StartServices() {
    }

    public static StartServices getInstance() {
        if (instance == null) {
            instance = new StartServices();
        }
        return instance;
    }

    public void startDb() {
        try {
            Server.createTcpServer("-tcpPort",
                    "9092",
                    "-tcpAllowOthers",
                    "-tcpDaemon",
                    "-ifNotExists").start();
            String status = Server.createWebServer("-trace", "-webPort", "0").start().getStatus();
            System.out.println("Status Web: "+status);
        }catch (SQLException ex){
            System.out.println("Problema con la base de datos: "+ex.getMessage());
        }
    }
}

package org.example.Servicios;

import org.example.Entidades.Foto;

public class FotoServices extends DataBaseServices<Foto>{
    private static FotoServices instance = null;

    private FotoServices() {
        super(Foto.class);
    }

    public static FotoServices getInstance() {
        if (instance == null) {
            instance = new FotoServices();
        }
        return instance;
    }
}

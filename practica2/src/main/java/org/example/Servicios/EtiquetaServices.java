package org.example.Servicios;

import org.example.Entidades.Etiqueta;

public class EtiquetaServices extends DataBaseServices<Etiqueta>{
    private static EtiquetaServices instance = null;

    private EtiquetaServices() {
        super(Etiqueta.class);
    }

    public static EtiquetaServices getInstance() {
        if (instance == null) {
            instance = new EtiquetaServices();
        }
        return instance;
    }
}

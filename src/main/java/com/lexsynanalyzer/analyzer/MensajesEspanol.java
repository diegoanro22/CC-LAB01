package com.lexsynanalyzer.analyzer;

public final class MensajesEspanol {

    public static String traducirLexico(String simbolo, String mensajeOriginal) {
        if (simbolo != null && simbolo.startsWith("\"")) {
            return "Cadena de texto sin cerrar: falta la comilla ('\"') que cierre el literal.";
        }
        if (simbolo == null || simbolo.isEmpty()) {
            return "Carácter no reconocido por el lenguaje.";
        }
        return "Carácter no válido: '" + simbolo + "'. No pertenece a ningún token reconocido por el lenguaje.";
    }

    // TODO(B - pipeline sintáctico): agregar aquí traducirSintactico(String simbolo, String mensajeOriginal)

    private MensajesEspanol() {
    }
}

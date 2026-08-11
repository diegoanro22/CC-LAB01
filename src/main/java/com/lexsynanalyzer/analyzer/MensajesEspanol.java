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

    public static String traducirSintactico(String simbolo, String mensajeOriginal) {
        String mensaje = mensajeOriginal == null ? "" : mensajeOriginal;
        String token = mostrarSimbolo(simbolo);

        if (mensaje.startsWith("missing ")) {
            String faltante = mensaje.substring("missing ".length());
            int indiceAt = faltante.indexOf(" at ");
            if (indiceAt >= 0) {
                faltante = faltante.substring(0, indiceAt);
            }
            return "Falta " + entreComillas(faltante)
                    + " antes de " + token + ".";
        }
        if (mensaje.startsWith("extraneous input ")) {
            return "El símbolo " + token + " no es válido en esta posición; elimínelo o corrija la estructura.";
        }
        if (mensaje.startsWith("mismatched input ")) {
            String esperado = extraerEsperado(mensaje);
            return "Se encontró " + token + " donde la sintaxis no lo permite"
                    + (esperado.isEmpty() ? "." : "; se esperaba " + esperado + ".");
        }
        if (mensaje.startsWith("no viable alternative")) {
            return "No se puede interpretar la estructura que comienza o termina en " + token + ".";
        }
        return "Error de sintaxis cerca de " + token + ".";
    }

    private static String extraerEsperado(String mensaje) {
        int indice = mensaje.indexOf("expecting ");
        return indice < 0 ? "" : entreComillas(mensaje.substring(indice + "expecting ".length()));
    }

    private static String entreComillas(String valor) {
        return valor.replace("<EOF>", "el final del archivo");
    }

    private static String mostrarSimbolo(String simbolo) {
        if (simbolo == null || simbolo.isBlank() || "<EOF>".equals(simbolo)) {
            return "el final del archivo";
        }
        return "'" + simbolo + "'";
    }

    private MensajesEspanol() {
    }
}

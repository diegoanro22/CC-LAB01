package com.lexsynanalyzer.analyzer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LexSynAnalyzerSyntacticTest {

    @Test
    void detectaYTraduceErrorSintactico(@TempDir Path dir) throws IOException {
        List<AnalysisError> sintacticos = analizarSintacticos(dir, "let x = 5\nprint(x);");

        assertFalse(sintacticos.isEmpty(), "debería detectar el punto y coma faltante");
        assertTrue(sintacticos.getFirst().descripcion().contains("Falta")
                        || sintacticos.getFirst().descripcion().contains("Se encontró"),
                "debería mostrar un mensaje comprensible en español");
        assertFalse(sintacticos.getFirst().descripcion().contains(" at "),
                "no debería mostrar fragmentos técnicos en inglés de ANTLR");
        assertEquals(2, sintacticos.getFirst().linea(),
                "ANTLR reporta el punto donde encontró el siguiente token inesperado");
    }

    @Test
    void noMarcaComoSintacticoCodigoValido(@TempDir Path dir) throws IOException {
        List<AnalysisError> sintacticos = analizarSintacticos(dir, "let x: integer = 5;\nprint(x);");

        assertTrue(sintacticos.isEmpty(), "no debería reportar errores sintácticos en código válido");
    }

    private List<AnalysisError> analizarSintacticos(Path dir, String contenido) throws IOException {
        Path archivo = dir.resolve("caso.cps");
        Files.writeString(archivo, contenido);
        return LexSynAnalyzer.analyze(archivo.toFile()).errores().stream()
                .filter(e -> e.tipo() == TipoError.SINTACTICO)
                .toList();
    }
}

package com.lexsynanalyzer.analyzer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LexSynAnalyzerLexicalTest {

    @Test
    void detectaCaracterNoValido(@TempDir Path dir) throws IOException {
        List<AnalysisError> lexicos = analizarLexicos(dir, "let x = 5 @ 2;");
        assertFalse(lexicos.isEmpty(), "debería detectar el carácter '@' como error léxico");
    }

    @Test
    void detectaStringSinCerrar(@TempDir Path dir) throws IOException {
        List<AnalysisError> lexicos = analizarLexicos(dir, "let s = \"hola;");
        assertFalse(lexicos.isEmpty(), "debería detectar el string sin cerrar como error léxico");
    }

    @Test
    void sinErroresLexicosEnCodigoValido(@TempDir Path dir) throws IOException {
        List<AnalysisError> lexicos = analizarLexicos(dir, "let x = 5;");
        assertTrue(lexicos.isEmpty(), "no debería reportar errores léxicos en código válido");
    }

    private List<AnalysisError> analizarLexicos(Path dir, String contenido) throws IOException {
        Path archivo = dir.resolve("caso.cps");
        Files.writeString(archivo, contenido);
        AnalysisResult resultado = LexSynAnalyzer.analyze(archivo.toFile());
        return resultado.errores().stream()
                .filter(e -> e.tipo() == TipoError.LEXICO)
                .toList();
    }
}

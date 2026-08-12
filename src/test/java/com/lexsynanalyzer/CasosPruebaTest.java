package com.lexsynanalyzer;

import com.lexsynanalyzer.analyzer.AnalysisError;
import com.lexsynanalyzer.analyzer.AnalysisResult;
import com.lexsynanalyzer.analyzer.LexSynAnalyzer;
import com.lexsynanalyzer.analyzer.TipoError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CasosPruebaTest {

    private static final Path DIRECTORIO_CASOS = Paths.get("src", "test", "resources", "casos");

    private File obtenerArchivo(String nombre) {
        File archivo = DIRECTORIO_CASOS.resolve(nombre).toFile();
        assertTrue(archivo.exists(), "El archivo de prueba debe existir: " + archivo.getAbsolutePath());
        return archivo;
    }

    @Test
    @DisplayName("Criterio 1: baja_sin_errores.cps debe compilar sin errores")
    void testBajaSinErrores() throws IOException {
        AnalysisResult result = LexSynAnalyzer.analyze(obtenerArchivo("baja_sin_errores.cps"));
        assertTrue(result.exitoso(), "No se esperaban errores en baja_sin_errores.cps, pero se encontraron: " + result.errores());
        assertEquals(0, result.errores().size());
    }

    @Test
    @DisplayName("Criterio 2: baja_errores_lexicos.cps debe tener >= 3 errores léxicos y 0 sintácticos")
    void testBajaErroresLexicos() throws IOException {
        AnalysisResult result = LexSynAnalyzer.analyze(obtenerArchivo("baja_errores_lexicos.cps"));
        assertFalse(result.exitoso());

        List<AnalysisError> lexicos = result.errores().stream()
                .filter(e -> e.tipo() == TipoError.LEXICO)
                .toList();
        List<AnalysisError> sintacticos = result.errores().stream()
                .filter(e -> e.tipo() == TipoError.SINTACTICO)
                .toList();

        assertTrue(lexicos.size() >= 3, "Se esperaban al menos 3 errores léxicos, se obtuvieron: " + lexicos.size());
        assertEquals(0, sintacticos.size(), "Se esperaban 0 errores sintácticos, se obtuvieron: " + sintacticos.size());
    }

    @Test
    @DisplayName("Criterio 3: baja_errores_sintacticos.cps debe tener >= 3 errores sintácticos y 0 léxicos")
    void testBajaErroresSintacticos() throws IOException {
        AnalysisResult result = LexSynAnalyzer.analyze(obtenerArchivo("baja_errores_sintacticos.cps"));
        assertFalse(result.exitoso());

        List<AnalysisError> lexicos = result.errores().stream()
                .filter(e -> e.tipo() == TipoError.LEXICO)
                .toList();
        List<AnalysisError> sintacticos = result.errores().stream()
                .filter(e -> e.tipo() == TipoError.SINTACTICO)
                .toList();

        assertEquals(0, lexicos.size(), "Se esperaban 0 errores léxicos, se obtuvieron: " + lexicos.size());
        assertTrue(sintacticos.size() >= 3, "Se esperaban al menos 3 errores sintácticos, se obtuvieron: " + sintacticos.size());
    }

    @Test
    @DisplayName("Criterio 4: baja_errores_mixto.cps debe tener >= 2 errores léxicos y >= 2 sintácticos")
    void testBajaErroresMixto() throws IOException {
        AnalysisResult result = LexSynAnalyzer.analyze(obtenerArchivo("baja_errores_mixto.cps"));
        assertFalse(result.exitoso());

        long countLexicos = result.errores().stream().filter(e -> e.tipo() == TipoError.LEXICO).count();
        long countSintacticos = result.errores().stream().filter(e -> e.tipo() == TipoError.SINTACTICO).count();

        assertTrue(countLexicos >= 2, "Se esperaban al menos 2 errores léxicos, se obtuvieron: " + countLexicos);
        assertTrue(countSintacticos >= 2, "Se esperaban al menos 2 errores sintácticos, se obtuvieron: " + countSintacticos);
    }

    @Test
    @DisplayName("Criterio 5: media_sin_errores.cps debe compilar sin errores")
    void testMediaSinErrores() throws IOException {
        AnalysisResult result = LexSynAnalyzer.analyze(obtenerArchivo("media_sin_errores.cps"));
        assertTrue(result.exitoso(), "No se esperaban errores en media_sin_errores.cps, pero se encontraron: " + result.errores());
        assertEquals(0, result.errores().size());
    }

    @Test
    @DisplayName("Criterio 6: media_errores_lexicos.cps debe tener >= 3 errores léxicos y 0 sintácticos")
    void testMediaErroresLexicos() throws IOException {
        AnalysisResult result = LexSynAnalyzer.analyze(obtenerArchivo("media_errores_lexicos.cps"));
        assertFalse(result.exitoso());

        List<AnalysisError> lexicos = result.errores().stream()
                .filter(e -> e.tipo() == TipoError.LEXICO)
                .toList();
        List<AnalysisError> sintacticos = result.errores().stream()
                .filter(e -> e.tipo() == TipoError.SINTACTICO)
                .toList();

        assertTrue(lexicos.size() >= 3, "Se esperaban al menos 3 errores léxicos, se obtuvieron: " + lexicos.size());
        assertEquals(0, sintacticos.size(), "Se esperaban 0 errores sintácticos, se obtuvieron: " + sintacticos.size());
    }

    @Test
    @DisplayName("Criterio 7: media_errores_sintacticos.cps debe tener >= 3 errores sintácticos y 0 léxicos")
    void testMediaErroresSintacticos() throws IOException {
        AnalysisResult result = LexSynAnalyzer.analyze(obtenerArchivo("media_errores_sintacticos.cps"));
        assertFalse(result.exitoso());

        List<AnalysisError> lexicos = result.errores().stream()
                .filter(e -> e.tipo() == TipoError.LEXICO)
                .toList();
        List<AnalysisError> sintacticos = result.errores().stream()
                .filter(e -> e.tipo() == TipoError.SINTACTICO)
                .toList();

        assertEquals(0, lexicos.size(), "Se esperaban 0 errores léxicos, se obtuvieron: " + lexicos.size());
        assertTrue(sintacticos.size() >= 3, "Se esperaban al menos 3 errores sintácticos, se obtuvieron: " + sintacticos.size());
    }

    @Test
    @DisplayName("Criterio 8: media_errores_mixto.cps debe tener >= 2 errores léxicos y >= 2 sintácticos")
    void testMediaErroresMixto() throws IOException {
        AnalysisResult result = LexSynAnalyzer.analyze(obtenerArchivo("media_errores_mixto.cps"));
        assertFalse(result.exitoso());

        long countLexicos = result.errores().stream().filter(e -> e.tipo() == TipoError.LEXICO).count();
        long countSintacticos = result.errores().stream().filter(e -> e.tipo() == TipoError.SINTACTICO).count();

        assertTrue(countLexicos >= 2, "Se esperaban al menos 2 errores léxicos, se obtuvieron: " + countLexicos);
        assertTrue(countSintacticos >= 2, "Se esperaban al menos 2 errores sintácticos, se obtuvieron: " + countSintacticos);
    }
}

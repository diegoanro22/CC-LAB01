package com.lexsynanalyzer.analyzer;

import com.lexsynanalyzer.parser.LexSynAnalyzerLexer;
import com.lexsynanalyzer.parser.LexSynAnalyzerParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class LexSynAnalyzer {

    public static AnalysisResult analyze(File archivo) throws IOException {
        CharStream input = CharStreams.fromPath(archivo.toPath());
        List<AnalysisError> errores = new ArrayList<>();

        LexSynAnalyzerLexer lexer = new LexSynAnalyzerLexer(input);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new CapturingErrorListener(TipoError.LEXICO, errores, MensajesEspanol::traducirLexico));

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LexSynAnalyzerParser parser = new LexSynAnalyzerParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new CapturingErrorListener(
                TipoError.SINTACTICO, errores, MensajesEspanol::traducirSintactico));

        parser.program();

        return new AnalysisResult(sinDuplicadosYOrdenados(errores));
    }

    private static List<AnalysisError> sinDuplicadosYOrdenados(List<AnalysisError> errores) {
        Map<String, AnalysisError> unicos = new LinkedHashMap<>();
        for (AnalysisError error : errores) {
            String clave = error.tipo() + ":" + error.linea() + ":" + error.columna();
            unicos.putIfAbsent(clave, error);
        }

        return unicos.values().stream()
                .sorted(Comparator.comparingInt(AnalysisError::linea)
                        .thenComparingInt(AnalysisError::columna)
                        .thenComparing(AnalysisError::tipo))
                .toList();
    }

    private LexSynAnalyzer() {
    }
}

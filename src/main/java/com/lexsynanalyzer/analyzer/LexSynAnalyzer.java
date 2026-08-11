package com.lexsynanalyzer.analyzer;

import com.lexsynanalyzer.parser.LexSynAnalyzerLexer;
import com.lexsynanalyzer.parser.LexSynAnalyzerParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        // TODO(B - pipeline sintáctico): addErrorListener(...) con el listener que traduce
        // errores sintácticos a español y los agrega a `errores` con TipoError.SINTACTICO.
        // También aplica aquí la deduplicación anti-ruido antes de devolver el resultado.

        parser.program();

        return new AnalysisResult(errores);
    }

    private LexSynAnalyzer() {
    }
}

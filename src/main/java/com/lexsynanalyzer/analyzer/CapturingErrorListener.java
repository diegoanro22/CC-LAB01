package com.lexsynanalyzer.analyzer;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CapturingErrorListener extends BaseErrorListener {

    private static final Pattern SIMBOLO_EN_MENSAJE = Pattern.compile("'(.*)'");

    private final TipoError tipo;
    private final List<AnalysisError> errores;
    private final BiFunction<String, String, String> traductor;

    public CapturingErrorListener(TipoError tipo, List<AnalysisError> errores, BiFunction<String, String, String> traductor) {
        this.tipo = tipo;
        this.errores = errores;
        this.traductor = traductor;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                             int charPositionInLine, String msg, RecognitionException e) {
        String simbolo = extraerSimbolo(offendingSymbol, msg);
        String descripcion = traductor.apply(simbolo, msg);
        errores.add(new AnalysisError(tipo, line, charPositionInLine + 1, simbolo, descripcion));
    }

    private static String extraerSimbolo(Object offendingSymbol, String msg) {
        if (offendingSymbol instanceof Token token) {
            return token.getText();
        }
        if (msg == null) {
            return "";
        }
        Matcher matcher = SIMBOLO_EN_MENSAJE.matcher(msg);
        return matcher.find() ? matcher.group(1) : msg;
    }
}

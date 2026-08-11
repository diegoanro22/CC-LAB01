package com.lexsynanalyzer.analyzer;

public record AnalysisError(
        TipoError tipo,
        int linea,
        int columna,
        String simbolo,
        String descripcion
) {
}

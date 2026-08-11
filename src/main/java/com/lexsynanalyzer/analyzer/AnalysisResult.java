package com.lexsynanalyzer.analyzer;

import java.util.List;

public record AnalysisResult(List<AnalysisError> errores) {

    public boolean exitoso() {
        return errores.isEmpty();
    }
}

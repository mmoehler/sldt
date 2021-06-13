package com.mmoehler.sldt.analysis;

public class AnalysisException extends RuntimeException {
    private final String rawResult;

    public AnalysisException(final String message, final String rawResult) {
        super(message);
        this.rawResult = rawResult;
    }

    public String getRawResult() {
        return rawResult;
    }
}

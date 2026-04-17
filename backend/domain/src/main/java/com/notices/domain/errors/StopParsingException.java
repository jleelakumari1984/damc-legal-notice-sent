package com.notices.domain.errors;

/** Thrown from within a SAX sheet handler to abort parsing early when a row limit is reached. */
public class StopParsingException extends RuntimeException {

    public StopParsingException() {
        // suppress stacktrace — used for flow control, not an error
        super(null, null, true, false);
    }
}

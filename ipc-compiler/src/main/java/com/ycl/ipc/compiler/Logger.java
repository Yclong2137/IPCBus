package com.ycl.ipc.compiler;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class Logger {
    private Messager msg;

    public Logger(Messager messager) {
        msg = messager;
    }

    /**
     * Print info log.
     */
    public void info(CharSequence info) {
        msg.printMessage(Diagnostic.Kind.NOTE, Consts.PREFIX_OF_LOGGER + info);

    }

    public void error(CharSequence error) {
        msg.printMessage(Diagnostic.Kind.ERROR, Consts.PREFIX_OF_LOGGER + "An exception is encountered, [" + error + "]");

    }

    public void error(Throwable error) {
        if (null != error) {
            msg.printMessage(Diagnostic.Kind.ERROR, Consts.PREFIX_OF_LOGGER + "An exception is encountered, [" + error.getMessage() + "]" + "\n" + formatStackTrace(error.getStackTrace()));
        }
    }

    public void warning(CharSequence warning) {
        msg.printMessage(Diagnostic.Kind.WARNING, Consts.PREFIX_OF_LOGGER + warning);

    }

    private String formatStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            sb.append("    at ").append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}

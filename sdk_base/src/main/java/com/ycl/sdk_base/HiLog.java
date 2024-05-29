package com.ycl.sdk_base;

import android.util.Log;

import java.util.Objects;

/**
 * HiLog
 * Created by Yclong on 2024/5/14.
 **/
public final class HiLog {

    private static final int CALL_STACK_INDEX = 4;
    /**
     * 调用链深度
     */
    private static final int CALL_STACK_DEPTH = 2;


    public static void i(String tag, String msg, int depth) {
        Log.i(tag, getCallerMethodChainResult(depth, " --> ") + msg);
    }

    public static void d(String tag, String msg) {
        Log.d(tag, getCallerMethodChainResult() + " --> " + msg);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, getCallerMethodChainResult() + " --> " + msg);
    }

    /**
     * 获取方法调用链
     *
     * @param stackDepth 调用栈深度
     * @return 调用链结果
     */
    private static String getCallerMethodChainResult(int stackDepth, String suffix) {
        if (stackDepth <= 0) return "";
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        //调用栈为空或本身则不打印
        if (stackTrace == null || stackTrace.length <= CALL_STACK_INDEX) return "";
        StringBuilder builder = new StringBuilder();
        String lastClassName = null;
        for (int i = Math.min(stackTrace.length - 1, CALL_STACK_INDEX + stackDepth - 1); i >= CALL_STACK_INDEX; i--) {
            StackTraceElement element = stackTrace[i];
            if (element == null) continue;
            String simpleClassName = getSimpleClassName(element.getFileName(), element.getClassName());
            //减少日志打印
            if (!Objects.equals(lastClassName, simpleClassName)) {
                builder.append((lastClassName = simpleClassName));
                builder.append(".");
            }
            builder.append(element.getMethodName())
                    .append("(")
                    .append(element.getLineNumber())
                    .append(")");
            if (i != CALL_STACK_INDEX) {
                builder.append("->");
            }
        }
        if (suffix != null) {
            builder.append(suffix);
        }
        return builder.toString();
    }

    /**
     * 获取方法调用链
     *
     * @return 调用链结果
     */
    private static String getCallerMethodChainResult() {
        return getCallerMethodChainResult(CALL_STACK_DEPTH, " --> ");
    }

    private static String getSimpleClassName(String fileName, String className) {
        if (fileName == null && className == null) {
            return "";
        }
        if (fileName == null) {
            return className;
        }
        fileName = getFileNameWithoutExt(fileName);

        if (className == null) {
            return fileName;
        }

        int index = className.indexOf(fileName);
        if (index > 0) {
            return className.substring(index);
        }
        return "";
    }

    /**
     * 获取文件名
     *
     * @param fileName
     * @return
     */
    private static String getFileNameWithoutExt(String fileName) {
        if (fileName == null) return "";
        int index = fileName.indexOf(".");
        if (index > 0) {
            return fileName.substring(0, index);
        }
        return "";
    }


}

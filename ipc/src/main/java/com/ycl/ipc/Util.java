package com.ycl.ipc;

import androidx.annotation.NonNull;

public final class Util {

    public static Object defaultValue(@NonNull Class<?> returnType) {
        if (boolean.class == returnType || Boolean.class == returnType) {
            return false;
        }
        if (void.class == returnType || Void.class == returnType) {
            return null;
        }
        if (returnType.isPrimitive() || Number.class.isAssignableFrom(returnType)) {
            // TODO: 2024/2/1 待商榷
            return -0xff;
        }
        return null;
    }

}

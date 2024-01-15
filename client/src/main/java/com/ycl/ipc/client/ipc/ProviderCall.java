package com.ycl.ipc.client.ipc;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;

public class ProviderCall {

    public static Bundle call(Context context, String authority, String methodName, String arg, Bundle bundle) {
        return call(authority, context, methodName, arg, bundle);
    }

    public static Bundle call(String authority, Context context, String method, String arg, Bundle bundle) {
        Uri uri = Uri.parse("content://" + authority);

        return ContentProviderCompat.call(context, uri, method, arg, bundle);
    }

    public static final class Builder {

        private Context context;

        private Bundle bundle = new Bundle();

        private String method;
        private String auth;
        private String arg;

        public Builder(Context context, String auth) {
            this.context = context;
            this.auth = "com.ycl.ipc.server." + auth;
        }

        public Builder methodName(String name) {
            this.method = name;
            return this;
        }

        public Builder arg(String arg) {
            this.arg = arg;
            return this;
        }

        public Builder addArg(String key, Object value) {
            if (value != null) {
                if (value instanceof Boolean) {
                    bundle.putBoolean(key, (Boolean) value);
                } else if (value instanceof Integer) {
                    bundle.putInt(key, (Integer) value);
                } else if (value instanceof String) {
                    bundle.putString(key, (String) value);
                } else if (value instanceof Serializable) {
                    bundle.putSerializable(key, (Serializable) value);
                } else if (value instanceof Bundle) {
                    bundle.putBundle(key, (Bundle) value);
                } else if (value instanceof Parcelable) {
                    bundle.putParcelable(key, (Parcelable) value);
                } else {
                    throw new IllegalArgumentException("Unknown type " + value.getClass() + " in Bundle.");
                }
            }
            return this;
        }

        public Bundle call() {
            return ProviderCall.call(auth, context, method, arg, bundle);
        }

    }

}

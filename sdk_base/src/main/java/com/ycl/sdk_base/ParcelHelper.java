package com.ycl.sdk_base;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ParcelHelper
 * Created by Yclong on 2024/5/21.
 **/
public final class ParcelHelper {

    public static <T extends Parcelable> T copy(T src) {
        Parcel parcel = null;
        try {
            parcel = Parcel.obtain();
            parcel.writeParcelable(src, 0);
            parcel.setDataPosition(0);
            return parcel.readParcelable(src.getClass().getClassLoader());
        } finally {
            if (null != parcel) {
                parcel.recycle();
            }
        }
    }

}

package ua.tonkoshkur.tetris.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

import ua.tonkoshkur.tetris.R;

public class FetchColor {

    public static int fetchAccentColor(Context context) {
        TypedValue typedValue = new TypedValue();

        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[] { androidx.appcompat.R.attr.colorPrimary });
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

}

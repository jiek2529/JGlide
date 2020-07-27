package com.jiek.jglide;

import android.content.Context;

public class JGlide {
    private volatile static JGlide jGlide;

    public static JRequest with(Context context) {
        return new JRequest(context);
    }


}

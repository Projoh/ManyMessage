package com.projoh.android.manymessage;

/**
 * Created by Mohamed on 5/22/2016.
 */
public enum ModelObject {

    CONTACTS(1, R.layout.view_contactsfinal),
    CUSTOM(2, R.layout.view_customization);


    private int mTitleResId;
    private int mLayoutResId;

    ModelObject(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }
}

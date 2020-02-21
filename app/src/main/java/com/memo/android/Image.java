package com.memo.android;

import android.graphics.Bitmap;
import android.provider.BaseColumns;

public class Image implements BaseColumns {

    public static final String TABLE_NAME = "Image";
    public static final String COLUMN_NAME_TITLE = "title";
    public static final String MEMO_NO = "memo_id";

    public String getImage_route() {
        return image_route;
    }

    public Bitmap getImage_bitmap() {
        return image_bitmap;
    }

    private String image_route;

    public void setImage_route(String image_route) {
        this.image_route = image_route;
    }

    public void setImage_bitmap(Bitmap image_bitmap) {
        this.image_bitmap = image_bitmap;
    }

    private Bitmap image_bitmap;
}

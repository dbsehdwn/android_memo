package com.memo.android;

import android.provider.BaseColumns;

import java.util.ArrayList;

public  class Memo implements BaseColumns {


    public static final String NO = "_ID";
    public static final String TABLE_NAME = "Memo";
    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_NAME_MAIN = "main";

    public String getTitle() {
        return title;
    }

    public String getMain() {
        return main;
    }

    private String title;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMain(String main) {
        this.main = main;
    }

    private String main;

    public ArrayList<String> getImagelist() {
        return imagelist;
    }

    public void setImagelist(ArrayList<String> imagelist) {
        this.imagelist = imagelist;
    }

    private ArrayList<String> imagelist;


    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    private int no;

}
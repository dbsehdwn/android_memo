package com.memo.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MemolistAdapter adapter = new MemolistAdapter(this);

        MemoDatabaseHelper dbHelper = new MemoDatabaseHelper(this);
        ImageDatabaseHelper imagedbhelper = new ImageDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SQLiteDatabase imagedb = imagedbhelper.getWritableDatabase();
        String[] projection = {
                Memo._ID,
                Memo.COLUMN_NAME_TITLE,
                Memo.COLUMN_NAME_MAIN
        };
        String[] imagelist = {
                Image._ID,
                Image.COLUMN_NAME_TITLE,
                Image.MEMO_NO
        };

        Cursor cursor = db.query(
                Memo.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,         // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null              // The sort order
        );

        while(cursor.moveToNext()) {
            String selection = Image.MEMO_NO + " LIKE ? ";
            String[] selectionArgs ={cursor.getInt(0)+""};
            Cursor cursor1 = imagedb.query(
                    Image.TABLE_NAME,   // The table to query
                    imagelist,             // The array of columns to return (pass null to get all)
                    selection,              // The columns for the WHERE clause
                    selectionArgs,         // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    null              // The sort order
            );
            int no = cursor.getInt(0);
            String title = cursor.getString(1);
            String main = cursor.getString(2);
            ArrayList<String> imglist = new ArrayList<>();
            while(cursor1.moveToNext()){
                imglist.add(cursor1.getString(1));
            }
            adapter.additem(no,title,main,imglist);
        }
        cursor.close();

        RecyclerView memolist = (RecyclerView)findViewById(R.id.memo_recyclerview);
        memolist.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        memolist.setLayoutManager(layoutManager);
        adapter.setListener(new MemolistAdapter.Listener(){

            public void onClick(int position){
                Intent intent = new Intent(MainActivity.this, MemoDetail.class);
                intent.putExtra("no",adapter.getNo(position));
                intent.putExtra("title",adapter.getTitle(position));
                intent.putExtra("main",adapter.getMain(position));
                intent.putExtra("imagelist",adapter.getImagelist(position));
                startActivity(intent);
            }
        });

    }
    public void write_memo(View v){
        ArrayList<String> img = new ArrayList<>();
        Intent intent  = new Intent(this,WriteMemo.class);
        intent.putExtra("imagelist",img);
        startActivity(intent);
    }
}

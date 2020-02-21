package com.memo.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MemoDetail extends AppCompatActivity {

    FileManager manager = new FileManager(this);

    String title,main;
    int no;
    ArrayList<String> imagelist = new ArrayList<>();
    ImagelistAdapter adapter = new ImagelistAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_detail);

        Intent intent = getIntent();

        TextView title_view = (TextView)findViewById(R.id.title);
        TextView main_view = (TextView)findViewById(R.id. main);

        title = intent.getStringExtra("title");
        main = intent.getStringExtra("main");
        no = intent.getIntExtra("no",0);
        imagelist = intent.getStringArrayListExtra("imagelist");


        title_view.setText(title);
        main_view.setText(main);

        if(!imagelist.isEmpty()){
            for(int i=0;imagelist.size()>i;i++){
                adapter.additem(imagelist.get(i),manager.getImage(imagelist.get(i)));
            }
        }


        RecyclerView imagelist = (RecyclerView)findViewById(R.id.image_recyclerview);
        imagelist.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        imagelist.setLayoutManager(layoutManager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        Intent intent = null;

        switch(item.getItemId())
        {
            case R.id.edit:
                intent = new Intent(MemoDetail.this, WriteMemo.class);
                intent.putExtra("no",no);
                intent.putExtra("title",title);
                intent.putExtra("main",main);
                intent.putExtra("imagelist",imagelist);
                startActivity(intent);
                break;
            case R.id.delete:
                MemoDatabaseHelper dbHelper = new MemoDatabaseHelper(this);
                ImageDatabaseHelper imagedbhelper = new ImageDatabaseHelper(this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                SQLiteDatabase imagedb = imagedbhelper.getWritableDatabase();
                // Define 'where' part of query.
                String selection = Memo.NO + " LIKE ?";
                String selection2 = Image.MEMO_NO + " LIKE ? ";
                // Specify arguments in placeholder order.
                String[] selectionArgs = { no+"" };
                // Issue SQL statement.
                db.delete(Memo.TABLE_NAME, selection, selectionArgs);
                imagedb.delete(Image.TABLE_NAME, selection2, selectionArgs);
                for(int i=0;adapter.getItemCount()>i;i++){
                    manager.DeleteImage(adapter.getImage_Name(i));
                }

                Toast.makeText(MemoDetail.this, "삭제되었습니다", Toast.LENGTH_SHORT).show();
                intent = new Intent(this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

        }


        return super.onOptionsItemSelected(item);
    }

}

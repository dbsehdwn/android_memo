package com.memo.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class WriteMemo extends AppCompatActivity {

    FileManager manager = new FileManager(this);

    EditText title_edit, main_edit;
    String title,main;
    int no;
    ImagelistAdapter adapter = new ImagelistAdapter();

    ArrayList<String> temp = new ArrayList<>();


    RecyclerView imagelist;
    LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);


    final String TAG = getClass().getSimpleName();

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_GET_PHOTO = 2;

    String mCurrentPhotoPath;
    Bitmap upload_img;
    File image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_memo);

        title_edit = (EditText)findViewById(R.id.title);
        main_edit = (EditText)findViewById(R.id.main);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        main = intent.getStringExtra("main");
        no = intent.getIntExtra("no",0);
        temp = intent.getStringArrayListExtra("imagelist");


        title_edit.setText(title);
        main_edit.setText(main);

        if(!temp.isEmpty()){
            for(int i=0;temp.size()>i;i++){
                adapter.additem(temp.get(i),manager.getImage(temp.get(i)));
            }
        }

        imagelist = (RecyclerView)findViewById(R.id.image_recyclerview);
        imagelist.setAdapter(adapter);
        imagelist.setLayoutManager(layoutManager);
        adapter.setListener(new ImagelistAdapter.Listener() {
            @Override
            public void onClick(final int position) {
                final List<String> ListItems = new ArrayList<>();
                ListItems.add("삭제하기");

                final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(WriteMemo.this);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        adapter.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position,adapter.getItemCount());
                    }
                });
                builder.show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.write_memo_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.memo_save :
                String edit_title = title_edit.getText().toString();
                String edit_main = main_edit.getText().toString();

                MemoDatabaseHelper dbHelper = new MemoDatabaseHelper(this);
                // Gets the data repository in write mode
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(Memo.COLUMN_NAME_TITLE, edit_title);
                values.put(Memo.COLUMN_NAME_MAIN, edit_main);

                String selection = Memo.NO + " LIKE ?";
                String[] selectionArgs = { no+"" };
                // Insert the new row, returning the primary key value of the new row
                if(db.update(Memo.TABLE_NAME, values, selection, selectionArgs)==0) {
                    no = (int)db.insert(Memo.TABLE_NAME, null, values);
                }

                ArrayList<String> list = new ArrayList<>();
                for(int i=0;adapter.getItemCount()>i;i++){
                    list.add(adapter.getImage_Name(i));
                    if(!temp.contains(adapter.getImage_Name(i))){
                        String path = adapter.getImage_Name(i);
                        Bitmap bitmap = adapter.getImage_Bitmap(i);
                        InsertImage(path);
                        manager.SaveImage(path,bitmap);
                    }
                }
                for(int i=0;temp.size()>i;i++){
                    if(!list.contains(temp.get(i))){
                        String path  = temp.get(i);
                        deletedb(path);
                        manager.DeleteImage(path);
                    }
                }

                ImageDatabaseHelper dbHelper1 = new ImageDatabaseHelper(this);
                SQLiteDatabase db1 = dbHelper1.getWritableDatabase();
                ContentValues values1 = new ContentValues();
                values1.put(Image.MEMO_NO,no);
                String selection1 = Image.MEMO_NO + " LIKE ?";
                String[] selectionArgs1 = { 0+"" };
                db1.update(Image.TABLE_NAME, values1, selection1, selectionArgs1);

                Toast.makeText(WriteMemo.this, "메모가 저장되었습니다", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.camera:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                        Log.d(TAG, "권한 설정 완료");
                        dispatchTakePictureIntent();
                    } else {
                        Log.d(TAG, "권한 설정 요청");
                        ActivityCompat.requestPermissions(WriteMemo.this,
                                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                    return true;
                }
            case R.id.album:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                        Log.d(TAG, "권한 설정 완료");
                        Intent intent1 = new Intent(Intent.ACTION_PICK);
                        intent1.setType("image/*");
                        startActivityForResult(intent1, REQUEST_GET_PHOTO);
                    } else {
                        Log.d(TAG, "권한 설정 요청");
                        ActivityCompat.requestPermissions(WriteMemo.this,
                                new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    }
                    return true;
                }
            case R.id.url:
                final EditText edittext = new EditText(this);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("URL을 입력하세요");
                builder.setView(edittext);
                builder.setPositiveButton("입력",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try{
                                    URL url = new URL(edittext.getText().toString());
                                    Glide.with(getApplicationContext()).asBitmap().load(url)
                                            .into(new SimpleTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                                    try{
                                                        createImageFile();
                                                        adapter.additem(image.getName(),resource);
                                                        adapter.notifyDataSetChanged();
                                                        image.deleteOnExit();
                                                    }catch(Exception e){
                                                        Toast.makeText(WriteMemo.this, "이미지를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }catch(Exception e){
                                    Toast.makeText(WriteMemo.this, "잘못된 url입니다", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                builder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image File name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a File: path for use with ACTION_VIEW intents

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getPackageName(),
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public Bitmap rotateImage(Bitmap source, int angle) {
        Matrix matrix = new Matrix();

        switch(angle) {
            case ExifInterface.ORIENTATION_NORMAL:
                return source;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1,1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1,1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1,1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
                default:
                    return source;
        }
        Bitmap bmRotated = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
        source.recycle();
        return bmRotated;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        try {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:

                    if (resultCode == RESULT_OK) {
                        upload_img = MediaStore.Images.Media
                                .getBitmap(getContentResolver(), Uri.fromFile(image));
                        }

                    break;

                case REQUEST_GET_PHOTO:
                    if (resultCode == RESULT_OK) {
                        createImageFile();
                        InputStream in = getContentResolver().openInputStream(intent.getData());
                        upload_img = BitmapFactory.decodeStream(in);
                        mCurrentPhotoPath = getPath(intent.getData());

                    }
                }
                ExifInterface ei = new ExifInterface(mCurrentPhotoPath);

                int angle = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
                adapter.additem(image.getName(),rotateImage(upload_img,angle));
                image.deleteOnExit();

        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                Log.d(TAG, "onRequestPermissionsResult");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED ) {
                    Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                    dispatchTakePictureIntent();
                    break;
                }
            case 2:
                Log.d(TAG, "onRequestPermissionsResult");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                    Intent intent1 = new Intent(Intent.ACTION_PICK);
                    intent1.setType("image/*");
                    startActivityForResult(intent1, REQUEST_GET_PHOTO);
                    break;
                }

        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }

    public void InsertImage(String path){
        ImageDatabaseHelper dbHelper = new ImageDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Image.COLUMN_NAME_TITLE, path);
        values.put(Image.MEMO_NO, no);

        db.insert(Image.TABLE_NAME, null, values);
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        imagelist.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        imagelist.setLayoutManager(layoutManager);
    }

    public void deletedb(String path){
        ImageDatabaseHelper dbHelper = new ImageDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection2 = Image.COLUMN_NAME_TITLE + " LIKE ? ";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { path };
        // Issue SQL statement.
        db.delete(Image.TABLE_NAME, selection2, selectionArgs);
    }

}

package com.memo.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;

public class FileManager {

    private Context context;

    FileManager(Context context){
        this.context = context;
    }

    public  Bitmap getImage(String path){
        File file = context.getFilesDir();
        return BitmapFactory.decodeFile(file+"/"+path);
    }

    public void DeleteImage(String path){
        File file = new File(context.getFilesDir()+"/"+path);
        file.delete();
    }

    public void SaveImage(String path, Bitmap bitmap){
        try{
            File file = context.getFilesDir();
            File tempFile = new File(file, path);
            tempFile.createNewFile();

            FileOutputStream out = new FileOutputStream(tempFile);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();


        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

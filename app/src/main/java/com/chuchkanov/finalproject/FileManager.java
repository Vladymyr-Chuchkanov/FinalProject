package com.chuchkanov.finalproject;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileManager {
    private String name;
    private String path;
    List<String> names;
    Activity act;
    Context context;
    private static String FILE_NAME="recent.txt";

    public FileManager(Activity act0) {
        act = act0;
        names=new ArrayList<>();
        context = act.getApplicationContext();
    }

    public void setFile(String n, String p){
        name = n;
        path = p;
    }

    public String unpackZip(String path, String zipname)
    {
        InputStream is;
        ZipInputStream zis;
        String filename=path.replace(".zip","");
        try
        {

            is = new FileInputStream(path);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null)
            {
                filename = ze.getName();

                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (ze.isDirectory()) {
                    File fmd = new File(filename);
                    fmd.mkdirs();
                    continue;
                }
                File test = new File(path.replace(zipname,filename));
                //FileOutputStream fout = openFileOutput(filename,MODE_PRIVATE);

                BufferedOutputStream dw = new BufferedOutputStream(new FileOutputStream(test));
                while ((count = zis.read(buffer)) != -1)
                {
                    String test_txt = new String(buffer, StandardCharsets.UTF_8);
                    //fout.write(buffer, 0, count);
                    dw.write(buffer,0,count);
                }
                dw.close();
                //fout.close();
                zis.closeEntry();
            }


            zis.close();

        }
        catch(IOException e)
        {
            e.printStackTrace();
            return "";
        }

        return filename;
    }

    public void addRecent(String name,String path){
        loadRecent();
        for(int i =0;i<names.size();i++){
            if(names.get(i).equals(name)){
                return;
            }
        }
        FileOutputStream fos = null;
        FileInputStream fin = null;
        try {
            fin = context.openFileInput(FILE_NAME);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);

            fos = context.openFileOutput(FILE_NAME,MODE_PRIVATE);
            fos.write((name+";"+path+"\n").getBytes(StandardCharsets.UTF_8));
            fos.write(bytes);
        }
        catch(IOException ex) {

            Toast.makeText(act, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally{
            try{
                if(fos!=null)
                    fos.close();
            }
            catch(IOException ex){

                Toast.makeText(act, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void loadRecent(){
        FileInputStream fin = null;
        try {
            fin = act.openFileInput(FILE_NAME);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String text = new String (bytes);
            if(text.equals("")){
                return;
            }
            String[]files = text.split("\n");
            String []files2= new String[files.length];
            int k = files.length-1;
            if(k<0){
                return;
            }
            for(int i =0;i<files.length;i++) {
                files2[i]=files[k-i].split(";")[0];

                names.add(files[k-i].split(";")[0]);
            }
        }
        catch(IOException ex) {

            Toast.makeText(act, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally{

            try{
                if(fin!=null)
                    fin.close();
            }
            catch(IOException ex){

                Toast.makeText(act, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}

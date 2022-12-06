package com.chuchkanov.finalproject;

import static org.apache.commons.io.FileUtils.getFile;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.io.FileUtils.getUserDirectory;
import static org.apache.commons.io.FileUtils.touch;
import org.apache.commons.io.FileUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Removable {
    private static final int PERMISSION_STORAGE = 101;
    private static String FILE_NAME="recent.txt";
    List<String> pathes;
    List<String> names;
    ListView lv;
    FileManager fileManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = (TextView) findViewById(R.id.textView);
        String temp =  Environment.getExternalStorageDirectory().toString();
        String temppath = getIntent().getStringExtra("name_path");
        fileManager= new FileManager(this);
        if(temppath!=null && temppath!=""){
            String name0 = temppath.split("/")[temppath.split("/").length-1];
            String name = fileManager.unpackZip(temppath, name0);
            temppath = temppath.replace(name0, name);
            fileManager.addRecent(name,temppath);
        }
        pathes = new ArrayList<>();
        names = new ArrayList<>();
        lv = (ListView) findViewById(R.id.recent_list);
        if (PermissionUtils.hasPermissions(MainActivity.this)) {
            loadRecent();
            return;
        }
        else {
            PermissionUtils.requestPermissions(MainActivity.this, PERMISSION_STORAGE);
        }
        starter();
        loadRecent();
    }

    @Override
    protected void onResume(){
        super.onResume();
        loadRecent();
    }

    public void starter(){

        FileOutputStream fos = null;
        try {


            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
        }
        catch(IOException ex) {

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally{
            try{
                if(fos!=null)
                    fos.close();
            }
            catch(IOException ex){

                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void loadRecent(){
        FileInputStream fin = null;
        try {
            fin = openFileInput(FILE_NAME);
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
            pathes.clear();
            names.clear();
            for(int i =0;i<files.length;i++) {
                files2[i]=files[i].split(";")[0];
                pathes.add(files[i].split(";")[1]);
                names.add(files[i].split(";")[0]);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,files2);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    openFile(position);
                }

            });
            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    String name = names.get(position);
                    CustomDialogFragment dialog = new CustomDialogFragment();
                    Bundle args = new Bundle();
                    args.putString("name", name);
                    args.putInt("pos", position);
                    dialog.setArguments(args);
                    dialog.show(getSupportFragmentManager(), "custom");
                    return true;
                }
            });

        }
        catch(IOException ex) {

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally{

            try{
                if(fin!=null)
                    fin.close();
            }
            catch(IOException ex){

                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void remove(String name, int pos){
        String path = pathes.get(pos);
        delRecent(name, path, pos);
        loadRecent();
    }



    public void openNewBook(View v){
        Intent intent1 = new Intent(this, FileManagerActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);


        startActivity(intent1);
    }

    public void openMenu(View v){
        Intent intent1 = new Intent(this, MenuActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent1);
    }

    public void openFile(int pos){
        String name = names.get(pos);
        String path = pathes.get(pos);

        File temp = new File(path);
        boolean check = temp.isFile();
        if(!check){
            Toast.makeText(this, "Файл не знайдено, спробуйте відкрити його власноруч!", Toast.LENGTH_SHORT).show();
            delRecent(name,path,pos);
            loadRecent();
            return;
        }
        if(pos != 0){
            toFirst(name,path, pos);
            loadRecent();
        }
        Intent intent1 = new Intent(this, ReaderActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent1.putExtra("filepath",path);
        startActivity(intent1);

    }
    public void toFirst(String name, String path, int pos){
        FileOutputStream fos = null;
        try {


            fos = openFileOutput(FILE_NAME,MODE_PRIVATE);
            fos.write((name+";"+path+"\n").getBytes(StandardCharsets.UTF_8));
            for(int i =0;i<names.size();i++){
                if(i!=pos){
                    fos.write((names.get(i)+";"+pathes.get(i)+"\n").getBytes(StandardCharsets.UTF_8));
                }
            }

        }
        catch(IOException ex) {

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally{
            try{
                if(fos!=null)
                    fos.close();
            }
            catch(IOException ex){

                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void delRecent(String name, String path, int pos){
        FileOutputStream fos = null;
        try {


            fos = openFileOutput(FILE_NAME,MODE_PRIVATE);

            for(int i =0;i<names.size();i++){
                if(i!=pos){
                    fos.write((names.get(i)+";"+pathes.get(i)+"\n").getBytes(StandardCharsets.UTF_8));
                }
            }

        }
        catch(IOException ex) {

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally{
            try{
                if(fos!=null)
                    fos.close();
            }
            catch(IOException ex){

                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


}
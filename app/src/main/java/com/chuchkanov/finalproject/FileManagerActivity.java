package com.chuchkanov.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileManagerActivity extends AppCompatActivity {

    TextView pathView;
    ListView treeView;
    List<String> pathList;
    List<String> pathes;
    List<String> names;
    FileManager fileManager;
    private static String FILE_NAME="recent.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileManager = new FileManager(this);
        setContentView(R.layout.activity_file_manager);
        String temp =  Environment.getExternalStorageDirectory().toString();
        pathView = (TextView)findViewById(R.id.textView3);
        treeView = (ListView) findViewById(R.id.storage_list);
        pathList = new ArrayList<>();
        pathes = new ArrayList<>();
        names = new ArrayList<>();
        loadRecent();
        pathView.setText("/");
        pathList.add(temp);
        File current = new File(temp);
        File[]files0 = current.listFiles();
        List<String> templist = new ArrayList<>();
        for(int i =0;i< files0.length;i++){
            if(files0[i].isFile() && getFileExtension( files0[i]).equals(".fb2")){
                templist.add(files0[i].getName());
            }
            if(files0[i].isDirectory() && !files0[i].getName().matches("[ ]?[.]{1}.*")){
                templist.add(files0[i].getName());
            }
        }
        String []files = new String[templist.size()];
        for(int i =0;i<files.length;i++){
            files[i]=templist.get(i);

        }
        ArrayAdapter<String> adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,files);
        treeView.setAdapter(adapter);
        treeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                nextDir(position);
            }

        });

    }
    private static String getFileExtension(File file) {
        String fileName = file.getName();

        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            String temp = fileName.substring(fileName.lastIndexOf(".") + 1);
            if(temp.equals("zip")){
                fileName = fileName.substring(0,fileName.lastIndexOf("."));
                temp = fileName.substring(fileName.lastIndexOf(".") + 1);
            }
            return temp;
        }
        else return "";
    }
    public void openMenu2(View v){
        Intent intent1 = new Intent(this, MenuActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent1);
    }

    public void backPath(View v){

        if(pathList.size()==1){
            Intent intent1 = new Intent(this, MainActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent1);
            return;
        }

        String temp = "";
        String path0="/";
        for(int i =0;i<pathList.size()-1;i++){
            temp+=pathList.get(i);
            temp+="/";
            if(i>0){
                path0+=pathList.get(i);
                path0+="/";
            }
        }


        pathView.setText(path0);
        pathList.remove(pathList.size()-1);
        File current = new File(temp);
        if(current.isFile()){
            openFile(current);
        }
        File[]files0 = current.listFiles();
        List<String> templist = new ArrayList<>();
        for(int i =0;i< files0.length;i++){
            if(files0[i].isFile() && getFileExtension( files0[i]).equals("fb2")){
                templist.add(files0[i].getName());
            }
            if(files0[i].isDirectory() && !files0[i].getName().matches("[ ]]?[.]{1}.*")){
                templist.add(files0[i].getName());
            }
        }
        String []files = new String[templist.size()];
        for(int i =0;i<files.length;i++){
            files[i]=templist.get(i);

        }
        ArrayAdapter<String> adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,files);
        treeView.setAdapter(adapter);
        treeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                nextDir(position);
            }

        });
    }

    public void openFile(File file){
        String name = file.getName();
        String name0 = file.getName();
        String path = file.getAbsolutePath();
        if(file.getName().substring(file.getName().lastIndexOf('.')+1).equals("zip")){
            String temp = name.replace(".zip","");
            File tempF = new File(path.replace(name0,temp));

            if(tempF.isFile()){
                name = temp;
                path = path.replace(".zip","");
            }else {

                name = fileManager.unpackZip(file.getAbsolutePath(), file.getName());
                path = path.replace(name0, name);
            }
        }

        fileManager.addRecent(name,path);
        Intent intent1 = new Intent(this, ReaderActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent1.putExtra("filepath",path);
        startActivity(intent1);
    }



    public void nextDir(int id){
        String temp = "";
        String path0="/";
        for(int i =0;i<pathList.size();i++){
            temp+=pathList.get(i);
            temp+="/";
            if(i>0){
                path0+=pathList.get(i);
                path0+="/";
            }
        }
        String next = treeView.getItemAtPosition(id).toString();
        temp+= next;
        path0+=next;

        File current = new File(temp);
        if(current.isFile()){

            openFile(current);
            return;
        }
        pathView.setText(path0);
        pathList.add(next);
        File[]files0 = current.listFiles();
        List<String> templist = new ArrayList<>();
        for(int i =0;i< files0.length;i++){
            if(files0[i].isFile() && getFileExtension( files0[i]).equals("fb2")){
                templist.add(files0[i].getName());
            }
            if(files0[i].isDirectory() && !files0[i].getName().matches("[ ]]?[.]{1}.*")){
                templist.add(files0[i].getName());
            }
        }
        String []files = new String[templist.size()];
        for(int i =0;i<files.length;i++){
            files[i]=templist.get(i);

        }
        ArrayAdapter<String> adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,files);
        treeView.setAdapter(adapter);
        treeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                nextDir(position);
            }

        });


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
            for(int i =0;i<files.length;i++) {
                files2[i]=files[k-i].split(";")[0];
                pathes.add(files[k-i].split(";")[1]);
                names.add(files[k-i].split(";")[0]);
            }
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
}
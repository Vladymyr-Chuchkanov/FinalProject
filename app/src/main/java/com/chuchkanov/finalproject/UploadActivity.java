package com.chuchkanov.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.chuchkanov.finalproject.MyTask;

import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class UploadActivity extends AppCompatActivity {
    public EditText _name;
    ListView listSearched;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        _name = (EditText) findViewById(R.id.search_book_name_f);

        listSearched = (ListView) findViewById(R.id.list_search);
    }

    public void openMenu4(View v){
        Intent intent1 = new Intent(this, MenuActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent1);
    }

    public void searchBook(View v){
        String name = _name.getText().toString().trim();

        while(name.contains("  ")){
            name = name.replace("  "," ");
        }
        name = name.replace(" ","+");
        String URL = "http://flibusta.is/booksearch?ask="+name;
        Activity act = this;
        String temp =  Environment.getExternalStorageDirectory().toString();
        temp+="/Download/";
        new com.chuchkanov.finalproject.MyTask(name, act,temp).execute();
        /*

        try {
            Document doc  = Jsoup.connect(URL).get();
            String temp0="";
            Element link = doc.select("a").first();
            int j = 0;
            //if(doc.select("ul") != null && doc.select("ul").get(1).)
        } catch (IOException e) {
            e.printStackTrace();
        }*/



    }

}

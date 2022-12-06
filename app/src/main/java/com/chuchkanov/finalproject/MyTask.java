package com.chuchkanov.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.FileUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static org.apache.commons.io.FileUtils.copyURLToFile;

public class MyTask extends AsyncTask<Void, Void, String> {
    private String _name;
    private Activity act;
    List<String> _ursl;
    List<String> _names;
    String _path;

    public MyTask(String name, Activity act0, String path) {
        _name=name;
        act = act0;
        _path=path;
    }



    @Override
    protected String doInBackground(Void... params) {
        String title = "";
        Document doc;
        String name = _name;
        String result_str="";
        try {
            doc = Jsoup.connect("http://flibusta.is/booksearch?ask="+name).get();
            title = doc.title();
            System.out.print(title);
            Elements h3s = doc.select("h3");
            Elements ulspager = doc.select("ul.pager");
            Elements uls = doc.select("ul");
            result_str+=getResult(uls);
            boolean check = false;
            for(int i =0;i<uls.size();i++){
                Elements ulitems = doc.select("li.pager-item");
                int jk = ulitems.size()/2;
                for(int j =1;j<=jk;j++){
                    String tempurl = "http://flibusta.is/booksearch?page="+Integer.toString(j+1)+"&ask=" + name;
                    Document doctemp = Jsoup.connect(tempurl).get();
                    result_str+=getResult(doctemp.select("ul"));
                }
                break;
            }
            int j = 0;
            //if(doc.select("ul") != null && doc.select("ul").get(1).)
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result_str;
    }

    public String getResult(Elements els){
        String res="";

        for(int i =0;i<els.size();i++){

            String temp2 = els.get(i).toString();
            if(temp2.split("\n")[1].contains("a href=\"/b/")&&temp2.split("\n")[1].contains("a href=\"/a/")){

                String[]lines = temp2.split("\n");
                for(int j =1;j<lines.length;j++){
                    String name="";
                    String url="";
                    String[]names = lines[j].split(">");
                    for(int y =0;y<names.length;y++){
                        String temp_1=names[y].split("<")[0];
                        name+=temp_1;
                    }
                    if(lines[j].split("\"").length<=1){
                        continue;
                    }
                    url = lines[j].split("\"")[1];
                    res+=url+";"+name+"\n";
                }


            }
            int h = 0;
            int u =3;
        }

        return res;
    }
    @Override
    protected void onPostExecute(String result) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        HashMap<String, String> map;
        this._ursl=new ArrayList<>();
        this._names=new ArrayList<>();
        String[]lines = result.split("\n");
        for(int i =0;i<lines.length;i++){
            map = new HashMap<>();
            _ursl.add(lines[i].split(";")[0]);
            _names.add(lines[i].split(";")[1]);
            map.put("name",lines[i].split(";")[1].split("-")[0]);
            if(lines[i].split(";")[1].split("-").length<=1){
                map.put("author",lines[i].split(";")[1]);
            }
            else {
                map.put("author", lines[i].split(";")[1].split("-")[1]);
            }
            arrayList.add(map);
        }
        String[] headers = new String[] {"name","author"};
        SimpleAdapter adapter = new SimpleAdapter(act, arrayList, android.R.layout.simple_list_item_2,
                headers,
                new int[]{android.R.id.text1, android.R.id.text2});

        ListView contactList = act.findViewById(R.id.list_search);
        contactList.setAdapter(adapter);
        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                openFile(position);

            }

        });



    }



    public void openFile(int pos){
        String url = "http://flibusta.is"+_ursl.get(pos);


        String path = _path+_names.get(pos).trim();
        new downloadTask(url,path,act).execute();

    }

}




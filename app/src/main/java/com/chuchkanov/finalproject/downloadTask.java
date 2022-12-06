package com.chuchkanov.finalproject;

import static org.apache.commons.io.FileUtils.copyURLToFile;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class downloadTask extends AsyncTask<Void, Void, String> {
    private String _url;
    private Activity _act;
    String _path;

    public downloadTask(String url, String path, Activity act) {
        _act = act;
        _path=path;
        _url=url;

    }


    @Override
    protected String doInBackground(Void... params) {
        _path=_path.replace(":","");
        _path=_path.replace("«","");
        _path=_path.replace("»","");
        if(_path.length()>100){
            _path=_path.substring(0,100);
        }
        File dest = new File(_path+".fb2.zip");

        try {
            copyURLToFile(new URL(_url+"/fb2"), dest);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return _path;
    }
    @Override
    protected void onPostExecute(String result) {
        Intent intent1 = new Intent(_act, MainActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent1.putExtra("name_path",_path+".fb2.zip");
        _act.startActivity(intent1);



    }
}

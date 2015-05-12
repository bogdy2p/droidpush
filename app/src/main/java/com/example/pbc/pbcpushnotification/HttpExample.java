package com.example.pbc.pbcpushnotification;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by pbc on 12.05.2015.
 */
public class HttpExample extends Activity {

    TextView httpStuff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        httpStuff = (TextView) findViewById(R.id.tvHttp);

        GetMethodExample test = new GetMethodExample();
        String returned = null;
        try {
            returned = test.getUserexists("13");
        } catch (Exception e) {
            e.printStackTrace();
        }
        httpStuff.setText(returned);


    }
}

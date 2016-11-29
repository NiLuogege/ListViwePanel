package com.example.well.luocpanel;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<String> data = new ArrayList<>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mian);

        for (int i = 0; i < 100; i++) {
            data.add("数据 " + (i + 1));
        }

        MyPanel mp = (MyPanel) findViewById(R.id.mp);
        MyAdapter myAdapter = new MyAdapter();
        mp.setAdapter(myAdapter);
        mp.setOnPositionChangedListener(new MyPanel.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(MyPanel listView, int positon, View ScroBarPanel) {
                TextView view = (TextView) ScroBarPanel;
                view.setText("位置" + positon);
            }
        });

    }



    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHoler holer = null;
            if (view == null) {
                holer = new ViewHoler();
                view = View.inflate(MainActivity.this, R.layout.list_item, null);
                holer.tv = (TextView) view.findViewById(R.id.tv);
                view.setTag(holer);
            }else{
                holer= (ViewHoler) view.getTag();
            }
            holer.tv.setText(data.get(i));
            return view;
        }

        class ViewHoler {
            TextView tv;
        }
    }

}

/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.masjidsolutions.mymasjid;


import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.masjidsolutions.mymasjid.R.layout;

public class SettingsMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( layout.activity_settings_menu );
        myListData[] myListData = new myListData[] {
                new myListData("Email", android.R.drawable.ic_dialog_email),
                new myListData("Info", android.R.drawable.ic_dialog_info),
                new myListData("Dialer", android.R.drawable.ic_dialog_dialer),
                new myListData("Alert", android.R.drawable.ic_dialog_alert),
                new myListData("Map", android.R.drawable.ic_dialog_map),
                new myListData("Email", android.R.drawable.ic_dialog_email),
                new myListData("Info", android.R.drawable.ic_dialog_info),
                new myListData("Dialer", android.R.drawable.ic_dialog_dialer),
                new myListData("Alert", android.R.drawable.ic_dialog_alert),
                new myListData("Map", android.R.drawable.ic_dialog_map),
        };

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        MyListAdapter adapter = new MyListAdapter(myListData);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
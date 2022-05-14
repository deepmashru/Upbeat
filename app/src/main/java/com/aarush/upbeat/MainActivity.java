package com.aarush.upbeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ListView listView;
    String[] items;
    private DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        runtimePermission();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawer = findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,
                toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }

    }

    public void runtimePermission(){
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        displaySong();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    public ArrayList<File> findSong(File file){
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();
            for (File singleFile : files){
                if (singleFile.isDirectory() && !singleFile.isHidden()){
                    arrayList.addAll(findSong(singleFile));
                }else {
                    if (singleFile.getName().endsWith(".mp3")|| singleFile.getName().endsWith(".wav")){
                        arrayList.add(singleFile);
                    }
                }
            }
        return arrayList;
    }
    public void displaySong() {
        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());
        items = new String[mySongs.size()];
        for (int i = 0; i < mySongs.size(); i++) {
            items[i] = mySongs.get(i).getName().toString().replace(".mp3", "").replace(".wav", "");
        }
        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songName = (String) listView.getItemAtPosition(i);
                startActivity(new Intent(getApplicationContext(),PlayerActivity.class)
                        .putExtra("songs",mySongs)
                        .putExtra("songname",songName)
                        .putExtra("pos",i)
                );
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                MainActivity.this.drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.share:
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.TEXT", "Download LifeHacks App:\nPsychology" +
                        " LifeHacks app provides mind-blowing psychology facts and life hacks that everyone" +
                        " should know \nDownload Now: \nhttps://play.google.com/store/apps/details?" +
                        "id=com.harshmashru.lifehacks");
                intent.putExtra("android.intent.extra.SUBJECT", "pub:AARUSH");
                MainActivity.this.startActivity(Intent.createChooser(intent, "Share Using"));
                break;
            case R.id.about:
                MainActivity.this.drawer.closeDrawer(GravityCompat.START);
                Intent xyz= new Intent(MainActivity.this, About.class);
                MainActivity.this.startActivity(xyz);
                break;
            case R.id.online:
                Intent intentx = new Intent("android.intent.action.VIEW",
                        Uri.parse("https://play.google.com/store/search?q=OnlineMusic"));
                MainActivity.this.startActivity(intentx);
                break;
            case R.id.visitweb:
                Toast.makeText(this,"Website will be avalible soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.moreapps:
                Intent intentz = new Intent("android.intent.action.VIEW",
                        Uri.parse("https://play.google.com/store/search?q=pub:AARUSH"));
                MainActivity.this.startActivity(intentz);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class CustomAdapter extends BaseAdapter{

            @Override
            public int getCount() {
                return items.length;
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
                View viewOr = getLayoutInflater().inflate(R.layout.list_item,null);
                TextView txtSong =(TextView) viewOr.findViewById(R.id.txtSong);
                txtSong.setSelected(true);
                txtSong.setText(items[i]);
                return viewOr;
            }
    }
}
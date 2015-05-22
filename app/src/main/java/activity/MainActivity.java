package activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.intern.picsartvideo.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import item.CustomGalleryItem;
import utils.FileUtils;
import utils.Utils;


public class MainActivity extends ActionBarActivity {

    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/req_images");

    private static Context context;
    private Button picsArtGalleryButton;
    private Button customGalleryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = this.getSharedPreferences("pics_art_video", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.putBoolean("pics_art_gallery_isopen", false);
        //editor.putBoolean("custom_gallery_isopen", false);
        //editor.putInt("edited_count", 0);
        //editor.putBoolean("isopen", false);
        editor.clear();
        editor.commit();


        Utils.initImageLoader(getApplicationContext());
        init();

    }

    private void init() {

        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
        context = this;
        FileUtils.craeteDir("req_images");

        //new MyTask().execute();


        picsArtGalleryButton = (Button) findViewById(R.id.pics_art_gallery_button);
        picsArtGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PicsArtGalleryActvity.class);
                startActivity(intent);
            }
        });

        customGalleryButton = (Button) findViewById(R.id.custom_gallery_button);
        customGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CustomGalleryActivity.class);
                startActivity(intent);
            }
        });
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        ArrayList<Integer> integers = new ArrayList<>();
        ArrayList<String> strings = new ArrayList<>();
        ArrayList<CustomGalleryItem> customGalleryItems = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            strings = Utils.getGalleryPhotos1(MainActivity.this);
        }

        @Override
        protected Void doInBackground(Void... params) {
            //
            for (int i = 0; i < strings.size(); i++) {
                try {
                    integers.add((int) Utils.getBitmapHeight(strings.get(i)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /*try {
                    customGalleryItems.add(new CustomGalleryItem(strings.get(i), false, (int) Utils.getBitmapWidth(), (int) Utils.getBitmapHeight(strings.get(i))));
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            FileUtils.writeListToJson1(MainActivity.this, customGalleryItems, "file.json");
            Log.d("gagaaag", "done");
        }
    }

}

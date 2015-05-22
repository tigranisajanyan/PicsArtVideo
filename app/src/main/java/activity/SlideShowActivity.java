package activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.intern.picsartvideo.R;

import java.io.File;
import java.util.ArrayList;

import adapter.ImagePagerAdapter;
import adapter.SlideShowAdapter;
import item.SlideShowItem;
import service.MyService;


public class SlideShowActivity extends ActionBarActivity {

    private ViewPager viewPager;
    private RecyclerView recyclerView;
    private Button openGalleryButton;
    private Button openPicsArtButton;

    private ImagePagerAdapter imagePagerAdapter;
    private SlideShowAdapter slideShowAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;

    private ArrayList<SlideShowItem> selectedImagesPathList = new ArrayList<>();

    public static final int REQUEST_CODE_FOR_EDIT = 300;
    public static final int REQUEST_CODE_FOR_CUSTOM_GALLERY = 200;
    public static final int REQUEST_CODE_FOR_PICS_ART = 100;

    public static final String INDEX = "index";
    public static final String EDITED_IMAGE_PATH = "edited_image_path";

    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/req_images");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show);

        Intent intent = getIntent();
        ArrayList<CharSequence> charSequences;
        charSequences = intent.getCharSequenceArrayListExtra("image_paths");
        for (int i = 0; i < charSequences.size(); i++) {
            SlideShowItem slideShowItem = new SlideShowItem(charSequences.get(i).toString(), false, false);
            //slideShowItem.path = charSequences.get(i).toString();
            if (intent.getBooleanExtra("isfile", false)) {
                slideShowItem.setIsFromFileSystem(true);
            }
            selectedImagesPathList.add(slideShowItem);
        }
        init();

    }

    private void init() {

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        recyclerView = (RecyclerView) findViewById(R.id.recycler_slide_show);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        openGalleryButton = (Button) findViewById(R.id.open_gallery_button);
        openPicsArtButton = (Button) findViewById(R.id.open_pics_art_button);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width);
        viewPager.setLayoutParams(layoutParams);
        imagePagerAdapter = new ImagePagerAdapter(selectedImagesPathList, SlideShowActivity.this, recyclerView);
        viewPager.setAdapter(imagePagerAdapter);

        slideShowAdapter = new SlideShowAdapter(selectedImagesPathList, this, viewPager, imagePagerAdapter);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        itemAnimator = new DefaultItemAnimator();

        recyclerView.setAdapter(slideShowAdapter);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        openGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SlideShowActivity.this, CustomGalleryActivity.class);
                startActivityForResult(intent, REQUEST_CODE_FOR_CUSTOM_GALLERY);
            }
        });

        openPicsArtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SlideShowActivity.this, PicsArtGalleryActvity.class);
                startActivityForResult(intent, REQUEST_CODE_FOR_PICS_ART);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_PICS_ART && resultCode == Activity.RESULT_OK) {
            ArrayList<CharSequence> all_path = data.getCharSequenceArrayListExtra("image_paths");
            if (all_path.size() > 0) {
                for (int i = 0; i < all_path.size(); i++) {
                    SlideShowItem slideShowItem = new SlideShowItem(all_path.get(i).toString(), false, false);
                    selectedImagesPathList.add(slideShowItem);
                }
                imagePagerAdapter.notifyDataSetChanged();
                slideShowAdapter.notifyDataSetChanged();
            }
        }
        if (requestCode == REQUEST_CODE_FOR_CUSTOM_GALLERY && resultCode == Activity.RESULT_OK) {
            ArrayList<CharSequence> all_path = data.getCharSequenceArrayListExtra("image_paths");
            if (all_path.size() > 0) {
                for (int i = 0; i < all_path.size(); i++) {
                    SlideShowItem slideShowItem = new SlideShowItem(all_path.get(i).toString(), false, true);
                    selectedImagesPathList.add(slideShowItem);
                }
                imagePagerAdapter.notifyDataSetChanged();
                slideShowAdapter.notifyDataSetChanged();
            }
        }
        if (requestCode == REQUEST_CODE_FOR_EDIT && resultCode == Activity.RESULT_OK) {
            if (data.getStringExtra(EDITED_IMAGE_PATH) != "") {
                SlideShowItem slideShowItem = new SlideShowItem(data.getStringExtra(EDITED_IMAGE_PATH), true, true);
                selectedImagesPathList.set(data.getIntExtra(INDEX, -1), slideShowItem);
                slideShowAdapter.notifyItemChanged(data.getIntExtra(INDEX, -1));
                imagePagerAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_slide_show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            if (selectedImagesPathList.size() > 0) {

                String[] pathsForDecoding = new String[selectedImagesPathList.size()];
                for (int i = 0; i < selectedImagesPathList.size(); i++) {
                    pathsForDecoding[i] = selectedImagesPathList.get(i).path;
                }

                Intent intentService = new Intent(this, MyService.class);
                intentService.putExtra("paths", pathsForDecoding);
                startService(intentService);
                Intent intent = new Intent("android.intent.action.videogen");
                intent.putExtra("myimagespath", myDir.toString());
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(getApplicationContext(), "you have no image", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("pics_art_video", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.putBoolean("pics_art_gallery_isopen", false);
        //editor.putBoolean("custom_gallery_isopen", false);
        //editor.putInt("edited_count", 0);
        //editor.putBoolean("isopen", false);
        editor.clear();
        editor.commit();
        super.onDestroy();
    }

}

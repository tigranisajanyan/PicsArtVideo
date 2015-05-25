package activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.intern.picsartvideo.R;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import adapter.CustomGalleryAdapter;
import item.CustomGalleryItem;
import utils.SpacesItemDecoration;
import utils.Utils;

public class CustomGalleryActivity extends ActionBarActivity implements RecyclerViewFragment.OnFragmentInteractionListener {

    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private ProgressBar progressBar;
    FragmentManager fragmentManager = getFragmentManager();
    RecyclerViewFragment multiSelectFragment = new RecyclerViewFragment();
    boolean brr = false;

    private CustomGalleryAdapter customGalleryAdapter;
    private ArrayList<CustomGalleryItem> customGalleryArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_gallery);

        init();

        /*actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);*/

    }

    private void init() {

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        customGalleryAdapter = new CustomGalleryAdapter(customGalleryArrayList, this);

        recyclerView = (RecyclerView) findViewById(R.id.gallery_rec_view);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        itemAnimator = new DefaultItemAnimator();

        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(true);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        recyclerView.setAdapter(customGalleryAdapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(2));



        /*recyclerView.setOnScrollListener(new MyScrollListener(this) {
            @Override
            public void onMoved(int distance) {
                actionBar.setTranslationY(-distance);
            }
        });*/

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToRecyclerView(recyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (brr == false) {

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.slide_out, R.anim.slide_out);
                    fragmentTransaction.add(R.id.frgmCont, multiSelectFragment);

                    brr = true;
                    //RecyclerViewFragment fragment = (RecyclerViewFragment) getFragmentManager().findFragmentById(R.id.frgmCont);
                    //fragment.setmAdapter(customGalleryAdapter.getSelected());

                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                    multiSelectFragment.setmAdapter(customGalleryAdapter.getSelected());


                } else {

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_in);
                    fragmentTransaction.remove(multiSelectFragment);
                    brr = false;
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });

        new MyTask().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_custom_gallery, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            if (customGalleryAdapter.getSelected().size() < 1) {
                Toast.makeText(getApplicationContext(), "no images selected", Toast.LENGTH_LONG).show();
            } else {
                SharedPreferences sharedPreferences = this.getSharedPreferences("pics_art_video", MODE_PRIVATE);
                if (sharedPreferences.getBoolean("custom_gallery_isopen", false) == true || sharedPreferences.getBoolean("pics_art_gallery_isopen", false) == true) {

                    Intent data = new Intent().putExtra("image_paths", customGalleryAdapter.getSelected());
                    setResult(RESULT_OK, data);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("custom_gallery_isopen", true);
                    editor.commit();

                } else {

                    Intent intent = new Intent(CustomGalleryActivity.this, SlideShowActivity.class);
                    intent.putCharSequenceArrayListExtra("image_paths", customGalleryAdapter.getSelected());
                    intent.putExtra("isfile", true);
                    startActivity(intent);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("custom_gallery_isopen", true);
                    editor.commit();

                }
                finish();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //FileUtils.readListFromJson1(CustomGalleryActivity.this,customGalleryArrayList,"file.json");
            customGalleryArrayList.addAll(Utils.getGalleryPhotos(CustomGalleryActivity.this));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            customGalleryAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }
    }

    public abstract class MyScrollListener extends RecyclerView.OnScrollListener {

        private int toolbarOffset = 0;
        private int toolbarHeight;

        public MyScrollListener(Context context) {
            int[] actionBarAttr = new int[]{android.R.attr.actionBarSize};
            TypedArray a = context.obtainStyledAttributes(actionBarAttr);
            toolbarHeight = (int) a.getDimension(0, 0) + 10;
            a.recycle();
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            clipToolbarOffset();
            onMoved(toolbarOffset);

            if ((toolbarOffset < toolbarHeight && dy > 0) || (toolbarOffset > 0 && dy < 0)) {
                toolbarOffset += dy;
            }
        }

        private void clipToolbarOffset() {
            if (toolbarOffset > toolbarHeight) {
                toolbarOffset = toolbarHeight;
            } else if (toolbarOffset < 0) {
                toolbarOffset = 0;
            }
        }

        public abstract void onMoved(int distance);
    }

}

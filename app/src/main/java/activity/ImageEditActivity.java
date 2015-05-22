package activity;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Environment;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.intern.picsartvideo.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import uk.co.senab.photoview.IPhotoView;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import utils.Utils;


public class ImageEditActivity extends ActionBarActivity {

    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/req_images");
    public static final String EDITED_IMAGE_PATH = "edited_image_path";
    public static final String INDEX = "index";
    public static final String IMAGE_PATH = "image_path";
    public static final String FILE_PREFIX = "file://";

    private PhotoView cropImageView;
    private ImageView cropImageCorner;
    private Button rotateButton;
    private Intent intent;
    private String fileName;
    private int count = 0;
    LinearLayout drop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);

        init();

    }

    private void init() {

        drop = (LinearLayout) findViewById(R.id.gag);
        SharedPreferences sharedPreferences = getSharedPreferences("pics_art_video", MODE_PRIVATE);
        count = sharedPreferences.getInt("edited_count", 0);
        cropImageView = (PhotoView) findViewById(R.id.crop_image_view);
        cropImageCorner = (ImageView) findViewById(R.id.corner_image);
        rotateButton = (Button) findViewById(R.id.rotate_button);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, width);
        cropImageView.setLayoutParams(layoutParams);
        cropImageCorner.setLayoutParams(layoutParams);


        cropImageView.setOnScaleChangeListener(new PhotoViewAttacher.OnScaleChangeListener() {
            @Override
            public void onScaleChange(float scaleFactor, float focusX, float focusY) {
                Log.d("gagagaga", focusX + "  /   " + focusY + "    //    " + scaleFactor);

            }
        });


        intent = getIntent();

        String path = intent.getStringExtra(IMAGE_PATH);
        if (intent.getStringExtra(IMAGE_PATH).contains("storage/emulated")) {
            path = FILE_PREFIX + intent.getStringExtra(IMAGE_PATH);
        }

        ImageLoader.getInstance().displayImage(path
                , cropImageView, new SimpleImageLoadingListener());

        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.setRotationBy(90);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            saveImage();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void saveImage() {

        new Thread(new Runnable() {

            public void run() {

                Looper.prepare();
                fileName = "image_edit_" + String.format("%03d", count) + ".jpg";
                count++;

                SharedPreferences sharedPreferences = getSharedPreferences("pics_art_video", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("edited_count", count);
                editor.commit();

                File file = new File(myDir, fileName);

                Bitmap bitmap = cropImageView.getVisibleRectangleBitmap();
                bitmap = Utils.scaleCenterCrop(bitmap, 720, 720);
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent data = new Intent();
                data.putExtra(EDITED_IMAGE_PATH, file.getAbsolutePath());
                data.putExtra(INDEX, intent.getIntExtra(INDEX, -1));
                if (intent.getBooleanExtra("isEdited", false) == true) {
                    new File(intent.getStringExtra(IMAGE_PATH)).delete();
                    Log.d("gagagagag", "donr");
                }
                setResult(RESULT_OK, data);
                finish();
            }
        }
        ).start();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

}

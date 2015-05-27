package activity;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.intern.picsartvideo.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import uk.co.senab.photoview.PhotoView;
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
    private EditText editText;
    private Button rotateButton;
    private Button button;
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
        editText = (EditText) findViewById(R.id.edt_txt);
        rotateButton = (Button) findViewById(R.id.rotate_button);
        button = (Button) findViewById(R.id.del);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, width);
        cropImageView.setLayoutParams(layoutParams);
        cropImageCorner.setLayoutParams(layoutParams);

        intent = getIntent();

        Animation scaleAnimation = new ScaleAnimation(0, 1, 1, 1);
        scaleAnimation.setDuration(750);
        editText.startAnimation(scaleAnimation);
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadow = new View.DragShadowBuilder(editText);
                float screenX = v.getLeft() + event.getX();
                float screenY = v.getTop() + event.getY();

                Log.d("gagagag",screenX+"   /   "+screenY);
                v.startDrag(data, shadow, null, 0);
                return false;
            }
        });

        cropImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return false;
            }
        });

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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = cropImageView.getVisibleRectangleBitmap();
                editText.setDrawingCacheEnabled(true);
                Bitmap b = editText.getDrawingCache();
                Canvas canvas = new Canvas(bitmap);
                canvas.drawBitmap(b, editText.getX(), editText.getY(), null);
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(new File(myDir, "gag.jpg"));
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
                }
                setResult(RESULT_OK, data);
                finish();
            }
        }
        ).start();
        Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}

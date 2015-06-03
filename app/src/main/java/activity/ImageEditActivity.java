package activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import com.example.intern.picsartvideo.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;


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
    private Button addStickerButton;
    private Button addTextButton;
    private Intent intent;
    private String fileName;
    private int count = 0;
    float x = 0;
    float y = 0;

    int stickerIndex;

    private FragmentManager fragmentManager = getFragmentManager();
    private RecyclerViewFragment multiSelectFragment = new RecyclerViewFragment();
    private boolean fragmentIsOpen = false;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);

        init();

    }

    private void init() {

        context = this;
        SharedPreferences sharedPreferences = getSharedPreferences("pics_art_video", MODE_PRIVATE);
        count = sharedPreferences.getInt("edited_count", 0);

        cropImageView = (PhotoView) findViewById(R.id.crop_image_view);
        cropImageCorner = (ImageView) findViewById(R.id.corner_image);
        editText = (EditText) findViewById(R.id.edt_txt);
        rotateButton = (Button) findViewById(R.id.rotate_button);
        addStickerButton = (Button) findViewById(R.id.add_sticker_button);
        addTextButton = (Button) findViewById(R.id.add_text_button);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, width);
        cropImageView.setLayoutParams(layoutParams);
        cropImageCorner.setLayoutParams(layoutParams);

        intent = getIntent();

        editText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                editText.startDrag(ClipData.newPlainText("", ""), new View.DragShadowBuilder(editText), null, 0);
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

        addTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getVisibility() == View.GONE) {
                    editText.setVisibility(View.VISIBLE);
                } else {
                    editText.setVisibility(View.GONE);
                }
            }
        });

        addStickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fragmentIsOpen == false) {

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.slide_out, R.anim.slide_out);
                    fragmentTransaction.add(R.id.frame_layout, multiSelectFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    fragmentIsOpen = true;

                    ArrayList<Drawable> bitmaps=new ArrayList<>();
                    bitmaps.add(getResources().getDrawable(R.drawable.sticker1));
                    bitmaps.add(getResources().getDrawable(R.drawable.sticker2));
                    bitmaps.add(getResources().getDrawable(R.drawable.sticker3));

                    multiSelectFragment.setAdapter(bitmaps);

                    cropImageCorner.setOnTouchListener(myTouchListener);

                } else {

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_in);
                    fragmentTransaction.remove(multiSelectFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    fragmentIsOpen = false;

                    cropImageCorner.setOnTouchListener(null);

                }
            }
        });

        cropImageView.setOnDragListener(new View.OnDragListener() {

            @Override
            public boolean onDrag(View v, DragEvent event) {
                // TODO Auto-generated method stub
                final int action = event.getAction();
                switch (action) {

                    case DragEvent.ACTION_DRAG_STARTED:
                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;

                    case DragEvent.ACTION_DROP:
                        x = event.getX();
                        y = event.getY();
                        editText.setX(x - editText.getWidth() / 2);
                        editText.setY(y - editText.getHeight() / 2);
                        //Log.d("gagagagaga3", event.getX() + "");
                        break;

                    case DragEvent.ACTION_DRAG_ENDED:
                        break;

                    default:
                        break;
                }
                return true;
            }
        });

        multiSelectFragment.setOnShapeChangedListener(new RecyclerViewFragment.OnStickerChangedListener() {
            @Override
            public void onStickerChanged(int shapeIndex) {
                Log.d("gagagagaga", shapeIndex + "");
                stickerIndex =shapeIndex;
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_in);
                fragmentTransaction.remove(multiSelectFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                fragmentIsOpen = false;

                cropImageCorner.setOnTouchListener(myTouchListener);
            }
        });

    }

    public static Context getContext() {
        return context;
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

                if (editText.getVisibility() == View.VISIBLE && !editText.getText().toString().equals("")) {

                    editText.setDrawingCacheEnabled(true);
                    Bitmap b = editText.getDrawingCache();
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawBitmap(b, editText.getX(), editText.getY(), null);
                }

                //bitmap = Utils.scaleCenterCrop(bitmap, 720, 720);

                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
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

    View.OnTouchListener myTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            Bitmap bitmap = cropImageView.getDrawingCache();
            Canvas canvas = new Canvas(bitmap);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap stickerBitmap=null;
            switch (stickerIndex) {

                case 0:
                    stickerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sticker1, options);
                    break;

                case 1:
                    stickerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sticker2, options);
                    break;

                case 2:
                    stickerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sticker3, options);
                    break;
                default:
                    break;
            }

            canvas.drawBitmap(stickerBitmap, null, new Rect((int) motionEvent.getX() - 75, (int) motionEvent.getY() - 75, (int) motionEvent.getX() + 75, (int) motionEvent.getY() + 75), null);
            cropImageView.setImageBitmap(bitmap);
            cropImageCorner.setOnTouchListener(null);

            return false;
        }
    };

}

package activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;


import com.example.intern.picsartvideo.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by intern on 6/4/15.
 */
public class EditTextDialod extends Dialog implements  AdapterView.OnItemClickListener {

    private GridView shapeGrid;
    private Context context;

    private OnShapeChangedListener onShapeChangedListener;

    private int categoryIndex = 0;

    public EditTextDialod(Context context) {
        super(context, R.style.Base_Theme_AppCompat_Dialog);
        this.context = context;
        setContentView(R.layout.edit_text_dialog);


//        findViewById(R.id.shape_btn_close).setOnClickListener(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shapeGrid = (GridView) findViewById(R.id.shape_grid);
        shapeGrid.setAdapter(new ShapeAdapter(context));
        shapeGrid.setOnItemClickListener(this);
    }

//    @Override
//    public void onClick(View v) {
//        dismiss();
//    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (onShapeChangedListener != null)
            onShapeChangedListener.onShapeChanged(position);
        dismiss();
    }

    public void setOnShapeChangedListener(OnShapeChangedListener l) {
        onShapeChangedListener = l;
    }

    public static interface OnShapeChangedListener {
        public void onShapeChanged(int shapeIndex);
    }

    private class ShapeAdapter extends BaseAdapter{
        Context context;

        public ShapeAdapter(Context c){
            context = c;
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView != null)
                view = convertView;
            else {
                view = getLayoutInflater().inflate(R.layout.gallery_item, parent,false);
            }
            view.findViewById(R.id.gag);
            return view;
        }
    }


}

package adapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.intern.picsartvideo.R;

import java.util.ArrayList;

import activity.ImageEditActivity;
import utils.Utils;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private ArrayList<CharSequence> mDataset;
    private Context context;

    public MyAdapter(ArrayList<CharSequence> myDataset) {
        mDataset = myDataset;
        this.context = context;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item
                , parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Bitmap largeIcon = BitmapFactory.decodeResource(ImageEditActivity.getContext().getResources(), R.drawable.icon);
        holder.icon.setImageBitmap(Utils.scaleCenterCrop(largeIcon, 70, 70));

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"boooo",Toast.LENGTH_LONG).show();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.ic);

        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void add(int position, String item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

}
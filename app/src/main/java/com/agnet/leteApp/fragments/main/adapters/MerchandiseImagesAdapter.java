package com.agnet.leteApp.fragments.main.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.fragments.main.ProjectFragment;
import com.agnet.leteApp.fragments.main.merchandise.MerchandiseCameraFragment;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.MerchandiseImg;
import com.agnet.leteApp.models.ProjectType;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.util.Collections;
import java.util.List;

/**
 * Created by alicephares on 8/5/16.
 */
public class MerchandiseImagesAdapter extends RecyclerView.Adapter<MerchandiseImagesAdapter.ViewHolder> {

    private List<Bitmap> merchandiseImg= Collections.emptyList();
    private LayoutInflater inflator;
    private Context c;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private int selected_position = 0;



    // Provide a suitable constructor (depends on the kind of dataset)
    public MerchandiseImagesAdapter(Context c, List<Bitmap> merchandiseImg) {
        this.merchandiseImg = merchandiseImg;
        this.inflator = LayoutInflater.from(c);
        this.c = c;

        _preferences = c.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = inflator.inflate(R.layout.card_merchandise_imgs, parent, false);
        // set the view's size, margins, padding and layout parameters

        ViewHolder vh = new ViewHolder(c, v);
        return vh;
    }

    int count = 0;

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //get a position of a current saleItem
        final Bitmap currentImg = merchandiseImg.get(position);
        holder.mImg.setImageBitmap(currentImg);
        //glide(holder.mImg, currentImg);

       /* if (selected_position == position) {
            holder.mWrapper.setBackgroundResource(R.drawable.round_corners_blue);
            holder.mName.setTextColor(Color.parseColor("#ffffff"));
            holder.mIconWrapper.setBackgroundResource(R.drawable.round_corners_blue);
            holder.mWrapper.setPadding(5, 5, 5, 5);

//            fragment.setProjectType(currentType.getName());

            glide(holder.mIcon, currentType.getSelectedIcon());
        } else {
            holder.mWrapper.setBackgroundResource(R.drawable.round_corners_white);
            holder.mName.setTextColor(Color.parseColor("#000000"));
            holder.mIconWrapper.setBackgroundResource(R.drawable.round_corners_with_grey_bg);
//            holder.mWrapper.setPadding(10,10,10,10);
            glide(holder.mIcon, currentType.getIcon());
        }*/

        holder.mWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              //  if (holder.getAdapterPosition() == RecyclerView.NO_POSITION) return;

                // Updating old as well as new positions
             /*   notifyItemChanged(selected_position);
                selected_position = holder.getAdapterPosition();
                notifyItemChanged(selected_position);*/

                //set type
//                fragment.setProjectType(currentType.getName());

             /*   if(position != 3){
                    //call projects
                    //convert locally swahili names for types to english
                    if(currentType.getName() == "Mauzo"){
                        fragment.getPorjects("Sales");
                    }else if(currentType.getName() == "Uwepo"){
                        fragment.getPorjects("Mapping");
                    }else {
                        fragment.getPorjects("Merchandise");
                    }

                }else {
                    //call outlets
                    fragment.getUserOutlets();
                }*/

                //store type of the project


            }
        });
    }

    private void glide(ImageView view, Bitmap bm) {
        Glide.with(view.getContext())
                .asBitmap()
                .load(bm)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {}
                });
    }

    private int displayImg(ImageView view, String url) {
        Context context = view.getContext();
        int id = context.getResources().getIdentifier(url, "drawable", context.getPackageName());

        return id;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mWrapper, mIconWrapper;
        public ImageView mImg;


        public ViewHolder(Context context, View view) {
            super(view);

            mWrapper = view.findViewById(R.id.wrapper);
            mImg = view.findViewById(R.id.img);

        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return merchandiseImg.size();
    }


}
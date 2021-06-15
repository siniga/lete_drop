package com.agnet.leteApp.fragments.main.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.fragments.main.ProjectFragment;
import com.agnet.leteApp.fragments.main.mapping.MappingFormListFragment;
import com.agnet.leteApp.fragments.main.merchandise.MerchandiseFormFragment;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.Project;
import com.agnet.leteApp.models.ProjectType;
import com.agnet.leteApp.service.Endpoint;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.vision.text.Line;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

/**
 * Created by alicephares on 8/5/16.
 */
public class ProjectTypeAdapter extends RecyclerView.Adapter<ProjectTypeAdapter.ViewHolder> {

    private List<ProjectType> types = Collections.emptyList();
    private LayoutInflater inflator;
    private Context c;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private ProjectFragment fragment;
    private int selected_position = 0;



    // Provide a suitable constructor (depends on the kind of dataset)
    public ProjectTypeAdapter(Context c, List<ProjectType> types, ProjectFragment fragment) {
        this.types = types;
        this.inflator = LayoutInflater.from(c);
        this.c = c;
        this.fragment = fragment;

        _preferences = c.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = inflator.inflate(R.layout.card_project_type, parent, false);
        // set the view's size, margins, padding and layout parameters

        ViewHolder vh = new ViewHolder(c, v);
        return vh;
    }

    int count = 0;

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //get a position of a current saleItem
        final ProjectType currentType = types.get(position);
        holder.mName.setText(currentType.getName());
        glide(holder.mIcon, currentType.getIcon());

        if (selected_position == position) {
            holder.mWrapper.setBackgroundResource(R.drawable.round_corners_blue);
            holder.mName.setTextColor(Color.parseColor("#ffffff"));
            holder.mIconWrapper.setBackgroundResource(R.drawable.round_corners_blue);
            holder.mWrapper.setPadding(5, 5, 5, 5);

            glide(holder.mIcon, currentType.getSelectedIcon()
            );
            fragment.getPorjects(currentType.getName());
        } else {
            holder.mWrapper.setBackgroundResource(R.drawable.round_corners_white);
            holder.mName.setTextColor(Color.parseColor("#000000"));
            holder.mIconWrapper.setBackgroundResource(R.drawable.round_corners_with_grey_bg);
//            holder.mWrapper.setPadding(10,10,10,10);
            glide(holder.mIcon, currentType.getIcon());
        }

        holder.mWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.getAdapterPosition() == RecyclerView.NO_POSITION) return;

                // Updating old as well as new positions
                notifyItemChanged(selected_position);
                selected_position = holder.getAdapterPosition();
                notifyItemChanged(selected_position);

                fragment.getPorjects(currentType.getName());
            }
        });


    }

    private void glide(ImageView view, String url) {
        Glide.with(c).load(displayImg(view, url))
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .error(R.drawable.ic_place_holder)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(view);
    }

    private int displayImg(ImageView view, String url) {
        Context context = view.getContext();
        int id = context.getResources().getIdentifier(url, "drawable", context.getPackageName());

        return id;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mWrapper, mIconWrapper;
        public TextView mName;
        public ImageView mIcon;


        public ViewHolder(Context context, View view) {
            super(view);

            mWrapper = view.findViewById(R.id.type_wrapper);
            mName = view.findViewById(R.id.type_name);
            mIcon = view.findViewById(R.id.icon);
            mIconWrapper = view.findViewById(R.id.icon_wrapper);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return types.size();
    }


}
package com.agnet.leteApp.fragments.main.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.fragments.main.mapping.MappingFormListFragment;
import com.agnet.leteApp.fragments.main.merchandise.MerchandiseFormFragment;
import com.agnet.leteApp.fragments.main.sales.ProductsFragment;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.Project;
import com.agnet.leteApp.service.Endpoint;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.Collections;
import java.util.List;

/**
 * Created by alicephares on 8/5/16.
 */
public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

    private List<Project> projects = Collections.emptyList();
    private LayoutInflater inflator;
    private Context c;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;


    // Provide a suitable constructor (depends on the kind of dataset)
    public ProjectAdapter(Context c, List<Project> projects) {
        this.projects = projects;
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
        View v = inflator.inflate(R.layout.card_project, parent, false);
        // set the view's size, margins, padding and layout parameters

        ViewHolder vh = new ViewHolder(c, v);
        return vh;
    }

    int count = 0;

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //get a position of a current saleItem
        final Project currentProject = projects.get(position);

        holder.mName.setText(currentProject.getName());
        holder.mClientName.setText(currentProject.getClient());
        holder.mProjectType.setText(currentProject.getType());

        holder.mWrapper.setOnClickListener(view -> {
                    _editor.putInt("CLIENT_ID", currentProject.getClientId());
                    _editor.putString("CLIENT", currentProject.getClient());
                    _editor.putInt("PROJECT_ID", currentProject.getId());
                    _editor.putString("PROJECT_NAME", currentProject.getName());
                    _editor.commit();

                    if(currentProject.getType().equals("Mapping")){
                        new FragmentHelper(c).replaceWithbackStack(new MappingFormListFragment(), "MappingFormListFragment", R.id.fragment_placeholder);
                    }else if(currentProject.getType().equals("Merchandise")){
                        new FragmentHelper(c).replaceWithbackStack(new MerchandiseFormFragment(), "MerchandiseFragment", R.id.fragment_placeholder);
                    }else{
                        new FragmentHelper(c).replaceWithbackStack(new ProductsFragment(), "ProductsFragment", R.id.fragment_placeholder);
                    }

                }


        );

        Endpoint.setStorageUrl(currentProject.getImg());
        String url = Endpoint.getStorageUrl();

        Glide.with(c).load(url)
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
                }).into(holder.mImg);
    }

    private int displayImg(ImageView view, String url) {
        Context context = view.getContext();
        int id = context.getResources().getIdentifier(url, "drawable", context.getPackageName());

        return id;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mWrapper;
        public TextView mName;
        public ImageView mImg;
        public TextView mClientName, mProjectType;


        public ViewHolder(Context context, View view) {
            super(view);

            mWrapper = view.findViewById(R.id.project_wrapper);
            mName = view.findViewById(R.id.project_name);
            mImg = view.findViewById(R.id.project_img);
            mClientName = view.findViewById(R.id.client_name);
            mProjectType = view.findViewById(R.id.project_type);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return projects.size();
    }


}
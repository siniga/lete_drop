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
import com.agnet.leteApp.fragments.main.mapping.MappingQuestionnaireFragment;
import com.agnet.leteApp.fragments.main.merchandise.MerchandiseFormFragment;
import com.agnet.leteApp.helpers.DateHelper;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.Form;
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
public class FormAdapter extends RecyclerView.Adapter<FormAdapter.ViewHolder> {

    private List<Form> forms= Collections.emptyList();
    private LayoutInflater inflator;
    private Context c;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;


    // Provide a suitable constructor (depends on the kind of dataset)
    public FormAdapter(Context c, List<Form> forms) {
        this.forms = forms;
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
        View v = inflator.inflate(R.layout.card_form, parent, false);
        // set the view's size, margins, padding and layout parameters

        ViewHolder vh = new ViewHolder(c, v);
        return vh;
    }

    int count = 0;

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //get a position of a current saleItem
        final Form currentForm = forms.get(position);
        holder.mName.setText(currentForm.getName());
        holder.mCreatedAt.setText(currentForm.getCreated_at());
        holder.mCreatedBy.setText(currentForm.getCreated_by());

        holder.mWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _editor.putInt("FORM_ID", currentForm.getId());
                _editor.putString("FORM_NAME", currentForm.getName());
                _editor.putString("TIME_STARTED",   DateHelper.getCurrentTime());

                _editor.commit();

                new FragmentHelper(c).replaceWithbackStack(new MappingQuestionnaireFragment()," MappingQuestionnaireFragment", R.id.fragment_placeholder);
            }
        });
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mWrapper;
        public TextView mName;
        public ImageView mImg;
        public TextView mCreatedAt, mCreatedBy;


        public ViewHolder(Context context, View view) {
            super(view);

            mWrapper = view.findViewById(R.id.wrapper);
            mName = view.findViewById(R.id.form_name);
            mImg = view.findViewById(R.id.form_img);
            mCreatedAt = view.findViewById(R.id.created_at);
            mCreatedBy = view.findViewById(R.id.created_by);



        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return forms.size();
    }


}
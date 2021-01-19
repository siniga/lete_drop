package com.agnet.lete_drop.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agnet.lete_drop.R;
import com.agnet.lete_drop.activities.MainActivity;
import com.agnet.lete_drop.dialogs.BottomSheetDialog;
import com.agnet.lete_drop.helpers.DatabaseHandler;
import com.agnet.lete_drop.models.RoutePath;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.geojson.Point;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegistrationFragment extends Fragment {

    private FragmentActivity _c;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private DatabaseHandler _dbHandler;
    private Button _submitOrderBtn;
    private LinearLayout _errorMsg;
    private List<RoutePath> _timeList = new ArrayList();
    private ShimmerFrameLayout _shimmerFrameLayout;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        _c = getActivity();

        _dbHandler = new DatabaseHandler(_c);

        _preferences = _c.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();

        _shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        _shimmerFrameLayout.setVisibility(View.GONE);

        final EditText phoneNum = view.findViewById(R.id.phone_input);
        _errorMsg = view.findViewById(R.id.error_msg);

        if (!_dbHandler.getUserPhone().equals("")) {
            phoneNum.setText("" + _dbHandler.getUserPhone());
        }

        double mLat = Double.parseDouble(_preferences.getString("mLATITUDE", null));
        double mLong = Double.parseDouble(_preferences.getString("mLONGITUDE", null));
        String drivers = _preferences.getString("DRIVERS", null);

        _submitOrderBtn = view.findViewById(R.id.submit_order);
        _submitOrderBtn.setVisibility(View.GONE);
    //    _errorMsg.setVisibility(View.VISIBLE);


        try {
            JSONArray driversArr = new JSONArray(drivers);

            if(_dbHandler.getDriverId() == 0){
                for (int i = 0; i <= driversArr.length() - 1; i++) {

                    JSONObject driversObjs = driversArr.getJSONObject(i);
                    Point origin = Point.fromLngLat(mLong, mLat);
                    int driverId =  driversObjs.getInt("id");
                    String driversPhone =  driversObjs.getString("phone");
                    // Log.d("OrderActivity1",partnerObjs.toString());

                    Point destination = Point.fromLngLat(driversObjs.getDouble("lng"), driversObjs.getDouble("lat"));
                    findNearestWharehouse(origin, destination,  driversPhone, driverId);

                }
            }else {
                _submitOrderBtn.setVisibility(View.VISIBLE);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }



        _submitOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(_c, "WOrk", Toast.LENGTH_SHORT).show();

                if(_dbHandler.getDriverId() == 0){
                    getTimeList();

                }

                if (!phoneNum.getText().toString().isEmpty()) {

                    //show bottom sheet
                    BottomSheetDialog bottomSheetDialogFragment = new BottomSheetDialog();
                    bottomSheetDialogFragment.show(getActivity().getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                    bottomSheetDialogFragment.setCancelable(false);
                    bottomSheetDialogFragment.setFragmentContext(RegistrationFragment.this);

                    //create user if not created
                    _dbHandler.createUser(phoneNum.getText().toString(),"anonymous", "",0);


                } else {
                    Toast.makeText(_c, "Weka namba ya simu ili kuendelea!", Toast.LENGTH_LONG).show();
                }

            }
        });


        return view;

    }


    private void findNearestWharehouse(Point origin, Point destination, String phone, int driverId) {
        _shimmerFrameLayout.setVisibility(View.VISIBLE);
        _shimmerFrameLayout.startShimmerAnimation();

        //TODO:use matrix api to query for time
        NavigationRoute.builder(_c)
                .accessToken(getString(R.string.mapbox_access_token))
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {

                        Double duration = response.body().routes().get(0).duration();
                        int time = duration.intValue();

                        _timeList.add(new RoutePath(phone, time, 1, driverId));

                        _submitOrderBtn.setVisibility(View.VISIBLE);
                        _errorMsg.setVisibility(View.GONE);
                        _shimmerFrameLayout.setVisibility(View.GONE);

                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        _shimmerFrameLayout.setVisibility(View.GONE);
                    }
                });
    }


    public void getTimeList() {

        int min = _timeList.get(0).getTime();

        if (_timeList.size() == 0) {

            Toast.makeText(_c, "Hakuna Muuzaji karibu yako! ", Toast.LENGTH_LONG).show();
            return;

        } else if (_timeList.size() == 1) {

            _editor.putString("CLOSEST_DRIVER_PHONE", _timeList.get(0).getPhone());
            _editor.putInt("CLOSEST_DRIVER_ID", _timeList.get(0).getDriverId());
            _editor.commit();

            _dbHandler.addDriverId( _timeList.get(0).getDriverId());

        } else {
            for (int i = 0; i < _timeList.size(); i++) {

                if (_timeList.get(i).getTime() <= min) {

                    min = _timeList.get(i).getTime();

                    _editor.putString("CLOSEST_DRIVER_PHONE", _timeList.get(i).getPhone());
                    _editor.putInt("CLOSEST_DRIVER_ID",_timeList.get(i).getDriverId());
                    _editor.commit();

                    _dbHandler.addDriverId(_timeList.get(i).getDriverId());

                }
            }
            }
        }


    @Override
    public void onResume() {
        super.onResume();

        TextView toolbarTitle = _c.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Jisajili");

        RelativeLayout viewCartWrapper = _c.findViewById(R.id.open_cart_wrapper);
        viewCartWrapper.setVisibility(View.GONE);


        //show back button
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

}

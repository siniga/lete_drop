package com.agnet.leteApp.fragments.main.mapping;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.agnet.leteApp.R;
import com.agnet.leteApp.application.mSingleton;
import com.agnet.leteApp.fragments.main.SuccessFragment;
import com.agnet.leteApp.fragments.main.adapters.FormAdapter;
import com.agnet.leteApp.helpers.DateHelper;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.Answer;
import com.agnet.leteApp.models.Form;
import com.agnet.leteApp.models.Option;
import com.agnet.leteApp.models.Quesionnaire;
import com.agnet.leteApp.models.ResponseData;
import com.agnet.leteApp.models.User;
import com.agnet.leteApp.service.Endpoint;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.vision.text.Line;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MappingQuestionnaireFragment extends Fragment {

    private FragmentActivity _c;
    private Gson _gson;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private ProgressBar _progressBar;
    private String Token;
    private User _user;
    private int _formId;
    private LinearLayout _questionnaireWrapper;
    private List post = new ArrayList();
    private List<Answer> _answers = new ArrayList<>();
    private Button _submitBtn;
    private LinearLayout _transparentLoader;
    private String _timeStarted;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapping_questionnaire, container, false);
        _c = getActivity();
        _gson = new Gson();
        _preferences = _c.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();

        _questionnaireWrapper = view.findViewById(R.id.questionnaire_main_wrapper);
        TextView formNameTxt = view.findViewById(R.id.form_name);
        TextView startedAtTxt = view.findViewById(R.id.started_at);
        _submitBtn = view.findViewById(R.id.submit_btn);
        _progressBar = view.findViewById(R.id.progress_bar);
        _transparentLoader = view.findViewById(R.id.transparent_loader);

        try {
            _user = _gson.fromJson(_preferences.getString("User", null), User.class);
            Token = _preferences.getString("TOKEN", null);
            _formId = _preferences.getInt("FORM_ID", 0);
            String formName = _preferences.getString("FORM_NAME", null);
            _timeStarted = _preferences.getString("TIME_STARTED", null);

            formNameTxt.setText(formName);
            startedAtTxt.setText(_timeStarted);

        } catch (NullPointerException e) {

        }

        _submitBtn.setOnClickListener(view1 -> {

            for (Answer answer : _answers) {
                if (answer.getAnswer().isEmpty()) {
                    Toast.makeText(_c, "Ingiza " + answer.getQuestion() + "!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            postFormResults();

            _submitBtn.setClickable(false);
            _transparentLoader.setVisibility(View.VISIBLE);
            _progressBar.setVisibility(View.VISIBLE);

        });

        getQuestionnare();

        return view;
    }

    public void createQuestionnaire(List<Quesionnaire> questions) {
        for (int i = 0; i <= questions.size() - 1; i++) {

            _answers.add(new Answer("", questions.get(i).getId()));

            switch (questions.get(i).getTypeId()) {
                case 1:
                    addTextBox(questions.get(i), i);
                    break;
                case 2:
                    addRadioBox(questions.get(i).getQuestion(), questions.get(i).getOptions(), i);
                    break;
                case 3:
                    addSelectBox(questions.get(i).getQuestion(), questions.get(i).getOptions(), i);
                    break;
                case 4:
                    addCheckbox(questions.get(i).getQuestion(), questions.get(i).getOptions(), i);
                    break;
                case 5:
                    addnumericBox(questions.get(i).getQuestion(), i);
                    break;
                case 9:
                    addLongitude(questions.get(i).getQuestion(), i);
                    break;
                case 10:
                    addLatitude(questions.get(i).getQuestion(), i);
                    break;
                case 11:
                    addStartTime(questions.get(i).getQuestion(), i);
                    break;
                case 12:
                    addCompleteTime(questions.get(i).getQuestion(), i);
                    break;
                case 13:
                    addLocation(questions.get(i).getQuestion(), i);
                    break;
                case 14:
                    addPostedBy(questions.get(i).getQuestion(), i);
                    break;
                default:
                    break;
            }
        }
    }

    public void addTextBox(Quesionnaire question, int indx) {
        LinearLayout parent = createControlParent();
        parent.addView(createHeader(question.getQuestion()));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                110
        );
        params.setMargins(0, 20, 0, 0);

        EditText textbox = new EditText(_c);
        textbox.setLayoutParams(params);
        textbox.setBackgroundResource(R.drawable.round_corners_with_stroke_grey);
        textbox.setHint("Ingiza jibu hapa");
        textbox.setTextSize(14);
        textbox.setPadding(25, 10, 10, 10);

        _answers.get(indx).setQuestion(question.getQuestion());
        textbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable mEdit) {
                String text = mEdit.toString();
                _answers.get(indx).setAnswer(text);


            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        parent.addView(textbox);


    }


    public void addRadioBox(String question, List<Option> options, int indx) {
        LinearLayout.LayoutParams modifyParent = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        LinearLayout parent = createControlParent();
        parent.setLayoutParams(modifyParent);
        parent.setMinimumHeight(300);

        parent.addView(createHeader(question));

        RadioGroup btnWrapper = new RadioGroup(_c);
        btnWrapper.setLayoutParams(modifyParent);
        btnWrapper.setPadding(0, 0, 0, 60);

        for (Option option : options) {
            LinearLayout.LayoutParams radioParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    100
            );

            radioParam.setMargins(0, 0, 0, 10);

            RadioButton rdbtn = new RadioButton(_c);
            rdbtn.setText(option.getOption());
            rdbtn.setId(option.getId());

            if (Build.VERSION.SDK_INT >= 21) {

                ColorStateList colorStateList = new ColorStateList(
                        new int[][]{

                                new int[]{-android.R.attr.state_enabled}, //disabled
                                new int[]{android.R.attr.state_enabled} //enabled
                        },
                        new int[]{

                                Color.parseColor("#FFFFFF") //disabled
                                , Color.parseColor("#001689") //enabled

                        }
                );


                rdbtn.setButtonTintList(colorStateList);//set the color tint list
//                radio.invalidate(); //could not be necessary
            }
            btnWrapper.addView(rdbtn);

        }
        _answers.get(indx).setQuestion(question);
        btnWrapper.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioGroup.findViewById(i);
                int index = radioGroup.indexOfChild(getView().findViewById(radioGroup.getCheckedRadioButtonId()));
                RadioButton r = (RadioButton) radioGroup.getChildAt(index);
                String selectedtext = r.getText().toString();

                _answers.get(indx).setAnswer(selectedtext);


            }
        });

        parent.addView(btnWrapper);
    }

    public void addSelectBox(String question, List<Option> options, int indx) {
        LinearLayout parent = createControlParent();
        parent.addView(createHeader(question));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                110
        );
        params.setMargins(0, 30, 0, 10);

        Spinner spinner = new Spinner(_c);
        ArrayAdapter optionAdapter = new ArrayAdapter(_c, android.R.layout.simple_spinner_dropdown_item, options);
        spinner.setAdapter(optionAdapter);
        spinner.setBackgroundResource(R.drawable.round_corners_with_stroke_grey);
        spinner.setLayoutParams(params);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the value selected by the user
                // e.g. to store it as a field or immediately call a method
                Option option = (Option) parent.getSelectedItem();
                _answers.get(indx).setAnswer(option.getOption());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        parent.addView(spinner);
    }

    public void addCheckbox(String question, List<Option> options, int indx) {
        LinearLayout parent = createControlParent();
        parent.addView(createHeader(question));

        for (Option option : options) {
            LinearLayout.LayoutParams checkboxParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    100
            );

            CheckBox checkBox = new CheckBox(_c);
            checkBox.setText(option.getOption());
            checkBox.setTextColor(Color.parseColor("#001689"));
            checkBox.setButtonDrawable(R.drawable.custom_checkbox_colors);
            parent.addView(checkBox);

            _answers.get(indx).setQuestion(question);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    _answers.get(indx).setAnswer(compoundButton.getText().toString());
                }
            });
        }
    }

    public void addnumericBox(String question, int indx) {
        LinearLayout parent = createControlParent();
        parent.addView(createHeader(question));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                110
        );
        params.setMargins(0, 30, 0, 0);

        EditText textbox = new EditText(_c);
        textbox.setLayoutParams(params);
        textbox.setBackgroundResource(R.drawable.round_corners_with_stroke_grey);
        textbox.setInputType(InputType.TYPE_CLASS_NUMBER);
        textbox.setHint("Ingiza jibu hapa");
        textbox.setTextSize(14);
        textbox.setPadding(25, 10, 10, 10);
        _answers.get(indx).setQuestion(question);

        textbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable mEdit) {
                String text = mEdit.toString();

                _answers.get(indx).setAnswer(text);

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        parent.addView(textbox);


    }


    private void addLatitude(String question, int i) {
        _answers.get(i).setQuestion(question);
        _answers.get(i).setAnswer(_preferences.getString("mLONGITUDE", null));
    }

    private void addLongitude(String question, int i) {
        _answers.get(i).setQuestion(question);
        _answers.get(i).setAnswer(_preferences.getString("mLATITUDE", null));
    }

    private void addStartTime(String question, int i) {
        _answers.get(i).setQuestion(question);
        _answers.get(i).setAnswer(DateHelper.getCurrentDate()+" "+_timeStarted);
    }
    private void addCompleteTime(String question, int i) {
        _answers.get(i).setQuestion(question);
        _answers.get(i).setAnswer(DateHelper.getCurrentDate()+" "+DateHelper.getCurrentTime());
    }

    private void addLocation(String question, int i) {

        //Get address base on location
        try {
            Geocoder geo = new Geocoder(_c.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(Double.parseDouble(_preferences.getString("mLATITUDE", null)), Double.parseDouble( _preferences.getString("mLONGITUDE", null)), 1);
            if (addresses.isEmpty()) {

            } else {
                if (addresses.size() > 0) {

                    _answers.get(i).setQuestion(question);
                    _answers.get(i).setAnswer(addresses.get(0).getSubAdminArea());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void addPostedBy(String question, int i) {
        _answers.get(i).setQuestion(question);
        _answers.get(i).setAnswer(_user.getName());
    }


    public TextView createHeader(String question) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        TextView textView = new TextView(_c);
        textView.setText(question);
        textView.setTextColor(Color.parseColor("#001689"));
        textView.setTextSize(20);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(0, 0, 50, 0);
        textView.setLayoutParams(params);


        return textView;

    }

    public LinearLayout createControlParent() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                300
        );
        LinearLayout controlParent = new LinearLayout(_c);
        controlParent.setLayoutParams(params);
        controlParent.setPadding(25, 25, 25, 15);
        controlParent.setOrientation(LinearLayout.VERTICAL);
        _questionnaireWrapper.addView(controlParent);

        return controlParent;
    }


    public void getQuestionnare() {

        Endpoint.setUrl("form/" + _formId);
        String url = Endpoint.getUrl();

        _transparentLoader.setVisibility(View.VISIBLE);
        _progressBar.setVisibility(View.VISIBLE);

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                response -> {

                    ResponseData res = _gson.fromJson(response, ResponseData.class);
                    if (res.getCode() == 200) {
                        Form formList = res.getForm();
                        List<Quesionnaire> questions = formList.getQuestions();
                        // Log.d("HERERESPONSE", _gson.toJson(formList.getQuestions()));

                        createQuestionnaire(questions);

                        _transparentLoader.setVisibility(View.GONE);
                        _progressBar.setVisibility(View.GONE);

                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                        _transparentLoader.setVisibility(View.GONE);
                        _progressBar.setVisibility(View.GONE);

                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if (response != null && response.data != null) {
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                            //TODO: display errors based on the message from the server
                            Toast.makeText(_c, "Kuna tatizo, angalia mtandao alafu jaribu tena", Toast.LENGTH_SHORT).show();
                        }


                    }
                }
        ) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + "" + Token);
                return params;
            }
        };
        mSingleton.getInstance(_c).addToRequestQueue(postRequest);

        postRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
    }

    public void postFormResults() {

        Endpoint.setUrl("results");
        String url = Endpoint.getUrl();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {


                    ResponseData res = _gson.fromJson(response, ResponseData.class);
                    if (res.getCode() == 201) {
                        new FragmentHelper(_c).replace(new MappingSuccessFragment(), "MappingSuccessFragment", R.id.fragment_placeholder);
                    } else {
                        Toast.makeText(_c, "Kuna tatizo la mtandao, jaribu tena", Toast.LENGTH_SHORT).show();
                    }

                    _submitBtn.setClickable(true);
                    _transparentLoader.setVisibility(View.VISIBLE);
                    _progressBar.setVisibility(View.VISIBLE);

                },
                error -> {
                    error.printStackTrace();

                    _submitBtn.setClickable(true);
                    _transparentLoader.setVisibility(View.VISIBLE);
                    _progressBar.setVisibility(View.VISIBLE);

                    NetworkResponse response = error.networkResponse;
                    String errorMsg = "";
                    if (response != null && response.data != null) {
                        String errorString = new String(response.data);
                        Log.i("log error", errorString);
                        //TODO: display errors based on the message from the server
                        Toast.makeText(_c, "Kuna tatizo, angalia mtandao alafu jaribu tena", Toast.LENGTH_SHORT).show();
                    }


                }
        ) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + "" + Token);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("results", _gson.toJson(_answers));
                params.put("user_id", "" + _user.getId());
                params.put("form_id", "" + _formId);
                params.put("lat", _preferences.getString("mLATITUDE", null));
                params.put("lng", _preferences.getString("mLONGITUDE", null));
                return params;
            }
        };
        mSingleton.getInstance(_c).addToRequestQueue(postRequest);

        postRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
    }


}



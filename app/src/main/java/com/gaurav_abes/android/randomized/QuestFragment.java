package com.gaurav_abes.android.randomized;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextSwitcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;

/**
 * Created by Gaurav on 26/09/17.
 */

public class QuestFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final String EXTRA_CHECKED_RADIO_BUTTON_ID = "extra_for_checked_radio_button_id";
    private static final String EXTRA_CHECKED_DISPLAY_TEXT = "extra_for_display_text";

    private TextSwitcher mTextSwitcher;
    private RadioGroup mRadioGroup;
    private RadioButton mTriviaRadioButton;
    private RadioButton mMathRadioButton;
    private RadioButton mDateRadioButton;
    private RadioButton mYearRadioButton;
    private EditText mNumberEditText;
    private EditText mMMEditText;
    private EditText mDDEditText;
    private LinearLayout mDateInputLinearLayout;
    private Button mSubmitButton;

    private String mCategory;
    private String mDisplayText;
    private int mCheckedRadioGroupId;

    public static QuestFragment newInstance() {
        return new QuestFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategory = "trivia";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_quest, container, false);
        mTextSwitcher = (TextSwitcher) v.findViewById(R.id.display_text);
        mRadioGroup = (RadioGroup) v.findViewById(R.id.radio_group);
        mTriviaRadioButton = (RadioButton) v.findViewById(R.id.trivia_radiobutton);
        mTriviaRadioButton.setOnClickListener(this);
        mTriviaRadioButton.setOnCheckedChangeListener(this);
        mMathRadioButton = (RadioButton) v.findViewById(R.id.math_radiobutton);
        mMathRadioButton.setOnClickListener(this);
        mMathRadioButton.setOnCheckedChangeListener(this);
        mDateRadioButton = (RadioButton) v.findViewById(R.id.date_radiobutton);
        mDateRadioButton.setOnClickListener(this);
        mDateRadioButton.setOnCheckedChangeListener(this);
        mYearRadioButton = (RadioButton) v.findViewById(R.id.year_radiobutton);
        mYearRadioButton.setOnClickListener(this);
        mYearRadioButton.setOnCheckedChangeListener(this);
        mNumberEditText = (EditText) v.findViewById(R.id.number_edittext);
        mDateInputLinearLayout = (LinearLayout) v.findViewById(R.id.date_linear_layout);
        mMMEditText = (EditText) v.findViewById(R.id.mm);
        mDDEditText = (EditText) v.findViewById(R.id.dd);
        mSubmitButton = (Button) v.findViewById(R.id.submit_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num;
                if (mRadioGroup.getCheckedRadioButtonId() == R.id.date_radiobutton)
                    num = mMMEditText.getText().toString() + "/" + mDDEditText.getText().toString();
                else
                    num = mNumberEditText.getText().toString();
                callQuestApi(mCategory, num);
            }
        });

        mCheckedRadioGroupId = R.id.trivia_radiobutton;
        mDisplayText = getString(R.string.facts);

        if (savedInstanceState != null) {
            mCheckedRadioGroupId = savedInstanceState.getInt(EXTRA_CHECKED_RADIO_BUTTON_ID);
            mDisplayText = savedInstanceState.getString(EXTRA_CHECKED_DISPLAY_TEXT);
        }

        mTextSwitcher.setText(mDisplayText);
        mRadioGroup.check(mCheckedRadioGroupId);
        dateToggled();
        return v;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_CHECKED_RADIO_BUTTON_ID, mCheckedRadioGroupId);
        outState.putString(EXTRA_CHECKED_DISPLAY_TEXT, mDisplayText);

    }

    private void callQuestApi(String category, String num) {
        getObservable(category, num)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        if (s.equals("")) {
                            this.onError(new Exception("API Error"));
                            return;
                        }
                        mDisplayText = s;
                        updateUI();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mDisplayText = "An error Occured. Please try again.";
                        mTextSwitcher.setText(mDisplayText);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private String urlConnectionWorker(String category, String num) {
        String apiUrl = getResources().getString(R.string.quest_api, num, category);
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String response = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                response += line;
            }
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private Observable<String> getObservable(final String category, final String num) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                try {
                    String result = urlConnectionWorker(category, num);
                    e.onNext(result);
                    e.onComplete();
                } catch (Exception ex) {
                    e.onError(ex);
                }
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            mCheckedRadioGroupId = compoundButton.getId();
            switch (compoundButton.getId()) {
                case R.id.trivia_radiobutton:
                    mCategory = "trivia";
                    break;
                case R.id.date_radiobutton:
                    mCategory = "date";
                    break;
                case R.id.year_radiobutton:
                    mCategory = "year";
                    break;
                case R.id.math_radiobutton:
                    mCategory = "math";
                    break;
                default:
                    break;
            }
        }
    }

    private void updateUI() {
        mTextSwitcher.setText(mDisplayText);
        mNumberEditText.setText("");
        mDDEditText.setText("");
        mMMEditText.setText("");
    }

    private void dateToggled() {
        if (mRadioGroup.getCheckedRadioButtonId() == R.id.date_radiobutton) {
            mDateInputLinearLayout.setVisibility(View.VISIBLE);
            mNumberEditText.setVisibility(GONE);
        } else {
            mDateInputLinearLayout.setVisibility(View.GONE);
            mNumberEditText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        dateToggled();
    }
}

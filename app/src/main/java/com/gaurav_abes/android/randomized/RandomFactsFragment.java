package com.gaurav_abes.android.randomized;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * Created by Gaurav on 26/09/17.
 */

public class RandomFactsFragment extends Fragment implements View.OnClickListener {

    private static final String DISPLAY_STRING_EXTRA = "display_string_extra";

    private TextSwitcher mDisplayTextSwitcher;
    private Button mTriviaButton;
    private Button mDateButton;
    private Button mMathButton;
    private Button mYearButton;

    private String mDisplayString;

    public static RandomFactsFragment newInstance() {
        return new RandomFactsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_random_facts, container, false);
        mDisplayTextSwitcher = (TextSwitcher) v.findViewById(R.id.display_text);
        mTriviaButton = v.findViewById(R.id.trivia_button);
        mDateButton = v.findViewById(R.id.date_button);
        mMathButton = v.findViewById(R.id.math_button);
        mYearButton = v.findViewById(R.id.year_button);
        mTriviaButton.setOnClickListener(this);
        mDateButton.setOnClickListener(this);
        mMathButton.setOnClickListener(this);

        mDisplayString = getString(R.string.facts);

        if (savedInstanceState != null) {
            mDisplayString = savedInstanceState.getString(DISPLAY_STRING_EXTRA);
        }
        mDisplayTextSwitcher.setText(mDisplayString);
        mYearButton.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        if (isIntentAvailable())
            switch (view.getId()) {
                case R.id.trivia_button:
                    callRandomFactApi("trivia");
                    break;
                case R.id.math_button:
                    callRandomFactApi("math");
                    break;
                case R.id.date_button:
                    callRandomFactApi("date");
                    break;
                case R.id.year_button:
                    callRandomFactApi("year");
                    break;
                default:
                    break;
            }
        else {
            Toast.makeText(getActivity(), "Internet not available.", Toast.LENGTH_SHORT).show();
        }
    }

    private void callRandomFactApi(String category) {
        getObservable(category)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        mDisplayString = s;
                        mDisplayTextSwitcher.setText(mDisplayString);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mDisplayTextSwitcher.setText("An error Occured. Please try again.");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DISPLAY_STRING_EXTRA, mDisplayString);
    }

    private String urlConnectionWorker(String category) {
        String apiUrl = getResources().getString(R.string.random_fact_api, category);
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

    private Observable<String> getObservable(final String category) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                try {
                    String result = urlConnectionWorker(category);
                    e.onNext(result);
                    e.onComplete();
                } catch (Exception ex) {
                    e.onError(ex);
                }
            }
        });
    }

    private boolean isIntentAvailable() {
        return true;
    }
}

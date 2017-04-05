package com.daliammao.errorlayout.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.daliammao.errorlayout.R;
import com.daliammao.widget.errorlayout.ErrorLayout;

public class MainActivity extends AppCompatActivity {

    ErrorLayout mErrorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mErrorLayout = (ErrorLayout)findViewById(R.id.el_main);
        mErrorLayout.setIsButtonVisible(true);
        mErrorLayout.setonErrorClickListener(new ErrorLayout.OnErrorClickListener() {
            @Override
            public void onClick(View v, int state) {
                System.out.println(v.toString() + state);
            }
        });
        mErrorLayout.setResources(ErrorLayout.STATE_NETWORK_ERROR,ErrorLayout.RESOURCES_BUTTON,R.string.try_set_resources);
    }

    int i = 0;
    public void changeState(View v){
        i = (++i%5)+1;
        switch (i){
            case ErrorLayout.STATE_HIDE:
                mErrorLayout.setErrorType(ErrorLayout.STATE_HIDE);
                break;
            case ErrorLayout.STATE_NETWORK_ERROR:
                mErrorLayout.setErrorType(ErrorLayout.STATE_NETWORK_ERROR);
                break;
            case ErrorLayout.STATE_NETWORK_LOADING:
                mErrorLayout.setErrorType(ErrorLayout.STATE_NETWORK_LOADING);
                break;
            case ErrorLayout.STATE_NODATA:
                mErrorLayout.setErrorType(ErrorLayout.STATE_NODATA);
                break;
            case ErrorLayout.STATE_NOLOGIN:
                mErrorLayout.setErrorType(ErrorLayout.STATE_NOLOGIN);
                break;
        }
    }
}

package com.hyd_coder.ppjoke.ui.publish;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hyd_coder.libnavannotationa.ActivityDestination;
import com.hyd_coder.ppjoke.R;

@ActivityDestination(pageUrl = "main/tabs/publish", needLogin = true)
public class PublishActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
    }
}

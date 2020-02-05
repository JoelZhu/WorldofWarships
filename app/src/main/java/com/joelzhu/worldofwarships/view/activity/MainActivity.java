package com.joelzhu.worldofwarships.view.activity;

import android.widget.Button;
import android.widget.Toast;

import com.joelzhu.base.view.BaseActivity;
import com.joelzhu.bindview.annotations.ContentView;
import com.joelzhu.bindview.annotations.FindView;
import com.joelzhu.bindview.annotations.OnClick;
import com.joelzhu.worldofwarships.R;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {
    @FindView(R.id.button)
    Button button;

    @OnClick(R.id.button)
    public void onButtonClicked() {
        Toast.makeText(MainActivity.this, "Hello world.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void createActivity() {
        Toast.makeText(MainActivity.this, "Create activity.", Toast.LENGTH_SHORT).show();
    }
}
package com.joelzhu.worldofwarships.view.activity;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.joelzhu.base.view.BaseActivity;
import com.joelzhu.bindview.annotations.ContentView;
import com.joelzhu.bindview.annotations.FindView;
import com.joelzhu.bindview.annotations.OnClick;
import com.joelzhu.common.http.ErrorCode;
import com.joelzhu.common.http.OnPostRequestListener;
import com.joelzhu.worldofwarships.R;
import com.joelzhu.worldofwarships.present.task.TaskManager;
import com.joelzhu.worldofwarships.present.task.command.SearchPlayerStrategy;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {
    @FindView(R.id.button)
    Button button;

    @FindView(R.id.player)
    TextView textView;

    @Override
    protected void createActivity() {
        Toast.makeText(MainActivity.this, "Create activity.", Toast.LENGTH_SHORT).show();

        TaskManager.getInstance().subscribeListener(SearchPlayerStrategy.class,
                new OnPostRequestListener() {
                    @Override
                    public void onPreRequest(String request) {

                    }

                    @Override
                    public void onErrorOccurred(ErrorCode errorCode) {
                        textView.setText("Error: " + errorCode);
                    }

                    @Override
                    public void onPostRequest(String response) {
                        textView.setText(response);
                    }
                });
    }

    @OnClick(R.id.button)
    public void onButtonClicked() {
        textView.setText("Searching...");
        TaskManager.getInstance().executeTask(null);
    }
}
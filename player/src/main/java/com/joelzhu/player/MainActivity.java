package com.joelzhu.player;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.joelzhu.base.view.AbsRemoteActivity;
import com.joelzhu.bindview.annotations.FindView;
import com.joelzhu.bindview.annotations.OnClick;
import com.joelzhu.bindview.annotations.OnItemClick;
import com.joelzhu.bindview.annotations.RootView;

import java.util.ArrayList;
import java.util.List;

@RootView(R.layout.activity_player)
public class MainActivity extends AbsRemoteActivity {
    @FindView(R.id.fragment_list)
    private ListView listView;
    
    @Override
    public void createActivity() {
        listView.setAdapter(new PlayerAdapter());
    }
    
    @OnClick(R.id.fragment_button)
    public void onButtonClicked() {
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
    }

    @OnItemClick(R.id.fragment_list)
    private void onItemClicked(View view, int position, long id) {
        Toast.makeText(this, "Clicked " + ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
    }

    private class PlayerAdapter extends BaseAdapter {
        private List<String> players;

        public PlayerAdapter() {
            players = new ArrayList<>();
            players.add("Joel");
            players.add("Zhu");
            players.add("JoelZhu");
        }

        @Override
        public int getCount() {
            return players.size();
        }

        @Override
        public Object getItem(int position) {
            return players.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(MainActivity.this);
            textView.setText(players.get(position));
            return textView;
        }
    }
}
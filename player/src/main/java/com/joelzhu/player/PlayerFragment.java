package com.joelzhu.player;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.joelzhu.base.view.BaseFragment;
import com.joelzhu.bindview.annotations.FindView;
import com.joelzhu.bindview.annotations.OnClick;
import com.joelzhu.bindview.annotations.OnItemClick;
import com.joelzhu.bindview.annotations.RootView;

import java.util.ArrayList;
import java.util.List;

@RootView(resName = "fragment_player")
public class PlayerFragment extends BaseFragment {
    @FindView(resName = "fragment_list")
    private ListView listView;

    @Override
    protected void createFragment(View rootView) {
        listView.setAdapter(new PlayerAdapter());
    }

    @OnClick(resName = "fragment_button")
    public void onButtonClicked() {
        Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
    }

    @OnItemClick(resName = "fragment_list")
    private void onItemClicked(View view, int position, long id) {
        Toast.makeText(getActivity(), "Clicked " + ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
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
            TextView textView = new TextView(getContext());
            textView.setText(players.get(position));
            return textView;
        }
    }
}
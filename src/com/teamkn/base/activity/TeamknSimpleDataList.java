package com.teamkn.base.activity;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.teamkn.R;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.widget.view.ListMoreButton;

import java.util.List;

abstract public class TeamknSimpleDataList<M, A extends TeamknBaseAdapter<M>> {

    private ListView list_view;
    private A adapter;

    //private int per_page = 20;

    public TeamknSimpleDataList(ListView list_view, A adapter) {
        this.list_view = list_view;
        this.adapter = adapter;
    }

    public A get_adapter() {
        return adapter;
    }

//	public void set_per_page(int per_page){
//		this.per_page = 20;
//	}


    public void load() {
        new TeamknAsyncTask<String, Void, List<M>>(adapter.activity, R.string.now_loading) {
            @Override
            public List<M> do_in_background(String... params)
                    throws Exception {
                return load_list_data();
            }

            @Override
            public void on_success(List<M> items) {
                adapter.add_items(items);
                bind_load_more_button_event();

//				if(items.size() < per_page){
//					load_more_view.setVisibility(View.GONE);
//				}

                list_view.setAdapter(adapter);
                list_view.setOnItemClickListener(list_item_click_listener());
            }
        }.execute();
    }

    public ListMoreButton<M> bind_load_more_button_event() {
        final ListMoreButton<M> load_more_view = new ListMoreButton<M>(adapter) {
            @Override
            public List<M> load() throws Exception {
                return load_list_more_data();
            }

        };

        list_view.addFooterView(load_more_view);
        return load_more_view;
    }

    abstract public List<M> load_list_data() throws Exception;

    abstract public List<M> load_list_more_data() throws Exception;

    abstract public OnItemClickListener list_item_click_listener();
}

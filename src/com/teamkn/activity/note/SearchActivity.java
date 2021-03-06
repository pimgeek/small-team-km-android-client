package com.teamkn.activity.note;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.search.SearchHistory;
import com.teamkn.base.search.Searcher;
import com.teamkn.model.Note;
import com.teamkn.model.database.NoteDBHelper;
import com.teamkn.widget.adapter.NoteListAdapter;

public class SearchActivity extends TeamknBaseActivity{
    public class RequestCode {
        public final static int EDIT_TEXT = 9;
    }
   
    View view_show;	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

	    
	    setContentView(R.layout.horz_scroll_with_image_menu);
        LinearLayout layout = (LinearLayout)findViewById(R.id.linearlayout_loading);
        LayoutInflater inflater = LayoutInflater.from(this);
        view_show = inflater.inflate(R.layout.search, null);
        layout.addView(view_show);
        

        EditText search_box    = (EditText) view_show.findViewById(R.id.search_box);
        Button   search_submit = (Button)   view_show.findViewById(R.id.search_submit);
        Button   search_clear  = (Button)   view_show.findViewById(R.id.search_clear);

        search_submit.setOnClickListener(new SearchSubmitClickListener());
        search_clear.setOnClickListener(new SearchClearClickListener());
        search_box.setOnKeyListener(new SearchBoxEnterListener());
        load_search_history();
    }

    private void add_records(ViewGroup layout) {
        LinkedList<String> records = SearchHistory.get();

        if (!records.isEmpty()) {
            layout.removeAllViews();
            for (String record_text: records) {
                Button record = new Button(this);
                record.setText(record_text);
                layout.addView(record);
                record.setOnClickListener(new SearchHistoryRecordClickListener());
            }
        }
    }

    private void load_search_history() {
        LinearLayout search_history =
                (LinearLayout) findViewById(R.id.search_history);
        add_records(search_history);
    }

    private void load_result_list(List<Note> notes) {
        ListView        search_result_list = (ListView) findViewById(R.id.search_result_list);
        NoteListAdapter note_list_adapter  = new NoteListAdapter(SearchActivity.this);

        note_list_adapter.add_items(notes);
        search_result_list.setAdapter(note_list_adapter);
        search_result_list.setOnItemClickListener(new NoteClickListener());
        search_result_list.setOnCreateContextMenuListener(new NoteItemContextMenuListener());
    }

    private void search_box_set_text(String text) {
        EditText search_box = (EditText) findViewById(R.id.search_box);
        search_box.setText(text);
    }

    private void do_search(String query_string) throws Exception {
        List<Note> result = Searcher.search(query_string);
        load_result_list(result);
    }

    private void do_search_box_search() {
        EditText search_box = (EditText) findViewById(R.id.search_box);
        String query_string = search_box.getText().toString();
        SearchHistory.put(query_string);

        LinearLayout search_history =
                (LinearLayout) findViewById(R.id.search_history);

        try {
            do_search(query_string);
            add_records(search_history);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo;
        menuInfo              = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        TextView note_info_tv = (TextView) menuInfo.targetView.findViewById(R.id.note_info_tv);
        String   uuid         = (String)   note_info_tv.getTag(R.id.tag_note_uuid);
        destroy_note_confirm(uuid);

        return super.onContextItemSelected(item);
    }

    protected void destroy_note_confirm(final String uuid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); //这里只能用this，不能用appliction_context

        builder.setMessage("确认要删除吗？")
                .setPositiveButton(R.string.dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int             which) {
                                NoteDBHelper.destroy(uuid);
                                do_search_box_search();
                            }
                        })
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();
    }

    // 处理其他activity界面的回调
    @Override
    protected void onActivityResult(int    requestCode,
                                    int    resultCode,
                                    Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case SearchActivity.RequestCode.EDIT_TEXT:
                do_search_box_search();
                break;
        }
    }



    private class SearchHistoryRecordClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Button record = (Button) view;
            String record_string = (String) record.getText();

            search_box_set_text(record_string);
            load_search_history();
            try {
                do_search(record_string);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class NoteClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView,
                                View           list_item,
                                int            i,
                                long           l) {
            TextView info_tv = (TextView) list_item.findViewById(R.id.note_info_tv);
            String   uuid    = (String)   info_tv.getTag(R.id.tag_note_uuid);
            String   kind    = (String)   info_tv.getTag(R.id.tag_note_kind);

            Intent intent  = new Intent(SearchActivity.this, EditNoteActivity.class);
            intent.putExtra(EditNoteActivity.Extra.NOTE_UUID, uuid);
            intent.putExtra(EditNoteActivity.Extra.NOTE_KIND, kind);

            if (kind == NoteDBHelper.Kind.IMAGE) {
                String image_path = Note.note_image_file(uuid).getPath();
                intent.putExtra(EditNoteActivity.Extra.NOTE_IMAGE_PATH,
                                image_path);
            }

            startActivityForResult(intent,
                                   SearchActivity.RequestCode.EDIT_TEXT);
        }

    }

    private class SearchClearClickListener implements View.OnClickListener {

        @Override
        public void onClick(View button) {
            search_box_set_text("");
        }
    }

    private class SearchSubmitClickListener implements View.OnClickListener {
        @Override
        public void onClick(View button) {
            do_search_box_search();
        }
    }

    private class SearchBoxEnterListener implements View.OnKeyListener {

        @Override
        public boolean onKey(View view, int keyCode, KeyEvent event) {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode           == KeyEvent.KEYCODE_ENTER)) {

                do_search_box_search();
                return true;
            }
            return false;
        }
    }

    private class NoteItemContextMenuListener implements View.OnCreateContextMenuListener {
        @Override
        public void onCreateContextMenu(ContextMenu                 menu,
                                        View                        view,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, 0, 0, "删除");
        }
    }
}

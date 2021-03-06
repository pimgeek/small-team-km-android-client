package com.teamkn.widget.adapter;

import java.io.ByteArrayInputStream;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.AccountManager;
import com.teamkn.Logic.CompressPhoto;
import com.teamkn.activity.chat.ChatActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.base.utils.ListViewUtility;
import com.teamkn.model.Attitudes;
import com.teamkn.model.Chat;
import com.teamkn.model.ChatNode;
import com.teamkn.model.User;
import com.teamkn.model.database.AttitudesDBHelper;
import com.teamkn.model.database.ChatNodeDBHelper.Kind;
import com.teamkn.model.database.UserDBHelper;

public class ChatNodeListAdapter extends TeamknBaseAdapter<ChatNode> {
  ChatActivity context;
  public ChatNodeListAdapter(ChatActivity activity) {	
	    super(activity);
	    this.context = activity;
  }

  @Override
  public View inflate_view() {
	    return inflate(R.layout.list_chat_node_item, null);
  }

  @Override
  public BaseViewHolder build_view_holder(
      View view) {
    
	    ViewHolder view_holder = new ViewHolder();
	    view_holder.list_chat_node_item_relativelayout = (RelativeLayout)view.findViewById(R.id.list_chat_node_item_relativelayout);
	    
	    view_holder.user_avatar_iv   = (ImageView) view.findViewById(R.id.user_avatar_iv);
	    view_holder.chat_node_content_tv = (TextView)  view.findViewById(R.id.chat_node_content_tv);
	    view_holder.user_content_iv = (ImageView)view.findViewById(R.id.user_content_iv);
	    view_holder.imagebutton_comment=(ImageButton)view.findViewById(R.id.imagebutton_comment);
	    view_holder.listview_comment_result = (ListView)view.findViewById(R.id.listview_comment_result);
	    view_holder.subLayout = (LinearLayout)view.findViewById(R.id.subLayout);
	    return view_holder;
  }

  @Override
  public void fill_with_data(BaseViewHolder holder, ChatNode item, int position) {
	    final ViewHolder view_holder = (ViewHolder) holder;
	    view_holder.list_chat_node_item_relativelayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
                ChatActivity.closeEditTextFouse();
				return false;
			}
		});
	    if(item.sender.user_avatar != null){
	      Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(item.sender.user_avatar));
	      Drawable drawable = new BitmapDrawable(bitmap);
	      view_holder.user_avatar_iv.setBackgroundDrawable(drawable);
	      
	    }else{
	      view_holder.user_avatar_iv.setBackgroundResource(R.drawable.user_default_avatar_normal);
	    }
	    if(item.kind.equals(Kind.IMAGE)){
	        String image_file_path = Chat.note_image_file(item.uuid).getPath();
	//  Bitmap bitmap = BitmapFactory.decodeFile(image_file_path);
	        Bitmap bitmap = CompressPhoto.get_thumb_bitmap_form_file(image_file_path);
	        view_holder.user_content_iv.setVisibility(View.VISIBLE);
	        view_holder.chat_node_content_tv.setVisibility(View.GONE);
	        view_holder.user_content_iv.setImageBitmap(bitmap);
	    }else{
	    	view_holder.chat_node_content_tv.setVisibility(View.VISIBLE);
	    	view_holder.user_content_iv.setVisibility(View.GONE);
	    	view_holder.chat_node_content_tv.setText(item.content);
	    }    
	    
	    List<Attitudes> attitudes_list = AttitudesDBHelper.find_list(item.id);	
	    final AttitudesListAdapter attitudes_adapter = new AttitudesListAdapter(activity);
        if(attitudes_list.size()>0){
	    	
	    	view_holder.subLayout.setVisibility(View.VISIBLE);
	    	
	    	attitudes_adapter.add_items(attitudes_list);
	    	view_holder.listview_comment_result.setAdapter(attitudes_adapter);
	    	// 确定listview的高度
	    	ListViewUtility.setListViewHeightBasedOnChildren(view_holder.listview_comment_result);
	    }else{
	    	attitudes_list.clear();
	    	view_holder.subLayout.setVisibility(View.GONE);
	    }
	    
	    final int chat_id = item.id;
	    final int server_chat_node_id = item.server_chat_node_id;
	    
	    User user = UserDBHelper.find_by_server_user_id(AccountManager.current_user().user_id);

	    Attitudes attitudes = AttitudesDBHelper.find_by_chat_node_id_AND_user_id
	    		(item.id,  user.id);
	    if(attitudes.chat_node_id==0){
	    	view_holder.imagebutton_comment.setImageDrawable(context.getResources().getDrawable(R.drawable.emotion_icn_smile_extrasmall));
	    }else{
	    	if(AttitudesDBHelper.Kind.HEART.equals(attitudes.kind)){
		    	view_holder.imagebutton_comment.setImageDrawable(context.getResources().getDrawable(R.drawable.emotion_icn_heart_extrasmall));
		    }
		    if(AttitudesDBHelper.Kind.GASP.equals(attitudes.kind)){
		    	view_holder.imagebutton_comment.setImageDrawable(context.getResources().getDrawable(R.drawable.emotion_icn_gasp_extrasmall));
		    }
		    if(AttitudesDBHelper.Kind.SAD.equals(attitudes.kind)){
		    	view_holder.imagebutton_comment.setImageDrawable(context.getResources().getDrawable(R.drawable.emotion_icn_sad_extrasmall));
		    }
		    if(AttitudesDBHelper.Kind.WINK.equals(attitudes.kind)){
		    	view_holder.imagebutton_comment.setImageDrawable(context.getResources().getDrawable(R.drawable.emotion_icn_wink_extrasmall));
		    }
		    if(AttitudesDBHelper.Kind.SMILE.equals(attitudes.kind)){
		    	view_holder.imagebutton_comment.setImageDrawable(context.getResources().getDrawable(R.drawable.emotion_icn_smile_extrasmall));
		    }
	    }

		view_holder.imagebutton_comment.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				int[] intXY = new int[2];
				view_holder.imagebutton_comment.getLocationOnScreen(intXY);
				ChatActivity.showDialog(context,intXY,chat_id,server_chat_node_id,view_holder.imagebutton_comment,attitudes_adapter,view_holder.listview_comment_result);
			}
		});    
  } 

  private class ViewHolder implements BaseViewHolder {
	    RelativeLayout list_chat_node_item_relativelayout;
	    ImageView user_avatar_iv;
	    TextView chat_node_content_tv; 
	    ImageView user_content_iv;
	    ImageButton imagebutton_comment;
	    ListView listview_comment_result;
	    
	    LinearLayout subLayout;
  }
}

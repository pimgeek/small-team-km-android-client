package com.teamkn.model.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.teamkn.model.DataList;
import com.teamkn.model.base.BaseModelDBHelper;
import com.teamkn.model.base.Constants;

public class DataListDBHelper extends BaseModelDBHelper {
  public static void create( int user_id, String title, String kind,
			String public_boolean){
    SQLiteDatabase db = get_write_db();
    
    ContentValues values = new ContentValues();
    values.put(Constants.TABLE_DATA_LISTS_USER_ID,user_id);
    values.put(Constants.TABLE_DATA_LISTS_TITLE,title);
    values.put(Constants.TABLE_DATA_LISTS_KIND,kind);
    values.put(Constants.TABLE_DATA_LISTS_PUBLIC,public_boolean);
    
    db.insert(Constants.TABLE_DATA_LISTS, null, values);
    
    db.close();
  }
  
  public static void update(DataList dataList){
	    SQLiteDatabase db = get_write_db();  
	    ContentValues values = new ContentValues();
	    values.put(Constants.TABLE_DATA_LISTS_USER_ID,dataList.user_id);
	    values.put(Constants.TABLE_DATA_LISTS_TITLE,dataList.title);
	    values.put(Constants.TABLE_DATA_LISTS_KIND,dataList.kind);
	    values.put(Constants.TABLE_DATA_LISTS_PUBLIC,dataList.public_boolean);
	    values.put(Constants.TABLE_DATA_LISTS_SERVER_DATA_LIST_ID,dataList.server_data_list_id);
	    
	    if(dataList.id==0){
	    	db.insert(Constants.TABLE_DATA_LISTS, null, values);
	    }else{
	    	db.update(Constants.TABLE_DATA_LISTS, values, Constants.TABLE_DATA_LISTS_SERVER_DATA_LIST_ID + " = ? ", new String[]{dataList.id+""});
	    }
	    db.close();
  }
  
  public static DataList find(){
	  DataList datalist;
    SQLiteDatabase db = get_read_db();
    Cursor cursor = db.rawQuery("SELECT * FROM " + Constants.TABLE_DATA_LISTS + " where max("+Constants.KEY_ID+")",
            null);

    boolean has_value = cursor.moveToFirst();
    if(has_value){
    	datalist = build_by_cursor(cursor);
    }else{
    	datalist = DataList.NIL_DATA_LIST;
    }
    
    cursor.close();
    db.close();
    return datalist;
  }
  public static List<DataList> all() throws Exception {
      SQLiteDatabase db = get_read_db();
      List<DataList> datalists = new ArrayList<DataList>();
      Cursor cursor= db.query(Constants.TABLE_DATA_LISTS,
                      get_columns(),
                      null, null, null, null,
                      Constants.KEY_ID + " DESC");

      while (cursor.moveToNext()) {
    	  DataList datalist = build_by_cursor(cursor);
          datalists.add(datalist);
      }
	return datalists;
  }
  public static DataList find(int id){
	  DataList datalist;
    SQLiteDatabase db = get_read_db();
    Cursor cursor = db.query(Constants.TABLE_DATA_LISTS, get_columns(),
        Constants.KEY_ID + " = ?", 
        new String[]{id+""},null, null, null);

    boolean has_value = cursor.moveToFirst();
    if(has_value){
    	datalist = build_by_cursor(cursor);
    }else{
    	datalist = DataList.NIL_DATA_LIST;
    }
    
    cursor.close();
    db.close();
    return datalist;
  }
  
  public static DataList find_by_server_data_list_id(int server_data_list_id){
	DataList datalist;
    SQLiteDatabase db = get_read_db();
    Cursor cursor = db.query(Constants.TABLE_DATA_LISTS, get_columns(),
        Constants.TABLE_DATA_LISTS_SERVER_DATA_LIST_ID + " = ?", 
        new String[]{server_data_list_id+""},null, null, null);

    boolean has_value = cursor.moveToFirst();
    if(has_value){
    	datalist = build_by_cursor(cursor);
    }else{
    	datalist = DataList.NIL_DATA_LIST;
    }
    
    cursor.close();
    db.close();
    return datalist;
    
  }
  
  public static boolean is_exists(int server_data_list_id){
    SQLiteDatabase db = get_read_db();
    Cursor cursor = db.query(Constants.TABLE_DATA_LISTS, get_columns(),
        Constants.TABLE_DATA_LISTS_SERVER_DATA_LIST_ID + " = ?", 
        new String[]{server_data_list_id+""},null, null, null);

    boolean has_value = cursor.moveToFirst();
    
    cursor.close();
    db.close();
    return has_value;
  }

  private static DataList build_by_cursor(Cursor cursor) {
    int id = cursor.getInt(0);
    int user_id = cursor.getInt(1);
    String title = cursor.getString(2);
    String kind = cursor.getString(3);
    String public_boolean = cursor.getString(4);
    int server_data_list_id = cursor.getInt(5);
    return new DataList(id,user_id,title,kind,public_boolean,server_data_list_id);
  }

  private static String[] get_columns() {
    return new String[]{
        Constants.KEY_ID,
        Constants.TABLE_DATA_LISTS_USER_ID,
        Constants.TABLE_DATA_LISTS_TITLE,
        Constants.TABLE_DATA_LISTS_KIND,
        Constants.TABLE_DATA_LISTS_PUBLIC,
        Constants.TABLE_DATA_LISTS_SERVER_DATA_LIST_ID
    };
  }
}

package jp.senchan.android.wasatter.activity;

import java.util.ArrayList;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.item.Item;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class ChannelSelect extends Activity {

	public ArrayList<Item> channelList;
	public ListView listview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channel_list);
		init();	
	}
	
	protected void init(){
		listview = (ListView) findViewById(R.id.channelList);
	}
	protected void reload(){
		
	}
}

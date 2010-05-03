package jp.senchan.android.wasatter2.adapter;

import java.util.ArrayList;

import jp.senchan.android.wasatter2.R;
import jp.senchan.android.wasatter2.util.WasatterItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AdapterTodo extends ArrayAdapter<WasatterItem> {

	private ArrayList<WasatterItem> items;
	private LayoutInflater inflater;

	public AdapterTodo(Context context, int textViewResourceId, ArrayList<WasatterItem> items) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View getView(int position, View view, ViewGroup parent) {
		if (view == null) {
			// 受け取ったビューがnullなら新しくビューを生成
			view = this.inflater.inflate(R.layout.todo_row, null);
		}
		// データの取得
		WasatterItem item = this.items.get(position);
		if (item != null) {
			TextView text = (TextView) view.findViewById(R.id.todo_text);
			// テキストをビューにセットする
			if (text != null) {
				text.setText(item.text);
			}
		}
		return view;
	}
}
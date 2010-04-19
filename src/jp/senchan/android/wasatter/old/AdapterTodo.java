package jp.senchan.android.wasatter.old;

import java.util.ArrayList;

import jp.senchan.android.wasatter.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AdapterTodo extends ArrayAdapter<WassrTodo> {

	private ArrayList<WassrTodo> items;
	private LayoutInflater inflater;

	public AdapterTodo(Context context, int textViewResourceId, ArrayList<WassrTodo> items) {
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
		WassrTodo item = this.items.get(position);
		if (item != null) {
			TextView text = (TextView) view.findViewById(R.id.todo_text);
			// テキストをビューにセットする
			if (text != null) {
				text.setText(item.body);
			}
		}
		return view;
	}
}
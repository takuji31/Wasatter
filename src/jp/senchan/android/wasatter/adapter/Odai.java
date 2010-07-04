package jp.senchan.android.wasatter.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.item.Item;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

@SuppressWarnings("unchecked")
public class Odai extends ArrayAdapter implements WasatterAdapter{

	private ArrayList items;
	private LayoutInflater inflater;

	public Odai(Context context, int textViewResourceId, ArrayList items) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View getView(int position, View view, ViewGroup parent) {
		if (view == null) {
			// 受け取ったビューがnullなら新しくビューを生成
			view = this.inflater.inflate(R.layout.odai_row, null);
		}
		// データの取得
		Item item = (Item) this.items.get(position);
		if (item != null) {
			TextView text = (TextView) view.findViewById(R.id.status);
			// テキストをビューにセットする
			if (text != null) {
				text.setText(item.html);
			}
			TextView date = (TextView) view.findViewById(R.id.odai_date);
			date.setText(new SimpleDateFormat(Wasatter.ODAI_DATE_FORMAT)
					.format(new Date(item.epoch * 1000)));
		}
		return view;
	}

	
	public void updateView() {
		// TODO 自動生成されたメソッド・スタブ
		super.notifyDataSetChanged();
	}
}
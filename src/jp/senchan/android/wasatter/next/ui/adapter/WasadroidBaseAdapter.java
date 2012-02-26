package jp.senchan.android.wasatter.next.ui.adapter;

import java.util.List;

import jp.senchan.android.wasatter3.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

@SuppressWarnings("unchecked")
public class WasadroidBaseAdapter extends ArrayAdapter {

    protected LayoutInflater inflater;
	public WasadroidBaseAdapter(Context context, int textViewResourceId, List objects) {
		super(context, textViewResourceId, objects);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 30;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return inflater.inflate(R.layout.account_list_row, null);
	}

}

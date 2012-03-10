package jp.senchan.android.wasatter.next.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class WasadroidBaseAdapter<T> extends BaseAdapter {

    protected LayoutInflater inflater;
    protected List<T> mList;
    protected Context mContext;
    
	public WasadroidBaseAdapter(Context context, List<T> list) {
		super();
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mList = list;
		mContext = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}
	
	public abstract int getViewLayoutId();
	
	public abstract View createView(T item, View v);

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    if(convertView == null) {
	        convertView = inflater.inflate(getViewLayoutId(), null);
	    }
        return createView(getItem(position), convertView);
	}

    @Override
    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}

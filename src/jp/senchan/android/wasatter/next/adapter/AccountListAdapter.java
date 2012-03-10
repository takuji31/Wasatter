package jp.senchan.android.wasatter.next.adapter;

import java.util.List;

import jp.senchan.android.wasatter.next.model.dataobject.AccountData;
import jp.senchan.android.wasatter.R;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class AccountListAdapter extends WasadroidBaseAdapter<AccountData> {

    public AccountListAdapter(Context context, List<AccountData> list) {
        super(context, list);
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.account_list_row;
    }

    @Override
    public View createView(AccountData data, View v) {
        TextView textName = (TextView) v.findViewById(R.id.text_name);
        textName.setText(data.name);
        String serviceName = mContext.getResources().getStringArray(R.array.service_names)[data.type];
        TextView textServiceName = (TextView) v.findViewById(R.id.text_service_name);
        textServiceName.setText(serviceName);
        
        return v;
    }

}

package jp.senchan.android.wasatter.next.ui.activity;

import com.androidquery.util.AQUtility;

import jp.senchan.android.wasatter.WasatterActivity;
import jp.senchan.android.wasatter.next.ui.fragment.DebugMenuListFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

public class DebugMenuActivity extends WasatterActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, new DebugMenuListFragment());
        ft.commit();
        AQUtility.setDebug(true);
    }
}
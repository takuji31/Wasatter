package jp.senchan.android.wasatter.app;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import jp.senchan.android.wasatter.WasatterActivity;
import jp.senchan.android.wasatter.app.fragment.PostFragment;

public class PostActivity extends WasatterActivity {
    @Override
    protected void onCreate(Bundle arg0) {
        // TODO 自動生成されたメソッド・スタブ
        super.onCreate(arg0);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, new PostFragment());
        ft.commit();
    }
    
}

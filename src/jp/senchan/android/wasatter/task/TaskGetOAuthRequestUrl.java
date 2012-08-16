/**
 *
 */
package jp.senchan.android.wasatter.task;

import jp.senchan.android.wasatter2.R;
import jp.senchan.android.wasatter.app.OAuthToken;
import twitter4j.TwitterException;
import android.os.AsyncTask;
import android.webkit.WebView;
import android.widget.Button;

/**
 * @author takuji
 * 
 */
public class TaskGetOAuthRequestUrl extends AsyncTask<Void, Void, Void> {
    private OAuthToken target;
    private Button btn;

    public TaskGetOAuthRequestUrl(OAuthToken target) {
        // TODO 自動生成されたコンストラクター・スタブ
        this.target = target;
    }

    @Override
    protected void onPreExecute() {
        // TODO 自動生成されたメソッド・スタブ
        btn = (Button) this.target.findViewById(R.id.button_set_token);
        btn.setClickable(false);
    }

    @Override
    protected Void doInBackground(Void... params) {
        // TODO 自動生成されたメソッド・スタブ
        try {
            this.target.request = this.target.twitter.getOAuthRequestToken();
        } catch (TwitterException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        // TODO 自動生成されたメソッド・スタブ
        WebView wv = (WebView) this.target.findViewById(R.id.web);
        wv.loadUrl(this.target.request.getAuthenticationURL());
        wv.requestFocus();
        btn.setClickable(true);
    }
}

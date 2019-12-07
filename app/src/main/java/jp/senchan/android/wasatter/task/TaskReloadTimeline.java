package jp.senchan.android.wasatter.task;

import android.os.AsyncTask;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.StatusItemComparator;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.WasatterItem;
import jp.senchan.android.wasatter.activity.Setting;
import jp.senchan.android.wasatter.adapter.Timeline;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TaskReloadTimeline extends
        AsyncTask<String, String, ArrayList<WasatterItem>> {
    protected ListView listview;
    protected int mode;
    public static final int MODE_TIMELINE = 1;
    public static final int MODE_REPLY = 2;
    public static final int MODE_MYPOST = 3;
    public static final int MODE_ODAI = 4;
    public static final int MODE_CHANNEL_LIST = 6;
    public static final int MODE_CHANNEL = 7;
    public static final String[] msg = new String[]{"Timeline", "Reply",
            "My post", "Odai", "TODO", "Channel list", "Channel status"};

    // コンストラクタ
    public TaskReloadTimeline(ListView lv, int mode) {
        this.listview = lv;
        this.mode = mode;
    }

    // バックグラウンドで実行する処理
    protected ArrayList<WasatterItem> doInBackground(String... param) {
        ArrayList<WasatterItem> result = new ArrayList<WasatterItem>();
        Twitter twitter = new TwitterFactory(
                new ConfigurationBuilder()
                        .setOAuthConsumerKey(Wasatter.OAUTH_KEY)
                        .setOAuthConsumerSecret(Wasatter.OAUTH_SECRET)
                        .setOAuthAccessToken(Setting.getTwitterToken())
                        .setOAuthAccessTokenSecret(Setting.getTwitterTokenSecret())
                    .build()
        ).getInstance();
        switch (this.mode) {
            case TaskReloadTimeline.MODE_TIMELINE:
                try {
                    ResponseList<twitter4j.Status> homeTimeline = twitter.getHomeTimeline();
                    for (twitter4j.Status status : homeTimeline) {
                        result.add(new WasatterItem(status));
                    }
                } catch (TwitterException e) {
                    publishProgress(Wasatter.SERVICE_TWITTER, String.valueOf(e
                            .getStatusCode()));
                }
                break;
            case TaskReloadTimeline.MODE_REPLY:
                try {
                    ResponseList<twitter4j.Status> mentionsTimeline = twitter.getMentionsTimeline();
                    for (twitter4j.Status status : mentionsTimeline) {
                        result.add(new WasatterItem(status));
                    }
                } catch (TwitterException e) {
                    publishProgress(Wasatter.SERVICE_TWITTER, String.valueOf(e
                            .getStatusCode()));
                }

                break;
            case TaskReloadTimeline.MODE_MYPOST:
                try {
                    ResponseList<twitter4j.Status> userTimeline = twitter.getUserTimeline();
                    for (twitter4j.Status status : userTimeline) {
                        result.add(new WasatterItem(status));
                    }
                } catch (TwitterException e) {
                    publishProgress(Wasatter.SERVICE_TWITTER, String.valueOf(e
                            .getStatusCode()));
                }

                break;
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        // まず、何が起こってここに飛んできたか判定
        String service = values[0];
        String error = values[1];
        Wasatter.displayHttpError(error, service);
    }

    // 進行中に出す処理
    protected void onPreExecute() {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append("Loading ");
        sb.append(TaskReloadTimeline.msg[this.mode - 1]);
        sb.append("...");
        Wasatter.main.loading_timeline_text.setText(sb.toString());
        Wasatter.main.layout_progress_timeline.setVisibility(View.VISIBLE);
    }

    ;

    // メインスレッドで実行する処理
    @Override
    protected void onPostExecute(ArrayList<WasatterItem> result) {
        // 取得結果の代入
        boolean set = false;
        switch (this.mode) {
            case TaskReloadTimeline.MODE_TIMELINE:
                Wasatter.main.list_timeline = result;
                set = Wasatter.main.button_timeline.isChecked();
                break;
            case TaskReloadTimeline.MODE_REPLY:
                Wasatter.main.list_reply = result;
                set = Wasatter.main.button_reply.isChecked();
                break;
            case TaskReloadTimeline.MODE_MYPOST:
                Wasatter.main.list_mypost = result;
                set = Wasatter.main.button_mypost.isChecked();
                break;

            case TaskReloadTimeline.MODE_ODAI:
                Wasatter.main.list_odai = result;
                set = Wasatter.main.button_odai.isChecked();
                break;
            case TaskReloadTimeline.MODE_CHANNEL_LIST:
                Wasatter.main.list_channel_list = result;
                set = Wasatter.main.button_channel.isChecked();
                break;
            case TaskReloadTimeline.MODE_CHANNEL:
                Wasatter.main.list_channel = result;
                set = Wasatter.main.button_channel.isChecked();
                break;
        }

        // ListViewへの挿入
        if (set) {
            boolean channel = this.mode == TaskReloadTimeline.MODE_CHANNEL;
            Timeline adapter = new Timeline(this.listview
                    .getContext(), R.layout.timeline_row, result, channel);
            if (!channel) {
                adapter.sort(new StatusItemComparator());
            }
            this.listview.setAdapter(adapter);
            this.listview.requestFocus();
        }
        Wasatter.main.layout_progress_timeline.setVisibility(View.GONE);
    }
}
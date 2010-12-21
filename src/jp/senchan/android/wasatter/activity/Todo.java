/**
 *
 */
package jp.senchan.android.wasatter.activity;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.WassrClient;
import jp.senchan.android.wasatter.WassrTodo;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author takuji
 * 
 */
public class Todo extends Activity {
	public WassrTodo status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.todo_detail);
		Bundle extras = this.getIntent().getExtras();
		if (extras != null) {
			this.status = (WassrTodo) extras
					.getSerializable(Wasatter.ITEM_DETAIL);
		}
		if (this.status != null) {
			TextView body = (TextView) this.findViewById(R.id.todobody);
			body.setText(this.status.body);
		}
		Button todo_start_button = (Button) this.findViewById(R.id.todo_start);
		todo_start_button.setOnClickListener(new TodoStartClickListener());
		Button todo_stop_button = (Button) this.findViewById(R.id.todo_stop);
		todo_stop_button.setOnClickListener(new TodoStopClickListener());
		Button todo_comp_button = (Button) this
				.findViewById(R.id.todo_complete);
		todo_comp_button.setOnClickListener(new TodoCompleteClickListener());
		Button todo_del_button = (Button) this.findViewById(R.id.todo_delete);
		todo_del_button.setOnClickListener(new TodoDeleteClickListener());
	}

	private class TodoStartClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Todo関連処理のオール非同期化
			if (WassrClient.startTodo(Todo.this.status.rid)) {
				Toast.makeText(Todo.this,
						R.string.notice_message_todo_start_success,
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(Todo.this,
						R.string.notice_message_todo_start_failure,
						Toast.LENGTH_LONG).show();
			}
		}
	}

	private class TodoStopClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO 自動生成されたメソッド・スタブ
			if (WassrClient.stopTodo(Todo.this.status.rid)) {
				Toast.makeText(Todo.this,
						R.string.notice_message_todo_stop_success,
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(Todo.this,
						R.string.notice_message_todo_stop_failure,
						Toast.LENGTH_LONG).show();
			}
		}
	}

	private class TodoCompleteClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO 自動生成されたメソッド・スタブ
			if (WassrClient.completeTodo(Todo.this.status.rid)) {
				Toast.makeText(Todo.this,
						R.string.notice_message_todo_complete_success,
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(Todo.this,
						R.string.notice_message_todo_complete_failure,
						Toast.LENGTH_LONG).show();
			}
		}
	}

	private class TodoDeleteClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO 自動生成されたメソッド・スタブ
			if (WassrClient.deleteTodo(Todo.this.status.rid)) {
				Toast.makeText(Todo.this,
						R.string.notice_message_todo_delete_success,
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(Todo.this,
						R.string.notice_message_todo_delete_failure,
						Toast.LENGTH_LONG).show();
			}
			Todo.this.finish();
		}
	}
}

package jp.senchan.android.wasatter.next.ui.fragment;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.WasatterFragment;
import jp.senchan.android.wasatter.next.ui.fragment.imagepicker.ImagePickerFragment;
import jp.senchan.android.wasatter.next.ui.fragment.imagepicker.ImagePickerFragment.OnImagePickedLisntener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.androidquery.AQuery;

public class PostFragment extends WasatterFragment implements OnImagePickedLisntener {
	public static final String TAG_PICKER = "tag_picker";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO 自動生成されたメソッド・スタブ
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.post, null);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.post, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.menu_image:
			ImagePickerFragment df = new ImagePickerFragment(this);
			getFragmentManager().beginTransaction().add(df, TAG_PICKER).commit();
			break;
		default:
			break;
		}
    	return true;
    }

	@Override
	public void onImagePicked(Bitmap image) {
		// TODO 画像をpickした後の処理
		AQuery aq = new AQuery(activity(), getView());
		aq.id(R.id.previewImageView).image(image);
	}
}

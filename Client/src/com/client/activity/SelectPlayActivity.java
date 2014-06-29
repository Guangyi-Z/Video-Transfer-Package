package com.client.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.androidsocket.R;
import com.model.ServerBeans;

/**
 * 
 * @author caolijie 2014-06-29 选择实时视频播放或历史视频
 */
public class SelectPlayActivity extends Activity implements OnClickListener {
	private Context mContext;
	private ServerBeans mServerBeans;
	private Button mBtnRealTimeVideo;
	private Button mBtnHistoryVideo;
	private ActionBar mActionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_play);
		initView();
		initListener();
		initData();
	}

	private void initView() {
		mActionBar = getActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setBackgroundDrawable(getResources().getDrawable(
                R.color.actionbar_background));
        mActionBar.setIcon(R.drawable.ic_launcher);
        mActionBar.setTitle("返回");
		mBtnRealTimeVideo = (Button) findViewById(R.id.btn_realTime_video);
		mBtnHistoryVideo = (Button) findViewById(R.id.btn_history_video);
	}

	private void initListener() {
		mBtnRealTimeVideo.setOnClickListener(this);
		mBtnHistoryVideo.setOnClickListener(this);
	}

	private void initData() {
		mServerBeans = (ServerBeans) getIntent().getSerializableExtra(
				"serverBeans");
	}

	@Override
	public void onClick(View view) {
		Intent intent = null;
		switch (view.getId()) {
		case R.id.btn_history_video:

			break;
		case R.id.btn_realTime_video:
			intent = new Intent(this,ProducerListActivity.class);
			intent.putExtra("serverBeans", mServerBeans);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

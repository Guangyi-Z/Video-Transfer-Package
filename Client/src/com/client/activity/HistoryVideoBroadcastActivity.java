package com.client.activity;

import java.io.File;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.androidsocket.R;

public class HistoryVideoBroadcastActivity extends Activity {

	private final static String TAG = "HistoryVideoBroadcastActivity";
	private Button button = null;
	private VideoView videoView;
	private String catalogName;
	private String videoName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_video_broadcast);
		initView();
		initData();
	}

	private void initView() {
		videoView = (VideoView) findViewById(R.id.history_video);
		button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new buttonOnClik());
	}

	private void initData() {
		videoName = getIntent().getExtras().getString("videoName");
		Log.e(TAG + "1~~~~~~~~~~", videoName);
		catalogName = getIntent().getExtras().getString("catalogName");
		Log.e(TAG + "2~~~~~~~", catalogName);
	}

	class buttonOnClik implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			System.out.println("播放网络上的音频");
			try {

				String path = catalogName + File.separator + videoName;
				// String path =
				// "http://192.168.253.1:8080/httpGetVideo/5a3bbe53835ffd43/LoveStory.mp4";
				ConnectionDetector cd = new ConnectionDetector(
						HistoryVideoBroadcastActivity.this);
				Log.e(TAG, "11111111111");
				if (cd.isConnectingToInternet()) {
					MediaController mediaController = new MediaController(
							HistoryVideoBroadcastActivity.this); // 通过videoView提供的uri函数借口播放服务器视频
					mediaController.setAnchorView(videoView);
					Uri video = Uri.parse(path);
					videoView.setMediaController(mediaController);
					videoView.setVideoURI(video);
					videoView.start();
				} else {
					Toast.makeText(HistoryVideoBroadcastActivity.this,
							"网络连接没有开启，请设置网络连接", 2000).show();
				}

			} catch (Exception e) {
				// TODO: handle exception
				Toast.makeText(HistoryVideoBroadcastActivity.this,
						"Error connecting", Toast.LENGTH_SHORT).show();
			}
		}
	}

}

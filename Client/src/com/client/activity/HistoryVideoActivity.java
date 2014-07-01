package com.client.activity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidsocket.R;
import com.client.view.CustomDialog;
import com.model.PacketBean;
import com.model.ProducerBean;
import com.model.ServerBeans;

/**
 * 
 * @author lican 2014-06-30 某个producer的视频目录
 */
public class HistoryVideoActivity extends Activity implements
		OnItemClickListener {
	private static final boolean BUG = true;
	private static final String TAG = "HistoryVideoActivity";
	private Context mContext;
	private ListView mListView;
	private MyAdapter mAdapter;
	private List<String> mHistoryVideoList = new ArrayList<String>();
	private ServerBeans mServerBeans;
	private ActionBar mActionBar;
	private String catalogName;

	private Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				// 刷新Video列表
				mListView.setAdapter(mAdapter);
				mAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_video_list);
		mContext = getApplicationContext();
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
		mActionBar.setTitle("History_Video");

		mListView = (ListView) findViewById(R.id.history_video_list);
	}

	private void initListener() {
		mListView.setOnItemClickListener(this);
	}

	private void initData() {
		mAdapter = new MyAdapter(mContext);
		mListView.setAdapter(mAdapter);
		mServerBeans = (ServerBeans) getIntent().getSerializableExtra("serverBeans");
		catalogName = getIntent().getExtras().getString("catalogName");
		requestLinkServer(mServerBeans);
	}

	// 链接服务器，并接受服务器返回的Video列表
	private void requestLinkServer(final ServerBeans serverBeans) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				ObjectInputStream is = null;
				ObjectOutputStream os = null;
				try {
					Log.e(TAG,"ip:"+ serverBeans.getIp()+" port:"+serverBeans.getPort());
					Socket socket = new Socket(serverBeans.getIp(), serverBeans
							.getPort());

					// 请求获取video列表
					os = new ObjectOutputStream(socket.getOutputStream());
					PacketBean packetBean = new PacketBean();
					packetBean.setPacketType(PacketBean.VIDEO_LIST);
					packetBean.setData(catalogName);
					os.writeObject(packetBean);
					os.flush();

					// 获取Server返回的video列表
					is = new ObjectInputStream(socket.getInputStream());
					packetBean = (PacketBean) is.readObject();
					if (packetBean != null) {
						if (packetBean.getPacketType() == PacketBean.VIDEO_LIST) {
							Log.e(TAG, packetBean.getData().toString());
							mHistoryVideoList = (List<String>) packetBean.getData();
							Log.e(TAG, mHistoryVideoList.toString());
							myHandler.sendEmptyMessage(0);
						}
					}
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					try {
						if (is != null) {
							is.close();
						}
						if (os != null) {
							os.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	/**
	 * listview的点击事件
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long id) {
		String videoName = mHistoryVideoList.get(position);
		if(videoName!=null){
			Intent intent = new Intent(this,HistoryVideoBroadcastActivity.class);
			intent.putExtra("videoName", videoName);
			intent.putExtra("catalogName", catalogName);
			startActivity(intent);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	class MyAdapter extends BaseAdapter {
		LayoutInflater mLayoutInflater;
		Context mContext;

		public MyAdapter(Context mContext) {
			this.mContext = mContext;
			mLayoutInflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			return mHistoryVideoList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mHistoryVideoList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HolderView holderView = null;
			if (convertView == null) {
				holderView = new HolderView();
				convertView = mLayoutInflater.inflate(R.layout.history_video_item,null);
				holderView.historyVideo = (TextView) convertView.findViewById(R.id.history_video_item);
				convertView.setTag(holderView);
			} else {
				holderView = (HolderView) convertView.getTag();
			}
			String hCatalog = mHistoryVideoList.get(position);
			holderView.historyVideo.setText(hCatalog);
			return convertView;
		}

		class HolderView {
			TextView historyVideo;
		}
	}

}

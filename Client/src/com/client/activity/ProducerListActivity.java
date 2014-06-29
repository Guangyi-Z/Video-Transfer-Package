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
import android.text.TextUtils;
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
import com.client.model.PacketBean;
import com.client.model.ProducerBeans;
import com.client.model.ServerBeans;
import com.client.view.CustomDialog;

/**
 * 
 * @author caolijie 2014-06-26 某个Server上在线Producer列表界面
 */
public class ProducerListActivity extends Activity implements
		OnItemClickListener {
	private static final boolean BUG = true;
	private static final String TAG = "ProducerListActivity";
	private Context mContext;
	private ListView mListView;
	private MyAdapter mAdapter;
	private List<ProducerBeans> mProducerList = new ArrayList<ProducerBeans>();
	private ServerBeans mServerBeans;
	private ActionBar mActionBar;

	private Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
		};
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_producer_list);
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
		mActionBar.setTitle("Producer");

		mListView = (ListView) findViewById(R.id.producer_list);
	}

	private void initListener() {
		mListView.setOnItemClickListener(this);
	}

	private void initData() {
		mAdapter = new MyAdapter(mContext);
		mListView.setAdapter(mAdapter);
		mServerBeans = (ServerBeans) getIntent().getSerializableExtra(
				"serverBeans");
		requestLinkServer(mServerBeans);
	}

	// 链接服务器，并接受服务器返回的Producer列表
	private void requestLinkServer(ServerBeans serverBeans) {
		ObjectInputStream is = null;
		ObjectOutputStream os = null;
		try {
			Socket socket = new Socket(serverBeans.getIp(),
					serverBeans.getPort());
			is = new ObjectInputStream(socket.getInputStream()); // 从socket流中接收数据

			os = new ObjectOutputStream(socket.getOutputStream());
			PacketBean packetBean = new PacketBean();
			packetBean.setPacketType(PacketBean.PRODUCER_LIST);
			os.writeObject(packetBean);
			os.flush();

			packetBean = (PacketBean) is.readObject();
			if (packetBean != null) {
				if (packetBean.getPacketType() == PacketBean.PRODUCER_LIST) {
					mProducerList = (List<ProducerBeans>) packetBean.getData();
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

	/**
	 * listview的点击事件
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long id) {

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
			return mProducerList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mProducerList.get(arg0);
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
				convertView = mLayoutInflater.inflate(R.layout.producer_item,
						null);
				holderView.tvProducerIP = (TextView) convertView
						.findViewById(R.id.tv_server_ip);
				holderView.tvProducerName = (TextView) convertView
						.findViewById(R.id.tv_server_name);
				holderView.tvProducerPort = (TextView) convertView
						.findViewById(R.id.tv_server_port);
				convertView.setTag(holderView);
			} else {
				holderView = (HolderView) convertView.getTag();
			}
			ProducerBeans producerBeans = mProducerList.get(position);
			holderView.tvProducerIP.setText(producerBeans.getIp());
			holderView.tvProducerName.setText(producerBeans.getName());
			holderView.tvProducerPort.setText(producerBeans.getPort() + "");
			return convertView;
		}

		class HolderView {
			TextView tvProducerName;
			TextView tvProducerIP;
			TextView tvProducerPort;
		}
	}

}

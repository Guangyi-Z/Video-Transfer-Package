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
 * @author caolijie 2014-06-26 某个Server上在线Producer列表界面
 */
public class ProducerListActivity extends Activity implements
		OnItemClickListener {
	private static final boolean BUG = true;
	private static final String TAG = "ProducerListActivity";
	private Context mContext;
	private ListView mListView;
	private MyAdapter mAdapter;
	private List<ProducerBean> mProducerList = new ArrayList<ProducerBean>();
	private ServerBeans mServerBeans;
	private ActionBar mActionBar;

	private Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				// 刷新Producer列表
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

					// 请求获取Producer列表
					os = new ObjectOutputStream(socket.getOutputStream());
					PacketBean packetBean = new PacketBean();
					packetBean.setPacketType(PacketBean.PRODUCER_LIST);
					os.writeObject(packetBean);
					os.flush();

					// 获取Server返回的Producer列表
					is = new ObjectInputStream(socket.getInputStream());
					packetBean = (PacketBean) is.readObject();
					if (packetBean != null) {
						if (packetBean.getPacketType() == PacketBean.PRODUCER_LIST) {
							mProducerList = (List<ProducerBean>) packetBean.getData();
							Log.e(TAG, mProducerList.toString());
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
		ProducerBean producerBean = mProducerList.get(position);
		if(producerBean!=null){
			// 用户输入密码及身份验证
			this.showAuthorizationDialog(producerBean);
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
						.findViewById(R.id.tv_producer_ip);
				holderView.tvProducerName = (TextView) convertView
						.findViewById(R.id.tv_producer_name);
				holderView.tvProducerPort = (TextView) convertView
						.findViewById(R.id.tv_producer_port);
				convertView.setTag(holderView);
			} else {
				holderView = (HolderView) convertView.getTag();
			}
			ProducerBean producerBean = mProducerList.get(position);
			holderView.tvProducerIP.setText(producerBean.getIp());
			holderView.tvProducerName.setText(producerBean.getAndroidName());
			holderView.tvProducerPort.setText(producerBean.getPort() + "");
			return convertView;
		}

		class HolderView {
			TextView tvProducerName;
			TextView tvProducerIP;
			TextView tvProducerPort;
		}
	}
	
	/**
	 * 身份验证的dialog
	 */
	private void showAuthorizationDialog(final ProducerBean producerBean) {
		final CustomDialog dialog = new CustomDialog(this,
				R.layout.dialog_enter_password, R.style.custom_dialog);
		dialog.show();
		TextView txtCancel = (TextView) dialog
				.findViewById(R.id.auth_tv_cancel);
		TextView txtOk = (TextView) dialog.findViewById(R.id.auth_tv_ok);
		final EditText etPassword = (EditText) dialog
				.findViewById(R.id.auth_et_password);

		// cancel btn
		txtCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		// confirm btn
		txtOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
				String passwd = etPassword.getText().toString().trim();
				//  身份验证			
				Socket socket= null;
				ObjectInputStream is = null;
				ObjectOutputStream os = null;
				try {
					socket = new Socket(mServerBeans.getIp(), mServerBeans.getPort());
					is = new ObjectInputStream(
							socket.getInputStream()); // 从socket流中接收数据
					os = new ObjectOutputStream(
							socket.getOutputStream());

					// output
					PacketBean passBean;
					passBean = new PacketBean(PacketBean.AUTHORIZATION, 
							producerBean.getAndroidName() + "|" + passwd);
					os.writeObject(passBean);
					os.flush();
					
					// input
					PacketBean resBean= (PacketBean) is.readObject();
					if(resBean!=null && 
							resBean.getPacketType()==PacketBean.AUTHORIZATION && 
							(Boolean) resBean.getData()){
						 // 身份验证成功，跳转实时视频界面
						Intent intent = new Intent(ProducerListActivity.this,RealTimeVideoActivity.class);
						intent.putExtra("producerBean", producerBean);
						startActivity(intent);
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Connection Close");
				}
			}
		});
	}
}

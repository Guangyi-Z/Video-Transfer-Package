package com.client.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
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
import com.client.model.ServerBeans;
import com.client.view.CustomDialog;

/**
 * 
 * @author caolijie 2014-06-26 客户端首页的在线服务器列表界面
 */
public class ServerListActivity extends Activity implements OnClickListener,
		OnItemClickListener,OnItemLongClickListener {
	private static final boolean BUG = true;
	private static final String TAG = "ServerListActivity";
	private Context mContext;
	private ListView mListView;
	private MyAdapter mAdapter;
	private List<ServerBeans> mServerList = new ArrayList<ServerBeans>();

	private Button mAddBtn;
	private ActionBar mActionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server_list);
		mContext = getApplicationContext();
		initView();
		initListener();
		initData();
	}

	private void initView() {
		mActionBar = getActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mActionBar.setHomeButtonEnabled(false);
        mActionBar.setBackgroundDrawable(getResources().getDrawable(
                R.color.actionbar_background));
        mActionBar.setIcon(R.drawable.ic_launcher);
        mActionBar.setTitle("客户端");
        
		mListView = (ListView) findViewById(R.id.server_list);
		mAddBtn = (Button) findViewById(R.id.btn_add_server);
	}

	private void initListener() {
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
		mAddBtn.setOnClickListener(this);
	}

	private void initData() {
		mAdapter = new MyAdapter(mContext);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_add_server:
			showAddServerDialog();
			break;
		default:
			break;
		}
	}

	/**
	 * listview的点击事件
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long id) {
		ServerBeans serverBeans = mServerList.get(position);
		Intent intent = new Intent(this,SelectPlayActivity.class);
		intent.putExtra("serverBeans", serverBeans);
		startActivity(intent);
	}

	/**
	 * listView的长按事件
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position,
			long id) {
		mServerList.remove(position);
		mAdapter.notifyDataSetChanged();
		return false;
	}
	/**
	 * 添加服务器的dialog
	 */
	private void showAddServerDialog() {
		final CustomDialog addserverDialog = new CustomDialog(this,
				R.layout.dialog_add_server, R.style.custom_dialog);
		addserverDialog.show();
		TextView txtCancel = (TextView) addserverDialog
				.findViewById(R.id.tv_cancel);
		TextView txtOk = (TextView) addserverDialog.findViewById(R.id.tv_ok);
		final EditText etServerName = (EditText) addserverDialog
				.findViewById(R.id.et_server_name);
		final EditText etServerIP = (EditText) addserverDialog
				.findViewById(R.id.et_server_ip);
		final EditText etServerPort = (EditText) addserverDialog
				.findViewById(R.id.et_server_port);

		txtCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addserverDialog.dismiss();
			}
		});
		txtOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String serverName = etServerName.getText().toString().trim();
				String serverIP = etServerIP.getText().toString().trim();
				String serverPort = etServerPort.getText().toString().trim();
				boolean b = checkValid(serverName,serverIP,serverPort);
				if(!b){
					return ;
				}
				addServer("服务器1", "192.168.253.1", 9901);
//				addServer(serverName, serverIP, Integer.parseInt(serverPort));
				addserverDialog.dismiss();
			}
		});
	}
	private boolean checkValid(String serverName, String serverIP,
			String serverPort) {
		return true;
	}
	/**
	 * 添加一个服务器
	 * 
	 * @param serverName
	 * @param serverIP
	 * @param serverPort
	 */
	private void addServer(String serverName, String serverIP, int serverPort) {
		ServerBeans serverBeans = new ServerBeans();
		serverBeans.setIp(serverIP);
		serverBeans.setName(serverName);
		serverBeans.setPort(serverPort);
		mServerList.add(serverBeans);
		mAdapter.notifyDataSetChanged();
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
			return mServerList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mServerList.get(arg0);
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
				convertView = mLayoutInflater.inflate(R.layout.server_item,
						null);
				holderView.tvServerIP = (TextView) convertView
						.findViewById(R.id.tv_server_ip);
				holderView.tvServerName = (TextView) convertView
						.findViewById(R.id.tv_server_name);
				holderView.tvServerPort = (TextView) convertView
						.findViewById(R.id.tv_server_port);
				convertView.setTag(holderView);
			} else {
				holderView = (HolderView) convertView.getTag();
			}
			ServerBeans serverBeans = mServerList.get(position);
			holderView.tvServerIP.setText(serverBeans.getIp());
			holderView.tvServerName.setText(serverBeans.getName());
			holderView.tvServerPort.setText(serverBeans.getPort() + "");
			return convertView;
		}

		class HolderView {
			TextView tvServerName;
			TextView tvServerIP;
			TextView tvServerPort;
		}
	}

}

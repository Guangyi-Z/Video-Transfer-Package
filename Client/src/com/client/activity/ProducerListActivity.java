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
	private ActionBar mActionBar;

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
        if(item.getItemId() == android.R.id.home){
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

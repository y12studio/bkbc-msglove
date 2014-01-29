/*
 * Copyright 2014 Y12STUDIO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.blackbananacoin.msglove;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.params.MainNetParams;

import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener,
		TextWatcher {

	private EditText edL;
	private EditText edO;
	private EditText edV;
	private EditText edE;
	private EditText edMsgLove;
	private TextView tvBtcAddr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.btnGenPrivKey).setOnClickListener(this);
		edL = (EditText) findViewById(R.id.edL);
		edO = (EditText) findViewById(R.id.edO);
		edV = (EditText) findViewById(R.id.edV);
		edE = (EditText) findViewById(R.id.edE);
		edMsgLove = (EditText) findViewById(R.id.edMsgLove);
		tvBtcAddr = (TextView) findViewById(R.id.tvBtcAddr);
		edL.addTextChangedListener(this);
		edO.addTextChangedListener(this);
		edV.addTextChangedListener(this);
		edE.addTextChangedListener(this);
		edMsgLove.addTextChangedListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void testKeyGen() {
		try {
			String seed = "致春嬌:謝謝你帶來的美好，這個冬天在淡水海岸將過去鎖進比特幣記憶鍊是我最後的溫柔:-)保重志明";
			Address addr = createAddr(seed);
			Log.i(UI.TAG, "addr=" + addr.toString());
			// 01-29 05:15:07.165: I/bkbc-msglove(3722):
			// addr=18QqG8wEhbeLj8yVjfguYULmdNSbKFJtpW

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param seed
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	private Address createAddr(String seed) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		MessageDigest d512 = MessageDigest.getInstance("SHA-512");
		byte[] d1 = d512.digest(seed.getBytes("UTF-8"));
		d512.reset();
		byte[] privKeyBytes = d512.digest(d1);
		BigInteger privKey = new BigInteger(1, privKeyBytes);
		ECKey key = new ECKey(privKey);
//		Log.v(UI.TAG,
//				"private Key="
//						+ Utils.bytesToHexString(key.getPrivKeyBytes()));
//		Log.v(UI.TAG,
//				"hash160=" + Utils.bytesToHexString(key.getPubKeyHash()));
		Address addr = new Address(MainNetParams.get(), key.getPubKeyHash());
		return addr;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnGenPrivKey:
			testKeyGen();
			break;

		default:
			break;
		}

	}

	public void afterTextChanged(Editable s) {
		String tL = edL.getText().toString();
		String tO = edO.getText().toString();
		String tV = edV.getText().toString();
		String tE = edE.getText().toString();
		String tMsgLove = edMsgLove.getText().toString();
		String msgLove = tMsgLove.replace("-L-", tL).replace("-O-", tO)
				.replace("-V-", tV).replace("-E-", tE);
		Log.i(UI.TAG, "msglove=" + msgLove);
		try {
			Address addr = createAddr(msgLove);			
			Log.i(UI.TAG, "addr=" + addr.toString());
			tvBtcAddr.setText(addr.toString());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

}

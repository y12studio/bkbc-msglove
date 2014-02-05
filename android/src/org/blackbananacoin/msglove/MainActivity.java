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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.blackbananacoin.common.bitcoin.Bitcoins;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.DumpedPrivateKey;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.params.MainNetParams;

public class MainActivity extends Activity implements OnClickListener,
		TextWatcher {

	private EditText edL;
	private EditText edO;
	private EditText edV;
	private EditText edE;
	private EditText edMsgLove;
	private EditText edBtcAmount;
	private TextView tvBtcAddr;

	private static class KeyInfo {
		private Address addr;
		private DumpedPrivateKey privateKeyEncoded;

		public Address getAddr() {
			return addr;
		}

		public void setAddr(Address addr) {
			this.addr = addr;
		}

		public DumpedPrivateKey getPrivateKeyEncoded() {
			return privateKeyEncoded;
		}

		public void setPrivateKeyEncoded(DumpedPrivateKey privateKeyEncoded) {
			this.privateKeyEncoded = privateKeyEncoded;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.btnPay).setOnClickListener(this);
		findViewById(R.id.btnShare).setOnClickListener(this);
		findViewById(R.id.btnRestorePrivKey).setOnClickListener(this);
		findViewById(R.id.btnReset).setOnClickListener(this);
		edL = (EditText) findViewById(R.id.edL);
		edO = (EditText) findViewById(R.id.edO);
		edV = (EditText) findViewById(R.id.edV);
		edE = (EditText) findViewById(R.id.edE);
		edMsgLove = (EditText) findViewById(R.id.edMsgLove);
		edBtcAmount = (EditText) findViewById(R.id.edBtcAmount);
		tvBtcAddr = (TextView) findViewById(R.id.tvBtcAddr);
		edL.addTextChangedListener(this);
		edO.addTextChangedListener(this);
		edV.addTextChangedListener(this);
		edE.addTextChangedListener(this);
		edMsgLove.addTextChangedListener(this);
		handleIncomeIntent();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private KeyInfo createKeyInfo(String seed) throws NoSuchAlgorithmException,
			UnsupportedEncodingException, AddressFormatException {
		KeyInfo r = new KeyInfo();
		MessageDigest d256 = MessageDigest.getInstance("SHA-256");
		byte[] d1 = d256.digest(seed.getBytes("UTF-8"));
		d256.reset();
		byte[] privKeyBytes = d256.digest(d1);
		ECKey eckey = new ECKey(new BigInteger(1, privKeyBytes));
		DumpedPrivateKey dkey = eckey.getPrivateKeyEncoded(MainNetParams.get());
		Log.v(UI.TAG, "dump=" + dkey.toString());
		Address addr = new Address(MainNetParams.get(), eckey.getPubKeyHash());
		Log.v(UI.TAG, "Addr=" + addr.toString());
		r.setAddr(addr);
		//r.setPrivateKey(eckey.getPrivKeyBytes());
		r.setPrivateKeyEncoded(dkey);
		testDumpKey(dkey.toString());
		return r;
	}

	private void testDumpKey(String input) {
		try {
			final ECKey key = new DumpedPrivateKey(MainNetParams.get(), input)
					.getKey();
			final Address address = new Address(MainNetParams.get(),
					key.getPubKeyHash());

			Log.v(UI.TAG, "[testDump]Addr=" + address.toString());

		} catch (final AddressFormatException x) {
			x.printStackTrace();
		}
	}

	public void onClick(View v) {
		String addr;
		double amount;
		switch (v.getId()) {
		case R.id.btnPay:
			addr = tvBtcAddr.getText().toString().trim();
			amount = Double.parseDouble(edBtcAmount.getText().toString());
			String bitcoinUri = Bitcoins.buildUri(addr, amount);
			Log.i(UI.TAG, "bitcoinUri=" + bitcoinUri);
			final Intent intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(bitcoinUri));
			startActivity(intent);
			break;
		case R.id.btnShare:
			String msglove = edMsgLove.getText().toString().trim();
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, msglove);
			sendIntent.setType("text/plain");
			startActivity(sendIntent);
			break;
		case R.id.btnRestorePrivKey:
			handleClickRestoreKey();
			break;
		case R.id.btnReset:
			edMsgLove.setText(UI.MSG);
			break;
		default:
			break;
		}

	}

	private void handleClickRestoreKey() {
		// wallet key save to /mnt/sdcard/datelabel.key
		// private key file content
		// BitcoinxxxxxxxxxxxxxxxAddress 2014-01-28T09:13:54Z
		SimpleDateFormat fmDateKeyContent = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss'Z'");
		SimpleDateFormat fmFileName = new SimpleDateFormat(
				"'msglove-'MMdd-HHmmss'.key'");
		long time = new Date().getTime();
		long createTime = time - 48 * 60 * 60 * 1000; // 2 days ago
		String timeInKey = fmDateKeyContent.format(new Date(createTime));
		Log.i(UI.TAG, "timeKey=" + timeInKey);

		String keyFileName = fmFileName.format(new Date());
		KeyInfo ki = getKeyInfoFromUi();
		if (ki != null) {
			String keyContent = "# KEEP YOUR PRIVATE KEYS SAFE !\n"
					+ ki.getPrivateKeyEncoded().toString() + " "
					+ timeInKey + "\n# End of private keys";
			Log.i(UI.TAG, "keycontent=" + keyContent);
			if (isExternalStorageWritable()) {
				File f = getDownloadStorageDir(keyFileName);
				Log.i(UI.TAG, "keyFile=" + f.getAbsolutePath());
				try {
					FileWriter filewriter = new FileWriter(f);
					BufferedWriter out = new BufferedWriter(filewriter);
					out.write(keyContent);
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void handleIncomeIntent(){
		// Get intent, action and MIME type
	    Intent intent = getIntent();
	    String action = intent.getAction();
	    String type = intent.getType();
	    if (Intent.ACTION_SEND.equals(action) && type != null) {
	        if ("text/plain".equals(type)) {
	            handleSendText(intent); // Handle text being sent
	        }
	    } else {
	        // Handle other intents, such as being started from the home screen
	    }
	}

	private void handleSendText(Intent intent) {
		String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
		if(sharedText!=null){
			edMsgLove.setText(sharedText);			
		}
		
	}

	public File getDownloadStorageDir(String keyFileName) {
		File file = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
				keyFileName);
		return file;
	}

	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	public void afterTextChanged(Editable s) {
		KeyInfo ki = getKeyInfoFromUi();
		if (ki != null) {
			Address addr = ki.getAddr();
			// Log.i(UI.TAG, "addr=" + addr.toString());
			tvBtcAddr.setText(addr.toString());
		}
	}

	public KeyInfo getKeyInfoFromUi() {
		KeyInfo ki = null;
		String tL = edL.getText().toString();
		String tO = edO.getText().toString();
		String tV = edV.getText().toString();
		String tE = edE.getText().toString();
		String tMsgLove = edMsgLove.getText().toString();
		String msgLove = tMsgLove.replace("-L-", tL).replace("-O-", tO)
				.replace("-V-", tV).replace("-E-", tE);
		// Log.i(UI.TAG, "msglove=" + msgLove);
		try {
			ki = createKeyInfo(msgLove);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (AddressFormatException e) {
			Log.e(UI.TAG, "error=" + e.getMessage());
			e.printStackTrace();
		}
		return ki;
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

}

package com.example.version;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

public class MainActivity extends Activity {

	private int localVersion = 0;// ���ذ汾��
	private int serverVersion = 0;// �������汾��
	public ProgressDialog pBar;// ������
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		try {
			localVersion = getPackageManager().getPackageInfo(getPackageName(),0).versionCode;// ��ȡ���ذ汾��
			serverVersion = 2;// �ٶ��������汾Ϊ2�����ذ汾Ĭ����1
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		checkVersion(); // ���ü��汾���µķ���
	}

	// �����µİ汾
	private void checkVersion() {
		// TODO Auto-generated method stub
		if (localVersion < serverVersion) {
			// �����°汾����ʾ�û�����
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("�������")
				 .setMessage("�������°汾��������������ʹ��")
				 .setPositiveButton("����",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,int which) {
									// TODO Auto-generated method stub
									pBar = new ProgressDialog(MainActivity.this);
									pBar.setTitle("��������");
									pBar.setMessage("���Ժ�...");
									pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
									downFile("http://www.yasite.net/apk/locatecamera.apk");
								}
							}).setNegativeButton("ȡ��",new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							});
			alert.create().show();
		}
	}

	// �ļ�����
	public void downFile(final String url) {
		// TODO Auto-generated method stub
		pBar.show();
		new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				
				try {
					HttpResponse response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					pBar.setMax((int)length);//���ý����������ֵΪ�ļ��ĳ���
					InputStream inputStream = entity.getContent();
					FileOutputStream outputStream = null;
					if(inputStream!=null){
						File file = new File(Environment.getExternalStorageDirectory(),"locatecamera.apk");
						outputStream = new FileOutputStream(file);
						byte[] bt = new byte[1024];
						int len = -1;
						int count = 0;
						while((len = inputStream.read(bt))!=-1){
							outputStream.write(bt,0,len);
							count +=len;
							pBar.setProgress(count);
							if(length>0){
							}
						}
					}
					outputStream.flush();
					if(outputStream!=null){
						outputStream.close();
					}
					down();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}.start();
	}

	void down() {
		// TODO Auto-generated method stub
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				pBar.cancel();
				update();
			}
		});
	}
	//����װ
	void update() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File("/sdcard/locatecamera.apk")),"application/vnd.android.package-archive");
		startActivity(intent);
	}
	
}

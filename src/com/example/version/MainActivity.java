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

	private int localVersion = 0;// 本地版本号
	private int serverVersion = 0;// 服务器版本号
	public ProgressDialog pBar;// 进度条
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		try {
			localVersion = getPackageManager().getPackageInfo(getPackageName(),0).versionCode;// 获取本地版本号
			serverVersion = 2;// 假定服务器版本为2，本地版本默认是1
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		checkVersion(); // 调用检测版本更新的方法
	}

	// 检查更新的版本
	private void checkVersion() {
		// TODO Auto-generated method stub
		if (localVersion < serverVersion) {
			// 发现新版本，提示用户更新
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("软件升级")
				 .setMessage("发现有新版本，建议立即更新使用")
				 .setPositiveButton("更新",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,int which) {
									// TODO Auto-generated method stub
									pBar = new ProgressDialog(MainActivity.this);
									pBar.setTitle("正在下载");
									pBar.setMessage("请稍候...");
									pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
									downFile("http://www.yasite.net/apk/locatecamera.apk");
								}
							}).setNegativeButton("取消",new DialogInterface.OnClickListener() {

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

	// 文件下载
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
					pBar.setMax((int)length);//设置进度条的最大值为文件的长度
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
	//程序安装
	void update() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File("/sdcard/locatecamera.apk")),"application/vnd.android.package-archive");
		startActivity(intent);
	}
	
}

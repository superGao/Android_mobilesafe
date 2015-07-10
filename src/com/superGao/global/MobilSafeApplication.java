package com.superGao.global;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
import android.os.Environment;
import android.renderscript.Element;
/**
 * 捕获全局异常
 * @author gao
 *
 */
public class MobilSafeApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		
		Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
	}
	/**
	 * 处理所有未经捕获的异常
	 * @author gao
	 *
	 */
	class MyUncaughtExceptionHandler implements UncaughtExceptionHandler{
		//
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			// 将异常信息保存到sdcard中，必要时候上传到服务器
			File file=new File(Environment.getExternalStorageDirectory(),"error.log");
			try {
				PrintWriter writer=new PrintWriter(file);
				ex.printStackTrace(writer);
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			//发生异常直接结束当前进程
			android.os.Process.killProcess(android.os.Process.myPid());
			
		}
		
	}
}

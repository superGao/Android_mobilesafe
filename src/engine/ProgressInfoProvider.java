package engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.superGao.domain.ProgressInfo;
import com.superGao.mobilesafe.R;

/**
 * 获取进程的相关信息
 * 
 * @author gao
 *
 */
public class ProgressInfoProvider {

	public static ArrayList<ProgressInfo> getRunningProgresses(Context con){
		//活动管理器
		ActivityManager am=(ActivityManager) con.getSystemService(Context.ACTIVITY_SERVICE);
		
		PackageManager pm=con.getPackageManager();
		
		List<RunningAppProcessInfo> runningProgress=am.getRunningAppProcesses();
		
		ArrayList<ProgressInfo> list=new ArrayList<ProgressInfo>();
		
		for(RunningAppProcessInfo runProgress:runningProgress){
			ProgressInfo info=new ProgressInfo();
			
			String packageName=runProgress.processName ;
			info.packageName=packageName;
			
			//获取某个应用的内存信息
			int pid=runProgress.pid;
			android.os.Debug.MemoryInfo[] progressMemoryInfo=am.getProcessMemoryInfo(new int[]{pid});
			android.os.Debug.MemoryInfo memoryInfo=progressMemoryInfo[0];
			//获取内存大小
			long totalPrivateDirty = memoryInfo.getTotalPrivateDirty()*1024;
			info.memory=totalPrivateDirty;
			
			try {
				//获取应用信息
				ApplicationInfo appInfo=pm.getApplicationInfo(packageName, 0);
				//应用名称
				String name=appInfo.loadLabel(pm).toString();
				//应用图标
				Drawable icon=appInfo.loadIcon(pm);
				
				//存入ProgressInfo
				info.name=name;
				info.icon=icon;
				
				int flags=appInfo.flags;
				if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
					// 系统进程
					info.isUserProgress = false;
				} else {
					// 用户进程
					info.isUserProgress = true;
				}
				
				
				
			} catch (Exception e) {
				// 解决部分系统应用没有图标和名称的问题
				info.name=packageName;
				info.icon=con.getResources().getDrawable(R.drawable.system_default);
				info.isUserProgress=false;
				e.printStackTrace();
			}
			
			list.add(info);
		}
		
		return list;
	}
	
	/**
	 * 获取正在运行的进程数
	 */
	public static int getRunningProcessNum(Context ctx){
		ActivityManager am=(ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		return am.getRunningAppProcesses().size();
	}
	
	/**
	 * 获取剩余内存
	 */
	public static long getAvailMemo(Context ctx){
		ActivityManager am=(ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo info=new MemoryInfo();
		
		am.getMemoryInfo(info);
		
		return info.availMem;
	}
	
	/**
	 * 获取总内存
	 */
	public static long getTotalMemo(Context ctx){
		try {
			FileReader reader=new FileReader(new File("proc/meminfo"));
			BufferedReader br=new BufferedReader(reader);
			String line=br.readLine();
			char[] charArray=line.toCharArray();
			
			StringBuffer sb=new StringBuffer();
			
			for(char c: charArray){
				if(c>='0'&&c<='9'){
					sb.append(c);
				}
			}
			
			String stuNum=sb.toString();
			
			return Long.parseLong(stuNum)*1024;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	/**
	 * 杀死后台所有进程
	 */
	public static void killAllProgress(Context ctx){
		ActivityManager am=(ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProgress=am.getRunningAppProcesses();
		
		for(RunningAppProcessInfo runningAppProcessInfo:runningAppProgress){
			String packageName=runningAppProcessInfo.processName;
			
			//跳过手机卫士
			if(packageName.equals(ctx.getPackageName())){
				continue;
			}
			
			am.killBackgroundProcesses(packageName);
		}
	}
	
	
}

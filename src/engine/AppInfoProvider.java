package engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.superGao.domain.AppInfo;

/**
 * 提供应用信息
 * 获取已经安装app
 * 
 * @author gao
 *
 */
public class AppInfoProvider {

	public static ArrayList<AppInfo> getInstallApp(Context con){
		PackageManager pm=con.getPackageManager();
		List<PackageInfo> installedPackages=pm.getInstalledPackages(0);
		
		ArrayList<AppInfo> list=new ArrayList<AppInfo>();
		for(PackageInfo packageInfo:installedPackages){
			AppInfo info=new AppInfo();
			
			String packageName=packageInfo.packageName;
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			String name=applicationInfo.loadLabel(pm).toString();
			Drawable icon=applicationInfo.loadIcon(pm);
			
			int flags=applicationInfo.flags;
			//判断应用是否是用户应用
			if((flags&ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
				//系统APP
				info.isUserApp=false;
			}else{
				//用户APP
				info.isUserApp=true;
			}
			//判断应用安装位置
			if((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)==ApplicationInfo.FLAG_EXTERNAL_STORAGE){
				//SD卡
				info.isRom=false;
			}else{
				info.isRom=true;
			}
			//将用户信息封装到AppInfo中
			info.packageName=packageName;
			info.name=name;
			info.icon=icon;
			//将应用信息保存到集合中
			list.add(info);
		}
		
		return list;
	}
}

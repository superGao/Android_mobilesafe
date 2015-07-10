package receiver;

import service.UpdateWidgetService;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/**
 * 窗口小部件组件

 * @author gao
 * 
 */
public class MyWidget extends AppWidgetProvider {

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}

	// widget第一次被添加
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		System.out.println("onEnabled");
		// 启动定时更新的服务
		context.startService(new Intent(context, UpdateWidgetService.class));
	}

	// widget被添加/widget要周期性更新,最短半小时一更新
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		// 启动定时更新的服务, 确保服务已经启动
		context.startService(new Intent(context, UpdateWidgetService.class));
	}

	// widget被删除
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		System.out.println("onDeleted");
	}

	// 最后一个widget被移除
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		System.out.println("onDisabled");
		// 停止定时更新的服务
		context.stopService(new Intent(context, UpdateWidgetService.class));
	}
}

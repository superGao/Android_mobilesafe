package receiver;

import engine.ProgressInfoProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * widget一键清理
 * 
 * @author gao
 * 
 */
public class KillProcessReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ProgressInfoProvider.killAllProgress(context);
	}

}

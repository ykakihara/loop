package com.kskkbys.loop;

import com.kskkbys.loop.logger.FlurryLogger;
import com.kskkbys.loop.logger.KLog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends BaseActivity {

	private static final String TAG = SplashActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		KLog.v(TAG, "onCreate");
		
		getSupportActionBar().hide();
		
		// If first launch, show splash
		LoopApplication app = (LoopApplication)getApplication();
		if (app.isFirstLaunch()) {
			FlurryLogger.logEvent(FlurryLogger.SEE_SPLASH);
			setContentView(R.layout.activity_splash);
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					startMainActivity();
				}
			}, 1000);
		} else {
			startMainActivity();
		}
	}
	
	private void startMainActivity() {
		startActivity(new Intent(SplashActivity.this, MainActivity.class));
		finish();
	}
}

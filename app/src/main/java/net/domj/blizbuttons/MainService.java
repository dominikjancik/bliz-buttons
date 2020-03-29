package net.domj.blizbuttons;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

import androidx.annotation.NonNull;

/**
 * Created by matt on 08/08/2016.
 */

public class MainService extends Service implements View.OnTouchListener {

  private static final String TAG = MainService.class.getSimpleName();

  private WindowManager windowManager;

  private View floatyView;
  private View supportView;

  private WindowManager.LayoutParams params;
  private WindowManager.LayoutParams supportParams;

  Handler mHandler;

  private static final int SHOW_HOME_BUTTON = 0;
  private static final int SHOW_SUPPORT_BUTTON = 1;

  @Override
  public IBinder onBind(Intent intent) {

    return null;
  }

  @Override
  public void onCreate() {

    super.onCreate();

    windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

    addOverlayView();
  }

  private void goToHomescreen() {
    Intent startMain = new Intent(Intent.ACTION_MAIN);
    startMain.addCategory(Intent.CATEGORY_HOME);
    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(startMain);
  }

  public void startNewActivity(String packageName) {
    Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
    if (intent == null) {
      // Bring user to the market or let them choose an app?
      intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(Uri.parse("market://details?id=" + packageName));
    }
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }

  private void addOverlayView() {


    int layoutParamsType;

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      layoutParamsType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
    }
    else {
      layoutParamsType = LayoutParams.TYPE_PHONE;
    }

    params = new WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        layoutParamsType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT);

    params.gravity = Gravity.END | Gravity.BOTTOM;
    params.x = 0;
    params.y = 0;

    supportParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutParamsType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);

    supportParams.gravity = Gravity.END | Gravity.TOP;
    supportParams.x = 0;
    supportParams.y = 0;

    LayoutInflater inflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));

    if (inflater != null) {
      floatyView = inflater.inflate(R.layout.layout_chat_head, null);
      final ImageView image = floatyView.findViewById(R.id.chat_head_profile_iv);
      image.setOnTouchListener(this);
      windowManager.addView(floatyView, params);

      supportView = inflater.inflate(R.layout.support_view, null);
      final ImageView supportImage = supportView.findViewById(R.id.support_iv);
      supportImage.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
          startNewActivity( "com.teamviewer.quicksupport.market");
          supportView.setVisibility(View.INVISIBLE);
          mHandler.sendEmptyMessageDelayed(SHOW_SUPPORT_BUTTON, 5000);
          return true;
        }
      });

      final ImageView cardsImage = supportView.findViewById(R.id.cards);
      cardsImage.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
          startNewActivity( "com.potatojam.classic.solitaire.klondike");
          return true;
        }
      });

      windowManager.addView(supportView, supportParams);

      mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
          switch (msg.what) {
            case SHOW_HOME_BUTTON:
              floatyView.setVisibility(View.VISIBLE);
              break;
            case SHOW_SUPPORT_BUTTON:
              supportView.setVisibility(View.VISIBLE);
              break;
          }
        }
      };

    }
    else {
      Log.e("SAW-example", "Layout Inflater Service is null; can't inflate and display R.layout.floating_view");
    }


  }

  @Override
  public void onDestroy() {

    super.onDestroy();

    if (floatyView != null) {

      windowManager.removeView(floatyView);

      floatyView = null;
    }
  }

  private int x = 0;
  long lastPress = 0;


  @Override
  public boolean onTouch(View view, MotionEvent motionEvent) {
    //view.performClick();
    switch (motionEvent.getAction()) {
      case MotionEvent.ACTION_DOWN:
        lastPress = System.currentTimeMillis();
        break;
      case MotionEvent.ACTION_UP:
        long releaseTime = System.currentTimeMillis();
        if (releaseTime - lastPress > 2000) {
          floatyView.setVisibility(View.INVISIBLE);
          mHandler.sendEmptyMessageDelayed(SHOW_HOME_BUTTON, 2000);
        } else {
          goToHomescreen();
        }
    }

    Log.v(TAG, "onTouch...");
    Log.v(TAG, view.toString());
    Log.v(TAG, Float.toString(motionEvent.getX()));
    Log.v(TAG, Float.toString(motionEvent.getY()));

    //goToHomescreen();
    // Kill service
    //onDestroy();

    return true;
  }
}

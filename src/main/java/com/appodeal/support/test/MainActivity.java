package com.appodeal.support.test;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.InterstitialCallbacks;
import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeCallbacks;
import java.util.ArrayList;
import java.util.HashMap;
import static android.provider.ContactsContract.CommonDataKinds.Organization.TITLE;
import static android.provider.MediaStore.Video.VideoColumns.DESCRIPTION;
import static com.mopub.common.util.DeviceUtils.isNetworkAvailable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button button;
    private final int IDD_THREE_BUTTONS = 0;
    public static int adDelay = 30;
    public CountDownTimer cdt;
    public static boolean btnClicked = false;
    private boolean isNativeLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.btnBeginTest);
        String appKey = "7d6fe9573883925dec3fb34baaeb006efe232da4c0627693";
        Appodeal.disableNetwork(this, "yandex");
        Appodeal.disableLocationPermissionCheck();
        Appodeal.initialize(this, appKey, Appodeal.INTERSTITIAL | Appodeal.BANNER_TOP | Appodeal.NATIVE);
        Appodeal.setTesting(true);
        Appodeal.show(this, Appodeal.BANNER_TOP);
        Appodeal.setAutoCacheNativeIcons(true); //нативная реклама
        Appodeal.setAutoCacheNativeMedia(false);
        Appodeal.initialize(this, appKey, Appodeal.NATIVE);
        Appodeal.setNativeCallbacks(new NativeCallbacks() {
            @Override
            public void onNativeLoaded() {
                Log.d("Appodeal", "onNativeLoaded");
                isNativeLoaded = true;
            }

            @Override
            public void onNativeFailedToLoad() {
                Log.d("Appodeal", "onNativeFailedToLoad");
                isNativeLoaded=false;
            }

            @Override
            public void onNativeShown(NativeAd nativeAd) {
                Log.d("Appodeal", "onNativeShown");
            }

            @Override
            public void onNativeClicked(NativeAd nativeAd) {
                Log.d("Appodeal", "onNativeClicked");
            }
        });


        final MainActivity mainAct =  this;
        final TextView tv = (TextView) findViewById(R.id.main_timer_text);
        this.cdt = new CountDownTimer(adDelay * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                tv.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                if (!btnClicked) {
                    Appodeal.show(mainAct, Appodeal.INTERSTITIAL);
                } else {
                    btnClicked = false;
                    cdt.start();
                }
            }
        }.start();

        Appodeal.setBannerCallbacks(new BannerCallbacks() {
            @Override
            public void onBannerLoaded(int height, boolean isPrecache) {
                Log.d("Appodeal", "onBannerLoaded");
            }
            @Override
            public void onBannerFailedToLoad() {
                Log.d("Appodeal", "onBannerFailedToLoad");
            }
            @Override
            public void onBannerShown() {
                Log.d("Appodeal", "onBannerShown");
                closeBanner();
            }
            @Override
            public void onBannerClicked() {
                Log.d("Appodeal", "onBannerClicked");
            }
        });

        Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {
            @Override
            public void onInterstitialLoaded(boolean isPrecache) {
                Log.d("Appodeal", "onInterstitialLoaded");
            }
            @Override
            public void onInterstitialFailedToLoad() {
                Log.d("Appodeal", "onInterstitialFailedToLoad");
            }
            @Override
            public void onInterstitialShown() {

            }
            @Override
            public void onInterstitialClicked() {
                Log.d("Appodeal", "onInterstitialClicked");
            }
            @Override
            public void onInterstitialClosed() {
                Log.d("Appodeal", "onInterstitialClosed");
            }
        });
        checkedConnection();
        //нативная реклама
        ListView listView = (ListView) findViewById(R.id.listView);

        // создаем массив списков
        ArrayList<HashMap<String, Object>> catList = new ArrayList<>();

        HashMap<String, Object> hashMap;

        hashMap = new HashMap<>();
        hashMap.put (TITLE, "Новость 1"); // Название
        hashMap.put(DESCRIPTION, "Описание 1"); // Описание

        catList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put (TITLE, "Новость 2"); // Название
        hashMap.put(DESCRIPTION, "Описание 2"); // Описание

        catList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put (TITLE, "Новость 3"); // Название
        hashMap.put(DESCRIPTION, "Описание 3"); // Описание

        catList.add(hashMap);

        SimpleAdapter adapter = new SimpleAdapter(this, catList,
                R.layout.list_item, new String[]{TITLE, DESCRIPTION, },
                new int[]{R.id.native_title, R.id.native_description});

        // Устанавливаем адаптер для списка
        listView.setAdapter(adapter);
    }
    private void checkedConnection(){
        if (!isNetworkAvailable(this)){
            showDialog(IDD_THREE_BUTTONS);
        }
    }
    public void showNativeAd() { //нативная реклама
        Log.d("Appodeal", "showNativeAd");
        ConstraintLayout nativeView = findViewById(R.id.native_item);
        NativeAd nativeAd;
        try {
            nativeAd = Appodeal.getNativeAds(1).get(0);
        } catch (IndexOutOfBoundsException i) {
            Log.d("Appodeal", "No more ads available now.");
            return;
        }

        TextView nativeAdSign = nativeView.findViewById(R.id.native_ad_sign);
        nativeAdSign.setText("Ad");

        TextView nativeTitle = nativeView.findViewById(R.id.native_title);
        nativeTitle.setText(nativeAd.getTitle());

        TextView nativeDescription = nativeView.findViewById(R.id.native_description);
        nativeDescription.setMaxLines(3);
        nativeDescription.setEllipsize(TextUtils.TruncateAt.END);
        nativeDescription.setText(nativeAd.getDescription());
        ((ImageView) nativeView.findViewById(R.id.native_image)).setImageBitmap(nativeAd.getImage());
       View providerView = nativeAd.getProviderView(MainActivity.this);
       if (providerView != null) {
           FrameLayout providerViewContainer = nativeView.findViewById(R.id.native_provider_view);
           providerViewContainer.addView(providerView);
       }

        nativeAd.registerViewForInteraction(nativeView);
        nativeView.setVisibility(View.VISIBLE);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case IDD_THREE_BUTTONS:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.question)
                        .setNeutralButton(R.string.yes_i_want,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        InputMethodManager mgr =
                                                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        if (mgr != null) {
                                        }
                                        startActivityForResult(
                                                new Intent(Settings.ACTION_WIFI_SETTINGS), 0);

                                    }
                                })
                        .setNegativeButton(R.string.no,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                return builder.create();
            default:
                return null;
        }
    }
    private void closeBanner(){
        Handler handler = new Handler();
        final MainActivity kek = this;
        handler.postDelayed(new Runnable() {
            public void run() {
                Appodeal.hide(kek, Appodeal.BANNER_TOP);
                Appodeal.destroy(Appodeal.BANNER_TOP);
            }
        }, 5000);
    }

    @Override
    public void onResume() {
        Log.d("Appodeal", "onResume call");
        super.onResume();
        cdt.start();
    }

    public void onClick(View view) {
        if (isNativeLoaded) {
            showNativeAd();
        }
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        button.startAnimation(anim);
        button.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        btnClicked = true;
    }
}

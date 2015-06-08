package com.teanesh.company.taschenlampe;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;


public class MainActivity extends ActionBarActivity {
    public static Camera cam = null;
    public TextView mText;
    public boolean onoffthenuss = true;
    public boolean flackeronoff = true;
    public boolean flackeronoffloop = false;
    public int sleepi = 100;
    public Button buttonText;
    public CheckBox checkFlackern;
    public TextView Flackerwert_object;
    public double Flackerwert_frequenz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* EventListener f&uuml;r den Button f&uuml;r das An/Ausschalten */
        final Button buttonClick = (Button) findViewById(R.id.buttonStart);
        buttonClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.lamp();
            }
        });
        /* EventListener f&uuml;r die CheckBox f&uuml;r das Flackern */
        final Button checkClick = (Button) findViewById(R.id.checkFlackern);
        checkClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.flacker();
            }
        });
         /* EventListener zum Setzen der Freuenzy f&uuml;r das Flackern */
        final SeekBar changeSeek = (SeekBar) findViewById(R.id.seekFlackern);
        Flackerwert_object = (TextView) findViewById(R.id.frequency_value);     //z.B. 5Hz
        changeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sleepi = changeSeek.getProgress()+1;
                Flackerwert_frequenz = 1000.0/(sleepi*2.0)+0.0005;
                Flackerwert_frequenz = (int) (Flackerwert_frequenz*100.0);
                Flackerwert_object.setText(Double.toString( Flackerwert_frequenz / 100.0)+"Hz");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //
            }
        });
        /* Grafik: */
        buttonText = buttonClick;
        setgraphic();
        //Set Tabs by https://www.youtube.com/watch?v=irDdBxamuZs
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        //Tab 1
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("flash");
        tabSpec.setContent(R.id.tabflash);
        tabSpec.setIndicator(getString(R.string.flash));
        tabHost.addTab(tabSpec);
        //Tab 2
        tabSpec = tabHost.newTabSpec("morsen");
        tabSpec.setContent(R.id.tabmorsen);
        tabSpec.setIndicator(getString(R.string.morsen));
        tabHost.addTab(tabSpec);

    }

    private void setgraphic() {
         /* Setzt die von der Bildschirmgr&ouml;&szlig;e abh&auml;ngende H&ouml;he/Breite des Buttons UND CheckBox */
        android.view.Display display = ((android.view.WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        buttonText.setHeight((int) (display.getHeight() * 0.68));
        buttonText.setWidth((int) (display.getWidth() * 0.50));
        checkFlackern = (CheckBox) findViewById(R.id.checkFlackern);
        checkFlackern.setWidth((int) (display.getWidth() * 0.77));
        checkFlackern.setBottom((int) (display.getHeight() * 0.77));
        /* Pr&uuml;ft erstmalig, ob es &uuml;berhaupt einen Blitz gibt. */
        mText = (TextView) findViewById(R.id.status);
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            mText.setText(R.string.status_true);
        } else {
            mText.setText(R.string.status_false);
        }
    }

    private void lamp() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            if (onoffthenuss) {
                cam = Camera.open();
                Parameters p = cam.getParameters();
                p.setFlashMode(Parameters.FLASH_MODE_TORCH);
                cam.setParameters(p);
                cam.startPreview();
                onoffthenuss = false;
                buttonText.setText(R.string.stop);
            } else {
                cam.stopPreview();
                cam.release();
                cam = null;
                onoffthenuss = true;
                buttonText.setText(R.string.start);
            }
        }
    }

    private void flacker() {
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                        if (flackeronoff) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    checkFlackern.setText(R.string.flash_off);
                                }
                            });
                            cam = Camera.open();
                            Parameters p = cam.getParameters();
                            flackeronoff = false;
                            flackeronoffloop = true;
                            while (flackeronoffloop) {
                                SystemClock.sleep(sleepi);
                                p.setFlashMode(Parameters.FLASH_MODE_TORCH);
                                cam.setParameters(p);
                                cam.startPreview();
                                SystemClock.sleep(sleepi);
                                p.setFlashMode(Parameters.FLASH_MODE_OFF);
                                cam.setParameters(p);
                                cam.stopPreview();
                            }
                            cam.release();
                            cam = null;
                        } else {
                            flackeronoffloop = false;
                            flackeronoff = true;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    checkFlackern.setText(R.string.flash_on);
                                }
                            });
                        }
                    }
                } catch(Exception e) {}
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

}


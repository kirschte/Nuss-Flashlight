package com.teanesh.company.taschenlampe;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
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
                MainActivity.this.beforelamp();
            }
        });
        /* EventListener f&uuml;r die CheckBox f&uuml;r das Flackern */
        final Button checkClick = (Button) findViewById(R.id.checkFlackern);
        checkClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.beforeflacker();flacker();
            }
        });
         /* EventListener zum Setzen der Freuency f&uuml;r das Flackern */
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

        /**
         * This Activity keeps the screen on using the window manager, you don't have to worry about managing this
         * it will be kept on for the duration of the Activity life.
         * No permissions are needed in your manifest.
         */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), R.string.setting_pressed, Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_credits:
                Toast.makeText(getApplicationContext(), R.string.credits_pressed, Toast.LENGTH_SHORT).show();
                break;
        }


        return super.onOptionsItemSelected(item);
    }





    private void beforelamp() { //checkt, ob die Taschenlampe bereits flackert, wenn ja, dann mache sie aus und mache die Lampe an.
        if (flackeronoff) {
            lamp();
        } else {
            flacker();
            checkFlackern.setChecked(false);
            SystemClock.sleep(1000); //damit das Flackern in Ruhe ausgehen kann...
            lamp();
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

    private void beforeflacker() {
        if (onoffthenuss) {//checkt, ob die Taschenlampe bereits leuchtet, wenn ja, dann mache sie aus und mache das Flackern an.
            flacker();
        } else {
            lamp();
            flacker();
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    checkFlackern.setText(R.string.flash_on);
                                }
                            });
                        } else {
                            flackeronoffloop = false;
                            flackeronoff = true;
                        }
                    }
                } catch(Exception ignored) {}
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


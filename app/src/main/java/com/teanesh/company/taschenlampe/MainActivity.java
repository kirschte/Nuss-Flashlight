package com.teanesh.company.taschenlampe;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import java.util.concurrent.ExecutionException;


public class MainActivity extends ActionBarActivity {
    public TextView mText;
    public static Camera cam = null;
    public boolean onoffthenuss = true;
    public boolean flackeronoff = true;
    public boolean flackeronoffloop = false;
    public int sleepi;
    public Button buttonText;
    public CheckBox checkFlackern;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* EventListener f&uuml;r den Button f&uuml;r das An/Ausschalten */
        final Button buttonClick = (Button) findViewById(R.id.buttonStart);
        buttonClick.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lamp();
            }
        });
        /* EventListener f&uuml;r die CheckBox f&uuml;r das Flackern */
        final Button checkClick = (Button) findViewById(R.id.checkFlackern);
        checkClick.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sleepi=10;
                flacker();
            }
        });
        /* Grafik: */
        buttonText = (Button) findViewById(R.id.buttonStart);
        setgraphic();

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
}


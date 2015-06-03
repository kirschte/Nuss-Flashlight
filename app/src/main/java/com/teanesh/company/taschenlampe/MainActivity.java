package com.teanesh.company.taschenlampe;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;


public class MainActivity extends ActionBarActivity {
    public TextView mText;
    public static Camera cam = null;
    public boolean onoffthenuss = true;
    public Button buttonText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //EventListener f&uuml;r den Button
        final Button buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lamp();
            }
        });
        /* Setzt die von der Bildschirmgr&ouml;&szlig;e abh&auml;ngende H&ouml;he/Breite des Buttons */
        android.view.Display display = ((android.view.WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        buttonStart.setHeight((int) (display.getHeight() * 0.68));
        buttonStart.setWidth((int)  (display.getWidth()  * 0.50));
        /* Pr&uuml;ft erstmalig, ob es &uuml;berhaupt einen Blitz gibt. */
        mText = (TextView) findViewById(R.id.status);
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            mText.setText(R.string.status_true);
        } else {
            mText.setText(R.string.status_false);
        }
    }

    private void lamp() {
        buttonText = (Button) findViewById(R.id.buttonStart);
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
}

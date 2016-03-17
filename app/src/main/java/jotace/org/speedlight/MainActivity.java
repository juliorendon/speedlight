package jotace.org.speedlight;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Icon;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Camera camera;
    private Camera.Parameters parameters;
    private ImageButton flashlightButton;
    boolean isFlashLightOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Removing default Activity Name from layout
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Obtaining the font
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Animated.ttf");

        // Setting custom font to main title
        TextView txtTitle = (TextView)findViewById(R.id.main_title);
        txtTitle.setTypeface(font);

        // Getting layout
        final RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.main_layout);

        if (isFlashSupported()) {

            // Camera Parameters
            camera = Camera.open();
            parameters = camera.getParameters();

            // Getting flashlight button
            flashlightButton = (ImageButton) findViewById(R.id.flashlight_button);
            flashlightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isFlashLightOn) {
                        mainLayout.setBackgroundColor(getResources().getColor(R.color.gray));
                        v.setBackgroundResource(R.drawable.off);
                        isFlashLightOn = false;

                        // Turning OFF flashlight
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera.setParameters(parameters);
                        camera.stopPreview();
                    } else {
                        mainLayout.setBackgroundResource(R.drawable.on_background);
                        v.setBackgroundResource(R.drawable.on);
                        isFlashLightOn = true;

                        // Turning ON flashlight
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(parameters);
                        camera.startPreview();
                    }
                }
            });
        } else {
            showNoFlashAlert();
        }

    }

    private void showNoFlashAlert() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.no_camera_validation)
                .setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.dialog_error)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).show();
    }

    private boolean isFlashSupported() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            // Confirmation Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            // Add the buttons
            builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    finish();
                }
            }).setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.exit_speedlight)
                    .setMessage(R.string.exit_speedlight_message)
                    .create().show();


            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if(camera != null){
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        super.onDestroy();
    }
} // END
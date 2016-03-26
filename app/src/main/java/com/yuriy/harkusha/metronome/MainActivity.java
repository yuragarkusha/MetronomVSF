package com.yuriy.harkusha.metronome;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private boolean serviceIsWork = false;
    private EditText editBPM;
    private SeekBar seekBar;
    private int bpm;
    private boolean vibrationOn = false;
    private boolean lightOn = false;
    private boolean soundOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        editBPM = (EditText) findViewById(R.id.bpmEditText);
        bpm = Integer.parseInt(String.valueOf(editBPM.getText()));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bpm = progress;
                editBPM.setText(Integer.toString(bpm));
            }
        });

    }
    public void startStopButtonClick(View view) {
        if (!serviceIsWork) {
            onClickStart();
            updateElementsAfterStartService();
        } else {
            onClickStop();
            updateElementsAfterStopService();
        }
    }

    public void onClickStart() {
        serviceIsWork = true;
        Intent intent = new Intent(this, MetronomeService.class);
        int getText = (Integer.parseInt(String.valueOf(editBPM.getText())));
        if (getText > 200) {
            getText = 200;
        }
        bpm = getText;
        intent.putExtra("bpm", bpm);
        intent.putExtra("vibrationOn", vibrationOn);
        intent.putExtra("lightOn", lightOn);
        intent.putExtra("soundOn", soundOn);
        startService(intent);

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void onClickStop() {
        Intent stopIntent = new Intent(MainActivity.this, MetronomeService.class);
        stopService(stopIntent);
        serviceIsWork = false;
    }

    public void updateElementsAfterStartService() {
        Button button = (Button) findViewById(R.id.buttonStartStop);
        button.setText("STOP");
        button.setBackgroundDrawable(getResources().getDrawable(R.drawable.stop_button));

        ImageView indicator = (ImageView) findViewById(R.id.indicator);
        indicator.setImageDrawable(getResources().getDrawable(R.drawable.indicator_on));
    }

    public void updateElementsAfterStopService() {
        Button button = (Button) findViewById(R.id.buttonStartStop);
        button.setText("START");
        button.setBackgroundDrawable(getResources().getDrawable(R.drawable.start_button));

        ImageView indicator = (ImageView) findViewById(R.id.indicator);
        indicator.setImageDrawable(getResources().getDrawable(R.drawable.indicator_off));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public void onVibrationToggleClick(View view) {
        TextView vibrationTextView = (TextView) findViewById(R.id.textView_vibration);
        ImageView vibrationImageView = (ImageView) findViewById(R.id.imageView_vibration);

        if (((ToggleButton) view).isChecked()) {
            vibrationImageView.setImageResource(R.drawable.ic_vibration_on);
            vibrationTextView.setTextColor(getResources().getColor(R.color.colorForOn));
            vibrationOn = true;
        } else {
            vibrationImageView.setImageResource(R.drawable.ic_vibration_off);
            vibrationTextView.setTextColor(getResources().getColor(R.color.colorForOff));
            vibrationOn = false;
        }
    }

    public void onFlashlightToggleClick(View view) {
        TextView flashlightTextView = (TextView) findViewById(R.id.textView_flashlight);
        ImageView flashlightImageView = (ImageView) findViewById(R.id.imageView_flashlight);

        if (((ToggleButton) view).isChecked()) {
            flashlightImageView.setImageResource(R.drawable.ic_flashlight_on);
            flashlightTextView.setTextColor(getResources().getColor(R.color.colorForOn));
            lightOn = true;
        } else {
            flashlightImageView.setImageResource(R.drawable.ic_flashlight_off);
            flashlightTextView.setTextColor(getResources().getColor(R.color.colorForOff));
            lightOn = false;
        }
    }

    public void onSoundToggleClick(View view) {
        TextView soundTextView = (TextView) findViewById(R.id.textView_sound);
        ImageView soundImageView = (ImageView) findViewById(R.id.imageView_sound);

        if (((ToggleButton) view).isChecked()) {
            soundImageView.setImageResource(R.drawable.ic_sound_on);
            soundTextView.setTextColor(getResources().getColor(R.color.colorForOn));
            soundOn = true;
        } else {
            soundImageView.setImageResource(R.drawable.ic_sound_off);
            soundTextView.setTextColor(getResources().getColor(R.color.colorForOff));
            soundOn = false;
        }
    }
}

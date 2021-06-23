package com.azetech.physyo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends Activity {

    private TextView timer, count;
    private RelativeLayout playLayout, pauseLayout, resetLayout, settings;
    private TextView pauseButton;
    private int count_text = 25;
    private boolean isTimerRunning = false, isPause = false;
    CountDownTimer generalTimer, autoTimer, pauseTimer;
    private LottieAnimationView animationView;

    String timeLeft = "0", settingsMode = null;
    boolean settingsMute = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        playLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mode = loadMode();
                if (mode.equals("auto")) {
                    startAutoTimer(loadTime());
                    playLayout.setVisibility(View.GONE);
                    pauseLayout.setVisibility(View.VISIBLE);
                } else {
                    startGeneralTimer();
                }
            }
        });

        pauseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoTimer.cancel();
                if (isPause) {
                    isTimerRunning = false;
                    pauseButton.setText("Resume");
                    isPause = false;
                    autoSettings(false);
                } else {
                    isTimerRunning = true;
                    pauseButton.setText("Pause");
                    isPause = true;
                    autoSettings(true);
                }
            }
        });

        resetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTimerRunning) {
                    reset();
                    timeLeft = loadTime();
                    pauseLayout.setVisibility(View.GONE);
                    playLayout.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(MainActivity.this, "Timer already running", Toast.LENGTH_SHORT).show();
                }

            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTimerRunning){
                    Toast.makeText(MainActivity.this, "Timer already running", Toast.LENGTH_SHORT).show();
                }
                else {
                    showDialog();
                }
            }
        });
    }

    private void showDialog() {
        final Dialog modeDialog =new Dialog(this);
        modeDialog.setContentView(R.layout.mode_selector);
        modeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView timerPlus = (TextView) modeDialog.findViewById(R.id.timer_plus_button);
        final TextView timerMinus = (TextView) modeDialog.findViewById(R.id.timer_minus_button);
        final TextView counterPlus = (TextView) modeDialog.findViewById(R.id.counter_plus_button);
        final TextView counterMinus = (TextView) modeDialog.findViewById(R.id.counter_minus_button);
        final TextView delayPlus = (TextView) modeDialog.findViewById(R.id.delay_plus_button);
        final TextView delayMinus = (TextView) modeDialog.findViewById(R.id.delay_minus_button);
        final TextView delayValue = (TextView) modeDialog.findViewById(R.id.delay_time);
        delayValue.setText(loadDelay());
        final TextView timerValue = (TextView) modeDialog.findViewById(R.id.timer_time);
        timerValue.setText(loadTime());
        final TextView counterValue = (TextView) modeDialog.findViewById(R.id.counter_time);
        counterValue.setText(loadCount());

        final RelativeLayout delayLayout = modeDialog.findViewById(R.id.delay_container);
        delayLayout.setVisibility(View.GONE);
        final RelativeLayout generalButton = modeDialog.findViewById(R.id.general_mode_button);
        generalButton.setBackgroundResource(R.color.button_unselected);
        final RelativeLayout autoButton = modeDialog.findViewById(R.id.auto_mode_button);
        autoButton.setBackgroundResource(R.color.button_unselected);
        final RelativeLayout soundOn = modeDialog.findViewById(R.id.sound_on_button);
        generalButton.setBackgroundResource(R.color.button_unselected);
        final RelativeLayout soundOff = modeDialog.findViewById(R.id.sound_off_button);
        autoButton.setBackgroundResource(R.color.button_unselected);
        final RelativeLayout saveButton = modeDialog.findViewById(R.id.ok_button);

        final ImageView closeButton = (ImageView) modeDialog.findViewById(R.id.close_button);

        settingsMode = loadMode();
        if (settingsMode.equals("auto")){
            setButtonBg(autoButton, generalButton);
            delayLayout.setVisibility(View.VISIBLE);
        }
        else {
            setButtonBg(generalButton, autoButton);
        }

        if (loadSound()){
            soundOff.setBackgroundResource(R.color.button_active);
            soundOn.setBackgroundResource(R.color.button_unselected);
        }
        else {
            soundOff.setBackgroundResource(R.color.button_unselected);
            soundOn.setBackgroundResource(R.color.button_active);
        }

        delayPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = String.valueOf(delayValue.getText());
                String newValue = increment(value);
                delayValue.setText(newValue);
            }
        });

        delayMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = String.valueOf(delayValue.getText());
                String newValue = decrement(value);
                delayValue.setText(newValue);
            }
        });

        timerPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = String.valueOf(timerValue.getText());
                String newValue = increment(value);
                timerValue.setText(newValue);
            }
        });

        timerMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = String.valueOf(timerValue.getText());
                String newValue = decrement(value);
                timerValue.setText(newValue);
            }
        });

        counterPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = String.valueOf(counterValue.getText());
                String newValue = increment(value);
                counterValue.setText(newValue);
            }
        });

        counterMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = String.valueOf(counterValue.getText());
                String newValue = decrement(value);
                counterValue.setText(newValue);
            }
        });

        generalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonBg(generalButton, autoButton);
                settingsMode = "general";
                delayLayout.setVisibility(View.GONE);
            }
        });

        autoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonBg(autoButton, generalButton);
                settingsMode = "auto";
                delayLayout.setVisibility(View.VISIBLE);
            }
        });

        soundOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundOff.setBackgroundResource(R.color.button_unselected);
                soundOn.setBackgroundResource(R.color.button_active);
                settingsMute = false;
            }
        });

        soundOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundOff.setBackgroundResource(R.color.button_active);
                soundOn.setBackgroundResource(R.color.button_unselected);
                settingsMute = true;
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modeDialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timerVal = String.valueOf(timerValue.getText());
                String counterVal = String.valueOf(counterValue.getText());
                String delayVal = String.valueOf(delayValue.getText());
                save(settingsMode, "mode");
                if (settingsMode.equals("auto")){
                    save(delayVal, "delay");
                }
                save(timerVal, "timer");
                save(counterVal, "counter");
                saveSound(settingsMute);
                modeDialog.dismiss();
                recreate();
            }
        });

        modeDialog.show();
    }

    private void setButtonBg(RelativeLayout button, RelativeLayout button1) {
        button.setBackgroundResource(R.color.button_active);
        button1.setBackgroundResource(R.color.button_unselected);
    }

    private void autoSettings(boolean buttonState) {
        autoTimer.cancel();
        if (buttonState) {
            timeLeft = String.valueOf(timer.getText());
            pauseButton.setText("Resume");
            animationView.setVisibility(View.GONE);
            isTimerRunning = false;
        } else {
            pauseButton.setText("Pause");
            animationView.setVisibility(View.VISIBLE);
            if (!isTimerRunning){
                isTimerRunning = true;
                pauseTimer = new CountDownTimer(Long.parseLong(timeLeft) * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        isTimerRunning = true;
                        timer.setText(String.valueOf(millisUntilFinished / 1000));
                        notifyTickSound();
                    }

                    @Override
                    public void onFinish() {
                        isTimerRunning = false;
                        int cnt = Integer.parseInt(count.getText().toString());
                        if (cnt == 1) {
                            reset();
                            animationView.setVisibility(View.VISIBLE);
                            autoTimer.cancel();
                        } else {
                            notifySound();
                            count_text--;
                            count.setText(String.valueOf(count_text));
                            timer.setText("0");
                            try {
                                animationView.setVisibility(View.GONE);
                                Thread.sleep(Integer.parseInt(loadDelay())*1000);
                                animationView.setVisibility(View.VISIBLE);
                            } catch (InterruptedException e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            autoTimer.start();
                        }
                    }
                }.start();
            }
            else {
                Toast.makeText(MainActivity.this, "Timer already running", Toast.LENGTH_SHORT).show();
            }
        }    }

    private void startAutoTimer(String t) {
        if (!isTimerRunning){
            isTimerRunning = true;
            long delay = Long.parseLong(t);
            animationView.setVisibility(View.VISIBLE);
            autoTimer = new CountDownTimer(delay * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    isTimerRunning = true;
                    timer.setText(String.valueOf(millisUntilFinished / 1000));
                    notifyTickSound();
                }

                @Override
                public void onFinish() {
                    isTimerRunning = false;
                    int cnt = Integer.parseInt(count.getText().toString());
                    if (cnt == 1) {
                        reset();
                        animationView.setVisibility(View.VISIBLE);
                        autoTimer.cancel();
                    } else {
                        notifySound();
                        count_text--;
                        count.setText(String.valueOf(count_text));
                        timer.setText("0");
                        try {
                            animationView.setVisibility(View.GONE);
                            Thread.sleep(Integer.parseInt(loadDelay())*1000);
                            animationView.setVisibility(View.VISIBLE);
                        } catch (InterruptedException e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        autoTimer.start();
                    }
                }
            }.start();
        }
        else {
            Toast.makeText(MainActivity.this, "Timer already running", Toast.LENGTH_SHORT).show();
        }
    }

    public void startGeneralTimer() {
        if (!isTimerRunning) {
            isTimerRunning = true;
            String t = loadTime();
            long delay = Long.parseLong(t);
            animationView.setVisibility(View.VISIBLE);
            generalTimer = new CountDownTimer(delay * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    isTimerRunning = true;
                    timer.setText(String.valueOf(millisUntilFinished / 1000));
                    notifyTickSound();
                }

                @Override
                public void onFinish() {
                    isTimerRunning = false;
                    int cnt = Integer.parseInt(count.getText().toString());
                    if (cnt == 1) {
                        reset();
                        animationView.setVisibility(View.VISIBLE);
                    } else {
                        notifySound();
                        count_text--;
                        count.setText(String.valueOf(count_text));
                        timer.setText("0");
                        animationView.setVisibility(View.GONE);
                    }
                }
            }.start();
        } else {
            Toast.makeText(MainActivity.this, "Timer already running", Toast.LENGTH_SHORT).show();
        }
    }

    private void notifyTickSound() {
        if (!loadSound()) {
            final MediaPlayer mp = MediaPlayer.create(this, R.raw.when);
            mp.start();

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
        }
    }

    private void notifySound() {
        if (!loadSound()) {
            final MediaPlayer mp = MediaPlayer.create(this, R.raw.sharp);
            mp.start();

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
        }
    }

    private void reset() {
        count_text = Integer.parseInt(loadCount());
        timer.setText(loadTime());
        count.setText(loadCount());
    }

    public String loadTime() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String val = prefs.getString("timer", "5");
        return val;
    }

    public String loadCount() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String val = prefs.getString("counter", "25");
        return val;
    }

    public String loadMode() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String val = prefs.getString("mode", "general");
        return val;
    }

    public String loadDelay() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String val = prefs.getString("delay", "2");
        return val;
    }

    private void save(String value, String attribute) {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(attribute, value);
        editor.commit();
    }

    private void saveSound(boolean value) {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("sound", value);
        editor.commit();
    }

    public boolean loadSound() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean val = prefs.getBoolean("sound", true);
        return val;
    }

    private String increment(String value){
        try{
            int val = Integer.parseInt(value);
            val += 1;
            return String.valueOf(val);
        }
        catch (Exception e){
            return value;
        }
    }

    private String decrement(String value){
        try{
            int val = Integer.parseInt(value);
            val -= 1;
            if (val <= 0){
                return "0";
            }
            else {
                return String.valueOf(val);
            }
        }
        catch (Exception e){
            return value;
        }
    }

    public void init() {
        timer = (TextView) findViewById(R.id.timer_text);
        timer.setText(loadTime());
        count = (TextView) findViewById(R.id.count_text);
        count.setText(loadCount());
        pauseButton = (TextView) findViewById(R.id.pause);

        settings = (RelativeLayout) findViewById(R.id.settings_selector_button_container);

        resetLayout = (RelativeLayout) findViewById(R.id.reset_layout);
        playLayout = (RelativeLayout) findViewById(R.id.play_layout);
        pauseLayout = (RelativeLayout) findViewById(R.id.pause_layout);
        pauseLayout.setVisibility(View.GONE);

        animationView = (LottieAnimationView) findViewById(R.id.counter_anim);
        animationView.setVisibility(View.GONE);
    }
}

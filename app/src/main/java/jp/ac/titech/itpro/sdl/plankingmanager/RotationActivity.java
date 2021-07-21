package jp.ac.titech.itpro.sdl.plankingmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class RotationActivity extends AppCompatActivity
        implements SensorEventListener {

    protected final static double RAD2DEG = 180/Math.PI;
    protected final static double upThresholdStart = 7; //degree
    protected final static double downThresholdStart = -7; //degree
    protected final static double upThresholdStop = 20; //degree
    protected final static double downThresholdStop = -20; //degree
    protected final static double upThresholdMeasure = 7; //degree
    protected final static double downThresholdMeasure = -7; //degree
    private final double DT = 1;
    private final long[] pattern = new long[]{0,100,500,100,500};


    TextToSpeech t1;
    TextView timer_view,pitchText;
    Button ready,stopHome;
    private int minute,second,m_second;
    private int readySecond = 1;
    private int callFlag;
    private double grad;
    private final LoopEngine loopEngine = new LoopEngine();
    private double startDate;
    private boolean readyFlag = true;
    private boolean beforReadyFlag = true;

    private SensorManager sensorManager;

    private final float[] rotationMatrix = new float[9];
    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];
    private final float[] attitude = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotation);

        timer_view = (TextView)findViewById(R.id.timer);
        ready =(Button)findViewById(R.id.ready_button);
        stopHome =(Button)findViewById(R.id.stop_home_button);
        pitchText = (TextView)findViewById(R.id.pitch);

        timer_view.setText(getString(R.string.ready_txt));

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });


        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    startDate =System.currentTimeMillis();
                    loopEngine.start();
                    ready.setVisibility(View.INVISIBLE);
                    beforReadyFlag = false;
            }
        });


        stopHome.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(readyFlag) {
                    ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).cancel();
                    finish();
                } else {
                    loopEngine.stop();
                    moveResult();
                }
            }
        });



        initSensor();
    }

    public void onResume(){
        super.onResume();
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);
    }

    public void onPause(){
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    protected void initSensor(){
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onSensorChanged(SensorEvent event) {

        switch(event.sensor.getType()){
            case Sensor.TYPE_MAGNETIC_FIELD:
                geomagnetic = event.values.clone();
                break;
            case Sensor.TYPE_ACCELEROMETER:
                gravity = event.values.clone();
                break;
        }

        if(geomagnetic != null && gravity != null){

            SensorManager.getRotationMatrix(
                    rotationMatrix, null,
                    gravity, geomagnetic);

            SensorManager.getOrientation(
                    rotationMatrix,
                    attitude);

            grad = attitude[1] * RAD2DEG;

            pitchText.setText(Integer.toString((int)grad));

            if(!beforReadyFlag) {
                tellGrad();
            }

        }

    }

    public void update(){
        minute =(int)(((System.currentTimeMillis()-startDate)/1000)/60);
        second =(int)(((System.currentTimeMillis()-startDate)/1000)%60);
        m_second =(int)(((System.currentTimeMillis()-startDate)/10)%100);
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d",minute,second,m_second);
        timer_view.setText(timeLeftFormatted);
    }

    public void countDown(){
        readySecond =(int)(Math.abs(Math.abs(((System.currentTimeMillis()-startDate)/1000)%60)-4));
        String timeLeftFormatted = String.format(Locale.getDefault(), "%01d",readySecond);
        timer_view.setText(timeLeftFormatted);
    }

    public void tellGrad(){
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if(downThresholdStop - DT  <= grad && grad <= downThresholdMeasure - DT){
            if(callFlag == 0) {
                t1.speak(getString(R.string.leaning_back_voice), TextToSpeech.QUEUE_FLUSH, null);
                vibrator.vibrate(pattern,0);
                callFlag = 1;
            }
        } else if(upThresholdStop + DT >= grad && grad >= upThresholdMeasure + DT) {
            if(callFlag == 0) {
                t1.speak(getString(R.string.leaning_forward_voice), TextToSpeech.QUEUE_FLUSH, null);
                vibrator.vibrate(pattern,0);
                callFlag = 1;
            }
        } else if(downThresholdMeasure + DT <= grad && grad <= upThresholdMeasure - DT) {
            if (callFlag == 1) {
                callFlag = 0;
                t1.speak(getString(R.string.good_posure_voice), TextToSpeech.QUEUE_FLUSH, null);
                vibrator.cancel();
            }
        } //else if(grad > upThresholdStop || grad < downThresholdStop){
          //  vibrator.cancel();
        //}
    }


    public void moveResult(){
        ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).cancel();
        String trainTime = getIntent().getStringExtra("Train day");
        float measureScore = (float)(minute*60 + second + m_second*0.001);
        Intent intent = new Intent(RotationActivity.this, ResultActivity.class);
        intent.putExtra("Train day", trainTime );
        intent.putExtra("SCORE", measureScore );
        startActivity(intent);
    }

    class LoopEngine extends Handler {
        private boolean isUpdate;
        public void start(){
            this.isUpdate = true;
            handleMessage(new Message());
        }


        public void stop(){
            this.isUpdate = false;
        }

        @Override
        public void handleMessage(Message msg) {
            this.removeMessages(0);//既存のメッセージは削除
            if(this.isUpdate){

                if(readySecond == 0){
                    readyFlag = false;
                    t1.speak(getString(R.string.start_measure_voice), TextToSpeech.QUEUE_FLUSH, null);
                    readySecond = 1;
                    startDate =System.currentTimeMillis();
                }

                if(readyFlag) {
                    if(grad >= downThresholdStart + DT && upThresholdStart - DT >= grad){
                        RotationActivity.this.countDown();
                    } else {
                        timer_view.setText(R.string.grad_txt);
                        startDate =System.currentTimeMillis();
                    }
                }
                else {
                    stopHome.setText(R.string.stop_button);

                    if(grad >= downThresholdStop && upThresholdStop >= grad) {
                        RotationActivity.this.update();//自身が発したメッセージを取得してupdateを実行
                    } else if(grad > upThresholdStop || grad < downThresholdStop){
                        stop();
                        moveResult();
                    }
                }
                sendMessageDelayed(obtainMessage(0), 10);//10ミリ秒後にメッセージを出力
            }
        }
    }
}
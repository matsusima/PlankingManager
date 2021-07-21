package jp.ac.titech.itpro.sdl.plankingmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class ReadyActivity extends AppCompatActivity
        implements SensorEventListener {

    protected final static double RAD2DEG = 180/Math.PI;

    TextView count_view,pitchText;
    int second,m_second;
    Button ready;

    private ReadyActivity.LoopEngine loopEngine = new LoopEngine();
    private long startDate;
    SensorManager sensorManager;

    private float[] rotationMatrix = new float[9];
    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];
    private float[] attitude = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready);

        ready =(Button)findViewById(R.id.ready_button);
        count_view = (TextView)findViewById(R.id.countDown);
        pitchText = (TextView)findViewById(R.id.pitch);

        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDate =System.currentTimeMillis();
                loopEngine.start();
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

            pitchText.setText(Integer.toString(
                    (int)(attitude[1] * RAD2DEG)));

        }

    }

    public void countDown(){
        second =(int)((((System.currentTimeMillis()-startDate))/1000)%60);
        m_second =(int)(((System.currentTimeMillis()-startDate)/10)%100);
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d",second,m_second);
        count_view.setText(timeLeftFormatted);
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
                ReadyActivity.this.countDown();
                sendMessageDelayed(obtainMessage(0), 10);//10ミリ秒後にメッセージを出力
            }
        }
    };
}
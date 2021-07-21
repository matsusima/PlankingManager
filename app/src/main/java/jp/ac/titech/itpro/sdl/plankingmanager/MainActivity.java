package jp.ac.titech.itpro.sdl.plankingmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button ready, ranking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ready =(Button)findViewById(R.id.readyButton);
        ranking = (Button)findViewById(R.id.rankingButton);


        //SharedPreferences datastore = getSharedPreferences("GAME_DATA", MODE_PRIVATE);
        //SharedPreferences.Editor editor = datastore.edit();
        //editor.clear();
        //editor.apply();




        ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RankingActivity.class);
                startActivity(intent);
            }
        });

        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RotationActivity.class);
                intent.putExtra("Train day", getDate() );
                startActivity(intent);
            }
        });
    }

    public static String getDate(){

        //final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        final Date date = new Date(System.currentTimeMillis());

        return df.format(date);
    }
}
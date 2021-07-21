package jp.ac.titech.itpro.sdl.plankingmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class RankingActivity extends AppCompatActivity {


    Button home,back;
    TextView topScore,secondScore,thirdScore,fourthScore,fifthScore;
    TextView topDay,secondDay,thirdDay,fourthDay,fifthDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        home = (Button)findViewById(R.id.homeButton);
        back = (Button)findViewById(R.id.backButton);
        topScore = (TextView)findViewById(R.id.topScore);
        topDay = (TextView)findViewById(R.id.topDay);
        secondScore = (TextView)findViewById(R.id.secondScore);
        secondDay = (TextView)findViewById(R.id.secondDay);
        thirdScore = (TextView)findViewById(R.id.thirdScore);
        thirdDay = (TextView)findViewById(R.id.thirdDay);
        fourthScore = (TextView)findViewById(R.id.fourthScore);
        fourthDay = (TextView)findViewById(R.id.fourthDay);
        fifthScore = (TextView)findViewById(R.id.fifthScore);
        fifthDay = (TextView)findViewById(R.id.fifthDay);


        SharedPreferences datastore = getSharedPreferences("GAME_DATA", MODE_PRIVATE);
        topScore.setText(scoreToTime(datastore.getFloat("Top_SCORE", 0)));
        topDay.setText(datastore.getString("Top_DAY", ""));
        secondScore.setText(scoreToTime(datastore.getFloat("Second_SCORE", 0)));
        secondDay.setText(datastore.getString("Second_DAY", ""));
        thirdScore.setText(scoreToTime(datastore.getFloat("Third_SCORE", 0)));
        thirdDay.setText(datastore.getString("Third_DAY", ""));
        fourthScore.setText(scoreToTime(datastore.getFloat("Fourth_SCORE", 0)));
        fourthDay.setText(datastore.getString("Fourth_DAY", ""));
        fifthScore.setText(scoreToTime(datastore.getFloat("Fifth_SCORE", 0)));
        fifthDay.setText(datastore.getString("Fifth_DAY", ""));


        //Intent intentRanking = new Intent(RankingActivity.this, MainActivity.class);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RankingActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //スタックをクリア
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    public String scoreToTime(float x){
        int minute =(int)((x)/60);
        int second =(int)((x)%60);
        int m_second =(int)((x*1000)%1000);
        return String.format(Locale.getDefault(), "%02d:%02d:%02d",minute,second,m_second);
    }
}
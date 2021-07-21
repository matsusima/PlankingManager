package jp.ac.titech.itpro.sdl.plankingmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;


public class ResultActivity extends AppCompatActivity {

    private final static int rank_num = 5;

    private final String[] day = new String[rank_num];
    private final float[] timeranking = new float[rank_num];

    Button home,ranking;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        home = findViewById(R.id.homeButton);
        ranking = findViewById(R.id.rankingButton);


        TextView scoreLabel = findViewById(R.id.scoreLabel);
        TextView highScoreValue = findViewById(R.id.highScoreValue);

        float score = getIntent().getFloatExtra("SCORE", 0);
        String trainTime = getIntent().getStringExtra("Train day");
        scoreLabel.setText(scoreToTime(score));

        SharedPreferences datastore = getSharedPreferences("GAME_DATA", MODE_PRIVATE);
        timeranking[0] = datastore.getFloat("Top_SCORE", 0);
        timeranking[1] = datastore.getFloat("Second_SCORE", 0);
        timeranking[2] = datastore.getFloat("Third_SCORE", 0);
        timeranking[3] = datastore.getFloat("Fourth_SCORE", 0);
        timeranking[4] = datastore.getFloat("Fifth_SCORE", 0);

        day[0] = datastore.getString("Top_DAY", "");
        day[1] = datastore.getString("Second_DAY", "");
        day[2] = datastore.getString("Third_DAY", "");
        day[3] = datastore.getString("Fourth_DAY", "");
        day[4] = datastore.getString("Fifth_DAY", "");

        highScoreValue.setText(scoreToTime(timeranking[0]));


        //降順にsort
        if (score >= timeranking[rank_num-1]){
            timeranking[rank_num-1] = score;
            day[rank_num-1] = trainTime;
        }
        for (int i=1; i < rank_num; i++) {
            if (score >= timeranking[rank_num-i-1]) {
                timeranking[rank_num - i] = timeranking[rank_num - i - 1];
                timeranking[rank_num - i - 1] = score;
                day[rank_num - i] = day[rank_num - i - 1];
                day[rank_num - i - 1] = trainTime;
            }
        }

        SharedPreferences.Editor editor = datastore.edit();
        editor.putFloat("Top_SCORE", timeranking[0]);
        editor.putString("Top_DAY", day[0]);
        editor.putFloat("Second_SCORE", timeranking[1]);
        editor.putString("Second_DAY", day[1]);
        editor.putFloat("Third_SCORE", timeranking[2]);
        editor.putString("Third_DAY", day[2]);
        editor.putFloat("Fourth_SCORE", timeranking[3]);
        editor.putString("Fourth_DAY", day[3]);
        editor.putFloat("Fifth_SCORE", timeranking[4]);
        editor.putString("Fifth_DAY", day[4]);
        editor.apply();


        //saveScore();

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //スタックをクリア
                startActivity(intent);
            }
        });

        ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, RankingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //スタックをクリア
                startActivity(intent);
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
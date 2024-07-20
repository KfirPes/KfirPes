package dev.tomco.a24b_11345a_l02_03;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.Random;

import dev.tomco.a24b_11345a_l02_03.Utilities.MoveDetector;
import dev.tomco.a24b_11345a_l02_03.Utilities.SoundPlayer;
import dev.tomco.a24b_11345a_l02_03.Interfaces.MoveCallback;
import dev.tomco.a24b_11345a_l02_03.Utilities.MoveDetector;
import dev.tomco.a24b_11345a_l02_03.Utilities.SoundPlayer;


import dev.tomco.a24b_11345a_l02_03.Logic.GameManager;

public class GameActivity extends AppCompatActivity {

    public static final String KEY_STATUS = "KEY_STATUS";
    private MaterialTextView main_LBL_score;
    private MaterialButton main_BTN_yes;
    private MaterialButton main_BTN_no;
    private AppCompatImageView[] main_IMG_hearts;

    private LinearLayoutCompat[][] main_obstacle;
    private LinearLayoutCompat[][] main_coin;
    private LinearLayoutCompat[] main_body;

    private GameManager gameManager;
    private static final long DELAY1 = 40L;
    final Handler handler = new Handler();
    private boolean timerOn = false;
    private long startTime;
    private int crashes=0;
    private SoundPlayer soundPlayer;
    private MoveDetector moveDetector;
    private boolean status=false;

    Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, DELAY1);
            if (gameManager.getStatus()%3==0) {
                main_obstacle[gameManager.getRowObs()][gameManager.getColObs()]
                        .setVisibility(View.INVISIBLE);
                if (gameManager.getRowObs() != 17) {
                    main_obstacle[gameManager.getRowObs() + 1][gameManager.getColObs()]
                            .setVisibility(View.VISIBLE);
                    gameManager.setRowObs();
                } else {
                    Random rnd = new Random();
                    int randomNumber = rnd.nextInt(5);
                    main_obstacle[0][randomNumber]
                            .setVisibility(View.VISIBLE);
                    gameManager.setColObs(randomNumber);
                    refreshUI();
                }
            } if (gameManager.getStatus()%2==0) {
                main_coin[gameManager.getRowCoin()][gameManager.getColCoin()]
                        .setVisibility(View.INVISIBLE);
                if (gameManager.getRowCoin()!=17) {
                    main_coin[gameManager.getRowCoin() + 1][gameManager.getColCoin()]
                            .setVisibility(View.VISIBLE);
                    gameManager.setRowCoin();
                }
                else{
                    Random rnd=new Random();
                    int randomNumber = rnd.nextInt(5);
                    main_coin[0][randomNumber]
                            .setVisibility(View.VISIBLE);
                    gameManager.setColCoin(randomNumber);
                    refreshUI();
                }
            }
            gameManager.setStatus();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        findViews();
        gameManager = new GameManager(main_IMG_hearts.length);
        initViews();
    }

    private void initViews() {
        main_LBL_score.setText(String.valueOf(gameManager.getScore()));
        Intent previousActivity = getIntent();
        boolean status = previousActivity.getBooleanExtra(KEY_STATUS,false);
        this.status=status;
        if (status=false) {
            main_BTN_yes.setOnClickListener(view -> LeftRightBody(true));
            main_BTN_no.setOnClickListener(view -> LeftRightBody(false));
        }
        refreshUI();
        refreshObstracleCoin();
    }
    private void initMoveDetector() {
        moveDetector = new MoveDetector(this,
                new MoveCallback() {
                    @Override
                    public void moveX() {
                        gameManager.setNumBody(moveDetector.getMoveCountX());
                    }

                }
        );
    }
    private void LeftRightBody(boolean where){
        gameManager.setNumBody(where);
        int num=gameManager.getNumBody();
        for (int i=0;i<5;i++)
        {
            if (num==i){
                main_body[i].setVisibility(View.VISIBLE);
            }
            else
                main_body[i].setVisibility(View.INVISIBLE);
        }

    }

    private void refreshUI() {
        //lost:
        if (gameManager.isGameLost()) {
            //show "LOST!"
            Log.d("Game Status:", "GAME OVER " + gameManager.getScore());
            toastAndVibrate("😭 GAME OVER"+ gameManager.getScore());
            gameManager.setWrongAnswers(0);
            for(int i=0;i<3;i++)
                main_IMG_hearts[i].setVisibility(View.VISIBLE);
        }
        //won:


        //game still on:
        else {
            main_LBL_score.setText(String.valueOf(gameManager.getScore()));
            if (gameManager.getWrongAnswers() != 0) {
                main_IMG_hearts[main_IMG_hearts.length - gameManager.getWrongAnswers()]
                        .setVisibility(View.INVISIBLE);
            }
            if(gameManager.getWrongAnswers()>crashes) {
                crashes = gameManager.getWrongAnswers();
                toastAndVibrate("crash");
            }
        }
    }
    private void refreshObstracleCoin(){
        Random rndObs=new Random();
        int randomNumberObs = rndObs.nextInt(5);
        main_obstacle[0][randomNumberObs]
                .setVisibility(View.VISIBLE);
        gameManager.setColObs(randomNumberObs);
        Random rnd=new Random();
        int randomNumberCoin = rnd.nextInt(5);
        main_coin[0][randomNumberCoin]
                .setVisibility(View.VISIBLE);
        gameManager.setColCoin(randomNumberCoin);
        if (!timerOn) {
            startTime = System.currentTimeMillis();
            handler.postDelayed(runnable1, 0);
            timerOn = true;
        }
    }

    private void findViews() {
        main_LBL_score = findViewById(R.id.main_LBL_score);
        main_BTN_yes = findViewById(R.id.main_BTN_yes);
        main_BTN_no = findViewById(R.id.main_BTN_no);
        main_IMG_hearts = new AppCompatImageView[]{
                findViewById(R.id.main_IMG_heart1),
                findViewById(R.id.main_IMG_heart2),
                findViewById(R.id.main_IMG_heart3)
        };
        main_obstacle = new LinearLayoutCompat[18][5];
        main_obstacle[0][0]=findViewById(R.id.main_Obstacle1);main_obstacle[0][1]=findViewById(R.id.main_Obstacle2);main_obstacle[0][2]=findViewById(R.id.main_Obstacle3);main_obstacle[0][3]=findViewById(R.id.main_Obstacle4);main_obstacle[0][4]=findViewById(R.id.main_Obstacle5);
        main_obstacle[1][0]=findViewById(R.id.main_Obstacle6);main_obstacle[1][1]=findViewById(R.id.main_Obstacle7);main_obstacle[1][2]=findViewById(R.id.main_Obstacle8);main_obstacle[1][3]=findViewById(R.id.main_Obstacle9);main_obstacle[1][4]=findViewById(R.id.main_Obstacle10);
        main_obstacle[2][0]=findViewById(R.id.main_Obstacle11);main_obstacle[2][1]=findViewById(R.id.main_Obstacle12);main_obstacle[2][2]=findViewById(R.id.main_Obstacle13);main_obstacle[2][3]=findViewById(R.id.main_Obstacle14);main_obstacle[2][4]=findViewById(R.id.main_Obstacle15);
        main_obstacle[3][0]=findViewById(R.id.main_Obstacle16);main_obstacle[3][1]=findViewById(R.id.main_Obstacle17);main_obstacle[3][2]=findViewById(R.id.main_Obstacle18);main_obstacle[3][3]=findViewById(R.id.main_Obstacle19);main_obstacle[3][4]=findViewById(R.id.main_Obstacle20);
        main_obstacle[4][0]=findViewById(R.id.main_Obstacle21);main_obstacle[4][1]=findViewById(R.id.main_Obstacle22);main_obstacle[4][2]=findViewById(R.id.main_Obstacle23);main_obstacle[4][3]=findViewById(R.id.main_Obstacle24);main_obstacle[4][4]=findViewById(R.id.main_Obstacle25);
        main_obstacle[5][0]=findViewById(R.id.main_Obstacle26);main_obstacle[5][1]=findViewById(R.id.main_Obstacle27);main_obstacle[5][2]=findViewById(R.id.main_Obstacle28);main_obstacle[5][3]=findViewById(R.id.main_Obstacle29);main_obstacle[5][4]=findViewById(R.id.main_Obstacle30);
        main_obstacle[6][0]=findViewById(R.id.main_Obstacle31);main_obstacle[6][1]=findViewById(R.id.main_Obstacle32);main_obstacle[6][2]=findViewById(R.id.main_Obstacle33);main_obstacle[6][3]=findViewById(R.id.main_Obstacle34);main_obstacle[6][4]=findViewById(R.id.main_Obstacle35);
        main_obstacle[7][0]=findViewById(R.id.main_Obstacle36);main_obstacle[7][1]=findViewById(R.id.main_Obstacle37);main_obstacle[7][2]=findViewById(R.id.main_Obstacle38);main_obstacle[7][3]=findViewById(R.id.main_Obstacle39);main_obstacle[7][4]=findViewById(R.id.main_Obstacle40);
        main_obstacle[8][0]=findViewById(R.id.main_Obstacle41);main_obstacle[8][1]=findViewById(R.id.main_Obstacle42);main_obstacle[8][2]=findViewById(R.id.main_Obstacle43);main_obstacle[8][3]=findViewById(R.id.main_Obstacle44);main_obstacle[8][4]=findViewById(R.id.main_Obstacle45);
        main_obstacle[9][0]=findViewById(R.id.main_Obstacle46);main_obstacle[9][1]=findViewById(R.id.main_Obstacle47);main_obstacle[9][2]=findViewById(R.id.main_Obstacle48);main_obstacle[9][3]=findViewById(R.id.main_Obstacle49);main_obstacle[9][4]=findViewById(R.id.main_Obstacle50);
        main_obstacle[10][0]=findViewById(R.id.main_Obstacle51);main_obstacle[10][1]=findViewById(R.id.main_Obstacle52);main_obstacle[10][2]=findViewById(R.id.main_Obstacle53);main_obstacle[10][3]=findViewById(R.id.main_Obstacle54);main_obstacle[10][4]=findViewById(R.id.main_Obstacle55);
        main_obstacle[11][0]=findViewById(R.id.main_Obstacle56);main_obstacle[11][1]=findViewById(R.id.main_Obstacle57);main_obstacle[11][2]=findViewById(R.id.main_Obstacle58);main_obstacle[11][3]=findViewById(R.id.main_Obstacle59);main_obstacle[11][4]=findViewById(R.id.main_Obstacle60);
        main_obstacle[12][0]=findViewById(R.id.main_Obstacle61);main_obstacle[12][1]=findViewById(R.id.main_Obstacle62);main_obstacle[12][2]=findViewById(R.id.main_Obstacle63);main_obstacle[12][3]=findViewById(R.id.main_Obstacle64);main_obstacle[12][4]=findViewById(R.id.main_Obstacle65);
        main_obstacle[13][0]=findViewById(R.id.main_Obstacle66);main_obstacle[13][1]=findViewById(R.id.main_Obstacle67);main_obstacle[13][2]=findViewById(R.id.main_Obstacle68);main_obstacle[13][3]=findViewById(R.id.main_Obstacle69);main_obstacle[13][4]=findViewById(R.id.main_Obstacle70);
        main_obstacle[14][0]=findViewById(R.id.main_Obstacle71);main_obstacle[14][1]=findViewById(R.id.main_Obstacle72);main_obstacle[14][2]=findViewById(R.id.main_Obstacle73);main_obstacle[14][3]=findViewById(R.id.main_Obstacle74);main_obstacle[14][4]=findViewById(R.id.main_Obstacle75);
        main_obstacle[15][0]=findViewById(R.id.main_Obstacle76);main_obstacle[15][1]=findViewById(R.id.main_Obstacle77);main_obstacle[15][2]=findViewById(R.id.main_Obstacle78);main_obstacle[15][3]=findViewById(R.id.main_Obstacle79);main_obstacle[15][4]=findViewById(R.id.main_Obstacle80);
        main_obstacle[16][0]=findViewById(R.id.main_Obstacle81);main_obstacle[16][1]=findViewById(R.id.main_Obstacle82);main_obstacle[16][2]=findViewById(R.id.main_Obstacle83);main_obstacle[16][3]=findViewById(R.id.main_Obstacle84);main_obstacle[16][4]=findViewById(R.id.main_Obstacle85);
        main_obstacle[17][0]=findViewById(R.id.main_Obstacle86);main_obstacle[17][1]=findViewById(R.id.main_Obstacle87);main_obstacle[17][2]=findViewById(R.id.main_Obstacle88);main_obstacle[17][3]=findViewById(R.id.main_Obstacle89);main_obstacle[17][4]=findViewById(R.id.main_Obstacle90);
        main_coin = new LinearLayoutCompat[18][5];
        main_coin[0][0]=findViewById(R.id.main_coin1);main_coin[0][1]=findViewById(R.id.main_coin2);main_coin[0][2]=findViewById(R.id.main_coin3);main_coin[0][3]=findViewById(R.id.main_coin4);main_coin[0][4]=findViewById(R.id.main_coin5);
        main_coin[1][0]=findViewById(R.id.main_coin6);main_coin[1][1]=findViewById(R.id.main_coin7);main_coin[1][2]=findViewById(R.id.main_coin8);main_coin[1][3]=findViewById(R.id.main_coin9);main_coin[1][4]=findViewById(R.id.main_coin10);
        main_coin[2][0]=findViewById(R.id.main_coin11);main_coin[2][1]=findViewById(R.id.main_coin12);main_coin[2][2]=findViewById(R.id.main_coin13);main_coin[2][3]=findViewById(R.id.main_coin14);main_coin[2][4]=findViewById(R.id.main_coin15);
        main_coin[3][0]=findViewById(R.id.main_coin16);main_coin[3][1]=findViewById(R.id.main_coin17);main_coin[3][2]=findViewById(R.id.main_coin18);main_coin[3][3]=findViewById(R.id.main_coin19);main_coin[3][4]=findViewById(R.id.main_coin20);
        main_coin[4][0]=findViewById(R.id.main_coin21);main_coin[4][1]=findViewById(R.id.main_coin22);main_coin[4][2]=findViewById(R.id.main_coin23);main_coin[4][3]=findViewById(R.id.main_coin24);main_coin[4][4]=findViewById(R.id.main_coin25);
        main_coin[5][0]=findViewById(R.id.main_coin26);main_coin[5][1]=findViewById(R.id.main_coin27);main_coin[5][2]=findViewById(R.id.main_coin28);main_coin[5][3]=findViewById(R.id.main_coin29);main_coin[5][4]=findViewById(R.id.main_coin30);
        main_coin[6][0]=findViewById(R.id.main_coin31);main_coin[6][1]=findViewById(R.id.main_coin32);main_coin[6][2]=findViewById(R.id.main_coin33);main_coin[6][3]=findViewById(R.id.main_coin34);main_coin[6][4]=findViewById(R.id.main_coin35);
        main_coin[7][0]=findViewById(R.id.main_coin36);main_coin[7][1]=findViewById(R.id.main_coin37);main_coin[7][2]=findViewById(R.id.main_coin38);main_coin[7][3]=findViewById(R.id.main_coin39);main_coin[7][4]=findViewById(R.id.main_coin40);
        main_coin[8][0]=findViewById(R.id.main_coin41);main_coin[8][1]=findViewById(R.id.main_coin42);main_coin[8][2]=findViewById(R.id.main_coin43);main_coin[8][3]=findViewById(R.id.main_coin44);main_coin[8][4]=findViewById(R.id.main_coin45);
        main_coin[9][0]=findViewById(R.id.main_coin46);main_coin[9][1]=findViewById(R.id.main_coin47);main_coin[9][2]=findViewById(R.id.main_coin48);main_coin[9][3]=findViewById(R.id.main_coin49);main_coin[9][4]=findViewById(R.id.main_coin50);
        main_coin[10][0]=findViewById(R.id.main_coin51);main_coin[10][1]=findViewById(R.id.main_coin52);main_coin[10][2]=findViewById(R.id.main_coin53);main_coin[10][3]=findViewById(R.id.main_coin54);main_coin[10][4]=findViewById(R.id.main_coin55);
        main_coin[11][0]=findViewById(R.id.main_coin56);main_coin[11][1]=findViewById(R.id.main_coin57);main_coin[11][2]=findViewById(R.id.main_coin58);main_coin[11][3]=findViewById(R.id.main_coin59);main_coin[11][4]=findViewById(R.id.main_coin60);
        main_coin[12][0]=findViewById(R.id.main_coin61);main_coin[12][1]=findViewById(R.id.main_coin62);main_coin[12][2]=findViewById(R.id.main_coin63);main_coin[12][3]=findViewById(R.id.main_coin64);main_coin[12][4]=findViewById(R.id.main_coin65);
        main_coin[13][0]=findViewById(R.id.main_coin66);main_coin[13][1]=findViewById(R.id.main_coin67);main_coin[13][2]=findViewById(R.id.main_coin68);main_coin[13][3]=findViewById(R.id.main_coin69);main_coin[13][4]=findViewById(R.id.main_coin70);
        main_coin[14][0]=findViewById(R.id.main_coin71);main_coin[14][1]=findViewById(R.id.main_coin72);main_coin[14][2]=findViewById(R.id.main_coin73);main_coin[14][3]=findViewById(R.id.main_coin74);main_coin[14][4]=findViewById(R.id.main_coin75);
        main_coin[15][0]=findViewById(R.id.main_coin76);main_coin[15][1]=findViewById(R.id.main_coin77);main_coin[15][2]=findViewById(R.id.main_coin78);main_coin[15][3]=findViewById(R.id.main_coin79);main_coin[15][4]=findViewById(R.id.main_coin80);
        main_coin[16][0]=findViewById(R.id.main_coin81);main_coin[16][1]=findViewById(R.id.main_coin82);main_coin[16][2]=findViewById(R.id.main_coin83);main_coin[16][3]=findViewById(R.id.main_coin84);main_coin[16][4]=findViewById(R.id.main_coin85);
        main_coin[17][0]=findViewById(R.id.main_coin86);main_coin[17][1]=findViewById(R.id.main_coin87);main_coin[17][2]=findViewById(R.id.main_coin88);main_coin[17][3]=findViewById(R.id.main_coin89);main_coin[17][4]=findViewById(R.id.main_coin90);
        main_body=new LinearLayoutCompat[]{findViewById(R.id.main_body1),findViewById(R.id.main_body2),findViewById(R.id.main_body3),findViewById(R.id.main_body4),findViewById(R.id.main_body5),};

    }
    public void toastAndVibrate(String text) {
        vibrate();
        toast(text);
    }

    public void toast(String text) {
        Toast.makeText(this, text,
                Toast.LENGTH_LONG).show();
    }

    public void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
    }
}
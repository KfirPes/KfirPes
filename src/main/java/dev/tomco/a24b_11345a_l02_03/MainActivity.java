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

import dev.tomco.a24b_11345a_l02_03.Logic.GameManager;
import dev.tomco.a24b_11345a_l02_03.Utilities.MoveDetector;
import dev.tomco.a24b_11345a_l02_03.Utilities.SoundPlayer;

public class MainActivity extends AppCompatActivity {

    private MaterialTextView main_LBL_score;
    private MaterialButton main_BTN_yes;
    private MaterialButton main_BTN_no;
    private SoundPlayer soundPlayer;
    private MoveDetector moveDetector;


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
        initViews();
    }


    private void initViews() {
        main_BTN_yes.setOnClickListener(view -> changeActivity(true));
        main_BTN_no.setOnClickListener(view -> changeActivity(false));
    }
    private void changeActivity(boolean status) {
        Intent scoreIntent = new Intent(this, GameActivity.class);
        scoreIntent.putExtra(GameActivity.KEY_STATUS, status);
        startActivity(scoreIntent);
        finish();
    }
    private void findViews() {
        main_LBL_score = findViewById(R.id.main_LBL_score);
        main_BTN_yes = findViewById(R.id.main_BTN_yes);
        main_BTN_no = findViewById(R.id.main_BTN_no);

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
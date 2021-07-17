package com.androcoders.bingo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GameActivity extends AppCompatActivity {
    private int val=1;
    private boolean isFilled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getSupportActionBar().hide();

        int numbers[] = {R.id.btn1,R.id.btn2,R.id.btn3,R.id.btn4,R.id.btn5,R.id.btn6,R.id.btn7,R.id.btn8,R.id.btn9,R.id.btn10,R.id.btn11,R.id.btn12,
                R.id.btn13,R.id.btn14,R.id.btn15,R.id.btn16,R.id.btn17,R.id.btn18,R.id.btn19,R.id.btn20,R.id.btn21,R.id.btn22,R.id.btn23,R.id.btn24,R.id.btn25};

        for (int i=0;i<numbers.length;i++){
            Button number = findViewById(numbers[i]);
            number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isFilled){
                        number.setText(""+val);
                        number.setTextColor(getColor(R.color.white));
                        val++;
                        if(val>25) isFilled=true;

                    }
                    else{
                        number.setBackground(getDrawable(R.drawable.gradient2));
                    }
                }
            });
        }

        int lines[]={R.id.diagonal1,R.id.diagonal2,R.id.row1,R.id.row2,R.id.row3,R.id.row4,R.id.row5,R.id.column1,R.id.column2,R.id.column3,R.id.column4,R.id.column5};


        for (int i=0;i<lines.length;i++){
            View line=findViewById(lines[i]);
            line.setVisibility(View.INVISIBLE);
        }
    }
}
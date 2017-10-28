package com.joelbarsky.gns_game;

import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.os.Bundle;
import android.content.*;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.*;
import java.lang.Object;
import android.util.DisplayMetrics;


public class Game extends Activity{

    int column1;
    int column2;
    int column3;
    int column4;
    int column5;
    int column6;
    int column7;
    int column8;
    int column9;
    int column10;
    int column11;
    int columnSnappingRange;

    int row1;
    private ViewGroup mainLayout;

    private ImageView aceD;
    private ImageView aceC;
    private ImageView aceH;
    private ImageView aceS;
    private ImageView twoD;
    private ImageView twoC;
    private ImageView twoH;
    private ImageView twoS;
    private ImageView threeD;
    private ImageView threeC;
    private ImageView threeH;
    private ImageView threeS;
    private ImageView fourD;
    private ImageView fourC;
    private ImageView fourH;
    private ImageView fourS;
    private ImageView fiveD;
    private ImageView fiveC;
    private ImageView fiveH;
    private ImageView fiveS;
    private ImageView sixD;
    private ImageView sixC;
    private ImageView sixH;
    private ImageView sixS;
    private ImageView sevenD;
    private ImageView sevenC;
    private ImageView sevenH;
    private ImageView sevenS;
    private ImageView eightD;
    private ImageView eightC;
    private ImageView eightH;
    private ImageView eightS;
    private ImageView nineD;
    private ImageView nineC;
    private ImageView nineH;
    private ImageView nineS;
    private ImageView tenD;
    private ImageView tenC;
    private ImageView tenH;
    private ImageView tenS;
    private ImageView jackD;
    private ImageView jackC;
    private ImageView jackH;
    private ImageView jackS;
    private ImageView queenD;
    private ImageView queenC;
    private ImageView queenH;
    private ImageView queenS;
    private ImageView kingD;
    private ImageView kingC;
    private ImageView kingH;
    private ImageView kingS;

    private int xDelta;
    private int yDelta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_screen);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels; //x resolution
        int height = metrics.heightPixels; //y resolution
        column1 = width/11/2+1*width/11;
        column2 = width/11*2+2*width/11;
        column3 = width/11*2+3*width/11;

        column4 = width/11*2+4*width/11;
        column5 = width/11*2+5*width/11;
        column6 = width/11*2+6*width/11;
        column7 = width/11*2+7*width/11;
        column8 = width/11*2+8*width/11;
        column9 = width/11*2+9*width/11;
        column10 = width/11*2+10*width/11;
        column11 = width/11*2+11*width/11;
        columnSnappingRange = width/11/2;
        row1 = height/6*1;

        Log.d("ApplicationTagName", "Display width in px is " + metrics.widthPixels);

        mainLayout = (RelativeLayout) findViewById(R.id.game_screen);
        aceD = (ImageView) findViewById(R.id.imageView2);
        aceC = (ImageView) findViewById(R.id.imageView);
        aceH = (ImageView) findViewById(R.id.imageView4);
        aceS = (ImageView) findViewById(R.id.imageView5);
//        twoD = (ImageView) findViewById(R.id.imageView7);
//        twoC = (ImageView) findViewById(R.id.imageView6);
//        twoH = (ImageView) findViewById(R.id.imageView8);
//        twoS = (ImageView) findViewById(R.id.imageView9);
//        threeD = (ImageView) findViewById(R.id.imageView11);
//        threeC = (ImageView) findViewById(R.id.imageView10);
//        threeH = (ImageView) findViewById(R.id.imageView12);
//        threeS = (ImageView) findViewById(R.id.imageView13);
//        fourD = (ImageView) findViewById(R.id.imageView16);
//        fourC = (ImageView) findViewById(R.id.imageView14);
//        fourH = (ImageView) findViewById(R.id.imageView17);
//        fourS = (ImageView) findViewById(R.id.imageView15);
//        fiveD = (ImageView) findViewById(R.id.imageView19);
//        fiveC = (ImageView) findViewById(R.id.imageView21);
//        fiveH = (ImageView) findViewById(R.id.imageView20);
//        fiveS = (ImageView) findViewById(R.id.imageView55);
//        sixD = (ImageView) findViewById(R.id.imageView23);
//        sixC = (ImageView) findViewById(R.id.imageView22);
//        sixH = (ImageView) findViewById(R.id.imageView24);
//        sixS = (ImageView) findViewById(R.id.imageView25);
//        sevenD = (ImageView) findViewById(R.id.imageView27);
//        sevenC = (ImageView) findViewById(R.id.imageView26);
//        sevenH = (ImageView) findViewById(R.id.imageView28);
//        sevenS = (ImageView) findViewById(R.id.imageView29);
//        eightD = (ImageView) findViewById(R.id.imageView31);
//        eightC = (ImageView) findViewById(R.id.imageView30);
//        eightH = (ImageView) findViewById(R.id.imageView32);
//        eightS = (ImageView) findViewById(R.id.imageView33);
//        nineD = (ImageView) findViewById(R.id.imageView35);
//        nineC = (ImageView) findViewById(R.id.imageView34);
//        nineH = (ImageView) findViewById(R.id.imageView36);
//        nineS = (ImageView) findViewById(R.id.imageView37);
//        tenD = (ImageView) findViewById(R.id.imageView29);
//        tenC = (ImageView) findViewById(R.id.imageView38);
//        tenH = (ImageView) findViewById(R.id.imageView40);
//        tenS = (ImageView) findViewById(R.id.imageView41);
//        jackD = (ImageView) findViewById(R.id.imageView43);
//        jackC = (ImageView) findViewById(R.id.imageView42);
//        jackH = (ImageView) findViewById(R.id.imageView44);
//        jackS = (ImageView) findViewById(R.id.imageView45);
//        queenD = (ImageView) findViewById(R.id.imageView47);
//        queenC = (ImageView) findViewById(R.id.imageView46);
//        queenH = (ImageView) findViewById(R.id.imageView48);
//        queenS = (ImageView) findViewById(R.id.imageView49);
//        kingD = (ImageView) findViewById(R.id.imageView52);
//        kingC = (ImageView) findViewById(R.id.imageView51);
//        kingH = (ImageView) findViewById(R.id.imageView53);
//        kingS = (ImageView) findViewById(R.id.imageView54);

        aceD.setOnTouchListener(onTouchListener());
        aceC.setOnTouchListener(onTouchListener());
        aceH.setOnTouchListener(onTouchListener());
        aceS.setOnTouchListener(onTouchListener());
//        twoD.setOnTouchListener(onTouchListener());
//        twoC.setOnTouchListener(onTouchListener());
//        twoH.setOnTouchListener(onTouchListener());
//        twoS.setOnTouchListener(onTouchListener());
//        threeD.setOnTouchListener(onTouchListener());
//        threeC.setOnTouchListener(onTouchListener());
//        threeH.setOnTouchListener(onTouchListener());
//        threeS.setOnTouchListener(onTouchListener());
//        fourD.setOnTouchListener(onTouchListener());
//        fourC.setOnTouchListener(onTouchListener());
//        fourH.setOnTouchListener(onTouchListener());
//        fourS.setOnTouchListener(onTouchListener());
//        fiveD.setOnTouchListener(onTouchListener());
//        fiveC.setOnTouchListener(onTouchListener());
//        fiveH.setOnTouchListener(onTouchListener());
//        fiveS.setOnTouchListener(onTouchListener());
//        sixD.setOnTouchListener(onTouchListener());
//        sixC.setOnTouchListener(onTouchListener());
//        sixH.setOnTouchListener(onTouchListener());
//        sixS.setOnTouchListener(onTouchListener());
//        sevenD.setOnTouchListener(onTouchListener());
//        sevenC.setOnTouchListener(onTouchListener());
//        sevenH.setOnTouchListener(onTouchListener());
//        sevenS.setOnTouchListener(onTouchListener());
//        eightD.setOnTouchListener(onTouchListener());
//        eightC.setOnTouchListener(onTouchListener());
//        eightH.setOnTouchListener(onTouchListener());
//        eightS.setOnTouchListener(onTouchListener());
//        nineD.setOnTouchListener(onTouchListener());
//        nineC.setOnTouchListener(onTouchListener());
//        nineH.setOnTouchListener(onTouchListener());
//        nineS.setOnTouchListener(onTouchListener());
//        tenD.setOnTouchListener(onTouchListener());
//        tenC.setOnTouchListener(onTouchListener());
//        tenH.setOnTouchListener(onTouchListener());
//        tenS.setOnTouchListener(onTouchListener());
//        jackD.setOnTouchListener(onTouchListener());
//        jackC.setOnTouchListener(onTouchListener());
//        jackH.setOnTouchListener(onTouchListener());
//        jackS.setOnTouchListener(onTouchListener());
//        queenD.setOnTouchListener(onTouchListener());
//        queenC.setOnTouchListener(onTouchListener());
//        queenH.setOnTouchListener(onTouchListener());
//        queenS.setOnTouchListener(onTouchListener());
//        kingD.setOnTouchListener(onTouchListener());
//        kingC.setOnTouchListener(onTouchListener());
//        kingH.setOnTouchListener(onTouchListener());
//        kingS.setOnTouchListener(onTouchListener());


    }
    private OnTouchListener onTouchListener(){
        return new OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:


                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
                                view.getLayoutParams();

                        xDelta = x - lParams.leftMargin;
                        yDelta = y - lParams.topMargin;
                        break;

                    case MotionEvent.ACTION_UP:
                        RelativeLayout.LayoutParams layoutParam = (RelativeLayout.LayoutParams) view
                                .getLayoutParams();
                        System.out.println("Column3: " + column5);
                        System.out.println("Left: " + layoutParam.leftMargin);
                        columnSnapping(layoutParam);
                        view.setLayoutParams(layoutParam);
 //                       Toast.makeText(MainActivity.this,
 //                               "thanks for new location!", Toast.LENGTH_SHORT)
 //                               .show();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                                .getLayoutParams();
                        layoutParams.leftMargin = x - xDelta;

                        layoutParams.topMargin = y - yDelta;
                        layoutParams.rightMargin = 0;
                        layoutParams.bottomMargin = 0;
                        view.setLayoutParams(layoutParams);
                        break;
                }
                mainLayout.invalidate();
                return true;
            }
        };
    }

    public void columnSnapping(RelativeLayout.LayoutParams layout){
        if (Math.abs(layout.leftMargin - column1) < columnSnappingRange){
            layout.leftMargin = column1;
        }
        if (Math.abs(layout.leftMargin - column2) < columnSnappingRange){
            layout.leftMargin = column2;
        }
        if (Math.abs(layout.leftMargin - column3) < columnSnappingRange){
            layout.leftMargin = column3;
        }
        if (Math.abs(layout.leftMargin - column4) < columnSnappingRange){
            layout.leftMargin = column4;
        }
        if (Math.abs(layout.leftMargin - column5) < columnSnappingRange){
            layout.leftMargin = column5;
        }
        if (Math.abs(layout.leftMargin - column6) < columnSnappingRange){
            layout.leftMargin = column6;
        }
        if (Math.abs(layout.leftMargin - column7) < columnSnappingRange){
            layout.leftMargin = column7;
        }
        if (Math.abs(layout.leftMargin - column8) < columnSnappingRange){
            layout.leftMargin = column8;
        }
        if (Math.abs(layout.leftMargin - column9) < columnSnappingRange) {
            layout.leftMargin = column9;
        }
        if (Math.abs(layout.leftMargin - column10) < columnSnappingRange){
            layout.leftMargin = column10;
        }
    }

    public void home_screen(View v){
        Intent toHome = new Intent(v.getContext(), Home.class);
        startActivityForResult(toHome, 0);
    }


}
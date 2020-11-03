package com.catata.juegolottie;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.icu.number.Scale;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {
    RelativeLayout contenedor;
    TextView tvPuntos;
    int puntos = 0;
    int ancho, alto;

    final int MIN_TIME_DISPLAY = 1000;
    final int MAX_TIME_DISPLAY = 2000;
    final int MIN_SIZE = 20;
    final int MAX_SIZE = 60;
    final int MIN_NEXT_TIME = 1000;
    final int MAX_NEXT_TIME = 2000;
    final int VIDAS = 5;

    final int[] colores={android.R.color.darker_gray,android.R.color.background_light,android.R.color.holo_red_light,android.R.color.holo_orange_dark,android.R.color.holo_purple};


    Boolean stop_game=false;

    LottieAnimationView winner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contenedor = findViewById(R.id.juego);
        tvPuntos = findViewById(R.id.tvPuntos);
        //explode = findViewById(R.id.explode);
        winner = findViewById(R.id.winner);

        tvPuntos.setText(""+puntos);



        contenedor.post(new Runnable() {
            @Override
            public void run() {
                alto = contenedor.getHeight();
                ancho = contenedor.getWidth();
            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!stop_game){
                    jugar(MainActivity.this);
                    new Handler().postDelayed(this,aleatorio(MIN_NEXT_TIME,MAX_NEXT_TIME));
                }
            }
        }, aleatorio(MIN_NEXT_TIME,MAX_NEXT_TIME));
    }



    private void jugar(Context c) {
        //Creamos la imagen
        ImageView mv = new ImageView(c);
        //Le ponemos la imagen del marciano
        mv.setImageResource(R.drawable.ic_marciano);
        mv.setColorFilter(getResources().getColor(colores[aleatorio(0,colores.length-1)]));
        //Centramos la imagen dentro
        mv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        int timeToLive = aleatorio(MIN_TIME_DISPLAY,MAX_TIME_DISPLAY);

        //Para que lo destruya solo
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mv!=null && !stop_game){
                    contenedor.removeView(mv);
                    puntos--;
                    tvPuntos.setText(""+puntos);
                }

            }
        }, timeToLive);


        //Calculamos DP
        final int pixels = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                aleatorio(MIN_SIZE,MAX_SIZE), getResources().getDisplayMetrics());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(pixels,pixels);
        params.leftMargin = aleatorio(0,ancho-pixels);
        params.topMargin = aleatorio(0,alto-pixels);

        contenedor.addView(mv,params);

        mv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacksAndMessages(null);

                puntos++;
                tvPuntos.setText(""+puntos);
                mostrarAnimacion(view.getX(),view.getY(),pixels);
                //Eliminamos el elemento del Layout
                contenedor.removeView(view);
                checkWin();
            }
        });


    }

    private void checkWin() {

        if(puntos>=VIDAS){
            stop_game = true;
            winner.setVisibility(View.VISIBLE);
            winner.playAnimation();

        }
    }

    private void mostrarAnimacion(float x, float y, int pixels) {

        LottieAnimationView explode = new LottieAnimationView(MainActivity.this);
        explode.setAnimation("explode.json");

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(pixels*2,pixels*2);
        params.leftMargin = (int)(x-(pixels/2));
        params.topMargin = (int)(y-(pixels/2));

        contenedor.addView(explode,params);

        explode.setVisibility(View.VISIBLE);

        explode.playAnimation();

        explode.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                explode.setVisibility(View.GONE);
            }
        });

    }

    private int aleatorio(int min, int max){
        return (int) Math.floor(Math.random()*(max-min+1)+min);
    }
}

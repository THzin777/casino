package com.ulbra.roleta;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageView roleta, flecha;
    private Button btnGirar, btnLogout;
    private TextView resultado, saudacaoTextView;
    private GridLayout gridNumeros;
    private int numeroEscolhido = -1;
    private Random random = new Random();
    private float ultimoAngulo = 0f;


    private final int[] ROULETTE_NUMBERS_MAP = {
            99, 27, 10, 25, 29, 12, 8, 19, 31, 18, 6, 21, 33, 16, 4, 23, 35, 14, 2,
            0, 28, 9, 26, 30, 11, 7, 20, 32, 17, 5, 22, 34, 15, 3, 24, 36, 13, 1
    };

    private final int NUMBER_OF_SLOTS = ROULETTE_NUMBERS_MAP.length;
    private final float ANGLE_PER_SLOT = 360f / NUMBER_OF_SLOTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        roleta = findViewById(R.id.roleta);
        flecha = findViewById(R.id.flecha);
        btnGirar = findViewById(R.id.btnGirar);
        btnLogout = findViewById(R.id.btnLogout);
        resultado = findViewById(R.id.resultado);
        gridNumeros = findViewById(R.id.gridNumeros);
        saudacaoTextView = findViewById(R.id.saudacao);

        criarQuadrados();

        btnGirar.setOnClickListener(v -> girarRoleta());
        btnLogout.setOnClickListener(v -> finish());
    }

    private void criarQuadrados() {
        gridNumeros.removeAllViews();


        for (int i = 0; i <= 36; i++) {
            final String numStr = String.valueOf(i);
            Button btn = new Button(this);
            btn.setText(numStr);

            int bgColor = 0xFFE0E0E0;
            int textColor = 0xFF000000;

            btn.setBackgroundColor(bgColor);
            btn.setTextColor(textColor);
            btn.setTextSize(12);

            btn.setOnClickListener(v -> {
                numeroEscolhido = Integer.parseInt(numStr);
                Toast.makeText(this, "Número selecionado: " + numeroEscolhido, Toast.LENGTH_SHORT).show();
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
            btn.setLayoutParams(params);

            gridNumeros.addView(btn);
        }


        Button btn00 = new Button(this);
        btn00.setText("00");
        btn00.setBackgroundColor(0xFFE0E0E0);
        btn00.setTextColor(0xFF000000);
        btn00.setTextSize(12);

        btn00.setOnClickListener(v -> {
            numeroEscolhido = 99;
            Toast.makeText(this, "Número selecionado: 00", Toast.LENGTH_SHORT).show();
        });

        GridLayout.LayoutParams params00 = new GridLayout.LayoutParams();
        params00.width = 0;
        params00.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params00.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
        btn00.setLayoutParams(params00);

        gridNumeros.addView(btn00);
    }

    private void girarRoleta() {
        if (numeroEscolhido == -1) {
            Toast.makeText(this, "Selecione um número antes!", Toast.LENGTH_SHORT).show();
            return;
        }

        btnGirar.setEnabled(false);

        if (saudacaoTextView != null) {
            saudacaoTextView.setVisibility(View.GONE);
        }

        int targetIndex = random.nextInt(NUMBER_OF_SLOTS);
        float anguloCentroSlot = targetIndex * ANGLE_PER_SLOT + (ANGLE_PER_SLOT / 2f);
        float anguloDeAlinhamento = (360f - anguloCentroSlot) % 360f;
        float offsetAleatorio = (random.nextFloat() - 0.5f) * (ANGLE_PER_SLOT / 2f);

        int voltas = 5 + random.nextInt(4);
        float destino = (voltas * 360f) + anguloDeAlinhamento + offsetAleatorio;

        float anguloInicial = ultimoAngulo % 360f;

        if (anguloInicial > destino % 360f) {
            anguloInicial = destino - (destino % 360f) + anguloInicial;
        }

        if (destino < anguloInicial) {
            anguloInicial = ultimoAngulo;
            destino = anguloInicial + (voltas * 360f) + anguloDeAlinhamento + offsetAleatorio;
        }

        final float finalDestino = destino;

        RotateAnimation rotate = new RotateAnimation(
                anguloInicial,
                finalDestino,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setDuration(4500);
        rotate.setFillAfter(true);
        rotate.setInterpolator(new DecelerateInterpolator(1.5f));

        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                resultado.setText("Girando...");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ultimoAngulo = finalDestino;
                float anguloParadaNormalizado = ultimoAngulo % 360f;
                float anguloDoSlotAlinhado = (360f - anguloParadaNormalizado) % 360f;
                int indexFinal = (int) (anguloDoSlotAlinhado / ANGLE_PER_SLOT);

                int numeroSorteadoInt = ROULETTE_NUMBERS_MAP[indexFinal];

                String numeroSorteadoStr;
                if (numeroSorteadoInt == 99) {
                    numeroSorteadoStr = "00";
                } else {
                    numeroSorteadoStr = String.valueOf(numeroSorteadoInt);
                }

                String msg;
                boolean ganhou = (numeroSorteadoInt == numeroEscolhido);

                if (ganhou) {
                    msg = "🎉 VENCEU! Número sorteado: " + numeroSorteadoStr;
                    resultado.setTextColor(0xFF007F00); // Verde
                } else {
                    if (numeroSorteadoInt == 0 || numeroSorteadoInt == 99) {
                        msg = "❌ Perdeu! Sorteado: " + numeroSorteadoStr + " (Casa)";
                    } else {
                        msg = "❌ Perdeu! Número sorteado: " + numeroSorteadoStr;
                    }
                    resultado.setTextColor(0xFFB00000); // Vermelho
                }

                resultado.setText(msg);
                btnGirar.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        roleta.startAnimation(rotate);
    }
}

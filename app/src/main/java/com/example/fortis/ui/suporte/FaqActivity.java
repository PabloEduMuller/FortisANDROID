package com.example.fortis.ui.suporte;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fortis.R;
import com.example.fortis.ui.home.HomeActivity;
import com.example.fortis.ui.perfil.PerfilActivity;
import com.example.fortis.ui.treino.MeusTreinosActivity;
import com.example.fortis.ui.treino.TreinoDoDiaRouterActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FaqActivity extends AppCompatActivity {

    private ImageButton ibVoltar;
    private ImageButton ibConfiguracao;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        associarViews();
        configurarBotoesHeader();
        configurarBottomNavigation();
    }

    private void associarViews() {
        // 1. O ID do botão "Sair/Voltar" foi corrigido no XML para ibVoltar
        ibVoltar = findViewById(R.id.ibVoltar);
        ibConfiguracao = findViewById(R.id.ibConfiguracao);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void configurarBotoesHeader() {
        // 2. Ação de "Voltar" (Cancelar)
        ibVoltar.setOnClickListener(v -> {
            finish(); // Apenas fecha a tela atual
        });

        // 3. Ação do botão de "Config" (apenas volta também)
        ibConfiguracao.setOnClickListener(v -> {
            finish(); // Apenas fecha a tela atual
        });
    }

    private void configurarBottomNavigation() {
        // Define o item "Ajuda" como selecionado
        bottomNavigationView.setSelectedItemId(R.id.nav_chat_suporte);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_ficha_treinos) {
                startActivity(new Intent(this, MeusTreinosActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_perfil) {
                startActivity(new Intent(this, PerfilActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.navigation_treino_do_dia) {
                startActivity(new Intent(this, TreinoDoDiaRouterActivity.class));
                // Não finaliza, pois o Router é rápido
                return true;
            } else if (itemId == R.id.nav_chat_suporte) {
                // Já estamos na tela de Ajuda (ou sub-tela), então não faz nada
                return true;
            }
            return false;
        });
    }
}
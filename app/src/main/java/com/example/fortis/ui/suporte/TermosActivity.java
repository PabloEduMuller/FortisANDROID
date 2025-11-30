package com.example.fortis.ui.suporte;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton; // <-- 1. IMPORTAR ImageButton

import androidx.appcompat.app.AppCompatActivity;
// (Remover importação da Toolbar)

import com.example.fortis.R;
// --- 2. IMPORTAR AS DEPENDÊNCIAS DE NAVEGAÇÃO ---
import com.example.fortis.ui.home.HomeActivity;
import com.example.fortis.ui.perfil.PerfilActivity;
import com.example.fortis.ui.treino.MeusTreinosActivity;
import com.example.fortis.ui.treino.TreinoDoDiaRouterActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TermosActivity extends AppCompatActivity {

    // --- 3. DECLARAR OS BOTÕES DO CABEÇALHO E RODAPÉ ---
    private ImageButton ibVoltar;
    private ImageButton ibConfiguracao;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termos); // (Layout da Etapa 41.1)

        associarViews();
        configurarBotoesHeader();
        configurarBottomNavigation();
    }

    private void associarViews() {
        // --- 4. ASSOCIAR OS IDS CORRETOS (do layout 41.1) ---
        ibVoltar = findViewById(R.id.ibVoltar);
        ibConfiguracao = findViewById(R.id.ibConfiguracao);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void configurarBotoesHeader() {
        // --- 5. AÇÃO DE "VOLTAR" (CANCELAR) ---
        ibVoltar.setOnClickListener(v -> {
            finish(); // Apenas fecha a tela atual
        });

        ibConfiguracao.setOnClickListener(v -> {
            finish(); // Apenas fecha a tela atual
        });
    }

    private void configurarBottomNavigation() {
        // --- 6. LÓGICA DO MENU INFERIOR ---
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
                return true;
            } else if (itemId == R.id.nav_chat_suporte) {
                return true; // Já estamos aqui
            }
            return false;
        });
    }
}
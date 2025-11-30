package com.example.fortis.ui.suporte;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri; // (Import do WhatsApp, mantido)
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fortis.R;
import com.example.fortis.data.SessionManager;
import com.example.fortis.ui.configuracoes.ConfiguracoesActivity;
import com.example.fortis.ui.home.HomeActivity;
import com.example.fortis.ui.login.LoginActivity;
import com.example.fortis.ui.perfil.PerfilActivity;
import com.example.fortis.ui.treino.MeusTreinosActivity;
import com.example.fortis.ui.treino.TreinoDoDiaRouterActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// --- 1. IMPORTAR A NOVA ACTIVITY ---
import com.example.fortis.ui.suporte.FaqActivity;
import com.example.fortis.ui.suporte.TermosActivity; // <-- ADICIONADO

public class AjudaActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    private ImageButton ibSairSistema, ibConfiguracao;
    private BottomNavigationView bottomNavigationView;
    private TextView tvFaq, tvSuporte, tvTermos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajuda);

        sessionManager = new SessionManager(this);

        associarViews();
        configurarBotoes();
        configurarBottomNavigation();
    }

    private void associarViews() {
        ibSairSistema = findViewById(R.id.ibSairSistema);
        ibConfiguracao = findViewById(R.id.ibConfiguracao);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        tvFaq = findViewById(R.id.tvFaq);
        tvSuporte = findViewById(R.id.tvSuporte);
        tvTermos = findViewById(R.id.tvTermos);
    }

    private void configurarBotoes() {
        ibConfiguracao.setOnClickListener(v -> {
            Intent intent = new Intent(this, ConfiguracoesActivity.class);
            startActivity(intent);
        });

        ibSairSistema.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            // Limpa a pilha para não acumular atividades
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Abre a tela de FAQ
        tvFaq.setOnClickListener(v -> {
            Intent intent = new Intent(this, FaqActivity.class);
            startActivity(intent);
        });

        // Abre o WhatsApp
        tvSuporte.setOnClickListener(v -> {
            abrirWhatsAppSuporte();
        });

        // --- 2. LÓGICA ATUALIZADA (Removemos o Toast) ---
        tvTermos.setOnClickListener(v -> {
            Intent intent = new Intent(this, TermosActivity.class);
            startActivity(intent);
        });
    }

    private void abrirWhatsAppSuporte() {
        // (Este é o método do passo anterior, mantido)
        String numeroWhatsApp = "5545999998888"; // Substitua pelo seu número
        String url = "https://wa.me/" + numeroWhatsApp;

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Não foi possível abrir o WhatsApp.", Toast.LENGTH_SHORT).show();
        }
    }

    // ... (configurarBottomNavigation e mostrarDialogoLogout permanecem iguais) ...
    private void configurarBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_chat_suporte);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_ficha_treinos) {
                Intent intent = new Intent(this, MeusTreinosActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_perfil) {
                Intent intent = new Intent(this, PerfilActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.navigation_treino_do_dia) {
                Intent intent = new Intent(this, TreinoDoDiaRouterActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_chat_suporte) {
                return true;
            }
            return false;
        });
    }

    private void mostrarDialogoLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Sair da Conta")
                .setMessage("Tem certeza que deseja se desconectar?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    sessionManager.clearSession();
                    Intent intent = new Intent(AjudaActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Não", null)
                .show();
    }
}
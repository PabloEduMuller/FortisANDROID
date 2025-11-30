package com.example.fortis.ui.configuracoes;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fortis.R;
import com.example.fortis.data.SessionManager;
import com.example.fortis.ui.login.LoginActivity;
import com.example.fortis.ui.perfil.EditarPerfilActivity;
import com.example.fortis.ui.perfil.MudarSenhaActivity;
import com.example.fortis.ui.perfil.PerfilActivity; // <-- 1. IMPORTAR PerfilActivity
import com.google.android.material.switchmaterial.SwitchMaterial;

public class ConfiguracoesActivity extends AppCompatActivity {

    private static final int EDITAR_PERFIL_REQUEST = 102;

    private TextView tvEditarPerfil;
    private TextView tvMudarSenha;
    private TextView tvGerenciarPlano;
    private SwitchMaterial switchNotificacoes;
    private Button btnSairDaConta;
    private ImageButton ibVoltar;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        sessionManager = new SessionManager(this);
        setResult(Activity.RESULT_CANCELED);
        associarViews();
        configurarBotoes();
    }

    private void associarViews() {
        ibVoltar = findViewById(R.id.ibSairSistema);
        tvEditarPerfil = findViewById(R.id.tvEditarPerfil);
        tvMudarSenha = findViewById(R.id.tvMudarSenha);
        tvGerenciarPlano = findViewById(R.id.tvGerenciarPlano);
        switchNotificacoes = findViewById(R.id.switchNotificacoes);
        btnSairDaConta = findViewById(R.id.btnSairDaConta);
    }

    private void configurarBotoes() {
        ibVoltar.setOnClickListener(v -> finish());

        tvEditarPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditarPerfilActivity.class);
            startActivityForResult(intent, EDITAR_PERFIL_REQUEST);
        });

        tvMudarSenha.setOnClickListener(v -> {
            // (Esta lógica foi implementada no passo anterior)
            Intent intent = new Intent(this, MudarSenhaActivity.class);
            startActivity(intent);
        });

        // --- 2. AÇÃO ATUALIZADA ---
        tvGerenciarPlano.setOnClickListener(v -> {
            // Abre a tela "Meu Perfil" onde os dados do plano são exibidos
            Intent intent = new Intent(this, PerfilActivity.class);
            startActivity(intent);
        });

        switchNotificacoes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(this, "Notificações Ativadas", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notificações Desativadas", Toast.LENGTH_SHORT).show();
            }
        });

        btnSairDaConta.setOnClickListener(v -> {
            mostrarDialogoLogout();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDITAR_PERFIL_REQUEST && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK);
            Toast.makeText(this, "Dados do perfil atualizados", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Sair da Conta")
                .setMessage("Tem certeza que deseja se desconectar?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    sessionManager.clearSession();
                    Intent intent = new Intent(ConfiguracoesActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Não", null)
                .show();
    }
}
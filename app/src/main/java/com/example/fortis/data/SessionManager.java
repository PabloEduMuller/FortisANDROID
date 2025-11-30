package com.example.fortis.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "FortisAppPref";
    private static final String KEY_AUTH_TOKEN = "authToken";
    private static final String KEY_ALUNO_ID = "alunoId";

    private SharedPreferences prefs;

    public SessionManager(Context context) {
        // Obter o SharedPreferences privado da aplicação
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Salva o token de autenticação JWT nas SharedPreferences.
     */
    public void saveAuthToken(String token) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.apply();
    }

    /**
     * Recupera o token de autenticação JWT.
     * Retorna null se o token não for encontrado.
     */
    public String getAuthToken() {
        return prefs.getString(KEY_AUTH_TOKEN, null);
    }

    /**
     * Salva o ID do aluno logado.
     */
    public void saveAlunoId(long id) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_ALUNO_ID, id);
        editor.apply();
    }

    /**
     * Recupera o ID do aluno logado.
     * Retorna -1 se não for encontrado.
     */
    public long getAlunoId() {
        return prefs.getLong(KEY_ALUNO_ID, -1);
    }

    /**
     * --- NOVO MÉTODO ADICIONADO ---
     * Limpa todos os dados da sessão (Token, ID do Aluno, etc.).
     * Usado para a funcionalidade de Logout.
     */
    public void clearSession() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); // Remove todas as chaves
        editor.apply();
    }
}
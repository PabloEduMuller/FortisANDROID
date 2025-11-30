package com.example.fortis.ui.progresso;

import android.content.Context;
import android.widget.TextView;

import com.example.fortis.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.List;

public class CustomMarkerView extends MarkerView {

    private final TextView tvContent;
    private final List<String> labels;

    public CustomMarkerView(Context context, int layoutResource, List<String> labels) {
        super(context, layoutResource);
        tvContent = findViewById(R.id.tvContent);
        this.labels = labels;
    }

    // Este método roda toda vez que você toca/arrasta no gráfico
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        int index = (int) e.getX(); // O eixo X é o índice (0, 1, 2...)

        String texto;
        if (index >= 0 && index < labels.size()) {
            // Mostra: "29/11 - 5000 kg"
            texto = labels.get(index) + "\n" + String.format("%.0f kg", e.getY());
        } else {
            texto = String.format("%.0f kg", e.getY());
        }

        tvContent.setText(texto);
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        // Centraliza a etiqueta acima do ponto tocado
        return new MPPointF(-(getWidth() / 2f), -getHeight());
    }
}
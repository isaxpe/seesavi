package com.gallegos.seesavi;

import android.graphics.Color;

public class DiaCalendario {
    public String numeroDia;
    public String fechaCompleta; // Formato YYYY-MM-DD
    public int colorFondo;

    public DiaCalendario(String numeroDia, String fechaCompleta) {
        this.numeroDia = numeroDia;
        this.fechaCompleta = fechaCompleta;
        this.colorFondo = Color.TRANSPARENT;
    }
}
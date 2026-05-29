package com.gallegos.seesavi;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.google.android.material.card.MaterialCardView; // Importación necesaria para las tarjetas

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // AHORA ENLAZAMOS LAS TARJETAS (MaterialCardView) CON SUS NUEVOS IDs
        MaterialCardView cardAdminMenus = findViewById(R.id.cardAdminMenus);
        MaterialCardView cardAdminAlumnos = findViewById(R.id.cardAdminAlumnos);
        MaterialCardView cardAdminAsistencias = findViewById(R.id.cardAdminAsistencias);
        Button btnAdminSalir = findViewById(R.id.btnAdminSalir); // Este sigue siendo un botón normal

        cardAdminMenus.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminMenusActivity.class));
        });

        cardAdminAlumnos.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminAlumnosActivity.class));
        });

        cardAdminAsistencias.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminAsistenciasActivity.class));
        });

        btnAdminSalir.setOnClickListener(v -> finish());
    }
}
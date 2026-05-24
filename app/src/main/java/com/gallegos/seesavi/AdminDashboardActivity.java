package com.gallegos.seesavi;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        Button btnAdminMenus = findViewById(R.id.btnAdminMenus);
        Button btnAdminAlumnos = findViewById(R.id.btnAdminAlumnos);
        Button btnAdminAsistencias = findViewById(R.id.btnAdminAsistencias);
        Button btnAdminSalir = findViewById(R.id.btnAdminSalir);

        btnAdminMenus.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminMenusActivity.class));
        });

        btnAdminAlumnos.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminAlumnosActivity.class));
        });

        btnAdminAsistencias.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminAsistenciasActivity.class));
        });

        btnAdminSalir.setOnClickListener(v -> finish());
    }
}
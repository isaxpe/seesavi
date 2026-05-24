package com.gallegos.seesavi;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AdminMenusActivity extends AppCompatActivity {

    private EditText inputFecha, inputPlato, inputSopa, inputBebida;
    private Switch switchHabilitar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menus);

        db = FirebaseFirestore.getInstance();
        inputFecha = findViewById(R.id.inputFechaMenu);
        inputPlato = findViewById(R.id.inputPlato);
        inputSopa = findViewById(R.id.inputSopa);
        inputBebida = findViewById(R.id.inputBebida);
        switchHabilitar = findViewById(R.id.switchHabilitarDia);
        Button btnGuardar = findViewById(R.id.btnGuardarMenu);

        btnGuardar.setOnClickListener(v -> guardarMenuEnNube());
    }

    private void guardarMenuEnNube() {
        String fecha = inputFecha.getText().toString().trim();
        if (fecha.isEmpty()) {
            Toast.makeText(this, "La fecha es obligatoria", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> menu = new HashMap<>();
        menu.put("plato_principal", inputPlato.getText().toString().trim());
        menu.put("sopa", inputSopa.getText().toString().trim());
        menu.put("bebida", inputBebida.getText().toString().trim());
        menu.put("disponible", switchHabilitar.isChecked()); // Falso cerrará el día

        db.collection("menus_diarios").document(fecha).set(menu)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Menú publicado con éxito", Toast.LENGTH_LONG).show();
                    finish(); // Regresa al dashboard
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al publicar", Toast.LENGTH_SHORT).show());
    }
}
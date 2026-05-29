package com.gallegos.seesavi;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentSnapshot;
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

        Button btnBuscar = findViewById(R.id.btnBuscarMenu);
        Button btnGuardar = findViewById(R.id.btnGuardarMenu);

        btnBuscar.setOnClickListener(v -> buscarMenuEnNube());
        btnGuardar.setOnClickListener(v -> guardarMenuEnNube());
    }

    private void buscarMenuEnNube() {
        String fechaBuscada = inputFecha.getText().toString().trim();
        if (fechaBuscada.isEmpty()) {
            Toast.makeText(this, "Escribe una fecha para buscar", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("menus_diarios").document(fechaBuscada).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            // Si el menú existe, llenamos los campos
                            inputPlato.setText(doc.getString("plato_principal"));
                            inputSopa.setText(doc.getString("sopa"));
                            inputBebida.setText(doc.getString("bebida"));

                            Boolean disponible = doc.getBoolean("disponible");
                            switchHabilitar.setChecked(disponible != null ? disponible : true);

                            Toast.makeText(this, "Menú cargado. Listo para editar.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Si no existe, limpiamos los campos para crear uno nuevo
                            inputPlato.setText("");
                            inputSopa.setText("");
                            inputBebida.setText("");
                            switchHabilitar.setChecked(true);
                            Toast.makeText(this, "No hay menú registrado. Crea uno nuevo.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
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

        // El método .set() actualiza automáticamente si el documento ya existe
        db.collection("menus_diarios").document(fecha).set(menu)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Menú guardado/actualizado con éxito", Toast.LENGTH_LONG).show();
                    finish(); // Regresa al dashboard
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show());
    }
}
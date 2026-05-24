package com.gallegos.seesavi;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class AdminAsistenciasActivity extends AppCompatActivity {

    private EditText inputFecha;
    private TextView txtTotal;
    private ListView listView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_asistencias);

        db = FirebaseFirestore.getInstance();
        inputFecha = findViewById(R.id.inputFechaReporte);
        txtTotal = findViewById(R.id.txtTotalAsistencias);
        listView = findViewById(R.id.listaAsistencias);
        Button btnBuscar = findViewById(R.id.btnBuscarReporte);

        btnBuscar.setOnClickListener(v -> buscarAsistencias());
    }

    private void buscarAsistencias() {
        String fechaBuscada = inputFecha.getText().toString().trim();
        if (fechaBuscada.isEmpty()) {
            Toast.makeText(this, "Escribe una fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        txtTotal.setText("Buscando...");

        // Buscar todos los documentos en "asistencias" que tengan esa fecha
        db.collection("asistencias")
                .whereEqualTo("fecha", fechaBuscada)
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    List<String> listaAlumnos = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshots) {
                        String control = doc.getString("num_control");
                        listaAlumnos.add("Control: " + control + " - Confirmado ✔️");
                    }

                    txtTotal.setText("Total confirmados: " + listaAlumnos.size());

                    // Llenar la lista visual
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this, android.R.layout.simple_list_item_1, listaAlumnos);
                    listView.setAdapter(adapter);

                    if (listaAlumnos.isEmpty()) {
                        Toast.makeText(this, "Nadie ha confirmado para este día", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show());
    }
}
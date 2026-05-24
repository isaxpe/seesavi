package com.gallegos.seesavi;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AdminAlumnosActivity extends AppCompatActivity {

    private EditText inputNumControl, inputNombre, inputPassword, inputEliminarNumControl;
    private Button btnAnexar, btnQuitar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_alumnos);

        db = FirebaseFirestore.getInstance();

        // Vincular vistas de registro
        inputNumControl = findViewById(R.id.inputAdminNumControl);
        inputNombre = findViewById(R.id.inputAdminNombre);
        inputPassword = findViewById(R.id.inputAdminPassword);
        btnAnexar = findViewById(R.id.btnAnexarAlumno);

        // Vincular vistas de eliminación
        inputEliminarNumControl = findViewById(R.id.inputAdminEliminarNumControl);
        btnQuitar = findViewById(R.id.btnQuitarAlumno);

        // Configurar Eventos
        btnAnexar.setOnClickListener(v -> anexarEstudiante());
        btnQuitar.setOnClickListener(v -> quitarEstudiante());
    }

    private void anexarEstudiante() {
        String numControl = inputNumControl.getText().toString().trim();
        String nombre = inputNombre.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (numControl.isEmpty() || nombre.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos de registro", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear mapa con los datos del alumno (idéntico al esquema del Login)
        Map<String, Object> nuevoAlumno = new HashMap<>();
        nuevoAlumno.put("nombre_completo", nombre);
        nuevoAlumno.put("contrasena", password);

        // Insertar en la colección "usuarios" usando el número de control como ID único
        db.collection("usuarios").document(numControl).set(nuevoAlumno)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Estudiante anexado con éxito.", Toast.LENGTH_LONG).show();
                    // Limpiar campos
                    inputNumControl.setText("");
                    inputNombre.setText("");
                    inputPassword.setText("");
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar en la nube", Toast.LENGTH_SHORT).show());
    }

    private void quitarEstudiante() {
        String numControlAEliminar = inputEliminarNumControl.getText().toString().trim();

        if (numControlAEliminar.isEmpty()) {
            Toast.makeText(this, "Escribe un número de control para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Eliminar el documento directamente en Firestore
        db.collection("usuarios").document(numControlAEliminar).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Estudiante eliminado del sistema correctamente.", Toast.LENGTH_LONG).show();
                    inputEliminarNumControl.setText("");
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al eliminar el estudiante", Toast.LENGTH_SHORT).show());
    }
}
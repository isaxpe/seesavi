package com.gallegos.seesavi;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private EditText inputNumeroControl;
    private EditText inputPassword;
    private Button btnIniciarSesion;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Conectar código con la vista
        inputNumeroControl = findViewById(R.id.inputNumeroControl);
        inputPassword = findViewById(R.id.inputPassword);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        db = FirebaseFirestore.getInstance();

        btnIniciarSesion.setOnClickListener(v -> validarCredenciales());
    }

    private void validarCredenciales() {
        String numControl = inputNumeroControl.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        // 1. Validar que no haya campos vacíos
        if (numControl.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. ACCESO DIRECTO PARA EL ADMINISTRADOR (Con usuario numérico)
        if (numControl.equals("9999") && password.equals("admin123")) {
            Toast.makeText(this, "¡Bienvenido, Administrador!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
            startActivity(intent);
            finish(); // Cierra el login para que no se pueda regresar con el botón atrás
            return; // Detiene la ejecución aquí para que no busque en la colección de alumnos
        }

        // 3. Flujo normal para estudiantes
        btnIniciarSesion.setText("Verificando...");
        btnIniciarSesion.setEnabled(false);

        db.collection("usuarios").document(numControl).get()
                .addOnCompleteListener(task -> {
                    btnIniciarSesion.setText("INICIAR SESIÓN");
                    btnIniciarSesion.setEnabled(true);

                    if (task.isSuccessful()) {
                        DocumentSnapshot documento = task.getResult();

                        if (documento.exists()) {
                            String passFirebase = documento.getString("contrasena");

                            if (passFirebase != null && passFirebase.equals(password)) {
                                String nombre = documento.getString("nombre_completo");
                                Toast.makeText(this, "¡Bienvenido, " + nombre + "!", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(MainActivity.this, CalendarioActivity.class);
                                // AQUÍ ENVIAMOS EL NÚMERO DE CONTROL REAL A LA SIGUIENTE PANTALLA
                                intent.putExtra("NUM_CONTROL", numControl);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Estudiante no encontrado en el sistema", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Error conectando al servidor", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
package com.gallegos.seesavi;

import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MenuDetalleActivity extends AppCompatActivity {

    private TextView txtFechaMenu, txtPlatoPrincipal, txtSopa, txtBebida;
    private Button btnConfirmarAsistencia;
    private FirebaseFirestore db;
    private String fechaRecibida = "";
    private String numControlUsuario = ""; // Listo para recibir al alumno real

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detalle);

        // Nombres correctos que coinciden con tu XML
        txtFechaMenu = findViewById(R.id.txtFechaMenu);
        txtPlatoPrincipal = findViewById(R.id.txtPlatoPrincipal);
        txtSopa = findViewById(R.id.txtSopa);
        txtBebida = findViewById(R.id.txtBebida);
        btnConfirmarAsistencia = findViewById(R.id.btnConfirmarAsistencia);
        db = FirebaseFirestore.getInstance();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fechaRecibida = extras.getString("FECHA");
            numControlUsuario = extras.getString("NUM_CONTROL"); // Recibe al usuario real
            String plato = extras.getString("PLATO");
            String sopa = extras.getString("SOPA");
            String bebida = extras.getString("BEBIDA");

            txtFechaMenu.setText("Menú del " + fechaRecibida);
            txtPlatoPrincipal.setText("- " + plato);
            txtSopa.setText("- " + sopa);
            txtBebida.setText("- " + bebida);

            // Verificar estatus de asistencia y tiempo
            verificarEstatusAsistencia();
        }
    }

    private void verificarEstatusAsistencia() {
        if (numControlUsuario == null || numControlUsuario.isEmpty()) return;

        // 1. Calcular si la fecha ya es del pasado
        Calendar hoy = Calendar.getInstance();
        hoy.set(Calendar.HOUR_OF_DAY, 0); hoy.set(Calendar.MINUTE, 0); hoy.set(Calendar.SECOND, 0); hoy.set(Calendar.MILLISECOND, 0);

        String[] partes = fechaRecibida.split("-");
        Calendar calFecha = Calendar.getInstance();
        calFecha.set(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]) - 1, Integer.parseInt(partes[2]), 0, 0, 0);
        calFecha.set(Calendar.MILLISECOND, 0);

        boolean esFechaPasada = calFecha.before(hoy);

        // 2. Consultar a Firebase usando el NUM_CONTROL real
        String idDocumento = numControlUsuario + "_" + fechaRecibida;

        db.collection("asistencias").document(idDocumento).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();

                        if (doc.exists()) {
                            // El alumno SÍ había confirmado asistencia para este día
                            if (esFechaPasada) {
                                btnConfirmarAsistencia.setBackgroundColor(Color.parseColor("#FF9800"));
                                btnConfirmarAsistencia.setText("SÍ ASISTISTE A ESTE MENÚ (NARANJA)");
                                btnConfirmarAsistencia.setEnabled(false);
                            } else {
                                btnConfirmarAsistencia.setBackgroundColor(Color.parseColor("#2196F3"));
                                btnConfirmarAsistencia.setText("LUGAR APARTADO (AZUL)");
                                btnConfirmarAsistencia.setEnabled(true);
                            }
                        } else {
                            // El alumno NO tiene registro de asistencia
                            if (esFechaPasada) {
                                btnConfirmarAsistencia.setBackgroundColor(Color.parseColor("#9E9E9E"));
                                btnConfirmarAsistencia.setText("NO CONFIRMASTE A TIEMPO (GRIS)");
                                btnConfirmarAsistencia.setEnabled(false);
                            } else {
                                btnConfirmarAsistencia.setBackgroundColor(Color.parseColor("#4CAF50"));
                                btnConfirmarAsistencia.setText("CONFIRMAR ASISTENCIA (VERDE)");
                                btnConfirmarAsistencia.setEnabled(true);

                                // Configurar la acción de clic solo si está disponible para confirmarse
                                btnConfirmarAsistencia.setOnClickListener(v -> realizarConfirmacion(idDocumento));
                            }
                        }
                    }
                });
    }

    private void realizarConfirmacion(String idDocumento) {
        Map<String, Object> asistencia = new HashMap<>();
        asistencia.put("usuario", numControlUsuario); // Guardamos al alumno real
        asistencia.put("num_control", numControlUsuario);
        asistencia.put("fecha", fechaRecibida);
        asistencia.put("asistio", true);

        btnConfirmarAsistencia.setEnabled(false);
        btnConfirmarAsistencia.setText("Procesando...");

        db.collection("asistencias").document(idDocumento).set(asistencia)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "¡Asistencia Confirmada!", Toast.LENGTH_SHORT).show();
                    btnConfirmarAsistencia.setBackgroundColor(Color.parseColor("#2196F3"));
                    btnConfirmarAsistencia.setText("LUGAR APARTADO (AZUL)");
                    btnConfirmarAsistencia.setEnabled(true);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al confirmar", Toast.LENGTH_SHORT).show();
                    btnConfirmarAsistencia.setEnabled(true);
                    btnConfirmarAsistencia.setText("CONFIRMAR ASISTENCIA (VERDE)");
                });
    }
}
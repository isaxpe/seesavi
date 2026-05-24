package com.gallegos.seesavi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarioActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CalendarioAdapter adapter;
    private List<DiaCalendario> listaDias;
    private TextView txtMesAnio;
    private Button btnMesAnterior, btnMesSiguiente;

    private FirebaseFirestore db;
    private Calendar mesActualVisualizado;
    private String numControlUsuario = ""; // Se recibe dinámicamente desde el Login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        recyclerView = findViewById(R.id.recyclerCalendario);
        txtMesAnio = findViewById(R.id.txtMesAnio);
        btnMesAnterior = findViewById(R.id.btnMesAnterior);
        btnMesSiguiente = findViewById(R.id.btnMesSiguiente);

        db = FirebaseFirestore.getInstance();
        listaDias = new ArrayList<>();
        mesActualVisualizado = Calendar.getInstance();

        // Recibir el número de control real desde el Login
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            numControlUsuario = extras.getString("NUM_CONTROL");
        }

        // Configurar la cuadrícula de 7 columnas para los días de la semana
        recyclerView.setLayoutManager(new GridLayoutManager(this, 7));
        adapter = new CalendarioAdapter(listaDias, fecha -> viajarDetalleMenu(fecha));
        recyclerView.setAdapter(adapter);

        // Controles de navegación del mes
        btnMesAnterior.setOnClickListener(v -> {
            mesActualVisualizado.add(Calendar.MONTH, -1);
            construirMes();
        });

        btnMesSiguiente.setOnClickListener(v -> {
            mesActualVisualizado.add(Calendar.MONTH, 1);
            construirMes();
        });

        // Construir el mes inicial
        construirMes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refrescar los colores desde Firebase cada vez que regresamos a esta pantalla
        if (listaDias != null && !listaDias.isEmpty()) {
            consultarAsistenciasEnNube();
        }
    }

    private void construirMes() {
        listaDias.clear();

        int anio = mesActualVisualizado.get(Calendar.YEAR);
        int mes = mesActualVisualizado.get(Calendar.MONTH);

        // Actualizar el título de la pantalla
        String[] nombresMeses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        txtMesAnio.setText(nombresMeses[mes] + " " + anio);

        Calendar calConstructor = Calendar.getInstance();
        calConstructor.set(anio, mes, 1);
        int diasEnElMes = calConstructor.getActualMaximum(Calendar.DAY_OF_MONTH);
        int diaDeSemanaDelPrimero = calConstructor.get(Calendar.DAY_OF_WEEK); // 1=Dom, 2=Lun...

        // Rellenar espacios vacíos al principio si el mes no empieza en Domingo
        for (int i = 1; i < diaDeSemanaDelPrimero; i++) {
            listaDias.add(new DiaCalendario("", ""));
        }

        Calendar hoy = Calendar.getInstance();
        hoy.set(Calendar.HOUR_OF_DAY, 0); hoy.set(Calendar.MINUTE, 0); hoy.set(Calendar.SECOND, 0); hoy.set(Calendar.MILLISECOND, 0);

        // Generar los objetos de cada día con sus colores base temporales
        for (int i = 1; i <= diasEnElMes; i++) {
            calConstructor.set(anio, mes, i);
            String fechaParaFirebase = String.format(Locale.getDefault(), "%04d-%02d-%02d", anio, mes + 1, i);
            DiaCalendario diaNuevo = new DiaCalendario(String.valueOf(i), fechaParaFirebase);

            boolean esFinSemana = (calConstructor.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calConstructor.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);

            Calendar comparador = (Calendar) calConstructor.clone();
            comparador.set(Calendar.HOUR_OF_DAY, 0); comparador.set(Calendar.MINUTE, 0); comparador.set(Calendar.SECOND, 0); comparador.set(Calendar.MILLISECOND, 0);

            if (esFinSemana) {
                diaNuevo.colorFondo = Color.parseColor("#F44336"); // Rojo: Fin de semana inactivo
            } else if (comparador.before(hoy)) {
                diaNuevo.colorFondo = Color.parseColor("#9E9E9E"); // Gris: Día escolar pasado
            } else {
                diaNuevo.colorFondo = Color.parseColor("#4CAF50"); // Verde: Día escolar disponible/futuro
            }
            listaDias.add(diaNuevo);
        }

        adapter.notifyDataSetChanged();

        // Cruzar contra Firebase para pintar los Azules (apartados) y Naranjas (asistidos)
        consultarAsistenciasEnNube();
    }

    private void consultarAsistenciasEnNube() {
        if (numControlUsuario == null || numControlUsuario.isEmpty()) return;

        db.collection("asistencias")
                .whereEqualTo("num_control", numControlUsuario)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> fechasAsistidas = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        fechasAsistidas.add(doc.getString("fecha"));
                    }

                    // Evaluar la lista visual actual contra las asistencias encontradas en la nube
                    for (DiaCalendario dia : listaDias) {
                        if (dia.fechaCompleta.isEmpty() || dia.colorFondo == Color.parseColor("#F44336")) continue;

                        if (fechasAsistidas.contains(dia.fechaCompleta)) {
                            if (dia.colorFondo == Color.parseColor("#9E9E9E")) {
                                dia.colorFondo = Color.parseColor("#FF9800"); // Pasado con registro = Naranja
                            } else if (dia.colorFondo == Color.parseColor("#4CAF50")) {
                                dia.colorFondo = Color.parseColor("#2196F3"); // Futuro con registro = Azul
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void viajarDetalleMenu(String fecha) {
        db.collection("menus_diarios").document(fecha).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        DocumentSnapshot doc = task.getResult();
                        Boolean disponible = doc.getBoolean("disponible");

                        if (disponible != null && disponible) {
                            Intent intent = new Intent(CalendarioActivity.this, MenuDetalleActivity.class);
                            intent.putExtra("FECHA", fecha);
                            intent.putExtra("PLATO", doc.getString("plato_principal"));
                            intent.putExtra("SOPA", doc.getString("sopa"));
                            intent.putExtra("BEBIDA", doc.getString("bebida"));
                            intent.putExtra("NUM_CONTROL", numControlUsuario); // Enviar usuario a la pantalla de confirmación
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "El comedor está cerrado este día", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "El menú de este día aún no ha sido publicado", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
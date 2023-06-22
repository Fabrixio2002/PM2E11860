package hn.uth.proyecto;

import static hn.uth.proyecto.ActivityCrear.currentPhotoPath;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static hn.uth.proyecto.ActivityCrear.currentPhotoPath;

import hn.uth.proyecto.Configuracion.SQLiteConexion;
import hn.uth.proyecto.Configuracion.Transacciones;


public class ActivityDetalle extends AppCompatActivity {
    private ImageView Imagen;
    private EditText tvNombre;
    private EditText tvTelefono;
    private EditText tvNotas;
    private Button VerImagen;
    private Button llamada;
    private Button actualizar;
    private Button compartir;
    private Button eliminar;
    private static final int REQUEST_CALL_PERMISSION = 1;
String numero,name;
int indice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);
        compartir=findViewById(R.id.compartir);
        eliminar=findViewById(R.id.Eliminar);
        tvNombre = findViewById(R.id.textViewNombres);
        tvTelefono = findViewById(R.id.textViewTelefonos);
        tvNotas = findViewById(R.id.textViewNotas);
        VerImagen=findViewById(R.id.VerImagen);
        llamada=findViewById(R.id.llamada);
        actualizar=findViewById(R.id.actulizar);
        // Obtener los datos del contacto del Intent
        Intent intent = getIntent();
        if (intent != null) {
            int id = intent.getIntExtra("id", 0);
            String nombre = intent.getStringExtra("nombres");
            String telefono = intent.getStringExtra("telefonos");
            String notas = intent.getStringExtra("notas");
            byte[] byteArray = getIntent().getByteArrayExtra("imagen");
            Bitmap imagen = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            indice=id;
           // Imagen.setImageBitmap(imagen);
            tvNombre.setText(nombre);
            tvTelefono.setText(telefono);
            tvNotas.setText(notas);
            numero = tvTelefono.getText().toString().trim();
            name=tvNombre.getText().toString().trim();
            VerImagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ActivityVerImagen.class);
                    intent.putExtra("photo_path", currentPhotoPath);
                    startActivity(intent);
                }
            });
            llamada.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    realizarLlamada();
                }
            });

            compartir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent (Intent.ACTION_SEND);
                    myIntent.setType("text/plain");
                    String shareBody = "Your body here";
                    String shareSub = "Nombre: "+name+" Numero: "+numero;
                    myIntent.putExtra(Intent.EXTRA_SUBJECT, shareBody); myIntent.putExtra (Intent. EXTRA_TEXT, shareSub);
                    startActivity (Intent.createChooser (myIntent, "Compartir"));
                }
            });

            actualizar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AgregarContactos();
                    Intent intent = new Intent(getApplicationContext(), ActivityList.class);
                    startActivity(intent);
                }
            });

            eliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    eliminar();
                    Intent intent = new Intent(getApplicationContext(), ActivityList.class);
                    startActivity(intent);
                }
            });

        }


    }
    private void realizarLlamada() {
        // Obtén el número de teléfono que deseas llamar
        String numeroTelefono = numero.toString(); // Reemplaza con el número de teléfono deseado

        // Crea un Intent con la acción ACTION_DIAL y el número de teléfono
        Intent intentLlamada = new Intent(Intent.ACTION_DIAL);
        intentLlamada.setData(Uri.parse("tel:" + numeroTelefono));

        // Verifica si hay una aplicación para manejar la acción de marcado
        if (intentLlamada.resolveActivity(getPackageManager()) != null) {
            // Inicia la actividad de marcado
            startActivity(intentLlamada);
        } else {
            Toast.makeText(this, "No se encontró una aplicación para realizar llamadas telefónicas", Toast.LENGTH_SHORT).show();
        }
    }

    private void AgregarContactos() {
        if (tvNombre.getText().toString().isEmpty() ||
                tvTelefono.getText().toString().isEmpty() ||
                tvNotas.getText().toString().isEmpty()) {
            // Verificar que todos los campos estén completos
            // y que se haya tomado una foto
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Campos vacíos");
            builder.setMessage("Por favor, completa todos los campos antes de agregar el contacto.");
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(Transacciones.nombres, tvNombre.getText().toString());
        valores.put(Transacciones.telefonos, tvTelefono.getText().toString());
        valores.put(Transacciones.notas, tvNotas.getText().toString());



        String whereClause = "id = ?"; // Reemplaza "id" con el nombre de la columna de identificación en tu tabla de contactos
        String[] whereArgs = {String.valueOf(indice)}; // Reemplaza "contactoId" con el ID del contacto que deseas actualizar

        int result = db.update(Transacciones.tablaContactos, valores, whereClause, whereArgs);
        if (result != -1) {
            Toast.makeText(getApplicationContext(), "Registro actualizado: " + result, Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(getApplicationContext(), "Error al actualizar el registro.", Toast.LENGTH_LONG).show();
        }

        db.close();
    }

    private void eliminar() {
        if (tvNombre.getText().toString().isEmpty() ||
                tvTelefono.getText().toString().isEmpty() ||
                tvNotas.getText().toString().isEmpty()) {
            // Verificar que todos los campos estén completos
            // y que se haya tomado una foto
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Campos vacíos");
            builder.setMessage("Por favor, completa todos los campos antes de agregar el contacto.");
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(Transacciones.nombres, tvNombre.getText().toString());
        valores.put(Transacciones.telefonos, tvTelefono.getText().toString());
        valores.put(Transacciones.notas, tvNotas.getText().toString());



        String whereClause = "id = ?"; // Reemplaza "id" con el nombre de la columna de identificación en tu tabla de contactos
        String[] whereArgs = {String.valueOf(indice)}; // Reemplaza "contactoId" con el ID del contacto que deseas actualizar

        int result = db.delete(Transacciones.tablaContactos,  whereClause, whereArgs);
        if (result != -1) {
            Toast.makeText(getApplicationContext(), "Registro actualizado: " + result, Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(getApplicationContext(), "Error al actualizar el registro.", Toast.LENGTH_LONG).show();
        }

        db.close();
    }


    }

package hn.uth.proyecto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hn.uth.proyecto.Configuracion.SQLiteConexion;
import hn.uth.proyecto.Configuracion.Transacciones;

public class ActivityCrear extends AppCompatActivity {

    private static final int peticion_acceso_camara = 1;
    private static final int peticion_captura_imagen = 2;
    private Button btnsalvar, btnfoto;
    private Spinner comboPais;
    private EditText ETnombre, ETtelefono, ETnotas;
    private ImageView imagen;
    private Uri photoUri;
    public  static    String currentPhotoPath;
    private String elementoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear);

        // ENLAZO LAS VARIABLES CON LOS OBJETOS DEL LAYOUT //
        btnsalvar = findViewById(R.id.btnsalvar);
        btnfoto = findViewById(R.id.btnfoto);
        comboPais = findViewById(R.id.ComboPais);
        ETnombre = findViewById(R.id.ETnombre);
        ETtelefono = findViewById(R.id.ETtelefono);
        ETnotas = findViewById(R.id.ETnotas);
        imagen = findViewById(R.id.imagen);

        // LLENAR SPINNER
        List<String> listaElementos = new ArrayList<>();
        listaElementos.add("Honduras");
        listaElementos.add("España ");
        listaElementos.add("Mexico ");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaElementos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comboPais.setAdapter(adapter);

        // FUNCION DE SELECCION DEL SPINNER
        comboPais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                elementoSeleccionado = listaElementos.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnsalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgregarContactos();

                Toast.makeText(ActivityCrear.this, ""+currentPhotoPath, Toast.LENGTH_SHORT).show();
            }
        });

        // FUNCION DE CLICK PARA TOMAR FOTO
        btnfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisos();
            }
        });
    }

    // Métodos para guardar la foto
    private void permisos() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, peticion_acceso_camara);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == peticion_acceso_camara) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(getApplicationContext(), "Se necesita el permiso para acceder a la cámara", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void TomarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, peticion_captura_imagen);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == peticion_captura_imagen && resultCode == RESULT_OK) {
            try {
                File foto = new File(currentPhotoPath);
                photoUri = Uri.fromFile(foto);

                // Mostrar la imagen en el ImageView
                imagen.setImageURI(photoUri);

                // Aquí puedes realizar cualquier otra acción que necesites con la imagen guardada
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    private long saveImageToDatabase(byte[] imageData) {
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(Transacciones.imagen, imageData);

        long result = db.insert(Transacciones.tablaContactos, null, valores);
        db.close();

        return result;
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "hn.uth.proyecto.file-provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, peticion_captura_imagen);
            }
        }
    }
    private byte[] convertImageToByteArray(Uri photoUri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(photoUri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        return byteArrayOutputStream.toByteArray();
    }


    // Agregar contactos

    private void AgregarContactos() {
        if (ETnombre.getText().toString().isEmpty() ||
                ETtelefono.getText().toString().isEmpty() ||
                ETnotas.getText().toString().isEmpty() ||
                photoUri == null) {
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
        valores.put(Transacciones.nombres, ETnombre.getText().toString());
        valores.put(Transacciones.telefonos, ETtelefono.getText().toString());
        valores.put(Transacciones.notas, ETnotas.getText().toString());

        try {
            // Convertir la imagen en un array de bytes
            byte[] imageData = convertImageToByteArray(photoUri);
            valores.put(Transacciones.imagen, imageData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        long result = db.insert(Transacciones.tablaContactos, null, valores);
        if (result != -1) {
            Toast.makeText(getApplicationContext(), "Registro ingresado: " + result, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Error al guardar el registro.", Toast.LENGTH_LONG).show();
        }

        db.close();
    }




}
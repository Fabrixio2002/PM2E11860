package hn.uth.proyecto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import hn.uth.proyecto.Configuracion.SQLiteConexion;
import hn.uth.proyecto.Configuracion.Transacciones;
import models.Contactos;

public class ActivityList extends AppCompatActivity {

    SQLiteConexion conexion;
    ListView listcontactos;
    ArrayList<Contactos> lista;
    ArrayList<String> ArregloPersonas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
        listcontactos = (ListView) findViewById(R.id.listcontactos);

        ObtenerTabla();

        ArrayAdapter adp = new ArrayAdapter(this, android.R.layout.simple_list_item_1, ArregloPersonas);
        listcontactos.setAdapter(adp);

        listcontactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Obtener el elemento seleccionado en la lista
                String selectedItem = (String) adapterView.getItemAtPosition(i);

                Contactos contactoSeleccionado = lista.get(i);
                // Crear Intent para abrir la nueva actividad
                Intent intent = new Intent(ActivityList.this, ActivityDetalle.class);
                // Pasar los datos del contacto seleccionado como extras del Intent
                intent.putExtra("id", contactoSeleccionado.getId());
                intent.putExtra("nombres", contactoSeleccionado.getNombres());
                intent.putExtra("telefonos", contactoSeleccionado.getTelefonos());
                intent.putExtra("notas", contactoSeleccionado.getNotas());
                // Convierte la imagen en un arreglo de bytes
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                contactoSeleccionado.getImagen();
                byte[] byteArray = stream.toByteArray();

// Pasa el arreglo de bytes como extra en el Intent
                intent.putExtra("imagen", byteArray);


                startActivity(intent);

            }
        });

    }

    private void ObtenerTabla() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        Contactos person = null;
        lista = new ArrayList<>();

        // Cursor de Base de Datos
        Cursor cursor = db.rawQuery(Transacciones.SelectTableContac, null);

        // recorremos el cursor
        while (cursor.moveToNext()) {
            person = new Contactos();
            person.setId(cursor.getInt(0));
            person.setNombres(cursor.getString(1));
            person.setTelefonos(cursor.getString(2));
            person.setNotas(cursor.getString(3));
            person.setImagen(cursor.getBlob(4)); // Obtener los datos como un arreglo de bytes

            lista.add(person);
        }

        cursor.close();

        fillList();
    }

    private void fillList()
    {
        ArregloPersonas = new ArrayList<String>();

        for(int i=0; i < lista.size(); i++)
        {
            ArregloPersonas.add(lista.get(i).getId() + "-"
                    +lista.get(i).getNombres() + " |"
                    +lista.get(i).getTelefonos());
        }
    }

}
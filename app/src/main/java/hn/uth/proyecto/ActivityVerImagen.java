package hn.uth.proyecto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

public class ActivityVerImagen extends AppCompatActivity {
    ImageView foto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_imagen);
    foto=findViewById(R.id.foto);
        Intent intent = getIntent();
        String currentPhotoPath = intent.getStringExtra("photo_path");
       Glide.with(this)
                .load(currentPhotoPath)
                .into(foto);

    }
}
package com.example.threads_app;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    String error = ""; // string field

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button cargar = findViewById(R.id.botonfoto);
        TextView t = findViewById(R.id.textomostar);
        ImageView img = findViewById(R.id.Imagenzorro);
        cargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        String pepe = getDataFromUrl("https://api.myip.com/");
                        String newimg = getDataFromUrl("https://randomfox.ca/floof/");
                        Log.i("mm",getImageUrlFromJson(newimg));

                        String urldisplay = getImageUrlFromJson(newimg);
                        Bitmap bitmap;

                        try {
                            InputStream in = new java.net.URL(urldisplay).openStream();
                            bitmap = BitmapFactory.decodeStream(in);

                            Handler h = new Handler(Looper.getMainLooper());
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    t.setText(pepe);
                                    img.setImageBitmap(bitmap);

                                }
                            });
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage());
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

    };

    private String getDataFromUrl(String demoIdUrl) {

        String result = null;
        int resCode;
        InputStream in;
        try {
            URL url = new URL(demoIdUrl);
            URLConnection urlConn = url.openConnection();

            HttpsURLConnection httpsConn = (HttpsURLConnection) urlConn;
            httpsConn.setAllowUserInteraction(false);
            httpsConn.setInstanceFollowRedirects(true);
            httpsConn.setRequestMethod("GET");
            httpsConn.connect();
            resCode = httpsConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpsConn.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        in, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                in.close();
                result = sb.toString();
            } else {
                error += resCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getImageUrlFromJson(String jsonString) {
        try {
            // Crear un objeto JSON a partir de la cadena de texto
            JSONObject json = new JSONObject(jsonString);

            // Obtener el valor asociado con la clave "image"
            String imageUrl = json.getString("image");

            return imageUrl;
        } catch (JSONException e) {
            e.printStackTrace();
            // Manejar la excepción si ocurre un error al analizar el JSON
        }

        // Devolver null si no se pudo obtener la URL de la imagen
        return null;
    }
}
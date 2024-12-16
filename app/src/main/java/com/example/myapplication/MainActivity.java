package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.AsyncTask;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText cepEditText;
    private Button consultButton;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cepEditText = findViewById(R.id.cepEditText);
        consultButton = findViewById(R.id.consultButton);
        resultTextView = findViewById(R.id.resultTextView);

        consultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cep = cepEditText.getText().toString();
                if (cep.length() == 8) {  // O CEP deve ter 8 dígitos
                    new GetCepData().execute(cep);
                } else {
                    resultTextView.setText("CEP inválido");
                }
            }
        });
    }

    private class GetCepData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String cep = params[0];
            String urlStr = "https://viacep.com.br/ws/" + cep + "/json/";

            try {
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    return response.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String logradouro = jsonObject.getString("logradouro");
                    String bairro = jsonObject.getString("bairro");
                    String localidade = jsonObject.getString("localidade");
                    String uf = jsonObject.getString("uf");

                    String address = logradouro + ", " + bairro + ", " + localidade + " - " + uf;
                    resultTextView.setText(address);
                } catch (JSONException e) {
                    e.printStackTrace();
                    resultTextView.setText("CEP não encontrado");
                }
            } else {
                resultTextView.setText("Erro na consulta");
            }
        }
    }
}

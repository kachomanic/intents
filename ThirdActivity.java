package com.example.rubens.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class ThirdActivity extends AppCompatActivity {
    private EditText editTextWeb;
    private EditText editTextPhone;
    private ImageButton imgbtnWeb;
    private ImageButton imgbtnPhone;
    private ImageButton imgbtnCamera;
    private final int PHONE_CALL_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.third_activity);

        editTextPhone = (EditText) findViewById(R.id.edittextphone);
        editTextWeb = (EditText) findViewById(R.id.edittextweb);
        imgbtnPhone = (ImageButton) findViewById(R.id.imgbtnphone);
        imgbtnWeb = (ImageButton) findViewById(R.id.imgbtnWeb);
        imgbtnCamera = (ImageButton) findViewById(R.id.imgbtncamera);

        imgbtnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = editTextPhone.getText().toString();
                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    //Comprobar version actual de android que se ejecuta
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        //Comprobar si ha aceptado, no ha aceptado o nunca se le pregunto
                        if(CheckPermission(Manifest.permission.CALL_PHONE)){
                            //Ha aceptado
                            Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phoneNumber));
                            if (ActivityCompat.checkSelfPermission(ThirdActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) { return; }
                            startActivity(i);
                        }else{
                            //Ha denegado o es la primera vez que se le pregunta
                            if(!shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)){
                                //No se le ha preguntado aun
                                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PHONE_CALL_CODE);

                            }else{
                                //Ha denegado
                                Toast.makeText(ThirdActivity.this, "Please enable the request permission", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                i.addCategory(Intent.CATEGORY_DEFAULT);
                                i.setData(Uri.parse("package:" + getPackageName()));
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                startActivity(i);
                            }
                        }

                        //requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PHONE_CALL_CODE);
                    } else {
                        OlderVersions(phoneNumber);
                    }
                }else{
                    Toast.makeText(ThirdActivity.this, "Insert a phone number", Toast.LENGTH_SHORT).show();
                }
            }

            @SuppressLint("MissingPermission")
            private void OlderVersions(String phoneNumber) {
                Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                if (CheckPermission(Manifest.permission.CALL_PHONE)) {
                    startActivity(intentCall);
                } else {
                    Toast.makeText(ThirdActivity.this, "Access not granted", Toast.LENGTH_SHORT).show();
                }

            }

        });

        //Boton para el navegador web
        imgbtnWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = editTextWeb.getText().toString();
                if(url != null && !url.isEmpty()){
                    //Intent intentWeb = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+url));
                    Intent intentWeb = new Intent();
                    intentWeb.setAction(Intent.ACTION_VIEW);
                    intentWeb.setData(Uri.parse("http://"+url));
                    startActivity(intentWeb);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //CASO DEL TELEFONO
        switch (requestCode) {
            case PHONE_CALL_CODE:
                String permission = permissions[0];
                int result = grantResults[0];
                if (permission.equals(Manifest.permission.CALL_PHONE)) {
                    //Comprobar si ha sido comprobado o denegada la peticion
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        //Concedio su permiso
                        String phoneNumber = editTextPhone.getText().toString();
                        Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        startActivity(intentCall);
                    }else{
                        //No dio permiso
                        Toast.makeText(ThirdActivity.this, "You declined the access", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }


    }

    private boolean CheckPermission(String permission){
        int result = this.checkCallingOrSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }


}

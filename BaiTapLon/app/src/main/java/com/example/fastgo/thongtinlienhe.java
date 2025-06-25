package com.example.fastgo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fastgo.R;

public class thongtinlienhe extends AppCompatActivity {
ImageButton quaylaittlh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thongtinlienhe);
quaylaittlh= findViewById(R.id.quaylaittlh);
quaylaittlh.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        startActivity(new Intent(thongtinlienhe.this,MainActivity.class));
    }
});


    }
}
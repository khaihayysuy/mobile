package com.example.fastgo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.fastgo.R;
import com.example.database.sql;
import com.google.android.material.textfield.TextInputEditText;

public class dangnhap extends AppCompatActivity {
    TextInputEditText nhapdangnhap, nhapmatkhau;
    Button btndangnhap;
    TextView tvdangky;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 trở lên
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.READ_MEDIA_VIDEO,
                                Manifest.permission.READ_MEDIA_AUDIO
                        },
                        PERMISSION_REQUEST_CODE);
            }
        } else {
            // Android 12 trở xuống
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        PERMISSION_REQUEST_CODE);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dangnhap);
        checkAndRequestPermissions();

        nhapdangnhap = findViewById(R.id.nhapdangnhap);
        nhapmatkhau = findViewById(R.id.nhapmatkhau);
        btndangnhap = findViewById(R.id.btndangnhap);
        tvdangky = findViewById(R.id.tvdangky);

        // Chuyển sang màn hình đăng ký
        tvdangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(dangnhap.this, dangky.class));
            }
        });

        // Xử lý sự kiện đăng nhập
        btndangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Tendangnhap = nhapdangnhap.getText().toString();
                String Matkhau = nhapmatkhau.getText().toString();
                if (Tendangnhap.isEmpty() || Matkhau.isEmpty()) {
                    Toast.makeText(dangnhap.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    sql sqlite = new sql(dangnhap.this);

                    if (sqlite.dangnhap(Tendangnhap, Matkhau) == 1) {

                        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("tendangnhap", Tendangnhap);
                        editor.apply(); // Lưu dữ liệu

                        Toast.makeText(dangnhap.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                        // Chuyển sang màn hình chính sau khi đăng nhập thành công
                        startActivity(new Intent(dangnhap.this, MainActivity.class));
                        finish(); // Đóng Activity đăng nhập
                    } else {
                        // Thông báo đăng nhập thất bại
                        Toast.makeText(dangnhap.this, "Tên đăng nhập hoặc mật khẩu không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Đã được cấp quyền truy cập", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Ứng dụng cần quyền truy cập để hoạt động", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
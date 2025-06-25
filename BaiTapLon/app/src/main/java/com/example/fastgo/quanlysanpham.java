package com.example.fastgo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.fastgo.R;
import com.example.database.sanpham;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class quanlysanpham extends AppCompatActivity {

    private EditText tenspadmin, giaspadmin, soluongspadmin;
    private Button btnthemadmin, btnxoaadmin, btnxemspadmin, btnchonhinhanhadmin;
    private sanpham databaseHelper;
    private ImageView anhadmin;
    private ImageButton quaylaiqlsp;
    private Uri selectedImageUri;
    private static final int REQUEST_STORAGE_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quanlysanpham);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        }

        tenspadmin = findViewById(R.id.tenspadmin);
        giaspadmin = findViewById(R.id.edtgiaspadmin);
        soluongspadmin = findViewById(R.id.soluongspadmin);
        anhadmin = findViewById(R.id.anhadmin);
        btnchonhinhanhadmin = findViewById(R.id.btnchonhinhanhadmin);
        btnthemadmin = findViewById(R.id.btnthemadmin);
        btnxoaadmin = findViewById(R.id.btnxoaadmin);
        btnxemspadmin = findViewById(R.id.btnxemspadmin);
        databaseHelper = new sanpham(this);
        quaylaiqlsp = findViewById(R.id.quaylaiqlsp);

        quaylaiqlsp.setOnClickListener(view -> startActivity(new Intent(quanlysanpham.this, quanlyapp.class)));

        btnchonhinhanhadmin.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        // Firebase Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseFirestore firestore = db;

        btnthemadmin.setOnClickListener(v -> {
            String name = tenspadmin.getText().toString();
            String price = giaspadmin.getText().toString();
            String quantity = soluongspadmin.getText().toString();
            String image = selectedImageUri != null ? selectedImageUri.toString() : null;

            if (name.isEmpty()) {
                Toast.makeText(this, "Tên sản phẩm không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }
            if (price.isEmpty()) {
                Toast.makeText(this, "Giá sản phẩm không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }
            if (quantity.isEmpty()) {
                Toast.makeText(this, "Số lượng sản phẩm không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedImageUri == null) {
                Toast.makeText(this, "Vui lòng chọn hình ảnh", Toast.LENGTH_SHORT).show();
                return;
            }

            // Ghi vào cơ sở dữ liệu SQLite
            SQLiteDatabase dbSQLite = databaseHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("tensp", name);
            values.put("gia", Double.parseDouble(price));
            values.put("soluong", Integer.parseInt(quantity));
            values.put("hinhanh", image);

            try {
                long result = dbSQLite.insertWithOnConflict("SanPham", null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (result != -1) {
                    Toast.makeText(this, "Thêm / cập nhật thành công vào SQLite", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Thêm / cập nhật thất bại vào SQLite", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            // Thêm vào Firestore
            Map<String, Object> product = new HashMap<>();
            product.put("tensp", name);
            product.put("gia", Double.parseDouble(price));
            product.put("soluong", Integer.parseInt(quantity));
            product.put("hinhanh", image);

            firestore.collection("products")
                    .add(product)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Sản phẩm đã được thêm thành công vào Firestore", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi khi thêm sản phẩm vào Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // Chức năng xóa sản phẩm
        btnxoaadmin.setOnClickListener(v -> {
            String name = tenspadmin.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên sản phẩm cần xóa", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            int rows = db.delete("SanPham", "tensp = ?", new String[]{name});
            if (rows > 0) {
                Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });

        // Chuyển đến danh sách sản phẩm
        btnxemspadmin.setOnClickListener(v -> startActivity(new Intent(this, quanlyitemsp.class)));
    }

    private ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    anhadmin.setImageURI(selectedImageUri);
                    Log.d("DEBUG", "Selected Image URI: " + selectedImageUri);
                } else {
                    Toast.makeText(this, "Chọn ảnh thất bại", Toast.LENGTH_SHORT).show();
                }
            });
}

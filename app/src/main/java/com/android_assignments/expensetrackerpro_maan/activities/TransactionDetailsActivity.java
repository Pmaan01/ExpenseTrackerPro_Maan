package com.android_assignments.expensetrackerpro_maan.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android_assignments.expensetrackerpro_maan.R;
import com.android_assignments.expensetrackerpro_maan.models.Transaction;
import com.android_assignments.expensetrackerpro_maan.utils.JsonStorageHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TransactionDetailsActivity extends AppCompatActivity {
    private Transaction transaction;
    private ImageView ivReceipt;
    private Button btnTakePhoto, btnUpload, btnAddFav;
    private List<Transaction> all;

    private Uri takePhotoUri;

    // Activity Result Launchers
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<String> pickImageLauncher;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private File lastImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        // Receive transaction
        transaction = getIntent().getParcelableExtra("transaction");

        ivReceipt = findViewById(R.id.ivReceipt);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnUpload = findViewById(R.id.btnUploadGallery);
        btnAddFav = findViewById(R.id.btnAddFavorite);

        TextView tvTitle = findViewById(R.id.tvDetTitle);
        TextView tvCategory = findViewById(R.id.tvDetCategory);
        TextView tvAmount = findViewById(R.id.tvDetAmount);
        TextView tvDate = findViewById(R.id.tvDetDate);
        TextView tvDesc = findViewById(R.id.tvDetDesc);

        if (transaction != null) {
            tvTitle.setText(transaction.title);
            tvCategory.setText(transaction.category);
            tvAmount.setText(String.format("$%.2f", transaction.amount));
            tvDate.setText(transaction.date);
            tvDesc.setText(transaction.description);
            if (transaction.receiptPath != null) ivReceipt.setImageURI(Uri.parse(transaction.receiptPath));
        }

        all = JsonStorageHelper.getTransactions(this);

        registerLaunchers();

        btnUpload.setOnClickListener(v -> {
            pickImageLauncher.launch("image/*");
        });

        btnTakePhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        btnAddFav.setOnClickListener(v -> {
            if (transaction == null) return;
            // toggle favorite on the transaction within the saved list
            for (Transaction t : all) {
                if (t.id.equals(transaction.id)) {
                    t.isFavorite = !t.isFavorite;
                    transaction.isFavorite = t.isFavorite;
                    break;
                }
            }
            JsonStorageHelper.saveTransactions(this);
            Toast.makeText(this, transaction.isFavorite ? "Added to favorites" : "Removed from favorites", Toast.LENGTH_SHORT).show();
        });
    }

    private void registerLaunchers() {
        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean success) {
                        if (success && lastImageFile != null && lastImageFile.exists()) {
                            // Persist the actual filesystem path (survives app restarts)
                            transaction.receiptPath = lastImageFile.getAbsolutePath();
                            ivReceipt.setImageURI(Uri.fromFile(lastImageFile));
                            JsonStorageHelper.saveTransactions(TransactionDetailsActivity.this); // persist changes
                        } else {
                            Toast.makeText(TransactionDetailsActivity.this, "Camera cancelled or failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri != null) {
                            String savedPath = copyUriToInternal(uri);
                            if (savedPath != null) {
                                transaction.receiptPath = savedPath;
                                ivReceipt.setImageURI(Uri.parse(savedPath));
                                JsonStorageHelper.saveTransactions(TransactionDetailsActivity.this);
                            }
                        }
                    }
                });


        requestCameraPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        launchCamera();
                    } else {
                        if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                            new AlertDialog.Builder(TransactionDetailsActivity.this)
                                    .setTitle("Camera permission required")
                                    .setMessage("Please enable camera permission in app settings to take photos.")
                                    .setPositiveButton("Open Settings", (d, w) -> {
                                        Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                                        i.setData(uri);
                                        startActivity(i);
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .show();
                        } else {
                            Toast.makeText(TransactionDetailsActivity.this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void launchCamera() {
        try {
            lastImageFile = createImageFile(); // assign to field
            takePhotoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", lastImageFile);
            takePictureLauncher.launch(takePhotoUri);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to create file for photo", Toast.LENGTH_SHORT).show();
        }
    }


    private File createImageFile() throws IOException {
        File imagesDir = new File(getFilesDir(), "images");
        if (!imagesDir.exists()) imagesDir.mkdirs();
        return File.createTempFile("receipt_" + System.currentTimeMillis(), ".jpg", imagesDir);
    }

    private String copyUriToInternal(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            if (is == null) return null;
            File out = new File(getFilesDir(), "images");
            if (!out.exists()) out.mkdirs();
            File dest = new File(out, "receipt_" + System.currentTimeMillis() + ".jpg");
            try (FileOutputStream fos = new FileOutputStream(dest)) {
                byte[] buf = new byte[4096];
                int r;
                while ((r = is.read(buf)) != -1) fos.write(buf, 0, r);
            } finally {
                is.close();
            }
            return Uri.fromFile(dest).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

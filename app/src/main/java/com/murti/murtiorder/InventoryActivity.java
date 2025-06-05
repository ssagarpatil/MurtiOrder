package com.murti.murtiorder;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class InventoryActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 101;

    private Uri imageUri;
    private Uri photoUri;
    private InventoryModel currentProductForEdit;

    private StorageReference storageRef;
    private DatabaseReference inventoryRef;

    private AlertDialog currentDialog;
    private AlertDialog progressDialog;
    private ProgressBar progressBar;

    private RecyclerView recyclerInventory;
    private InventoryAdapter adapter;
    private ArrayList<InventoryModel> inventoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        storageRef = FirebaseStorage.getInstance().getReference("inventory_images");
        inventoryRef = FirebaseDatabase.getInstance().getReference("inventory");

        recyclerInventory = findViewById(R.id.recyclerInventory);
        recyclerInventory.setHasFixedSize(true);
        recyclerInventory.setLayoutManager(new LinearLayoutManager(this));

        inventoryList = new ArrayList<>();
        adapter = new InventoryAdapter(inventoryList, new InventoryAdapter.OnInventoryActionListener() {
            @Override
            public void onDelete(InventoryModel product) {
                deleteProduct(product);
            }

            @Override
            public void onEdit(InventoryModel product) {
                currentProductForEdit = product;
                showEditProductDialog(product);
            }
        }, this);
        recyclerInventory.setAdapter(adapter);

        loadInventory();

        ImageButton addBtn = findViewById(R.id.addspeci);
        addBtn.setOnClickListener(v -> showAddProductDialog());
    }

    private void loadInventory() {
        inventoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                inventoryList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    InventoryModel p = ds.getValue(InventoryModel.class);
                    if (p != null) inventoryList.add(p);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InventoryActivity.this, "Failed to load inventory: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, null);
        builder.setView(dialogView);
        builder.setTitle("Add New Product");

        ImageView imagePreview = dialogView.findViewById(R.id.imagePreview);
        Button pickImageBtn = dialogView.findViewById(R.id.buttonPickImage);
        Button takePhotoBtn = dialogView.findViewById(R.id.buttonTakePhoto);
        EditText nameInput = dialogView.findViewById(R.id.editTextName);
        EditText priceInput = dialogView.findViewById(R.id.editTextPrice);
        Button saveBtn = dialogView.findViewById(R.id.buttonSave);
        ImageButton removeImageBtn = dialogView.findViewById(R.id.btnRemoveImage);

        imageUri = null;
        photoUri = null;
        imagePreview.setImageResource(R.drawable.ic_image_placeholder);
        removeImageBtn.setVisibility(View.GONE);

        currentDialog = builder.create();
        currentDialog.show();

        pickImageBtn.setOnClickListener(v -> openGallery());
        takePhotoBtn.setOnClickListener(v -> checkCameraPermission());

        removeImageBtn.setOnClickListener(v -> {
            imageUri = null;
            photoUri = null;
            imagePreview.setImageResource(R.drawable.ic_image_placeholder);
            removeImageBtn.setVisibility(View.GONE);
        });

        saveBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String price = priceInput.getText().toString().trim();

            if ((imageUri == null && photoUri == null) || name.isEmpty() || price.isEmpty()) {
                Toast.makeText(this, "Fill all fields and select image", Toast.LENGTH_SHORT).show();
                return;
            }

            showProgressDialog();
            uploadImageToFirebase(imageUri != null ? imageUri : photoUri, name, price);
        });
    }

    private void showEditProductDialog(InventoryModel product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, null);
        builder.setView(dialogView);
        builder.setTitle("Edit Product");

        ImageView imagePreview = dialogView.findViewById(R.id.imagePreview);
        Button pickImageBtn = dialogView.findViewById(R.id.buttonPickImage);
        Button takePhotoBtn = dialogView.findViewById(R.id.buttonTakePhoto);
        EditText nameInput = dialogView.findViewById(R.id.editTextName);
        EditText priceInput = dialogView.findViewById(R.id.editTextPrice);
        Button saveBtn = dialogView.findViewById(R.id.buttonSave);
        ImageButton removeImageBtn = dialogView.findViewById(R.id.btnRemoveImage);

        saveBtn.setText("Update");
        nameInput.setText(product.getName());
        priceInput.setText(product.getPrice());
        Glide.with(this).load(product.getImageUrl()).into(imagePreview);
        removeImageBtn.setVisibility(View.VISIBLE);

        currentDialog = builder.create();
        currentDialog.show();

        pickImageBtn.setOnClickListener(v -> openGallery());
        takePhotoBtn.setOnClickListener(v -> checkCameraPermission());

        removeImageBtn.setOnClickListener(v -> {
            imageUri = null;
            photoUri = null;
            imagePreview.setImageResource(R.drawable.ic_image_placeholder);
            removeImageBtn.setVisibility(View.GONE);
        });

        saveBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String price = priceInput.getText().toString().trim();

            if (name.isEmpty() || price.isEmpty()) {
                Toast.makeText(this, "Name and price cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            showProgressDialog();
            if (imageUri != null || photoUri != null) {
                uploadImageAndUpdateProduct(imageUri != null ? imageUri : photoUri, name, price);
            } else {
                updateProduct(product.getId(), name, price, product.getImageUrl());
            }
        });
    }

    private void showProgressDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);
        progressBar = view.findViewById(R.id.progressBarCircular);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view).setCancelable(false);
        progressDialog = builder.create();
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this,
                        "com.murti.murtiorder.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            return File.createTempFile("IMG_" + timeStamp + "_", ".jpg", storageDir);
        } catch (IOException e) {
            Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void uploadImageToFirebase(Uri imageUri, String name, String price) {
        StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");

        fileRef.putFile(imageUri)
                .addOnProgressListener(snapshot -> {
                    long progress = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    if (progressBar != null) progressBar.setProgress((int) progress);
                })
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String id = inventoryRef.push().getKey();
                    InventoryModel product = new InventoryModel(id, name, price, uri.toString());
                    inventoryRef.child(id).setValue(product).addOnCompleteListener(task -> {
                        dismissProgressDialog();
                        if (task.isSuccessful()) {
                            Toast.makeText(InventoryActivity.this, "Product added", Toast.LENGTH_SHORT).show();
                            if (currentDialog != null) currentDialog.dismiss();
                        } else {
                            Toast.makeText(InventoryActivity.this, "Error saving product", Toast.LENGTH_SHORT).show();
                        }
                    });
                }))
                .addOnFailureListener(e -> {
                    dismissProgressDialog();
                    Toast.makeText(InventoryActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadImageAndUpdateProduct(Uri imageUri, String name, String price) {
        StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");
        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> updateProduct(currentProductForEdit.getId(), name, price, uri.toString()))
                )
                .addOnFailureListener(e -> {
                    dismissProgressDialog();
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateProduct(String productId, String name, String price, String imageUrl) {
        InventoryModel updatedProduct = new InventoryModel(productId, name, price, imageUrl);
        inventoryRef.child(productId).setValue(updatedProduct)
                .addOnCompleteListener(task -> {
                    dismissProgressDialog();
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Product updated", Toast.LENGTH_SHORT).show();
                        if (currentDialog != null) currentDialog.dismiss();
                    } else {
                        Toast.makeText(this, "Failed to update product", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteProduct(InventoryModel product) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(product.getImageUrl());
        imageRef.delete().addOnSuccessListener(aVoid -> {
            inventoryRef.child(product.getId()).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to delete from database", Toast.LENGTH_SHORT).show();
                }
            });
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to delete image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && currentDialog != null) {
            ImageView preview = currentDialog.findViewById(R.id.imagePreview);
            ImageButton btnRemove = currentDialog.findViewById(R.id.btnRemoveImage);

            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                imageUri = data.getData();
                Glide.with(this).load(imageUri).centerCrop().into(preview);
                photoUri = null;
                btnRemove.setVisibility(View.VISIBLE);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Glide.with(this).load(photoUri).centerCrop().into(preview);
                imageUri = null;
                btnRemove.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
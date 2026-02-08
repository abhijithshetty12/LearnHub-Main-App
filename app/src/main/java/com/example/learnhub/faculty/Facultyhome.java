package com.example.learnhub.faculty;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.learnhub.Join;
import com.example.learnhub.Login;
import com.example.learnhub.R;
import com.example.learnhub.faculty.ui.home.HomeFragment;
import com.example.learnhub.showclasses;
import com.example.learnhub.student.JoinClass;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learnhub.databinding.ActivityFacultyhomeBinding;
import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.function.Function;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Facultyhome extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityFacultyhomeBinding binding;
    FirebaseStorage storage ;
    StorageReference storageReference ;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    TextView username,email;
    static  CircleImageView profileimg ;
    Boolean isFaculty;
    String uname,usertype;
    public static final String SHARED_PREFS = "shared_prefs";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent= getIntent();
         uname = intent.getStringExtra("username");
        String emailid = intent.getStringExtra("email");
         usertype=intent.getStringExtra("usertype");
        binding = ActivityFacultyhomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarFacultyhome.toolbar);
        binding.appBarFacultyhome.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("Faculty".equals(usertype)){


                Snackbar.make(view, "Create class", Snackbar.LENGTH_LONG)
                        .setAction("Create", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Redirect to Classes activity when "Proceed" is clicked
                                Intent intent = new Intent(Facultyhome.this, Classes.class);
                                intent.putExtra("username", uname);
                                intent.putExtra("email", emailid);
                                startActivity(intent);
                            }
                        })
                        .setAnchorView(R.id.fab).show();
            }else {
                    Snackbar.make(view, "Join class", Snackbar.LENGTH_LONG)
                            .setAction("Join", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(Facultyhome.this, "Class joined", Toast.LENGTH_SHORT).show();
                                    // Redirect to Classes activity when "Proceed" is clicked
                                   Intent intent = new Intent(Facultyhome.this, JoinClass.class);
                                    intent.putExtra("username", uname);
                                    intent.putExtra("email", emailid);
                                    startActivity(intent);
                                }
                            })
                            .setAnchorView(R.id.fab).show();
                }
                }


        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_facultyhome);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_logout) {
                    logoutUser();
                    return true;
                }
                if (item.getItemId() == R.id.nav_home){
                    navController.navigate(R.id.nav_home);
                    drawer.closeDrawer(GravityCompat.START);
                    return true;

                }
                if (item.getItemId() == R.id.nav_notification){
                    navController.navigate(R.id.nav_notification);
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }
                if (item.getItemId() == R.id.nav_help){
                    navController.navigate(R.id.nav_help);
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }

            private void logoutUser() {
                new AlertDialog.Builder(Facultyhome.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes",((dialog, which) -> {
                            SharedPreferences sharedPreferences =  getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                            SharedPreferences.Editor editor  = sharedPreferences.edit();
                            editor.putString("name","");
                            editor.putString("username","");
                            editor.putString("email","");
                            editor.putString("usertype","");
                            editor.putString("password","");
                            editor.apply();

                            startActivity(new Intent(Facultyhome.this, Login.class));
                            finish();
                        }))
                        .setNegativeButton("Cancel",((dialog, which) -> {
                            dialog.dismiss();
                        }))
                        .show();

            }
        });

        View headerView = navigationView.getHeaderView(0) ;
        profileimg = headerView.findViewById(R.id.profile_image);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        username = headerView.findViewById(R.id.username);
        username.setText(uname);
        email = headerView.findViewById(R.id.emailid);
        email.setText(emailid);
        isFaculty = "Faculty".equals(usertype);
        setProfile(emailid);

        imagePickerLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();

                        uploadImageToFirebase(uri,emailid);
                    } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                        Toast.makeText(this, ImagePicker.Companion.getError(result.getData()), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Image picking cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.facultyhome, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.changeprofile) {
            // Handle "Change profile" action
            Toast.makeText(this, "Change Profile clicked", Toast.LENGTH_SHORT).show();
            ImagePicker.Companion.with(this)
                    .crop()
                    .cropOval()
                    .maxResultSize(512, 512, true)
                    .provider(ImageProvider.BOTH)
                    .createIntentFromDialog(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            if (intent != null) {
                                imagePickerLauncher.launch(intent);
                            }
                            return Unit.INSTANCE;
                        }
                    });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_facultyhome);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public void finish() {
        super.finish();
    }
    private void uploadImageToFirebase(Uri imageUri,String emailid){
        if (imageUri!=null){
            StorageReference imageref = storageReference.child("image/"+usertype+"/"+emailid.replace(".", ",") +".jpg");
            imageref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();
                            saveImageToFirebase(emailid,downloadUrl);
                        }
                    });
                    Toast.makeText(Facultyhome.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Facultyhome.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
    private void saveImageToFirebase(String emailid,String imageUrl){
     DatabaseReference facultyref = FirebaseDatabase.getInstance().getReference("Faculty");
     DatabaseReference studentref = FirebaseDatabase.getInstance().getReference("Students");
     String uid = emailid.replace(".", ",") ;
     if (isFaculty){
     facultyref.child(uid).child("imageUrl").setValue(imageUrl)
             .addOnSuccessListener(aVoid -> {
                 Toast.makeText(this, "Image URL saved to Firebase", Toast.LENGTH_SHORT).show();setProfile(emailid);
             })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show());

     }else{
        studentref.child(uid).child("imageUrl").setValue(imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Image URL saved to Firebase", Toast.LENGTH_SHORT).show();setProfile(emailid);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show());
         }
    }
    private   void setProfile(String email){
        DatabaseReference facultyref = FirebaseDatabase.getInstance().getReference("Faculty");
        DatabaseReference studentref = FirebaseDatabase.getInstance().getReference("Students");
        if (isFaculty) {
            facultyref.child(email.replace(".", ",")).child("imageUrl").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String imageUrl = task.getResult().getValue(String.class);
                    if (imageUrl != null) {
                        loadImageFromUrl(imageUrl);
                    }
                }
            });
        }else {
            studentref.child(email.replace(".", ",")).child("imageUrl").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String imageUrl = task.getResult().getValue(String.class);
                    if (imageUrl != null) {
                      loadImageFromUrl(imageUrl);
                    }
                }
            });
        }
    }
    private  void loadImageFromUrl(String imageUrl) {

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.profileimg) // Add a placeholder image if needed
                .into(profileimg);    }
}
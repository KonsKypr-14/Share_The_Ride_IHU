package com.example.sharetheride;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProfileFragment extends Fragment {

  private static final String TEXT_ID = "text_id";

  public static ProfileFragment newInstance(@StringRes int textId) {
    ProfileFragment frag = new ProfileFragment();

    Bundle args = new Bundle();
    args.putInt(TEXT_ID, textId);
    frag.setArguments(args);

    return frag;
  }

  TextInputEditText email_input, name_input, surname_input, car_size_input, car_plate_input, phone_number_input;
  Button buttonUpdate;
  FirebaseAuth mAuth;
  FirebaseUser user;
  ProgressBar progressBar;
  TextView textView_register, textView_reset;
  FirebaseFirestore db;

  private NavigationView navigationView;


  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
  Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.activity_profile, container, false);

    buttonUpdate = layout.findViewById(R.id.update_btn);
    progressBar = layout.findViewById(R.id.progressbar);

    mAuth = FirebaseAuth.getInstance();
    user = mAuth.getCurrentUser();
    db = FirebaseFirestore.getInstance();

    //By using the below method we take the current status of the user.
    //What we need is to display the login in case the user is null
    user = ((MainActivity) getActivity()).getUser();  // Pass the logged-in user

    email_input = layout.findViewById(R.id.email);
    email_input.setText(user.getEmail());

    name_input = layout.findViewById(R.id.name);
    surname_input = layout.findViewById(R.id.surname);
    car_size_input = layout.findViewById(R.id.car_size);
    car_plate_input = layout.findViewById(R.id.car_plate);
    phone_number_input = layout.findViewById(R.id.phone_number);

    //Use the list to be more dynamic the logic. In case of new fields in DB we just add the field into the mapper.
    List<FieldMapper> fieldMappers = new ArrayList<>();

    fieldMappers.add(new FieldMapper(name_input, "name"));
    fieldMappers.add(new FieldMapper(surname_input, "surname"));
    fieldMappers.add(new FieldMapper(car_plate_input, "name"));
    fieldMappers.add(new FieldMapper(car_size_input, "car_size"));
    fieldMappers.add(new FieldMapper(email_input, "email"));
    fieldMappers.add(new FieldMapper(phone_number_input, "phone_number"));

    db.collection("users_collection").document(user.getUid()).get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
              @Override
              public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot != null && documentSnapshot.exists() ) {
                  //To avoid the write of each field, I create the class to be more dynamic
                  for (FieldMapper fieldMapper : fieldMappers) {
                    fieldMapper.setTextFromDocument(documentSnapshot);
                  }
                  //name_input.setText(documentSnapshot.getString("name"));
                  //surname_input.setText(documentSnapshot.getString("surname"));
                  //etc...
                }
              }
            });


    //if ( user != null ) {
    //  editText_email.setText(user.getEmail());
    //}else{
    //  editText_email.setText("login now"); //= user.getEmail().toString();
    //}
    //// Get the menu from the NavigationView
    //Menu menu = navigationView.getMenu();

    //// Find the login/register menu item
    //MenuItem loginMenuItem = menu.findItem(R.id.nav_log_reg);

    //editText_password = layout.findViewById(R.id.password);
    //textView_register = layout.findViewById(R.id.registerNow);
    //textView_reset = layout.findViewById(R.id.resetPass);

/*
    textView_register.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Fragment registerFragment = RegisterFragment.newInstance(R.string.click_to_register);
        // Get the FragmentManager to perform the transaction
        FragmentManager fragmentManager = getParentFragmentManager();
        // Start a FragmentTransaction to replace LoginFragment with RegisterFragment
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Optionally set animations for fragment transition
        fragmentTransaction.setCustomAnimations(R.anim.nav_enter, R.anim.nav_exit);
        // Replace the current fragment with RegisterFragment
        fragmentTransaction.replace(R.id.home_content, registerFragment);
        // Add the transaction to the back stack, so pressing "Back" returns to LoginFragment
        fragmentTransaction.addToBackStack(null);
        // Commit the transaction
        fragmentTransaction.commit();
      }
    });
    textView_reset.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        /*
        Fragment registerFragment = RegisterFragment.newInstance(R.string.click_to_register);
        // Get the FragmentManager to perform the transaction
        FragmentManager fragmentManager = getParentFragmentManager();
        // Start a FragmentTransaction to replace LoginFragment with RegisterFragment
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Optionally set animations for fragment transition
        fragmentTransaction.setCustomAnimations(R.anim.nav_enter, R.anim.nav_exit);
        // Replace the current fragment with RegisterFragment
        fragmentTransaction.replace(R.id.home_content, registerFragment);
        // Add the transaction to the back stack, so pressing "Back" returns to LoginFragment
        fragmentTransaction.addToBackStack(null);
        // Commit the transaction
        fragmentTransaction.commit();
        //*****
      }
    });
    */

    buttonUpdate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        progressBar.setVisibility(View.VISIBLE);
        String email, password;
        if ( user != null ) {
          email_input.setText(user.getEmail());

        //email = editText_email.getText().toString();
        //password = editText_password.getText().toString();

        if (email_input.getText().equals(null)){
          Toast.makeText(getActivity(), "Enter email", Toast.LENGTH_SHORT).show();
          progressBar.setVisibility(View.GONE);
          return;
        }
        //if (TextUtils.isEmpty(password)){
        //  Toast.makeText(getActivity(), "Enter password", Toast.LENGTH_SHORT).show();
        //  progressBar.setVisibility(View.GONE);
        //  return;
        //}

        Map<String , Object> map_business = new HashMap<>();

        map_business.put("car_model", "");
        map_business.put("car_size", "");
        map_business.put("email", email_input.getText().toString());
        map_business.put("name", name_input.getText().toString());
        map_business.put("phone_number ", phone_number_input.getText().toString());
        map_business.put("role", "");
        map_business.put("surname", surname_input.getText().toString());
        map_business.put("vehicle_plate", car_plate_input.getText().toString());
        db.collection("users_collection").document(mAuth.getUid())
                .set(map_business, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void aVoid) {
                    //Log.d(TAG, "DocumentSnapshot successfully written!");
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Account updated.", Toast.LENGTH_SHORT).show();
                  }
                })
                .addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                    //Log.w(TAG, "Error writing document", e);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Error updating the account.", Toast.LENGTH_SHORT).show();
                  }
                });
        }
        /*
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                      // Sign in success, update UI with the signed-in user's information
                      //Log.d(TAG, "signInWithEmail:success");
                      //updateUI(user);
                      Toast.makeText(getActivity(), "Login Successful.", Toast.LENGTH_SHORT).show();

                      //// User is logged in, set to "Logout"
                      //loginMenuItem.setTitle(R.string.menu_logout);
                      //loginMenuItem.setIcon(R.drawable.logout);  // Update the icon if needed

                      // Notify the MainActivity to update the navigation menu and user state
                      if (getActivity() instanceof MainActivity) {
                        //((MainActivity) getActivity()).setUser();  // Pass the logged-in user
                        //((MainActivity) getActivity()).updateLoginMenuItem();
                      }

                      // Notify the MainActivity to update the navigation menu
                      //if (getActivity() instanceof MainActivity) {
                      //}
                      //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                      //startActivity(intent);
                      //finish();
                    } else {
                      // If sign in fails, display a message to the user.
                      //Log.w(TAG, "signInWithEmail:failure", task.getException());
                      Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                      //updateUI(null);
                    }
                  }
                });
    */
      }
    });

    return layout;
  }



}


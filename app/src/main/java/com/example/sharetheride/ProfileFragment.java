package com.example.sharetheride;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
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
    Button buttonUpdate, buttonReg, buttonLog;
    FirebaseAuth mAuth;
    FirebaseUser user;
    ProgressBar progressBar;
    FirebaseFirestore db;
    LinearLayout layout_profile, layout_log_reg;

    private NavigationView navigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.activity_profile, container, false);

        //navigationView = ((MainActivity) getActivity()).getMenu();  // Pass the logged-in user

        //Menu menu = navigationView.getMenu();
        //MenuItem loginMenuItem = menu.findItem(R.id.nav_log_reg);

        //navigationView.getMenu().findItem(R.id.group_profile).setVisible(true);

        layout_profile = layout.findViewById(R.id.linear_profile);
        layout_log_reg = layout.findViewById(R.id.linear_log_reg);

        buttonReg = layout.findViewById(R.id.button_register);
        buttonLog = layout.findViewById(R.id.button_login);

        //By using the below method we take the current status of the user.
        //What we need is to display the login in case the user is null
        user = ((MainActivity) getActivity()).getUser();  // Pass the logged-in user
        if (user != null) {
            layout_profile.setVisibility(View.VISIBLE);
            layout_log_reg.setVisibility(View.GONE);
        } else {
            layout_profile.setVisibility(View.GONE);
            layout_log_reg.setVisibility(View.VISIBLE);
        }


        buttonReg.setOnClickListener(new View.OnClickListener() {
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

        buttonLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getApplicationContext(), Login.class);
                //startActivity(intent);
                //finish();

                Fragment loginFragment = LoginFragment.newInstance(R.string.click_to_login);
                // Get the FragmentManager to perform the transaction
                FragmentManager fragmentManager = getParentFragmentManager();
                // Start a FragmentTransaction to replace LoginFragment with RegisterFragment
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                // Optionally set animations for fragment transition
                fragmentTransaction.setCustomAnimations(R.anim.nav_enter, R.anim.nav_exit);
                // Replace the current fragment with RegisterFragment
                fragmentTransaction.replace(R.id.home_content, loginFragment);
                // Add the transaction to the back stack, so pressing "Back" returns to LoginFragment
                fragmentTransaction.addToBackStack(null);
                // Commit the transaction
                fragmentTransaction.commit();
            }
        });

        buttonUpdate = layout.findViewById(R.id.update_btn);
        progressBar = layout.findViewById(R.id.progressbar);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();


        //Initialize the object from layout into code
        email_input = layout.findViewById(R.id.email);
        name_input = layout.findViewById(R.id.name);
        surname_input = layout.findViewById(R.id.surname);
        car_size_input = layout.findViewById(R.id.car_size);
        car_plate_input = layout.findViewById(R.id.car_plate);
        phone_number_input = layout.findViewById(R.id.phone_number);


        //Initialize email + all the data in case the user is already logged in.
        if (user != null) {
            //Because we are on profile, we need the data to be retrieved/loaded so we display the bar and hide it on success.
            progressBar.setVisibility(View.VISIBLE);

            email_input.setText(user.getEmail());

            //Use the list to be more dynamic the logic.
            //In case of new fields in DB we just add the field into the mapper.
            List<FieldMapper> fieldMappers = new ArrayList<>();

            fieldMappers.add(new FieldMapper(name_input, "name"));
            fieldMappers.add(new FieldMapper(surname_input, "surname"));
            fieldMappers.add(new FieldMapper(car_plate_input, "vehicle_plate"));
            fieldMappers.add(new FieldMapper(car_size_input, "car_size"));
            fieldMappers.add(new FieldMapper(email_input, "email"));
            fieldMappers.add(new FieldMapper(phone_number_input, "phone_number"));

            db.collection("users_collection").document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        // Handle the error
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        //To avoid the write of each field, I create the class to be more dynamic
                        for (FieldMapper fieldMapper : fieldMappers) {
                            fieldMapper.setTextFromDocument(documentSnapshot);
                        }
                        email_input.setEnabled(false); //Disable the change of the email.

                        progressBar.setVisibility(View.GONE);
                        //name_input.setText(documentSnapshot.getString("name"));
                        //surname_input.setText(documentSnapshot.getString("surname"));
                        //etc...
                    }
                }
            });
        }

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                if (user != null) {
                    email_input.setText(user.getEmail());

                    //email = editText_email.getText().toString();
                    //password = editText_password.getText().toString();

                    if (email_input.getText().equals(null)) {
                        Toast.makeText(getActivity(), "Enter email", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                    //if (TextUtils.isEmpty(password)){
                    //  Toast.makeText(getActivity(), "Enter password", Toast.LENGTH_SHORT).show();
                    //  progressBar.setVisibility(View.GONE);
                    //  return;
                    //}

                    Map<String, Object> map_business = new HashMap<>();

                    map_business.put("car_model", "");
                    map_business.put("car_size", "");
                    map_business.put("email", email_input.getText().toString());
                    map_business.put("name", name_input.getText().toString());
                    map_business.put("phone_number", phone_number_input.getText().toString());
                    map_business.put("role", "");
                    map_business.put("surname", surname_input.getText().toString());
                    map_business.put("full_name", name_input.getText().toString() + " " + surname_input.getText().toString());
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
            }
        });

        return layout;
    }


}


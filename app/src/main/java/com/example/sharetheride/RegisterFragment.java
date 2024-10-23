package com.example.sharetheride;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {

    private static final String TEXT_ID = "text_id";

    TextInputEditText editText_email, editText_password, editText_password_re;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    public static RegisterFragment newInstance(@StringRes int textId) {
        RegisterFragment frag = new RegisterFragment();

        Bundle args = new Bundle();
        args.putInt(TEXT_ID, textId);
        frag.setArguments(args);

        return frag;
    }

    Button button_test;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.activity_register, container, false);
/*
    if (getArguments() != null) {
      String text = getString(getArguments().getInt(TEXT_ID));
      ((TextView) layout.findViewById(R.id.text)).setText(text);
    } else {
      throw new IllegalArgumentException("Argument " + TEXT_ID + " is mandatory");
    }*/

    /*button_test = layout.findViewById(R.id.button_test);

    button_test.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String email, password;
        Toast.makeText(getActivity(), "Button Clicked!", Toast.LENGTH_SHORT).show();
      }
    });
*/

        editText_email = layout.findViewById(R.id.email);
        editText_password = layout.findViewById(R.id.password);
        editText_password_re = layout.findViewById(R.id.password_re);
        buttonReg = layout.findViewById(R.id.register_btn);
        progressBar = layout.findViewById(R.id.progressbar);
        textView = layout.findViewById(R.id.loginNow);
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        textView.setOnClickListener(new View.OnClickListener() {
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

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                progressBar.setVisibility(View.VISIBLE);
                String email, password, password_re;

                email = editText_email.getText().toString();
                password = editText_password.getText().toString();
                password_re = editText_password_re.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getActivity(), "Enter email", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(getActivity(), "Password should be 6 or more characters", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getActivity(), "Enter password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(password_re)) {
                    Toast.makeText(getActivity(), "Enter password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (!password.equals(password_re)) {
                    Toast.makeText(getActivity(), "Passwords doesn't match", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    //Log.d(TAG, "createUserWithEmail:success");
                                    //FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(getActivity(), "Account created.", Toast.LENGTH_SHORT).show();
                                    Map<String, Object> map_business = new HashMap<>();

                                    map_business.put("car_model", "");
                                    map_business.put("car_size", "");
                                    map_business.put("email", editText_email.getText().toString());
                                    map_business.put("name", "");
                                    map_business.put("phone_number ", "");
                                    map_business.put("role", "");
                                    map_business.put("surname", "");
                                    map_business.put("vechicle_plate", "");
                                    db.collection("users_collection").document(mAuth.getUid())
                                            .set(map_business, SetOptions.merge())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    //Log.d(TAG, "DocumentSnapshot successfully written!");
                                                    Toast.makeText(getActivity(), "Account created.", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    //Log.w(TAG, "Error writing document", e);
                                                    Toast.makeText(getActivity(), "Error writing document.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                      /*
                      Intent intent = new Intent(getApplicationContext(), Login.class);
                      startActivity(intent);
                      finish();
                       */

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
                                } else {
                                    // If sign in fails, display a message to the user.
                                    //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

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
 */
        return layout;

    }
}


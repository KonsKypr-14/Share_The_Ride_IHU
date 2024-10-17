package com.example.sharetheride;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class LoginFragment extends Fragment {

  private static final String TEXT_ID = "text_id";



  public static LoginFragment newInstance(@StringRes int textId) {
    LoginFragment frag = new LoginFragment();

    Bundle args = new Bundle();
    args.putInt(TEXT_ID, textId);
    frag.setArguments(args);

    return frag;
  }

  TextInputEditText editText_email, editText_password;
  Button buttonLog;
  FirebaseAuth mAuth;
  ProgressBar progressBar;
  TextView textView_register, textView_reset;

  private NavigationView navigationView;

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
  Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.activity_login, container, false);

    //// Get the menu from the NavigationView
    //Menu menu = navigationView.getMenu();

    //// Find the login/register menu item
    //MenuItem loginMenuItem = menu.findItem(R.id.nav_log_reg);

    editText_email = layout.findViewById(R.id.email);
    editText_password = layout.findViewById(R.id.password);
    buttonLog = layout.findViewById(R.id.login_btn);
    progressBar = layout.findViewById(R.id.progressbar);
    textView_register = layout.findViewById(R.id.registerNow);
    textView_reset = layout.findViewById(R.id.resetPass);
    mAuth = FirebaseAuth.getInstance();

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
        */
      }
    });

    buttonLog.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        progressBar.setVisibility(View.VISIBLE);
        String email, password;

        email = editText_email.getText().toString();
        password = editText_password.getText().toString();

        if (TextUtils.isEmpty(email)){
          Toast.makeText(getActivity(), "Enter email", Toast.LENGTH_SHORT).show();
          progressBar.setVisibility(View.GONE);
          return;
        }
        if (TextUtils.isEmpty(password)){
          Toast.makeText(getActivity(), "Enter password", Toast.LENGTH_SHORT).show();
          progressBar.setVisibility(View.GONE);
          return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                      // Sign in success, update UI with the signed-in user's information
                      //Log.d(TAG, "signInWithEmail:success");
                      FirebaseUser user = mAuth.getCurrentUser();
                      //updateUI(user);
                      Toast.makeText(getActivity(), "Login Successful.", Toast.LENGTH_SHORT).show();

                      //// User is logged in, set to "Logout"
                      //loginMenuItem.setTitle(R.string.menu_logout);
                      //loginMenuItem.setIcon(R.drawable.logout);  // Update the icon if needed

                      // Notify the MainActivity to update the navigation menu and user state
                      if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).setUser(user);  // Pass the logged-in user
                        ((MainActivity) getActivity()).updateLoginMenuItem();
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
      }
    });

    return layout;
  }
}


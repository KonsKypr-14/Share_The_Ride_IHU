package com.example.sharetheride;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

public class HomeContentFragment_KK extends Fragment {

  private static final String TEXT_ID = "text_id";

  public static HomeContentFragment_KK newInstance(@StringRes int textId) {
    HomeContentFragment_KK frag = new HomeContentFragment_KK();

    Bundle args = new Bundle();
    args.putInt(TEXT_ID, textId);
    frag.setArguments(args);

    return frag;
  }

  Button button_test;

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
  Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.home_fragment_kk, container, false);
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


    // Find the button in the fragment layout
    Button myButton = layout.findViewById(R.id.button_test);

    // Set an OnClickListener for the button
    myButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // Handle the button click
        Toast.makeText(getActivity(), "Button Clicked!", Toast.LENGTH_SHORT).show();
      }
    });

    return layout;

  }
}


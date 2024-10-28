package com.example.sharetheride;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> clickedButton = new MutableLiveData<>();

    public void setClickedButton(String button) {
        clickedButton.setValue(button);
    }

    public LiveData<String> getClickedButton() {
        return clickedButton;
    }
}

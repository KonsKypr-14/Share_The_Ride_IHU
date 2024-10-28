package com.example.sharetheride;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> clickedButton = new MutableLiveData<>();
    private final MutableLiveData<String> map_location = new MutableLiveData<>();
    private final MutableLiveData<String> map_location_name = new MutableLiveData<>();

    public void setClickedButton(String button) {
        clickedButton.setValue(button);
    }

    public LiveData<String> getClickedButton() {
        return clickedButton;
    }

    public void setLocation(String location_retrieved) {
        map_location.setValue(location_retrieved);
    }

    public LiveData<String> getLocation() {
        return map_location;
    }

    public void setLocationName(String location_name_retrieved) {
        map_location_name.setValue(location_name_retrieved);
    }

    public LiveData<String> getLocationName() {
        return map_location_name;
    }
}

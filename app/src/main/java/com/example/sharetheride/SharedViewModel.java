package com.example.sharetheride;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> clickedButton = new MutableLiveData<>();
    private final MutableLiveData<String> map_location = new MutableLiveData<>();
    private final MutableLiveData<String> map_location_name = new MutableLiveData<>();
    private final MutableLiveData<String> map_location_start = new MutableLiveData<>();
    private final MutableLiveData<String> map_location_name_start = new MutableLiveData<>();
    private final MutableLiveData<String> map_location_end = new MutableLiveData<>();
    private final MutableLiveData<String> map_location_name_end = new MutableLiveData<>();

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

    public void setLocationStart(String location_retrieved_start) {
        map_location_start.setValue(location_retrieved_start);
    }

    public LiveData<String> getLocationStart() {
        return map_location_start;
    }

    public void setLocationNameStart(String location_name_retrieved_start) {
        map_location_name_start.setValue(location_name_retrieved_start);
    }

    public LiveData<String> getLocationNameStart() {
        return map_location_name_start;
    }

    public void setLocationEnd(String location_retrieved_end) {
        map_location_end.setValue(location_retrieved_end);
    }

    public LiveData<String> getLocationEnd() {
        return map_location_end;
    }

    public void setLocationNameEnd(String location_name_retrieved_end) {
        map_location_name_end.setValue(location_name_retrieved_end);
    }

    public LiveData<String> getLocationNameEnd() {
        return map_location_name_end;
    }

}

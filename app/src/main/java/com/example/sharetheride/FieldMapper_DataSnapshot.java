package com.example.sharetheride;

import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FieldMapper_DataSnapshot {
    private static TextInputEditText editText;
    private static String fieldName;

    public FieldMapper_DataSnapshot(TextInputEditText editText, String fieldName) {
        this.editText = editText;
        this.fieldName = fieldName;
    }

    public EditText getEditText() {
        return editText;
    }

    public String getFieldName() {
        return fieldName;
    }

    // Method to set the text from a DataSnapshot
    public static void setTextFromDataSnapshot(DataSnapshot dataSnapshot) {
        Object fieldValue = dataSnapshot.child(fieldName).getValue();
        if (fieldValue != null) {
            editText.setText(fieldValue.toString());
        }
    }

    // Static method to create a list of FieldMappers dynamically
    public static List<FieldMapper> createFieldMappers(List<TextInputEditText> uiElements, List<String> fieldNames) {
        List<FieldMapper> fieldMappers = new ArrayList<>();
        if (uiElements.size() == fieldNames.size()) {
            for (int i = 0; i < uiElements.size(); i++) {
                fieldMappers.add(new FieldMapper(uiElements.get(i), fieldNames.get(i)));
            }
        } else {
            throw new IllegalArgumentException("UI elements and field names lists must have the same size.");
        }
        return fieldMappers;
    }
}
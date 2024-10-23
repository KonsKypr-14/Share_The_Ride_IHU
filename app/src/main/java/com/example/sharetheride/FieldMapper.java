package com.example.sharetheride;

import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;

public class FieldMapper {
    private TextInputEditText editText;
    private String fieldName;

    public FieldMapper(TextInputEditText editText, String fieldName) {
        this.editText = editText;
        this.fieldName = fieldName;
    }

    public EditText getEditText() {
        return editText;
    }

    public String getFieldName() {
        return fieldName;
    }

    // Method to set the text from a DocumentSnapshot
    public void setTextFromDocument(DocumentSnapshot documentSnapshot) {
        String fieldValue = documentSnapshot.getString(fieldName);
        if (fieldValue != null) {
            editText.setText(fieldValue);
        }
    }
}

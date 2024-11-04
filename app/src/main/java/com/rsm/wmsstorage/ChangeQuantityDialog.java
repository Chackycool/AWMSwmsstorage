package com.rsm.wmsstorage;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.rsm.wmsequip.R;

public class ChangeQuantityDialog extends Dialog {
    private MyAdapterHeadInventory adapter;

    private TextView textViewOldQuantity;
    private EditText editTextNewQuantity;
    private int SC;
    private Button saveButton;
    private Button exitButton;
    public void setAdapter(MyAdapterHeadInventory adapter) {
        this.adapter = adapter;
    }

    public ChangeQuantityDialog(@NonNull Context context, double oldQuantity, int SC) {
        super(context, R.style.MyDialogStyle);
        this.SC = SC; // Збережіть значення SC
        setContentView(R.layout.dialog_change_quantity);
        textViewOldQuantity = findViewById(R.id.textViewOldQuantity);
        textViewOldQuantity.setText("Стара кількість: " + oldQuantity);

        editTextNewQuantity = findViewById(R.id.editTextNewQuantity);
        editTextNewQuantity.requestFocus();

        saveButton = findViewById(R.id.save);
        saveButton.setOnClickListener(view -> onSaveButtonClick());

        exitButton = findViewById(R.id.back);
        exitButton.setOnClickListener(view -> onExitButtonClick());
    }

    private void onSaveButtonClick() {
        // Ваш код для обробки натискання кнопки "Зберегти"
        Double newQuantity = getNewQuantity();
        String sql = "UPDATE Analit SET Qty = " + newQuantity + " WHERE SC = " + SC;
        DB.updateBD(sql);
        dismiss();
    }
    private void onExitButtonClick() {
        // Ваш код для обробки натискання кнопки "Вийти"
        dismiss(); // Закриття діалогового вікна
    }

    public void setNewQuantity(int newQuantity) {
        editTextNewQuantity.setText(String.valueOf(newQuantity));
    }

    public Double getNewQuantity() {
        try {
            String quantityStr = editTextNewQuantity.getText().toString();
            return quantityStr.isEmpty() ? 0 : Double.parseDouble(quantityStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0; // Повернути значення за замовчуванням у випадку помилки
        }
    }

}


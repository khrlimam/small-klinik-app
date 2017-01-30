package com.klinik.dev.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

/**
 * Created by khairulimam on 29/01/17.
 */
public class OnlyNumber implements ChangeListener<Number> {
    private final TextField textField;

    public OnlyNumber(TextField textField) {
        this.textField = textField;
    }
    @Override
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        char ch = textField.getText().charAt(oldValue.intValue());
        if (!(ch >= '0' && ch <= '9')) {
            textField.setText(textField.getText().substring(0, textField.getText().length() - 1));
        }
    }
}

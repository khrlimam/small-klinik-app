package com.klinik.dev.customui;

import com.klinik.dev.contract.OnOkFormContract;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import lombok.Data;

/**
 * Created by khairulimam on 02/02/17.
 */
@Data
public class RowSelectChangeListener implements ChangeListener<Number> {
    private OnOkFormContract onOkFormContract;
    private ObservableList data;

    public RowSelectChangeListener(OnOkFormContract onOkFormContract, ObservableList data) {
        this.onOkFormContract = onOkFormContract;
        this.data = data;
    }

    @Override
    public void changed(ObservableValue<? extends Number> ov,
                        Number oldVal, Number newVal) {

        int ix = newVal.intValue();

        if ((ix < 0) || (ix >= data.size())) {

            return; // invalid data
        }

        if (onOkFormContract != null)
            onOkFormContract.onPositive(data.get(ix));

    }
}

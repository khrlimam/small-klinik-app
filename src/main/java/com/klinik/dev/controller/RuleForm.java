package com.klinik.dev.controller;

import com.klinik.dev.Log;
import com.klinik.dev.bussiness.BRule;
import com.klinik.dev.contract.OnOkFormContract;
import com.klinik.dev.db.model.Rule;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import lombok.Data;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by khairulimam on 29/01/17.
 */
@Data
public class RuleForm implements Initializable {

    private OnOkFormContract onOkFormContract;
    private Rule rule = new Rule();

    @FXML
    TextField tfNamaJenisRule, tfJarakHari;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tfJarakHari.lengthProperty().addListener(new OnlyNumber(tfJarakHari));
    }

    @FXML
    private void onOk() {
        if (onOkFormContract != null) {
            onOkFormContract.onPositiveButtonClicked(getRule());
            return;
        }
        Log.w(getClass(), "Contract ain't implemented");
    }

    public Rule getRule() {
        BRule bRule = new BRule();
        bRule.setIntervalDays(Integer.parseInt(tfJarakHari.getText()));
        bRule.setRuleName(tfNamaJenisRule.getText());
        rule.setRule(bRule);
        return rule;
    }

}

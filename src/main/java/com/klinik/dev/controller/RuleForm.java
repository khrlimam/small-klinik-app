package com.klinik.dev.controller;

import com.klinik.dev.Log;
import com.klinik.dev.bussiness.BRule;
import com.klinik.dev.contract.OnOkFormContract;
import com.klinik.dev.customui.NumberTextField;
import com.klinik.dev.db.model.Rule;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import lombok.Data;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by khairulimam on 29/01/17.
 * <p>
 * <b>This class only acting as controller for ruleform.fxml</b>
 * all implementations done through interface/contract
 */
@Data
public class RuleForm implements Initializable {

    private OnOkFormContract onOkFormContract;

    @FXML
    TextField tfNamaJenisRule;
    @FXML
    NumberTextField tfJarakHari;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void onOkCreate() {
        if (onOkFormContract != null) {
            onOkFormContract.onPositiveButtonClicked(getRule());
            return;
        }
        Log.w(getClass(), "Contract ain't implemented");
    }

    public Rule getRule() {
        Rule rule = new Rule();
        BRule bRule = new BRule();
        bRule.setIntervalDays(Integer.parseInt(tfJarakHari.getText()));
        bRule.setRuleName(tfNamaJenisRule.getText());
        rule.setRule(bRule);
        return rule;
    }

}

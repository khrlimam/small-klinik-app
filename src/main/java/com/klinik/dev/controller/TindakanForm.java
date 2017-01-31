package com.klinik.dev.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.klinik.dev.Log;
import com.klinik.dev.bussiness.BRule;
import com.klinik.dev.bussiness.BTindakan;
import com.klinik.dev.contract.OnOkFormContract;
import com.klinik.dev.contract.PopulateFxWithThis;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.Rule;
import com.klinik.dev.db.model.Tindakan;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import lombok.Data;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by khairulimam on 29/01/17.
 */
@Data
public class TindakanForm implements Initializable {

    private OnOkFormContract onOkFormContract;
    private Tindakan tindakan = new Tindakan();
    private PopulateFxWithThis populateFxWithThis;
    private PopulateFxWithThis populateCbTindakanInPatientForm;
    private List<BRule> bRules;

//    bullshit with oo rule
    private PatientForm patientFormController;

    private Dao<Rule, Integer> rules = DaoManager.createDao(DB.getDB(), Rule.class);
    private List<Rule> allRules = rules.queryForAll();

    @FXML
    private TextField tfNamaTindakan;
    @FXML
    private ListView lvRules;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bRules = getbRules();
        initLvRulesItems();
        this.lvRules.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void addRuleToLvRules(BRule bRule) {
        String displayFormat = String.format("%s (%d hari)", bRule.getRuleName(), bRule.getIntervalDays());
        this.lvRules.getItems().add(displayFormat);
    }

    private void initLvRulesItems() {
        for (BRule rule : this.bRules) {
            addRuleToLvRules(rule);
        }
    }

    public void addItemTobRules(Object object) {
        bRules.add((BRule) object);
        notifyLvRule(object);
    }

    public void notifyLvRule(Object object) {
        addRuleToLvRules((BRule) object);
    }

    private List<BRule> getbRules() {
        List<BRule> bRules = new ArrayList<>();
        for (Rule rule : this.allRules) {
            bRules.add(rule.getRule());
        }
        return bRules;
    }

    public TindakanForm() throws SQLException {
    }

    @FXML
    private void onOk() {
        Tindakan tindakan = getTindakan();
        BTindakan bTindakan = tindakan.getTindakan();
        if (onOkFormContract != null) {
            onOkFormContract.onPositiveButtonClicked(tindakan);
            if (populateFxWithThis != null)
                populateFxWithThis.populate(bTindakan);
            if (patientFormController != null)
                patientFormController.populateTindakanData(tindakan);
            return;
        }
        Log.w(getClass(), "Contract ain't implemented yet");
    }

    public Tindakan getTindakan() {
        BTindakan bTindakan = new BTindakan();
        bTindakan.setNamaTindakan(tfNamaTindakan.getText());
        bTindakan.setBRules(getSelectedRulesFromLvRules());
        tindakan.setTindakan(bTindakan);
        return tindakan;
    }

    private List<BRule> getSelectedRulesFromLvRules() {
        List<BRule> bRules = new ArrayList<>();
        List<Integer> selectedIndices = lvRules.getSelectionModel().getSelectedIndices();
        for (Integer i : selectedIndices) {
            bRules.add(this.bRules.get(i));
        }
        return bRules;
    }
}

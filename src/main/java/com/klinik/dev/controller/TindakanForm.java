package com.klinik.dev.controller;

import com.google.common.eventbus.Subscribe;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.klinik.dev.Log;
import com.klinik.dev.bussiness.BRule;
import com.klinik.dev.bussiness.BTindakan;
import com.klinik.dev.contract.OnOkFormContract;
import com.klinik.dev.datastructure.SearchableCollections;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.Rule;
import com.klinik.dev.db.model.Tindakan;
import com.klinik.dev.events.EventBus;
import com.klinik.dev.events.RuleEvent;
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
    private List<BRule> bRules;

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
        EventBus.getInstance().register(this);
    }

    private String formatBRuleToString(BRule bRule) {
        return String.format("%s (%d hari)", bRule.getRuleName(), bRule.getIntervalDays());
    }

    private void addRulesItem(BRule bRule) {
        this.bRules.add(bRule);
        this.lvRules.getItems().add(formatBRuleToString(bRule));
    }

    private void updateRulesItem(Rule rule) {
        int indexOfUpdatedRule = SearchableCollections.binarySearch(allRules, rule);
        if (indexOfUpdatedRule > -1) {
            this.bRules.set(indexOfUpdatedRule, rule.getRule());
            this.lvRules.getItems().set(indexOfUpdatedRule, formatBRuleToString(rule.getRule()));
        }
    }

    private void deleteRulesItem(Rule rule) {
        int index = SearchableCollections.binarySearch(allRules, rule);
        if (index > -1) {
            this.bRules.remove(index);
            this.lvRules.getItems().remove(index);
        }
    }

    private void initLvRulesItems() {
        for (BRule rule : this.bRules) {
            this.lvRules.getItems().add(formatBRuleToString(rule));
        }
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
    private void onOkCreate() {
        Tindakan tindakan = getTindakan();
        if (onOkFormContract != null) {
            onOkFormContract.onPositiveButtonClicked(tindakan);
            return;
        }
        Log.w(getClass(), "Contract ain't implemented yet");
    }

    public Tindakan getTindakan() {
        Tindakan tindakan = new Tindakan();
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

    @Subscribe
    public void onRule(RuleEvent ruleEvent) {
        switch (ruleEvent.getOPERATION_TYPE()) {
            case CREATE:
                addRulesItem(ruleEvent.getRule().getRule());
                break;
            case UPDATE:
                updateRulesItem(ruleEvent.getRule());
                break;
            case DELETE:
                deleteRulesItem(ruleEvent.getRule());
                break;
        }
    }
}

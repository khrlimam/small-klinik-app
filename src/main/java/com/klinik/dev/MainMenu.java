package com.klinik.dev;

/**
 * Created by khairulimam on 26/01/17.
 */

import com.klinik.dev.controller.Main;
import com.klinik.dev.controller.PatientFormDialog;
import com.klinik.dev.controller.TindakanAndRuleFormDialog;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.SQLException;

public class MainMenu extends Application {

    private static final String TINDAKAN_AND_RULE_DIALOG = "/uis/tindakanruleformdialog.fxml";
    private static final String PATIENT_DIALOG = "/uis/patientdialog.fxml";
    private static final String MAIN_UI = "/uis/main.fxml";
    private PatientFormDialog patientFormDialogController;
    private TindakanAndRuleFormDialog tindakanAndRuleFormDialogController;

    private Main main;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException, SQLException {
        URL location = getClass().getResource(MAIN_UI);
        FXMLLoader loader = createFXMLLoader(location);
        Parent root = loader.load(location.openStream());
        primaryStage.setTitle(Util.APP_NAME);
        primaryStage.setScene(new Scene(root, 800, 600));
        this.main = loader.getController();
        main.setPasienStage(makeDialogStageFromLoaderAndOtherUnrospinsbleThings(PATIENT_DIALOG, "Tambah pasien", primaryStage));
        main.setPatientFormDialog(patientFormDialogController);
        main.setTindakanDanRuleStage(makeDialogStageFromLoaderAndOtherUnrospinsbleThings(TINDAKAN_AND_RULE_DIALOG, "Tambah tindakan dan rule", primaryStage));
        tindakanAndRuleFormDialogController.getTindakanFormController()
                .setPatientFormController(patientFormDialogController.getPatientFormController());
//
//                .setPopulateCbTindakanInPatientForm(patientFormDialogController
//                    .getPatientFormController()
//                    .getPopulateCbTindakanOnTindakanCreated());
        main.getPatientFormDialog().getPatientFormController().setPopulateFxWithThis(main);
        primaryStage.show();
    }

    private FXMLLoader createFXMLLoader(URL location) {
        return new FXMLLoader(location, null, new JavaFXBuilderFactory(), null, Charset.forName(FXMLLoader.DEFAULT_CHARSET_NAME));
    }

    private Stage makeDialogStageFromLoaderAndOtherUnrospinsbleThings(String fxml, String title, Stage owner) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            AnchorPane pane = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(owner);
            Scene scene = new Scene(pane);
            stage.setScene(scene);
            if (fxml.equals(PATIENT_DIALOG)) {
                //bullshit with the oo rules! ignore it for critical purpose!
                Log.w(getClass(), "Setting patienformdialogcontroller");
                patientFormDialogController = loader.getController();
            }
            if (fxml.equals(TINDAKAN_AND_RULE_DIALOG)) {
                //bullshit with the oo rules! ignore it for critical purpose!
                tindakanAndRuleFormDialogController = loader.getController();
            }
            return stage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

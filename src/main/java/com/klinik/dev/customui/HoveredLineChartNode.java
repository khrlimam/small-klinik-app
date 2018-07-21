package com.klinik.dev.customui;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Created by khairulimam on 05/02/17.
 */
public class HoveredLineChartNode extends StackPane {
  public HoveredLineChartNode(String value) {
    setPrefSize(10, 10);
    final Label label = createDataThresholdLabel(value);

    setOnMouseEntered(mouseEvent -> {
      getChildren().setAll(label);
      setCursor(Cursor.CROSSHAIR);
      toFront();
    });

    setOnMouseExited(mouseEvent -> getChildren().clear());

    setMargin(label, new Insets(70, 0, 0, 0));
  }

  private Label createDataThresholdLabel(String value) {
    final Label label = new Label(value);
    label.getStyleClass().addAll("default-color0", "chart-line-symbol", "chart-series-line");
    label.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-border-width: 1px");
    label.setTextFill(Color.DARKGRAY);
    label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
    return label;
  }
}
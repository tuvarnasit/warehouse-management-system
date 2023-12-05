package bg.tuvarna.sit.wms.validation;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import lombok.Getter;

public class ValidatingComboBox<T> extends ComboBox<T> {

  @Getter
  private BooleanProperty isValid = new SimpleBooleanProperty();

  public void setUp(String tooltipMessage) {

      valueProperty().addListener((o, oldValue, newValue) -> {
        isValid.set(newValue != null);
        setStyle((isValid.get()) ? "" : "-fx-border-color: red;-fx-focus-color:red;-fx-faint-focus-color: transparent;");
        tooltipProperty().set((isValid.get()) ? null : new Tooltip(tooltipMessage));
      });
  }
}

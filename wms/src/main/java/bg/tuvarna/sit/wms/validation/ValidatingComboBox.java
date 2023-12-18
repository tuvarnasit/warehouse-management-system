package bg.tuvarna.sit.wms.validation;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import lombok.Getter;

/**
 * This class extends the ComboBox class in JavaFX and adds validation functionality.
 *
 * @param <T> the type of the values in the combo box
 */
public class ValidatingComboBox<T> extends ComboBox<T> {

  @Getter
  private BooleanProperty isValid = new SimpleBooleanProperty();

  /**
   * This method adds an handler function which is triggered everytime the value of the combo box changes.
   * It sets the isValid field to false, changes the style of the combobox and the tooltip message when the value is invalid.
   * Otherwise the isValid field is set to true and the style is set to default
   *
   * @param tooltipMessage the tooltip message to display when the value is invalid
   */
  public void setUp(String tooltipMessage) {

      valueProperty().addListener((o, oldValue, newValue) -> {
        isValid.set(newValue != null);
        setStyle((isValid.get()) ? "" : "-fx-border-color: red;-fx-focus-color:red;-fx-faint-focus-color: transparent;");
        tooltipProperty().set((isValid.get()) ? null : new Tooltip(tooltipMessage));
      });
  }
}

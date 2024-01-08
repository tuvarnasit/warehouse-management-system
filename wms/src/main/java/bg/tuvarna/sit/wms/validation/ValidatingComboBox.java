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
  private Tooltip tooltip;

  /**
   * Adds a listener which validates the combobox, when the value of it changes
   */
  public ValidatingComboBox() {

    valueProperty().addListener((o, oldValue, newValue) -> validate(newValue));
  }

  /**
   * Sets up the tooltip with a tooltip message
   *
   * @param tooltipMessage the tooltip message to display when the value is invalid
   */
  public void setUp(String tooltipMessage) {
    tooltip = new Tooltip(tooltipMessage);
  }

  /**
   * This method validates the current combobox value. If there is no selected item
   * the isValid field is set to false, the style of the combobox is changed and a tooltip is shown on hover.
   * Otherwise the isValid field is set to true, the style is set to default and the tooltip is removed.
   *
   * @param value the current value of the combobox
   */
  private void validate(T value) {

    isValid.set(value != null);
    setStyle((isValid.get()) ? "" : "-fx-border-color: red;-fx-focus-color:red;-fx-faint-focus-color: transparent;");
    tooltipProperty().set((isValid.get()) ? null : tooltip);
  }
}

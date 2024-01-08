package bg.tuvarna.sit.wms.validation;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import lombok.Getter;

import java.util.function.Predicate;

/**
 * This class extends the TextField class in JavaFX and adds validation functionality.
 * It uses a Predicate function to validate the text entered by the user.
 */
public class ValidatingTextField extends TextField {

  @Getter
  private Predicate<String> validator;
  @Getter
  private BooleanProperty isValid = new SimpleBooleanProperty();
  private Tooltip tooltip;

  /**
   * Adds a listener which validates the text field, when the text changes
   */
  public ValidatingTextField() {

    validator = value -> true;
    textProperty().addListener((o, oldValue, newValue) -> validate(newValue));
  }

  /**
   * This method validates the current text of the text field. If the validator finds the text to be invalid
   * the isValid field is set to false, the style of the text field is changed and a tooltip is shown on hover.
   * Otherwise the isValid field is set to true, the style is set to default and the tooltip is removed.
   *
   * @param textValue the current text value
   */
  private void validate(String textValue) {

    if (textValue.trim().isEmpty() || !validator.test(textValue.trim())) {
      isValid.set(false);
      setStyle("-fx-text-box-border: red;-fx-focus-color:red;-fx-faint-focus-color: transparent;");
      tooltipProperty().set(tooltip);
      return;
    }
    isValid.set(true);
    setStyle("");
    tooltipProperty().set(null);
  }

  /**
   * Sets up the validator and tooltip for the text field.
   *
   * @param validator the validation function to use
   * @param tooltipMessage the tooltip message to display when the text is not valid
   */
  public void setUp(Predicate<String> validator, String tooltipMessage) {

    this.validator = validator;
    this.tooltip = new Tooltip(tooltipMessage);
  }

}

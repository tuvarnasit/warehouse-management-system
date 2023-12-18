package bg.tuvarna.sit.wms.validation;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Predicate;

/**
 * This class extends the TextField class in JavaFX and adds validation functionality.
 * It uses a Predicate function to validate the text entered by the user.
 */
@Getter
public class ValidatingTextField extends TextField {

  @Setter
  private Predicate<String> validationFunction;
  private BooleanProperty isValid = new SimpleBooleanProperty();

  /**
   * Sets up the validation function and tooltip message for the text field.
   * This method adds a listener to the text property of the text field, which validates the text whenever it changes.
   * If the text is not valid, the style of the text field is set to indicate the error, and the tooltip is set to the given tooltip message.
   * Otherwise the styles are set to default and the tooltip is removed.
   *
   * @param validationFunction the validation function to use
   * @param tooltipMessage the tooltip message to display when the text is not valid
   */
  public void setUp(Predicate<String> validationFunction, String tooltipMessage) {

    if (getValidationFunction() == null) {
      setValidationFunction(validationFunction);

      textProperty().addListener((o, oldValue, newValue) -> {
        isValid.set(!newValue.trim().isEmpty() && validationFunction.test(newValue.trim()));
        setStyle((isValid.get()) ? "" : "-fx-text-box-border: red;-fx-focus-color:red;-fx-faint-focus-color: transparent;");
        tooltipProperty().set((isValid.get()) ? null : new Tooltip(tooltipMessage));
      });
    }
    setValidationFunction(validationFunction);
  }

}

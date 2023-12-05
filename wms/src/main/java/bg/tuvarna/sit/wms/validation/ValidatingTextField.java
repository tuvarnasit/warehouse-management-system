package bg.tuvarna.sit.wms.validation;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Predicate;

@Getter
public class ValidatingTextField extends TextField {

  @Setter
  private Predicate<String> validationFunction;
  private BooleanProperty isValid = new SimpleBooleanProperty();

  public void setUp(Predicate<String> validationFunction, String tooltipMessage) {

    if (getValidationFunction() == null) {
      setValidationFunction(validationFunction);

      textProperty().addListener((o, oldValue, newValue) -> {
        isValid.set(!newValue.trim().isEmpty() && validationFunction.test(newValue.trim()));
        setStyle((isValid.get()) ? "" : "-fx-border-color: red;-fx-focus-color:red;-fx-faint-focus-color: transparent;");
        tooltipProperty().set((isValid.get()) ? null : new Tooltip(tooltipMessage));
      });
    }
    setValidationFunction(validationFunction);
  }

}

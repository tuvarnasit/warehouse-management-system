package bg.tuvarna.sit.wms.util;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;

import java.time.LocalDate;

/**
 * Utility class for validating JavaFX UI components.
 */
public class ValidationUtils {

  /**
   * Validates the input of a given text field based on provided validation criteria.
   * The method checks if the text field is not empty and if it matches the specified regex pattern.
   *
   * @param field              The text input control to validate.
   * @param validationCriteria The regex pattern the input must match.
   * @return true if the field is valid (not empty and matches the regex), false otherwise.
   */
  public static boolean validateField(TextInputControl field, String validationCriteria) {

    boolean isValid = !field.getText().trim().isEmpty() && field.getText().trim().matches(validationCriteria);
    field.setStyle(isValid ? "" : "-fx-border-color: red;");
    return isValid;
  }

  /**
   * Validates if a value has been selected in the given combo box.
   * The method checks if the combo box has a selected value.
   *
   * @param comboBox The combo box to validate.
   * @return true if a value is selected, false otherwise.
   */
  public static boolean validateComboBox(ComboBox<?> comboBox) {

    boolean isValid = comboBox.getValue() != null;
    comboBox.setStyle(isValid ? "" : "-fx-border-color: red;");
    return isValid;
  }

  /**
   * Validates the value of two date pickers.
   * It checks if the date of the first is before the date of the second date picker,
   * and if both are after the current date.
   *
   * @param startDatePicker the first date picker
   * @param endDatePicker the second date picker
   * @return true if the date values of the date pickers are valid; false otherwise
   */
  public static boolean validateDatePickers(DatePicker startDatePicker, DatePicker endDatePicker) {

    boolean startDateNotNull = startDatePicker.getValue() != null;
    boolean endDateNotNull = endDatePicker.getValue() != null;
    boolean startIsBeforeEnd = startDatePicker.getValue().isBefore(endDatePicker.getValue());
    boolean startIsAfterNow = startDatePicker.getValue().isAfter(LocalDate.now());
    boolean endIsAfterNow = endDatePicker.getValue().isAfter(LocalDate.now());

    return startDateNotNull && endDateNotNull && startIsBeforeEnd
        && startIsAfterNow && endIsAfterNow;
  }

  /**
   * Binds the managed property of a label to its visibility property.
   * This ensures that the label does not reserve layout space when it is not visible.
   *
   * @param label The label to bind.
   */
  public static void bindManagedToVisible(Label label) {

    label.managedProperty().bind(label.visibleProperty());
  }

  /**
   * Updates the visibility and text of an error label based on a boolean flag.
   * Sets the provided message and shows the label if the flag is true, hides the label otherwise.
   *
   * @param label   The label to update.
   * @param message The error message to display on the label.
   * @param show    A flag indicating whether the error message should be shown or not.
   */
  public static void showErrorLabel(Label label, String message, boolean show) {

    label.setText(message);
    label.setVisible(show);
  }
}

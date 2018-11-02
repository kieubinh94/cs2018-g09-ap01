package cs2018.ap.api.validation;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotBlankValidator implements ConstraintValidator<NotBlank, String> {
  @Override
  public void initialize(final NotBlank constraintAnnotation) {}

  @Override
  public boolean isValid(final String value, final ConstraintValidatorContext context) {
    return StringUtils.isNotBlank(value);
  }
}

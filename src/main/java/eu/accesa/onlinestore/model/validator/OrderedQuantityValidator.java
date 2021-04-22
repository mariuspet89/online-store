package eu.accesa.onlinestore.model.validator;

import eu.accesa.onlinestore.model.annotation.OrderedQuantity;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;

public class OrderedQuantityValidator
        implements ConstraintValidator<OrderedQuantity, Map<String, Integer>> {

    @Override
    public void initialize(OrderedQuantity constraintAnnotation) {
    }

    @Override
    public boolean isValid(Map<String, Integer> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // a separate @NotNull annotation should validate this case
        }

        return value.values().stream()
                .noneMatch(quantity -> quantity == null || quantity < 1);
    }
}

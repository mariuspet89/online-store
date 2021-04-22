package eu.accesa.onlinestore.model.annotation;

import eu.accesa.onlinestore.model.validator.OrderedQuantityValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OrderedQuantityValidator.class)
public @interface OrderedQuantity {

    String message() default "The ordered quantity must be greater than 0!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

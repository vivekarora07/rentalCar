package com.rental.util;

import com.rental.model.Customer;
import com.rental.validator.GenericValidation;
import com.rental.validator.Validation;

public class ValidatorUtil {
	public static final Validation<String> notNullString = GenericValidation.from(s -> s != null);
    public static final Validation<Customer> notNullCustomer = GenericValidation.from(s -> s != null);
    public static final Validation<String> notEmptyString = GenericValidation.from(s -> !s.isEmpty());
    public static final Validation<String> matchesIntegerNumberPattern = GenericValidation.from(s -> s.matches("\\d+"));


}

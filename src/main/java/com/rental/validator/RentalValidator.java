package com.rental.validator;

import com.rental.util.ValidatorUtil;

public class RentalValidator {


	public static void validateStringIsIntegerNumber(String type) throws InvalidRequestException {
		StringBuilder errorFields = new StringBuilder();
		errorFields.append(ValidatorUtil.notEmptyString.and(ValidatorUtil.matchesIntegerNumberPattern).test(type)
				.getFieldNameIfInvalid(type).orElse(""));
		String errors = errorFields.toString();
		if (!errors.isEmpty()) {
			throw new InvalidRequestException(ErrorCode.ERR06.getValue() + " " + errors);
		}
	}
}

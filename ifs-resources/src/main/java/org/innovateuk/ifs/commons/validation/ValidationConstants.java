package org.innovateuk.ifs.commons.validation;

public class ValidationConstants {

	public static final String EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX = "^[a-zA-Z0-9._%+-^[^{}|]*$]+@[a-zA-Z0-9.-^[^{}|]*$]+\\.[a-zA-Z^[^0-9{}|]*$]{2,}$";

	public static final int MAX_POST_CODE_LENGTH = 10;

	private ValidationConstants() {}
}

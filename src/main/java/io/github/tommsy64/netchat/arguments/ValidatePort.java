package io.github.tommsy64.netchat.arguments;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class ValidatePort implements IParameterValidator {
    public void validate(String name, String value) throws ParameterException {
        int n = Integer.parseInt(value);
        if (n < 0 || n > 65535) {
            throw new ParameterException("Parameter " + name + " is not a valid port");
        }
    }
}

package se.curity.oauth.core.util;

import java.util.List;

/**
 * Validatable object.
 * <p>
 * A Validatable object must be immutable and know if it contains any errors at construction time.
 */
public abstract class Validatable
{

    private final List<String> errors;

    protected Validatable(List<String> errors)
    {
        this.errors = errors;
    }

    public boolean isValid()
    {
        return errors.isEmpty();
    }

    public List<String> getValidationErrors()
    {
        return errors;
    }

}

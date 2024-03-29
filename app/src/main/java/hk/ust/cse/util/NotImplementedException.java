package hk.ust.cse.util;

/**
 * An exception indicating that the method is not yet implemented.
 */
public class NotImplementedException extends RuntimeException {

    /**
     * Creates a new instance of {@link NotImplementedException}.
     */
    public NotImplementedException() {
        super("This method is not implemented");
    }
}

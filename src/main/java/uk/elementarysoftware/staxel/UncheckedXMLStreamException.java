package uk.elementarysoftware.staxel;

import java.util.Objects;

import javax.xml.stream.XMLStreamException;

/**
 * Wraps an {@link XMLStreamException} with an unchecked exception.
 * <p>
 * The base exception for unexpected processing errors.  This Exception
 * class is used to report well-formedness errors as well as unexpected
 * processing conditions.
 * </p>
 */
public class UncheckedXMLStreamException extends RuntimeException {
    private static final long serialVersionUID = 1134305061645241069L;
    
    public UncheckedXMLStreamException(String message, XMLStreamException cause) {
        super(message, Objects.requireNonNull(cause));
    }

    public UncheckedXMLStreamException(XMLStreamException cause) {
        super(Objects.requireNonNull(cause));
    }

    public UncheckedXMLStreamException(String message) {
        super(message);
    }
}

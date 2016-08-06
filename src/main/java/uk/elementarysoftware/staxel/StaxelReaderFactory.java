package uk.elementarysoftware.staxel;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

/**
 * Entry point to Staxel parsing library. Use this class to create {@link StaxelReader} from various input sources.
 * Factory can be reused to create multiple readers.
 */
public class StaxelReaderFactory {
    
    private final XMLInputFactory factory;
    
    public StaxelReaderFactory() {
        this(XMLInputFactory.newInstance());
    }
    
    public StaxelReaderFactory(XMLInputFactory factory) {
        this.factory = factory;
    }

    /**
     * Create a new StaxelReader from a InputStream
     * @param is {@link InputStream} to read the XML data from
     * @throws UncheckedXMLStreamException
     */
    public StaxelReader fromStream(InputStream is) {
        return fromReader(new InputStreamReader(is));
    }
    
    /**
     * Create a new StaxelReader from a InputStream
     * @param is InputStream to read the XML data from
     * @param charsetName The name of a supported {@link java.nio.charset.Charset charset}

     * @throws UncheckedXMLStreamException
     */
    public StaxelReader fromStream(InputStream is, String charsetName) throws UnsupportedEncodingException {
        return fromReader(new InputStreamReader(is, charsetName));
    }
    
    /**
     * Create a new StaxelReader from a Reader
     * @param reader {@link Reader} to read the XML data from
     * @throws UncheckedXMLStreamException
     */
    public StaxelReader fromReader(Reader reader) {
        try {
            return new StaxelReader(factory.createXMLEventReader(reader));
        } catch (XMLStreamException e) {
            throw new UncheckedXMLStreamException(e);
        }
    }
}
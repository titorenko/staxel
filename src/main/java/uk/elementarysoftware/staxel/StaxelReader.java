package uk.elementarysoftware.staxel;

import java.util.Arrays;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.EventReaderDelegate;

/**
 * <p>
 * Decorator of {@link XMLEventReader} that adds support for {@link Cursor} creation.
 * </p>
 * 
 * {@link StaxelReaderFactory} can be used to create instance of this class or 
 * it can be directly instantiated. 
 */
public class StaxelReader extends EventReaderDelegate implements XMLEventReader, AutoCloseable {
    
    private final XMLEventReader reader;

    public StaxelReader(XMLEventReader reader) {
        super(reader);
        this.reader = reader;
    }
    
    /**
     * Creates cursor starting from element that has given suffix path.
     * Cursor is created from next {@link StartElement} if suffix is empty.
     * 
     * @param suffix path suffix of first cursor element, underlying reader will be advanced to that element 
     * 
     * @return {@link Cursor}
     * @throws UncheckedXMLStreamException
     */
    public Cursor getCursor(String... suffix) {
        String name  = advanceToNextStartElement(suffix);
        if (name == null) throw new UncheckedXMLStreamException("Start element with suffix "+Arrays.toString(suffix)+" not found");
        return new Cursor(this, name);
    }
    
    @Override
    public void close() {
        try {
            super.close();
        } catch (XMLStreamException e) {
            throw new UncheckedXMLStreamException(e);
        }
    }
    
    String getText() throws XMLStreamException {
        XMLEvent e = reader.peek();
        switch (e.getEventType()) {
        case XMLStreamReader.CHARACTERS:
        case XMLStreamReader.CDATA:
            return reader.nextEvent().asCharacters().getData();
        }
        return "";
    }
    
    private String advanceToNextStartElement(String[] suffix) {
        try {
            Path path = new Path();
            while (hasNext()) {      
                XMLEvent event = peek();
                if (event.isStartElement()) {
                    StartElement se = event.asStartElement();
                    path.push(se);
                    if (path.endsWith(suffix)) return getLocalName(se);
                } else if (event.isEndElement()) {
                    path.pop();
                }
                nextEvent();
            }
            return null;
        } catch (XMLStreamException e) {
            throw new UncheckedXMLStreamException(e);
        }
    }

    private String getLocalName(StartElement se) {
        return se.getName().getLocalPart();
    }
}
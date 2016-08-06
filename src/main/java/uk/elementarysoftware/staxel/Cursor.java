package uk.elementarysoftware.staxel;

import java.util.Iterator;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * <p>
 * Stax parser cursor that goes over nested XML segment. 
 * Once end of element from which parsing has started is seen, this cursor will stop 
 * emitting {@link XMLElement}s .
 * </p>
 * Most common usage for this class is to traverse it with foreach loop reacting on {@link XMLElement} name or path suffix. 
 * Cursor can only be traversed once.
 */
public class Cursor implements Iterable<XMLElement>, Iterator<XMLElement> {

    private final StaxelReader r;
    private final Path localPath = new Path();
    
    private XMLElement el;
    private boolean nextCalled = false;

    Cursor(StaxelReader r, String rootName) {
        this.r = r;
        this.el = advance();
    }
    
    Cursor(XMLElement el) {
        this.r = el.r;
        this.el = new XMLElement(el.r, new Path(el.getName()), el.se);
        this.localPath.push(el.getName());
    }

    public boolean hasNext() {
        if (nextCalled) {
            el = advance();
            nextCalled = false;
        }
        return el != null;
    }
    
    public XMLElement next() {
        if (!nextCalled) {
            nextCalled = true;
            return el;
        } else {
            if (hasNext()) return next();
            throw new IllegalStateException("End reached");
        }
    }
    
    private XMLElement advance() {
        try {
            while (this.r.hasNext()) {
                XMLEvent e = this.r.nextEvent();
                switch(e.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    StartElement se = e.asStartElement();
                    localPath.push(se);
                    return new XMLElement(this.r, localPath, se);
                case XMLStreamConstants.END_ELEMENT:
                    localPath.popOpt();
                    break;
                }
                if (isEndReached()) break;
            }
            return null;
        } catch (XMLStreamException e) {
            throw new UncheckedXMLStreamException(e);
        }
    }

    private boolean isEndReached() throws XMLStreamException {
        XMLEvent e = this.r.peek();
        if (e == null) return false;
        if (e.getEventType() == XMLEvent.END_DOCUMENT) return true;
        if (localPath.size() == 0) return true;
        return false;
    }

    @Override
    public Iterator<XMLElement> iterator() {
        return this;
    }
}
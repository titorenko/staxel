package uk.elementarysoftware.staxel;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

/**
 * Represents XML element under {@link Cursor}. Provides methods to access element path relative to start of cursor,
 * element text and element attributes. 
 */
public class XMLElement {

    final Path path;
    final StaxelReader r;
    final StartElement se;

    XMLElement(StaxelReader r, Path path, StartElement se) {
        this.path = path;
        this.r = r;
        this.se = se;
    }
    
    /**
     * Path from cursor root to this element. Path contains is composed from element names. 
     * @return path to this element
     */
    public Collection<String> getPath() {
        return path.fullPath();
    }
    
    /**
     * Element name that corresponds to tag name. Element name is always last item in getPath().
     * @return element name
     */
    public String getName() {
        assert se.getName().getLocalPart().equals(path.last()); 
        return path.last();
    }
    
    /**
     * Parse xml inside this element using supplied function that should map from child cursor to T.
     * @param childCursorToTParser function mapping from child cursor to T
     * @return parsing result as returned by childCursorToTParser
     */
    public <T> T parseWithChildCursor(Function<Cursor, T> childCursorToTParser) {
        Cursor cursor = new Cursor(this);
        T result = childCursorToTParser.apply(cursor);
        while(cursor.hasNext()) cursor.next();//exhaust everything if not already exhausted
        path.pop();//since fully exhausted by child cursor, this cursor path will not contain last element
        return result;
    }

    /**
     * Parse xml inside this element without returning result, relying on side effects.
     * @param childCursorConsumer child cursor consumer
     */
    public void parseWithChildCursor(Consumer<Cursor> childCursorConsumer) {
        parseWithChildCursor(cur -> {childCursorConsumer.accept(cur); return null;});
    }
    
    /**
     * Checks if path of this element from cursor root ends in the given suffix
     * @param suffix
     * @return true if path ends with given suffix
     */
    public boolean pathEndsWith(String... suffix) {
        return path.endsWith(suffix);
    }
    
    /**
     * Trimmed text inside this element.
     * Empty string is returned if this element has no inner text.
     * @return this element's text
     */
    public String getText() {
        return getRawText().trim();
    }
    
    /**
     * Text inside this element, not trimmed. 
     * Empty string is returned if this element has no inner text.
     * @return this element's text
     */
    public String getRawText() {
        try {
            return r.getText();
        } catch (XMLStreamException e) {
            throw new UncheckedXMLStreamException(e);
        }
    }
 
    /**
     * Return attribute value of this element   
     * @param name attribute name
     * @return attribute value or null if attribute is not present
     */
    public String getAttribute(String name) {
        Attribute atr = se.getAttributeByName(new QName(name));
        return atr == null ? null : atr.getValue();
    }
    
    @Override
    public String toString() {
        return "XMLElement@/"+path;
    }
}
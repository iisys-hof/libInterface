package de.iisys.libinterface.parser.node;

import org.parboiled.errors.ParsingException;

/**
 * Class to process the content nodes.
 */
public class ContentNode extends ParserNode {

    private String content;

    
    /**
     * Initializes {@link #content} with the given content string.
     * @param content the content
     */
    public ContentNode(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }

    /**
     * Gets the length of the content string.
     * @return 
     */
    @Override
    public int getLength() {
        return content.length();
    }

    /**
     * Return {@link #getContent() }.
     * @param object the object
     * @return Return {@link #getContent() }
     */
    @Override
    public String serialize(Object object) {
        return getContent();
    }

    /**
     * Deserializes with the given object and message. Returns the object
     * if the {@link #getContent() } equals with the real content.
     * @param object the object
     * @param message the message
     * @return deserialized object
     */
    @Override
    public Object deserialize(Object object, String message) {
        int position = getPosition();
        try {
            String realContent = message.substring(position, position + getLength());
            if (getContent().equals(realContent)) {
                return object;
            } else {
                throw new ParsingException("Content does not match, expected '" + getContent() + "' but got '" + realContent + "'.");
            }
        } catch (IndexOutOfBoundsException ex) {
            throw new ParsingException(ex);
        }
    }

}

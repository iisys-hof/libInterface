package de.iisys.libinterface.parser.node;

import org.parboiled.errors.ParsingException;

/**
 * Class to process the ECC block nodes.
 */
public class ECCBlockNode extends ParserNode {

    private String eccBlock;
    private String reference;

    /**
     * Initializes {@link ParserNode#ParserNode() } and also {@link #reference}
     * with the given reference string.
     * @param reference reference
     * @param children children nodes
     */
    public ECCBlockNode(String reference, ParserNode... children) {
        super(children);
        this.reference = reference;
    }

    /**
     * Calls {@link #resetChildren() } and initiailizes {@link #eccBlock} with null.
     */
    @Override
    public void reset() {
        resetChildren();
        eccBlock = null;
    }

    @Override
    public String getContent() {
        return eccBlock;
    }

    /**
     * Gets the length of {@link #eccBlock} string.
     * @return length of {@link #eccBlock}
     */
    @Override
    public int getLength() {
        return eccBlock.length();
    }

    public String getEccBlock() {
        return eccBlock;
    }

    public String getReference() {
        return reference;
    }

    /**
     * Initializes {@link #eccBlock} with {@link #serializeChildren(java.lang.Object)} and returns {@link 
     * @param object the object
     * @return {@link #getContent() }
     */
    @Override
    public String serialize(Object object) {
        eccBlock = serializeChildren(object);
        return getContent();
    }

    /**
     * Initializes {@link #eccBlock} with {@link #extractDelimiteredMessage(java.lang.String) } 
     * and returns {@link #deserializeChildren(java.lang.Object, java.lang.String) }.
     * @param object the object
     * @param message the message
     * @return 
     */
    @Override
    public Object deserialize(Object object, String message) {
        try {
            eccBlock = extractDelimiteredMessage(message);
            return deserializeChildren(object, eccBlock);
        } catch (IndexOutOfBoundsException ex) {
            throw new ParsingException(ex);
        }
    }

}

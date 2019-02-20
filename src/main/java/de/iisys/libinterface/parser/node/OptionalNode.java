package de.iisys.libinterface.parser.node;

import org.parboiled.errors.ParsingException;

/**
 * Class to process the optional nodes.
 */
public class OptionalNode extends ParserNode {

    /**
     * If optional content could not be found.
     */
    private boolean optionalFailure;

    /**
     * Initilaizes {@link ParserNode#ParserNode() }.
     * @param optionalChildren 
     */
    public OptionalNode(ParserNode... optionalChildren) {
        super(optionalChildren);
    }

    /**
     * Calls {@link #resetChildren() and puts {@link #optionalFailure} on false.
     */
    @Override
    public void reset() {
        resetChildren();
        optionalFailure = false;
    }

    /**
     * Return whether {@link #optionalFailure} is defined.
     * @return {@link #optionalFailure}
     */
    @Override
    protected boolean isDefined() {
        return !optionalFailure;
    }

    /**
     * Returns {@code ""} or {@link ParserNode#ParserNode() } 
     * whether {@link #optionalFailure} is true or false.
     * @return {@code ""} or {@link ParserNode#ParserNode() }
     */
    @Override
    public String getContent() {
        return isDefined() ? super.getContent() : "";
    }

    /**
     * Returns {@code 0} or {@link ParserNode#getLength()  } 
     * whether {@link #optionalFailure} is true or false.
     * @return {@code 0) or {@link ParserNode#getLength()  } 
     */
    @Override
    public int getLength() {
        return isDefined() ? super.getLength() : 0;
    }

    /**
     * Initializes {@link #optionalFailure} with the given optional failure boolean.
     * @param optionalFailure the optinal failure
     */
    protected void setOptionalFailure(boolean optionalFailure) {
        this.optionalFailure = optionalFailure;
    }

    /**
     * Returns {@link ParserNode#getEnd(java.lang.String, int, java.lang.String, boolean) }
     * @param message the message
     * @param position the position
     * @param followingContent the following content
     * @return {@link ParserNode#getEnd(java.lang.String, int, java.lang.String, boolean) }
     */
    @Override
    protected int getEnd(String message, int position, String followingContent) {
        if (followingContent != null && followingContent.length() > 0) {
            int end = message.indexOf(followingContent, position);
            if (end == -1) {
                followingContent = getFollowingContent(OptionalNode.class);
            }
        }

        return super.getEnd(message, position, followingContent, false);
    }

    /**
     * Serializes the children node.
     * @param object the object
     * @return {@link #serializeChildren(java.lang.Object) }
     */
    @Override
    public String serialize(Object object) {
        try {
            return serializeChildren(object);
        } catch (ParsingException ex) {
            setOptionalFailure(true);
            return "";
        }
    }

    /**
     * Returns {@link #deserializeChildren(java.lang.Object, java.lang.String) }
     * @param object the object
     * @param message the message
     * @return {@link #deserializeChildren(java.lang.Object, java.lang.String) }
     */
    @Override
    public Object deserialize(Object object, String message) {
        try {
            try {
                return deserializeChildren(object, extractDelimiteredMessage(message));
            } catch (ParsingException ex) {
                setOptionalFailure(true);
                return object;
            }
        } catch (IndexOutOfBoundsException ex) {
            throw new ParsingException(ex);
        }
    }

}

package de.iisys.libinterface.parser.node;

import de.iisys.libinterface.service.ReflectionService;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.parboiled.errors.ParsingException;

/**
 * Class to process the array nodes.
 */
public class ArrayNode extends ParserNode {

    private String delimiter;
    private String content;

    private int arrayPosition;

    /**
     * Initializes {@link ParserNode#ParserNode() } and also initializes {@link #delimiter}
     * with the given delimter string.
     * @param delimiter delimeter
     * @param childNodes the child nodes
     */
    public ArrayNode(String delimiter, ParserNode... childNodes) {
        super(childNodes);
        this.delimiter = delimiter;
    }

    /**
     * Calls {@link #resetChildren()} and puts {@link #content} to null and the
     * {@link #arrayPosition} to 0;
     */
    @Override
    public void reset() {
        resetChildren();
        content = null;
        arrayPosition = 0;
    }

    /**
     * Returns whether {@link #content} is null or not.
     * @returns Whether {@link #getContent() } is not null
     */
    @Override
    protected boolean isDefined() {
        return getContent() != null;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public int getLength() {
        return content.length();
    }

    public int getArrayPosition() {
        return arrayPosition;
    }

    /**
     * Increases the {@link #arrayPosition}.
     */
    public void increaseArrayPosition() {
        arrayPosition++;
    }

    /**
     * Decreases the {@link #arrayPosition}.
     */
    public void decreaseArrayPosition() {
        arrayPosition--;
    }

    /**
     * Gets the current array data.
     * @param data the data object
     * @return array position object
     */
    public Object getCurrentArrayData(Object data) {
        try {
            if (data.getClass().isArray()) {
                return ((Object[]) data)[getArrayPosition()];
            } else if (List.class.isAssignableFrom(data.getClass())) {
                return ((List) data).get(getArrayPosition());
            }
            return null;
        } catch (IndexOutOfBoundsException ex) {
            throw new ParsingException(ex);
        }
    }

    /**
     * Sets the current array data if the {@link #arrayPosition} is higher then the given objects length
     * and the field typ is array. Else the method creates a new ArrayList and adds the content of
     * the array position in the array list.
     * 
     * @param field the field
     * @param object object
     * @param content content object
     * @return the data
     * @throws IllegalAccessException 
     */
    public Object setCurrentArrayData(Field field, Object object, Object content) throws IllegalAccessException {
        try {
            Object data = ReflectionService.getData(field, object);

            if (field.getType().isArray()) {
                if (data == null) {
                    data = Array.newInstance(field.getType(), 16);
                }

                int length = ((Object[]) data).length;
                if (getArrayPosition() >= length) {
                    Object newData = Array.newInstance(field.getType(), length * 2);
                    System.arraycopy(data, 0, newData, 0, length);
                    data = newData;
                }
                ((Object[]) data)[getArrayPosition()] = content;
            } else if (List.class.isAssignableFrom(field.getType())) {
                if (data == null) {
                    data = new ArrayList<>();
                }

                int length = ((List) data).size();
                if (getArrayPosition() >= length) {
                    ((List) data).add(content);
                } else {
                    ((List) data).set(getArrayPosition(), content);
                }
            }

            return data;
        } catch (IndexOutOfBoundsException ex) {
            throw new ParsingException(ex);
        }
    }

    /**
     * Gets the field and object data.
     * @param node the reflection field node
     * @param object object
     * @return data
     */
    protected Object getData(ReflectionFieldNode node, Object object) {
        try {
            Field field = ReflectionService.getDeclaredFieldWithSuperField(object.getClass(), node.getFieldName());
            field.setAccessible(true);

            return ReflectionService.getData(field, object);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
            throw new ParsingException(ex);
        }
    }

    /**
     * Gets all current lists sizes of the data.
     * @param object the object
     * @return the length
     */
    protected int getAllListsSize(Object object) {
        List<ReflectionFieldNode> reflectionFieldChildren = getFilteredChildren(ReflectionFieldNode.class);

        int length = -1;
        for (ReflectionFieldNode reflectionFieldChild : reflectionFieldChildren) {
            if (!reflectionFieldChild.hasContext(OptionalNode.class)) {
                Object data = getData(reflectionFieldChild, object);

                int currentLength = -1;
                if (data != null && data.getClass().isArray()) {
                    currentLength = ((Object[]) data).length;
                } else if (data != null && List.class.isAssignableFrom(data.getClass())) {
                    currentLength = ((List) data).size();
                } else {
                    throw new ParsingException("Need array or list for field " + reflectionFieldChild.getFieldName() + ".");
                }

                if (length == -1 || length == currentLength) {
                    length = currentLength;
                } else {
                    return -1;
                }
            }
        }

        return length;
    }

    /**
     * Serializes the children.
     * @param object the object
     * @return content
     */
    @Override
    public String serialize(Object object) {
        // object contains lists or arrays that have to be written multiple times, all needed lists need equal size
        int length = getAllListsSize(object);
        if (length < 0) {
            throw new ParsingException("Arrays or Lists in object all need to have same size, maybe add null elements in between.");
        }

        StringBuilder contentBuilder = new StringBuilder();
        while (getArrayPosition() < length) {
            contentBuilder.append(serializeChildren(object)).append(delimiter);
            increaseArrayPosition();
        }

        // delete last delimiter after while loop as increase might behave unexpected so that the inner loop may not be checked by position + 1 < length.
        if (contentBuilder.lastIndexOf(delimiter) == contentBuilder.length() - delimiter.length()) {
            contentBuilder.delete(contentBuilder.length() - delimiter.length(), contentBuilder.length());
        }

        return content = contentBuilder.toString();
    }

    /**
     * Deserializes children nodes, increases the array position and resets children if the
     * tokens length is over 0.
     * @param object the object
     * @param message the message
     * @return object
     */
    @Override
    public Object deserialize(Object object, String message) {
        content = extractDelimiteredMessage(message);

        StringTokenizer tokenizer = new StringTokenizer(content, delimiter);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.length() > 0) {
                deserializeChildren(object, token);
                increaseArrayPosition();
                resetChildren();
            }
        }

        return object;
    }

}

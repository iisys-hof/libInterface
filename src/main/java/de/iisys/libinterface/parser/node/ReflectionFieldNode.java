package de.iisys.libinterface.parser.node;

import de.iisys.libinterface.message.annotation.Representation;
import de.iisys.libinterface.message.annotation.Represented;
import de.iisys.libinterface.service.ReflectionService;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.parboiled.errors.ParserRuntimeException;
import org.parboiled.errors.ParsingException;
import de.iisys.libinterface.message.annotation.Callback;

/**
 * Class to process the reflection field nodes.
 */
public class ReflectionFieldNode extends ParserNode {

    private String fieldName;
    private int fieldLength;

    private String content;
    private int length;

    /**
     * Initializes {@link #ReflectionFieldNode(java.lang.String, int) }
     * @param fieldName 
     */
    public ReflectionFieldNode(String fieldName) {
        this(fieldName, -1);
    }

    /**
     * Initializes {@link ParserNode}. Also initializes {@link #fieldName}
     * and {@link #fieldLength} with the given field name and field length.
     * @param fieldName the field name
     * @param fieldLength the field length
     */
    public ReflectionFieldNode(String fieldName, int fieldLength) {
        super();
        this.fieldName = fieldName;
        this.fieldLength = fieldLength;
    }

    /**
     * Initializes {@link #content} with null and {@link #length} with {@link #fieldLength}.
     */
    @Override
    public void reset() {
        content = null;
        length = fieldLength;
    }

    /**
     * Returns {@link #getContent() } if not null
     * @return {@link #getContent() }
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
        return length;
    }

    public String getFieldName() {
        return fieldName;
    }

    public int getFieldLength() {
        return fieldLength;
    }

    /**
     * Returns true for additional calculations.
     * @param field the field
     * @param object object
     * @return true
     */
    protected boolean additionalGetCalculations(Field field, Object object) {
        return true;
    }

    /**
     * Method to process object content.
     * @param field the field 
     * @param object object
     * @return content
     * @throws IllegalArgumentException
     * @throws IllegalAccessException 
     */
    protected Object processObjectContent(Field field, Object object) throws IllegalArgumentException, IllegalAccessException {
        if (!invokeCallbackMethod(field, object)) {
            throw new ParserRuntimeException("Callback method for '" + field.getName() + "' did return false, so something went wrong.");
        }

        Object content = ReflectionService.getData(field, object);

        // Represented
        if (content != null && field.isAnnotationPresent(Represented.class)) {
            try {
                Field representationField = ReflectionService.getDeclaredFieldWithSuperField(content.getClass(), content.toString());
                if (representationField.isAnnotationPresent(Representation.class)) {
                    String representation = representationField.getAnnotation(Representation.class).value();
                    if (!representation.equals(Representation.DEFAULT)) {
                        content = representation;
                    }
                }
            } catch (NoSuchFieldException ex) {
            }
        }

        // ArrayNode
        ArrayNode arrayNode = findParent(ArrayNode.class);
        if (arrayNode != null) {
            content = arrayNode.getCurrentArrayData(content);
        }

        if (additionalGetCalculations(field, object)) {
            return content;
        } else {
            return null;
        }
    }

    /**
     * Gets the field data with the given fieldname and content.
     * @param fieldName the field name
     * @param content the content
     * @return field data
     */
    protected String getField(String fieldName, Object content) {
        String data = null;

        try {
            Field declaredField = ReflectionService.getDeclaredFieldWithSuperField(content.getClass(), fieldName);
            declaredField.setAccessible(true);

            Object get = processObjectContent(declaredField, content);
            if (get != null) {
                data = get.toString();
            }
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
            throw new ParserRuntimeException("Could not get data of '" + fieldName + "'.", ex);
        }

        if (data != null && fieldLength >= 0) {
            if (data.length() < fieldLength) {
                data = data.concat(String.join("", Collections.nCopies(fieldLength - data.length(), " ")));
            } else if (data.length() > fieldLength) {
                data = data.substring(0, fieldLength);
            }
        }

        return data;
    }
    
    /**
     * Serializes the field.
     * @param object the object
     * @return serialized field data
     */
    @Override
    public String serialize(Object object) {
        String data = getField(fieldName, object);
        if (data == null) {
            throw new ParsingException("Could not serialize " + fieldName + " because there is no data set for it.");
        }
        return data;
    }

    /**
     * Returns true for additional set calculations.
     * @param field the field
     * @param object the object
     * @param content the content
     * @return true
     */
    protected boolean additionalSetCalculations(Field field, Object object, Object content) {
        return true;
    }

    /**
     * Method to filter declared fields.
     * @param field the field
     * @param object the object
     * @param content the content
     * @param declaredFields list of declared fields
     * @return filtered declared fields
     */
    protected List<Field> filterDeclaredFields(Field field, Object object, Object content, List<Field> declaredFields) {
        List<Field> filteredFieldList = new ArrayList<>(declaredFields.size());
        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(Representation.class)) {
                filteredFieldList.add(declaredField);
            }
        }
        return filteredFieldList;
    }

    /**
     * Method to process message content.
     * @param field the field
     * @param object the object
     * @param content the object
     * @throws IllegalArgumentException
     * @throws IllegalAccessException 
     */
    protected void processMessageContent(Field field, Object object, Object content) throws IllegalArgumentException, IllegalAccessException {
        // Represented
        if (field.isAnnotationPresent(Represented.class)) {
            Object newContent = null;
            Object defaultContent = null;

            List<Field> declaredFields = ReflectionService.getDeclaredFieldsWithSuperFields(field.getType());
            List<Field> representationFields = filterDeclaredFields(field, object, content, declaredFields);
            for (Field representationField : representationFields) {
                String representation = representationField.getAnnotation(Representation.class).value();

                if (representation.equals(Representation.DEFAULT)) {
                    defaultContent = representationField.get(null);
                } else if (representation.equals(content)) {
                    newContent = representationField.get(null);
                }
            }

            if (newContent != null) {
                content = newContent;
            } else if (defaultContent != null) {
                content = defaultContent;
            }
        }

        // ArrayNode
        ArrayNode arrayNode = findParent(ArrayNode.class);
        if (arrayNode != null) {
            content = arrayNode.setCurrentArrayData(field, object, content);
        }

        if (additionalSetCalculations(field, object, content)) {
            ReflectionService.setData(field, object, content);

            if (!invokeCallbackMethod(field, object)) {
                throw new ParserRuntimeException("Callback method for '" + field.getName() + "' did return false, so something went wrong.");
            }
        }
    }

    /**
     * Invokes the callback method.
     * @param field the field
     * @param object the object
     * @return whether the respone is true of false
     */
    protected boolean invokeCallbackMethod(Field field, Object object) {
        if (field.isAnnotationPresent(Callback.class)) {
            String methodName = field.getAnnotation(Callback.class).value();

            try {
                Method method = ReflectionService.getDeclaredMethodWithSuperMethod(object.getClass(), methodName);
                method.setAccessible(true);

                Object response = method.invoke(object);
                if (response != null && response.getClass().equals(Boolean.class)) {
                    return (Boolean) response;
                }
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException ex) {
                throw new ParserRuntimeException("Could not call method '" + methodName + "'.", ex);
            }
        }

        return true;
    }

    /**
     * Sets the field with the given field name, content and new content.
     * @param fieldName the field name
     * @param content the content
     * @param newContent the new content
     * @return the new content
     */
    protected Object setField(String fieldName, String content, Object newContent) {
        if (content != null) {
            try {
                Field declaredField = ReflectionService.getDeclaredFieldWithSuperField(newContent.getClass(), fieldName);
                declaredField.setAccessible(true);

                processMessageContent(declaredField, newContent, content);
            } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
                throw new ParserRuntimeException("Could not set data of '" + fieldName + "' to '" + content + "'.", ex);
            }
        }

        return newContent;
    }

    /**
     * Deserializes the object.
     * @param object the object
     * @param message the message
     * @return deserialized object
     */
    @Override
    public Object deserialize(Object object, String message) {
        int position = getPosition();

        if (fieldLength < 0) {
            int start = position;
            int end = getEnd(message, position);

            length = end - start;
            position = start;
        } else {
            length = fieldLength;
        }

        if (message.length() > 0 && length > 0 && position + length <= message.length()) {
            try {
                String value = message.substring(position, position + length);
                content = value;

                return setField(fieldName, value, object);
            } catch (IndexOutOfBoundsException ex) {
                throw new ParsingException(ex);
            }
        } else {
            return object;
        }
    }

}

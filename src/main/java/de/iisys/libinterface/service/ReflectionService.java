package de.iisys.libinterface.service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class that works as reflection service.
 */
public class ReflectionService {

    /**
     * Gets declared fields with super fields.
     * 
     * @param clazz Instances of the class {@code Class} represent classes and
     * interfaces in a running Java application
     * @return declared field
     */
    public static List<Field> getDeclaredFieldsWithSuperFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            Field[] declaredFields = clazz.getDeclaredFields();
            fields.addAll(Arrays.asList(declaredFields));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    /**
     * Gets declared field with super field.
     * 
     * @param clazz Instances of the class {@code Class} represent classes and
     * interfaces in a running Java application.
     * @param fieldName the field name
     * @return declared field
     * @throws NoSuchFieldException 
     */
    public static Field getDeclaredFieldWithSuperField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException | SecurityException ex) {
                currentClass = currentClass.getSuperclass();
            }
        }

        throw new NoSuchFieldException("Could not find field '" + fieldName + "' in class '" + clazz.getName() + "'.");
    }

    /**
     * Gets declared method with super method.
     * @param clazz Instances of the class {@code Class} represent classes and
     * interfaces in a running Java application.
     * @param methodName the method name
     * @param parameterTypes the parameter types of the method
     * @return the declared method
     * @throws NoSuchMethodException 
     */
    public static Method getDeclaredMethodWithSuperMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            try {
                return currentClass.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException | SecurityException ex) {
                currentClass = currentClass.getSuperclass();
            }
        }

        throw new NoSuchMethodException("Could not find method '" + methodName + "' in class '" + clazz.getName() + "'.");
    }

    /**
     * Gets declared field with super field by content
     * @param clazz Instances of the class {@code Class} represent classes and
     * interfaces in a running Java application.
     * @param content the content
     * @return declared field
     * @throws NoSuchFieldException
     * @throws IllegalAccessException 
     */
    public static Field getDeclaredFieldWithSuperFieldByContent(Class<?> clazz, String content) throws NoSuchFieldException, IllegalAccessException {
        return getFieldByContent(getDeclaredFieldsWithSuperFields(clazz), null, content);
    }

    /**
     * Gets declared field with super field by content.
     * @param object object
     * @param content content object
     * @return declared field
     * @throws NoSuchFieldException
     * @throws IllegalAccessException 
     */
    public static Field getDeclaredFieldWithSuperFieldByContent(Object object, Object content) throws NoSuchFieldException, IllegalAccessException {
        return getFieldByContent(getDeclaredFieldsWithSuperFields(object.getClass()), object, content);
    }

    /**
     * Gets field by content.
     * 
     * @param fields list of fields
     * @param object object
     * @param content content object
     * @return field
     * @throws NoSuchFieldException
     * @throws IllegalAccessException 
     */
    public static Field getFieldByContent(List<Field> fields, Object object, Object content) throws NoSuchFieldException, IllegalAccessException {
        for (Field field : fields) {
            field.setAccessible(true);
            if (getData(field, object).equals(content)) {
                return field;
            }
        }

        throw new NoSuchFieldException("Could not find field with content '" + content + "' in class '" + object.getClass().getName() + "'.");
    }

    /**
     * Gets content data with the given field and object.
     * @param field the field
     * @param object object
     * @return content data
     * @throws IllegalAccessException 
     */
    public static Object getData(Field field, Object object) throws IllegalAccessException {
        Object content;
        if (field.getType().equals(boolean.class)) {
            content = field.getBoolean(object);
        } else if (field.getType().equals(byte.class)) {
            content = field.getByte(object);
        } else if (field.getType().equals(short.class)) {
            content = field.getShort(object);
        } else if (field.getType().equals(int.class)) {
            content = field.getInt(object);
        } else if (field.getType().equals(long.class)) {
            content = field.getLong(object);
        } else if (field.getType().equals(float.class)) {
            content = field.getFloat(object);
        } else if (field.getType().equals(double.class)) {
            content = field.getDouble(object);
        } else if (field.getType().equals(char.class)) {
            content = field.getChar(object);
        } else {
            content = field.get(object);
        }
        return content;
    }

    /**
     * Sets the field if the field type equals any class instance representing 
     * the primitive type.
     * @param field the field
     * @param object object 
     * @param content object content
     * @throws IllegalAccessException 
     */
    public static void setData(Field field, Object object, Object content) throws IllegalAccessException {
        if (content != null && content.getClass().equals(String.class)) {
            if (field.getType().equals(boolean.class)) {
                field.setBoolean(object, Boolean.parseBoolean(content.toString()));
            } else if (field.getType().equals(byte.class)) {
                field.setByte(object, Byte.parseByte(content.toString()));
            } else if (field.getType().equals(short.class)) {
                field.setShort(object, Short.parseShort(content.toString()));
            } else if (field.getType().equals(int.class)) {
                field.setInt(object, Integer.parseInt(content.toString()));
            } else if (field.getType().equals(long.class)) {
                field.setLong(object, Long.parseLong(content.toString()));
            } else if (field.getType().equals(float.class)) {
                field.setFloat(object, Float.parseFloat(content.toString()));
            } else if (field.getType().equals(double.class)) {
                field.setDouble(object, Double.parseDouble(content.toString()));
            } else if (field.getType().equals(char.class)) {
                field.setChar(object, content.toString().charAt(0));
            } else {
                field.set(object, content.toString());
            }
        } else {
            field.set(object, content);
        }
    }

}

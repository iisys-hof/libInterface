package de.iisys.libinterface.parser.node;

import de.iisys.libinterface.message.annotation.ECC;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.parboiled.errors.ParsingException;

/**
 * Class to process the ECC calculation nodes.
 */
public class ECCCalculationNode extends ParserNode {

    private String ecc;
    private String reference;

    /**
     * Initializes {@link #reference} with the given reference string.
     * @param reference the reference
     */
    public ECCCalculationNode(String reference) {
        this.reference = reference;
    }

    /**
     * Initializes {@link #ecc} with null.
     */
    @Override
    public void reset() {
        ecc = null;
    }

    @Override
    public String getContent() {
        return ecc;
    }

    /**
     * Gets the length of {@link #ecc}.
     * @return length of {@link #ecc} string
     */
    @Override
    public int getLength() {
        return ecc.length();
    }

    public String getEcc() {
        return ecc;
    }

    public String getReference() {
        return reference;
    }

    /**
     * Calls the {@link ECC} methods and returns the invoke methode. 
     * @param object the object
     * @param content the content
     * @return invoked method of {@link ECC}
     */
    protected char callECCCalculationMethod(Object object, String content) {
        String className;
        String methodName;

        if (object.getClass().isAnnotationPresent(ECC.class)) {
            ECC eccAnnotation = object.getClass().getAnnotation(ECC.class);

            className = eccAnnotation.clazz();
            methodName = eccAnnotation.method();
        } else {
            try {
                className = (String) ECC.class.getMethod("clazz").getDefaultValue();
                methodName = (String) ECC.class.getMethod("method").getDefaultValue();
            } catch (NoSuchMethodException | SecurityException | IllegalArgumentException ex) {
                throw new ParsingException(ex);
            }
        }

        try {
            Class<?> serviceClass = Class.forName(className);
            Method method = serviceClass.getDeclaredMethod(methodName, String.class);
            method.setAccessible(true);

            return (char) method.invoke(null, content);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new ParsingException(ex);
        }
    }

    /**
     * Looks for the {@link ECCBlockNode} reference and returns it.
     * @return ecc block node reference
     */
    protected ECCBlockNode findReferenceECCBlockNode() {
        List<ECCBlockNode> eccBlockNodes = getFilteredNodes(ECCBlockNode.class);
        for (ECCBlockNode eccBlockNode : eccBlockNodes) {
            if (eccBlockNode.getReference().equals(getReference())) {
                return eccBlockNode;
            }
        }

        throw new ParsingException("Could not find ECCBlock with reference '" + getReference() + "'.");
    }

    /**
     * Serializes with the given object.
     * @param object the object
     * @return {@link #getContent() }
     */ 
    @Override
    public String serialize(Object object) {
        ECCBlockNode referenceECCBlockNode = findReferenceECCBlockNode();
        char calculatedEcc = callECCCalculationMethod(object, referenceECCBlockNode.getContent());

        ecc = Character.toString(calculatedEcc);

        return getContent();
    }

    /**
     * Deserializes with the given object and message.
     * @param object the object
     * @param message the message
     * @return deserialized object
     */
    @Override
    public Object deserialize(Object object, String message) {
        try {
            ecc = extractDelimiteredMessage(message);

            ECCBlockNode referenceECCBlockNode = findReferenceECCBlockNode();
            char calculatedEcc = callECCCalculationMethod(object, referenceECCBlockNode.getContent());

            if (!getEcc().equals(Character.toString(calculatedEcc))) {
                throw new ParsingException("ECC was not valid, message is corrupted as we got '" + getEcc() + "' but expected '" + calculatedEcc + "'.");
            }

            return object;
        } catch (IndexOutOfBoundsException ex) {
            throw new ParsingException(ex);
        }
    }

}

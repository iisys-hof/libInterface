package de.iisys.libinterface.message.interfaces;

import de.iisys.libinterface.message.annotation.MessageTemplate;
import java.io.Serializable;

/**
 * Class that returns the value of the class {@link MessageTemplate}.
 */
public interface Message extends Serializable {

    /**
     * Returns the value of the class {@link MessageTemplate} if an annotation
     * type is present on the element.
     * @return {@link MessageTemplate#value()}
     */
    default String getMessageTemplate() {
        if (getClass().isAnnotationPresent(MessageTemplate.class)) {
            return getClass().getAnnotation(MessageTemplate.class).value();
        }

        throw new UnsupportedOperationException(getClass().getName() + " has to be annotated with @" + MessageTemplate.class.getSimpleName() + ".");
    }

}

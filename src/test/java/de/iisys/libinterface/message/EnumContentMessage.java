package de.iisys.libinterface.message;

import de.iisys.libinterface.message.annotation.MessageTemplate;
import de.iisys.libinterface.message.annotation.Representation;
import de.iisys.libinterface.message.annotation.Represented;
import de.iisys.libinterface.message.interfaces.Message;

/**
 * Class to process enum content messages.
 */
@MessageTemplate("test-{content}-1<CR>")
public class EnumContentMessage implements Message {

    @Represented
    private Content content;

    /**
     * Default constructor.
     */
    public EnumContentMessage() {
    }

    /**
     * Initializes {@link #content} with the given content object.
     * @param content the content
     */
    public EnumContentMessage(Content content) {
        this.content = content;
    }

    public Content getContent() {
        return content;
    }

    /**
     * Enummeration with consists of contents.
     */
    public enum Content {

        @Representation("A")
        VALUE_A,
        @Representation("B")
        VALUE_B,
        @Representation("C")
        VALUE_C

    }

}

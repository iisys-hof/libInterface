package de.iisys.libinterface.message;

import de.iisys.libinterface.message.annotation.MessageTemplate;
import de.iisys.libinterface.message.interfaces.Message;

/**
 * Class to process string content messages.
 */
@MessageTemplate("test-{content}-2")
public class StringContentMessage implements Message {

    private String content;

    /**
     * Default constructor.
     */
    public StringContentMessage() {
    }

    /**
     * Initializes {@link #content} with the given content parameters.
     * @param content content message
     */
    public StringContentMessage(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public static class Content {

        public static final String VALUE_A = "A";
        public static final String VALUE_B = "B";
        public static final String VALUE_C = "C";

    }

    /**
     * Served as sub contents for {@link Content}
     */
    public static class SubContent extends Content {

        public static final String TEST_A = "A";
        public static final String TEST_D = "D";
        public static final String TEST_E = "E";
        public static final String TEST_F = "F";

    }

}

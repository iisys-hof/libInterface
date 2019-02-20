package de.iisys.libinterface.message;

import de.iisys.libinterface.message.annotation.MessageTemplate;
import de.iisys.libinterface.message.interfaces.Message;

/**
 * Class to process ecc messages.
 */
@MessageTemplate("<STX>~coming-[*{part}]<ETX>:eccRef~!eccRef!")
public class ECCMessage implements Message {

    private String part;

    /**
     * Default constructor.
     */
    public ECCMessage() {
    }

    /**
     * Initializes {@link #part} with the given Parameter.
     * @param part ECC Message part
     */
    public ECCMessage(String part) {
        this.part = part;
    }

    public String getPart() {
        return part;
    }

}

package de.iisys.libinterface.message;

import de.iisys.libinterface.message.annotation.MessageTemplate;
import de.iisys.libinterface.message.interfaces.Message;

/**
 * Class to process my name messages.
 */
@MessageTemplate("my-\\{name\\}-is-{myName}")
public class MyNameMessage implements Message {

    private String myName;

    /**
     * Default constructor.
     */
    public MyNameMessage() {
    }

    /**
     * Initializes {@link #myName} with the given parameter.
     * @param myName my name String
     */
    public MyNameMessage(String myName) {
        this.myName = myName;
    }

    public String getMyName() {
        return myName;
    }

}

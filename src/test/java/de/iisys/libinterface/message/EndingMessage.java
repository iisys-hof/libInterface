package de.iisys.libinterface.message;

import de.iisys.libinterface.message.annotation.MessageTemplate;
import de.iisys.libinterface.message.interfaces.Message;

/**
 * Class to process ending messages.
 */
@MessageTemplate("[{beginning}-]{ending}")
public class EndingMessage implements Message {

    private String beginning;
    private String ending;

    /**
     * Default constructor.
     */
    public EndingMessage() {
    }

    /**
     * Initializes {@link #beginning} and {@link #ending} with the given
     * parameters.
     * @param beginning the beginning
     * @param ending the ending
     */
    public EndingMessage(String beginning, String ending) {
        this.beginning = beginning;
        this.ending = ending;
    }

    public String getBeginning() {
        return beginning;
    }

    public String getEnding() {
        return ending;
    }

}

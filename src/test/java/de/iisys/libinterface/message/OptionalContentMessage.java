package de.iisys.libinterface.message;

import de.iisys.libinterface.message.annotation.MessageTemplate;
import de.iisys.libinterface.message.interfaces.Message;

/**
 * Class to process optional content messages.
 */
@MessageTemplate("a-[?{optional}\\!]-{5:setting}-message<CR><LF>")
public class OptionalContentMessage implements Message {

    private String optional;
    private String setting;

    public OptionalContentMessage() {
    }

    /**
     * Initializes {@link #setting} with the given setting parameter.
     * @param setting  setting optional content message
     */
    public OptionalContentMessage(String setting) {
        this.setting = setting;
    }

    /**
     * Initializes {@link #optional} and {@link #setting} with the given
     * optional and setting parameters.
     * @param optional optional content message
     * @param setting setting optional content message
     */
    public OptionalContentMessage(String optional, String setting) {
        this.optional = optional;
        this.setting = setting;
    }

    public String getOptional() {
        return optional;
    }

    public String getSetting() {
        return setting;
    }

}

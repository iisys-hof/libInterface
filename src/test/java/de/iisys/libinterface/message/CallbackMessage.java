package de.iisys.libinterface.message;

import de.iisys.libinterface.message.annotation.MessageTemplate;
import de.iisys.libinterface.message.interfaces.Message;
import de.iisys.libinterface.message.annotation.Callback;

/**
 * Class to process callback messages.
 */
@Callback("classCallback")
@MessageTemplate("call-{back}-message")
public class CallbackMessage implements Message {

    @Callback("callback")
    private String back;
    private String callback;
    private String classCallback;

    /**
     * Default Constructor.
     */
    public CallbackMessage() {
    }

    /**
     * Initializes {@link #back} with the given parameter.
     * @param back 
     */
    public CallbackMessage(String back) {
        this.back = back;
    }

    /**
     * Initializes {@link #callback} with {@link #back} in upper case.
     */
    private void callback() {
        callback = back.toUpperCase();
    }

    /**
     * Initializes {@link #classCallback} with two combined {@link back} Strings
     */
    private void classCallback() {
        classCallback = back + back;
    }

    public String getBack() {
        return back;
    }

    public String getCallback() {
        return callback;
    }

    public String getClassCallback() {
        return classCallback;
    }

}

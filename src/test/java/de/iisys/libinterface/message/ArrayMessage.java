package de.iisys.libinterface.message;

import de.iisys.libinterface.message.annotation.MessageTemplate;
import de.iisys.libinterface.message.interfaces.Message;
import java.util.Arrays;
import java.util.List;

/**
 * Class to process array messages.
 */
@MessageTemplate("message-data-\\>\\(({keys}\\:{values}:,)\\)<CR><LF>")
public class ArrayMessage implements Message {

    private List<String> keys;
    private List<String> values;

    /**
     * Default constructor.
     */
    public ArrayMessage() {
    }
    
    /**
     * Initializes {@link #keys} and {@link #values} with the given array parameters.
     * @param keys Array message key
     * @param values Array message value
     */
    public ArrayMessage(String[] keys, String[] values) {
        this.keys = Arrays.asList(keys);
        this.values = Arrays.asList(values);
    }

    public String[] getKeys() {
        return keys.toArray(new String[keys.size()]);
    }

    public String[] getValues() {
        return values.toArray(new String[values.size()]);
    }

}

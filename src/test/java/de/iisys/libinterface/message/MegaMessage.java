package de.iisys.libinterface.message;

import de.iisys.libinterface.message.annotation.MessageTemplate;
import de.iisys.libinterface.message.annotation.Representation;
import de.iisys.libinterface.message.annotation.Represented;
import de.iisys.libinterface.message.interfaces.Message;
import java.util.Arrays;
import java.util.List;
import de.iisys.libinterface.message.annotation.Callback;

/**
 * Class to process mega messages.
 */
@MessageTemplate("<STX>\\\\mega\\\\(\\([{keys}\\:]{values}\\):<CR><LF>)\\\\[\\!{tag}[\\[{extra}\\]]\\!]<ETX>")
public class MegaMessage implements Message {

    /**
     * Enummeration which consists of tags.
     */
    public enum Tag {
        @Representation("NEW")
        NEW,
        @Representation("OLD")
        OLD
    }

    private List<String> keys;
    private List<String> values;
    @Represented
    private Tag tag;
    @Callback("extra")
    private int extra;

    /**
     * Default constructor.
     */
    public MegaMessage() {
    }

    /**
     * Initializes {@link #keys} and {@link #values} with the given keys and values
     * array, if both arrays are not null.
     * Also initializes  {@link #tag} and {@link #extra} with the given parameters.
     * 
     * @param keys Mega message keys
     * @param values Mega message values
     * @param tag Mega message tags
     * @param extra Mega message extra
     */
    public MegaMessage(String[] keys, String[] values, Tag tag, int extra) {
        this.keys = keys != null ? Arrays.asList(keys) : null;
        this.values = values != null ? Arrays.asList(values) : null;
        this.tag = tag;
        this.extra = extra;
    }
    
    /**
     * Multiplys {@link #extra} by 2.
     */
    private void extra() {
        extra *= 2;
    }

    /**
     * Gets {@link #keys} if array is not null.
     * @return @see #keys
     */
    public String[] getKeys() {
        return keys != null ? keys.toArray(new String[keys.size()]) : null;
    }

    /**
     * Gets {@link #values} if array is not null.
     * @return @see #values
     */
    public String[] getValues() {
        return values != null ? values.toArray(new String[values.size()]) : null;
    }

    public Tag getTag() {
        return tag;
    }

    public int getExtra() {
        return extra;
    }

}

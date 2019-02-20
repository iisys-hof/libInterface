package de.iisys.libinterface.parser.node;

import de.iisys.libinterface.parser.service.MessageCharacters;
import org.parboiled.errors.ParsingException;

/**
 * Class to process message character nodes.
 */
public class MessageCharacterNode extends ParserNode {

    private String messageCharacterName;

    /**
     * Initializes {@link #messageCharacterName} with the given message character name
     * string.
     * @param messageCharacterName  message character name
     */
    public MessageCharacterNode(String messageCharacterName) {
        this.messageCharacterName = messageCharacterName;
    }

    /**
     * Returns the {@link MessageCharacters#nameToCharacter(java.lang.String) with the
     * {@link #messageCharacterName} as parameter.
     * @return name to the character
     */
    @Override
    public String getContent() {
        return Character.toString(MessageCharacters.nameToCharacter(messageCharacterName));
    }

    @Override
    public int getLength() {
        return 1;
    }

    /**
     * Gets {@link #getContent() }.
     * @param object the object
     * @return {@link #getContent() }
     */
    @Override
    public String serialize(Object object) {
        return getContent();
    }

    /**
     * Deserializes with the given object and message.
     * @param object the object
     * @param message the message
     * @return deserialized object
     */
    @Override
    public Object deserialize(Object object, String message) {
        int position = getPosition();
        try {
            String realCharacter = message.substring(position, position + 1);
            if (realCharacter.equals(getContent())) {
                return object;
            } else {
                throw new ParsingException("MessageCharacter did not match, expected " + getContent() + " but got '" + message.charAt(position) + "'.");
            }
        } catch (IndexOutOfBoundsException ex) {
            throw new ParsingException(ex);
        }
    }

}

package de.iisys.libinterface.parser.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Class that processes the message characters.
 */
public class MessageCharacters {

    /**
     * Null.
     */
    public static final char NUL = 0x0;

    /**
     * Start of heading.
     */
    public static final char SOH = 0x1;

    /**
     * Start of text.
     */
    public static final char STX = 0x2;

    /**
     * End of text.
     */
    public static final char ETX = 0x3;

    /**
     * End of transmission.
     */
    public static final char EOT = 0x4;

    /**
     * Enquiry.
     */
    public static final char ENQ = 0x5;

    /**
     * Acknowledge.
     */
    public static final char ACK = 0x6;

    /**
     * Bell.
     */
    public static final char BEL = 0x7;

    /**
     * Backspace.
     */
    public static final char BS = 0x8;

    /**
     * Horizontal tab.
     */
    public static final char TAB = 0x9;

    /**
     * NL line feed, new line.
     */
    public static final char LF = 0xA;

    /**
     * Vertical tab.
     */
    public static final char VT = 0xB;

    /**
     * NP form feed, new page.
     */
    public static final char FF = 0xC;

    /**
     * Carriage return.
     */
    public static final char CR = 0xD;

    /**
     * Shift out.
     */
    public static final char SO = 0xE;

    /**
     * Shift in.
     */
    public static final char SI = 0xF;

    /**
     * Data link escape.
     */
    public static final char DLE = 0x10;

    /**
     * Device control 1.
     */
    public static final char DC1 = 0x11;

    /**
     * Device control 2.
     */
    public static final char DC2 = 0x12;

    /**
     * Device control 3.
     */
    public static final char DC3 = 0x13;

    /**
     * Device control 4.
     */
    public static final char DC4 = 0x14;

    /**
     * Negative Acknowledge.
     */
    public static final char NAK = 0x15;

    /**
     * Synchronous idle.
     */
    public static final char SYN = 0x16;

    /**
     * End of transmission block.
     */
    public static final char ETB = 0x17;

    /**
     * Cancel.
     */
    public static final char CAN = 0x18;

    /**
     * End of medium.
     */
    public static final char EM = 0x19;

    /**
     * Substitute.
     */
    public static final char SUB = 0x1A;

    /**
     * Escape.
     */
    public static final char ESC = 0x1B;

    /**
     * File seperator.
     */
    public static final char FS = 0x1C;

    /**
     * Group seperator.
     */
    public static final char GS = 0x1D;

    /**
     * Record seperator.
     */
    public static final char RS = 0x1E;

    /**
     * Unit seperator.
     */
    public static final char US = 0x1F;

    /**
     * Space.
     */
    public static final char SPACE = 0x20;

    /**
     * Delete.
     */
    public static final char DEL = 0x7F;

    private static MessageCharacters instance;

    private Map<String, Character> nameToCharacter;
    private Map<Character, String> characterToName;

    /**
     * Creates new {@link HashMap} for the name to the characters and the 
     * character to the name.<br>
     * Calls {@link #fillMaps() }.
     */
    protected MessageCharacters() {
        nameToCharacter = new HashMap<>();
        characterToName = new HashMap<>();

        fillMaps();
    }

    /**
     * Gets name to the character.
     * @return name to character
     */
    protected Map<String, Character> getNameToCharacter() {
        return nameToCharacter;
    }

    /**
     * Gets the character to the name.
     * @return character to name
     */
    protected Map<Character, String> getCharacterToName() {
        return characterToName;
    }

    /**
     * Fills the HashMaps {@link #getNameToCharacter()} and {@link #getCharacterToName()}
     * if the field type equals the char class.
     */
    protected void fillMaps() {
        Field[] declaredFields = MessageCharacters.class.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getType().equals(char.class)) {
                try {
                    Character character = field.getChar(null);
                    String name = field.getName();

                    getNameToCharacter().put(name, character);
                    getCharacterToName().put(character, name);
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                }
            }
        }
    }

    /**
     * Creates a new {@link MessageCharacters} if the instance is null.
     * @return instance representing {@link MessageCharacters}
     */
    public static MessageCharacters getInstance() {
        if (instance == null) {
            instance = new MessageCharacters();
        }
        return instance;
    }

    /**
     * Gets the name to the character of the {@link MessageCharacters} instance.
     * @param name the name to the character
     * @return name of the {@link MessageCharacters} instance
     */
    public static char nameToCharacter(String name) {
        return getInstance().getNameToCharacter().get(name);
    }

    /**
     * Gets the character to the name of the {@link MessageCharacters} instance.
     * @param character the character to the name
     * @return character of the {@link MessageCharacters} instance
     */
    public static String characterToName(char character) {
        return getInstance().getCharacterToName().get(character);
    }

    /**
     * Parses the part of the given String up to the character {@code '<'} with the part of 
     * {@link #nameToCharacter} name that is up to the character {@code '>'} and returns
     * that parsed String.
     * @param string the given String
     * @return the parsed String
     */
    public static String parseString(String string) {
        StringBuilder parsedString = new StringBuilder(string.length());
        for (int i = 0; i < string.length(); i++) {
            char character = string.charAt(i);
            if (character == '<') {
                int end = string.indexOf('>', i + 1);
                if (end != -1) {
                    character = nameToCharacter(string.substring(i + 1, end));
                    i = end;
                }
            }

            parsedString.append(Objects.toString(character, ""));
        }

        return parsedString.toString();
    }

    /**
     * Repaces the given String. If the character name is not null appends the
     * character {@code '<' and '>'} between the character name.
     * @param string the string
     * @return replaced String
     */
    public static String prettyReplaceString(String string) {
        StringBuilder prettyString = new StringBuilder(string.length());
        for (int i = 0; i < string.length(); i++) {
            char character = string.charAt(i);
            String characterName = characterToName(character);
            if (characterName != null) {
                prettyString.append('<').append(characterName).append('>');
            } else {
                prettyString.append(character);
            }
        }

        return prettyString.toString();
    }

    /**
     * Adds all current fields in a unmodifiable list.
     * @param <T> the type
     * @param function the function
     * @return an unmodifiable list of values
     */
    public static <T> List<T> allFields(Function<Field, T> function) {
        List<T> values = new ArrayList<>();

        T latest = null;
        Field[] declaredFields = MessageCharacters.class.getDeclaredFields();
        for (Field field : declaredFields) {
            T current = function.apply(field);
            if (current != null) {
                values.add(latest = current);
            }
        }

        if (latest == null) {
            throw new RuntimeException("Could not determine template type <T> as there were no fields processed.");
        }

        return Collections.unmodifiableList(values);
    }

    /**
     * Gets the field names if the type of the field equals the char class.
     * @return field names
     */
    public static List<String> allNames() {
        return Collections.unmodifiableList(
                allFields(field -> {
                    if (field.getType().equals(char.class)) {
                        return field.getName();
                    }
                    return null;
                })
        );
    }

    /**
     * Returns an array of field names.
     * @return array of field names.
     */
    public static String[] allNamesArray() {
        List<String> allNames = allNames();
        return allNames.toArray(new String[allNames.size()]);
    }

    /**
     * Gets the field character if the field type equals the char class.
     * @return field char
     */
    public static List<Character> allCharacters() {
        return Collections.unmodifiableList(
                allFields(field -> {
                    if (field.getType().equals(char.class)) {
                        try {
                            return field.getChar(null);
                        } catch (IllegalArgumentException | IllegalAccessException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    return null;
                })
        );
    }

    /**
     * Returns an array of field chars.
     * @return 
     */
    public static char[] allChars() {
        List<Character> allCharacters = allCharacters();

        char[] allChars = new char[allCharacters.size()];
        for (int i = 0; i < allChars.length; i++) {
            allChars[i] = allCharacters.get(i);
        }
        return allChars;
    }

}

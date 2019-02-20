package de.iisys.libinterface.parser;

import de.iisys.libinterface.message.ArrayMessage;
import de.iisys.libinterface.message.CallbackMessage;
import de.iisys.libinterface.message.MyNameMessage;
import de.iisys.libinterface.message.ContentMessage;
import de.iisys.libinterface.message.ECCMessage;
import de.iisys.libinterface.message.EndingMessage;
import de.iisys.libinterface.message.MegaMessage;
import de.iisys.libinterface.message.MessageCharacterMessage;
import de.iisys.libinterface.message.OptionalContentMessage;
import de.iisys.libinterface.message.EnumContentMessage;
import de.iisys.libinterface.message.StringContentMessage;
import de.iisys.libinterface.parser.service.MessageCharacters;
import de.iisys.libinterface.parser.service.MessageParserService;
import de.iisys.libinterface.service.ReflectionService;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.parboiled.errors.ParsingException;

/**
 * Class to test every message type.
 */
public class MessageParserTest {

    private static MessageParserService messageParserService;

    @BeforeClass
    public static void setUpBeforeClass() {
        messageParserService = new MessageParserService();
    }

    /**
     * Creates and tests the {@link ContentMessage}.
     */
    @Test
    public void testContent() {
        ContentMessage message = new ContentMessage();
        String serialized = messageParserService.serialize(message);
        assertEquals("/?messageContent[]", serialized);

        ContentMessage deserialized = messageParserService.deserialize(serialized, ContentMessage.class);
        assertEquals(message.getClass(), deserialized.getClass());
    }

    /**
     * Creates and tests the {@link MessageCharacterMessage}
     */
    @Test
    public void testMessageCharacterName() {
        MessageCharacterMessage message = new MessageCharacterMessage();
        String serialized = messageParserService.serialize(message);
        assertEquals("/?" + MessageCharacters.ACK + "message" + MessageCharacters.CR + MessageCharacters.LF, serialized);

        MessageCharacterMessage deserialized = messageParserService.deserialize(serialized, MessageCharacterMessage.class);
        assertEquals(message.getClass(), deserialized.getClass());
    }

    /**
     * Creates and tests the {@link MyNameMessage}.
     */
    @Test
    public void testFieldResolving() {
        MyNameMessage message = new MyNameMessage("John Doe");
        String serialized = messageParserService.serialize(message);
        assertEquals("my-{name}-is-John Doe", serialized);

        MyNameMessage deserialized = messageParserService.deserialize(serialized, MyNameMessage.class);
        assertEquals("John Doe", deserialized.getMyName());
    }

    /**
     * Creates and tests the {@link OptionalContentMessage}.
     */
    @Test
    public void testOptionalFieldResolving() {
        OptionalContentMessage message = new OptionalContentMessage("nicer");
        String serialized = messageParserService.serialize(message);
        assertEquals("a--nicer-message" + MessageCharacters.CR + MessageCharacters.LF, serialized);

        OptionalContentMessage deserialized = messageParserService.deserialize(serialized, OptionalContentMessage.class);
        assertNull(deserialized.getOptional());
        assertEquals("nicer", deserialized.getSetting());

        message = new OptionalContentMessage("very", "great");
        serialized = messageParserService.serialize(message);
        assertEquals("a-?very!-great-message" + MessageCharacters.CR + MessageCharacters.LF, serialized);

        deserialized = messageParserService.deserialize(serialized, OptionalContentMessage.class);
        assertEquals("very", deserialized.getOptional());
        assertEquals("great", deserialized.getSetting());

        // expands to five characters with space as defined
        message = new OptionalContentMessage("totally", "fine");
        serialized = messageParserService.serialize(message);
        assertEquals("a-?totally!-fine -message" + MessageCharacters.CR + MessageCharacters.LF, serialized);

        deserialized = messageParserService.deserialize(serialized, OptionalContentMessage.class);
        assertEquals("totally", deserialized.getOptional());
        assertEquals("fine ", deserialized.getSetting());

        // will be cut off after five characters
        message = new OptionalContentMessage("not", "perfect");
        serialized = messageParserService.serialize(message);
        assertEquals("a-?not!-perfe-message" + MessageCharacters.CR + MessageCharacters.LF, serialized);

        deserialized = messageParserService.deserialize(serialized, OptionalContentMessage.class);
        assertEquals("not", deserialized.getOptional());
        assertEquals("perfe", deserialized.getSetting());
    }

    /**
     * Creates and tests {@link EndingMessage}.
     */
    @Test
    public void testEnding() {
        EndingMessage message = new EndingMessage("one", "ending");
        String serialized = messageParserService.serialize(message);
        assertEquals("one-ending", serialized);

        EndingMessage deserialized = messageParserService.deserialize(serialized, EndingMessage.class);
        assertEquals("one", deserialized.getBeginning());
        assertEquals("ending", deserialized.getEnding());

        message = new EndingMessage(null, "ending");
        serialized = messageParserService.serialize(message);
        assertEquals("ending", serialized);

        deserialized = messageParserService.deserialize(serialized, EndingMessage.class);
        assertNull(deserialized.getBeginning());
        assertEquals("ending", deserialized.getEnding());
    }

    /**
     * Creates and tests the {@link EnumContentMessage}.
     */
    @Test
    public void testEnumContent() {
        EnumContentMessage message = new EnumContentMessage(EnumContentMessage.Content.VALUE_B);
        String serialized = messageParserService.serialize(message);
        assertEquals("test-B-1" + MessageCharacters.CR, serialized);

        EnumContentMessage deserialized = messageParserService.deserialize(serialized, EnumContentMessage.class);
        assertEquals(EnumContentMessage.Content.VALUE_B, deserialized.getContent());
    }

    /**
     * Creates and tests the {@link StringContentMessage}.
     * @throws NoSuchFieldException
     * @throws IllegalAccessException 
     */
    @Test
    public void testStringContent() throws NoSuchFieldException, IllegalAccessException {
        StringContentMessage message = new StringContentMessage(StringContentMessage.Content.VALUE_A);
        String serialized = messageParserService.serialize(message);
        assertEquals("test-A-2", serialized);

        StringContentMessage deserialized = messageParserService.deserialize(serialized, StringContentMessage.class);
        assertEquals(StringContentMessage.Content.VALUE_A, deserialized.getContent());
        assertEquals("VALUE_A", ReflectionService.getDeclaredFieldWithSuperFieldByContent(StringContentMessage.Content.class, deserialized.getContent()).getName());

        message = new StringContentMessage(StringContentMessage.SubContent.TEST_A);
        serialized = messageParserService.serialize(message);
        assertEquals("test-A-2", serialized);

        deserialized = messageParserService.deserialize(serialized, StringContentMessage.class);
        assertEquals(StringContentMessage.SubContent.TEST_A, deserialized.getContent());
        assertEquals("TEST_A", ReflectionService.getDeclaredFieldWithSuperFieldByContent(StringContentMessage.SubContent.class, deserialized.getContent()).getName());

        message = new StringContentMessage(StringContentMessage.SubContent.TEST_E);
        serialized = messageParserService.serialize(message);
        assertEquals("test-E-2", serialized);

        deserialized = messageParserService.deserialize(serialized, StringContentMessage.class);
        assertEquals(StringContentMessage.SubContent.TEST_E, deserialized.getContent());
        assertEquals("TEST_E", ReflectionService.getDeclaredFieldWithSuperFieldByContent(StringContentMessage.SubContent.class, deserialized.getContent()).getName());
    }

    /**
     * Creates and tests the {@link CallbackMessage}.
     */
    @Test
    public void testCallback() {
        CallbackMessage message = new CallbackMessage("hello");
        String serialized = messageParserService.serialize(message);
        assertEquals("call-hello-message", serialized);
        assertEquals("hello", message.getBack());
        assertEquals("HELLO", message.getCallback());
        assertEquals("hellohello", message.getClassCallback());

        CallbackMessage deserialized = messageParserService.deserialize(serialized, CallbackMessage.class);
        assertEquals("hello", deserialized.getBack());
        assertEquals("HELLO", message.getCallback());
        assertEquals("hellohello", message.getClassCallback());
    }

    /**
     * Creates and tests the {@link ECCMessage}.
     */
    @Test
    public void testECC() {
        ECCMessage message = new ECCMessage("eccTest1");
        String serialized = messageParserService.serialize(message);
        assertEquals(MessageCharacters.STX + "coming-*eccTest1" + MessageCharacters.ETX + "g", serialized);

        ECCMessage deserialized = messageParserService.deserialize(serialized, ECCMessage.class);
        assertEquals("eccTest1", deserialized.getPart());

        // one higher, we get N instead of M
        message = new ECCMessage("eccTest2");
        serialized = messageParserService.serialize(message);
        assertEquals(MessageCharacters.STX + "coming-*eccTest2" + MessageCharacters.ETX + "d", serialized);

        deserialized = messageParserService.deserialize(serialized, ECCMessage.class);
        assertEquals("eccTest2", deserialized.getPart());

        message = new ECCMessage("eccTest3");
        serialized = messageParserService.serialize(message);
        assertEquals(MessageCharacters.STX + "coming-*eccTest3" + MessageCharacters.ETX + "e", serialized);

        // defective message
        try {
            messageParserService.deserialize(serialized.replace('e', 'f'), ECCMessage.class);
            assertFalse(true); // always crashes if the deserialization works
        } catch (ParsingException ex) {
            assertTrue(ex.getMessage().contains("ECC was not valid"));
        }
    }

    /**
     * Creates and tests the {@link ArrayMessage}.
     */
    @Test
    public void testArray() {
        ArrayMessage message = new ArrayMessage(
                new String[]{"one", "two", "three"},
                new String[]{"first", "second", "third"}
        );
        String serialized = messageParserService.serialize(message);
        assertEquals("message-data->(one:first,two:second,three:third)" + MessageCharacters.CR + MessageCharacters.LF, serialized);

        ArrayMessage deserialized = messageParserService.deserialize(serialized, ArrayMessage.class);
        assertArrayEquals(new String[]{"one", "two", "three"}, deserialized.getKeys());
        assertArrayEquals(new String[]{"first", "second", "third"}, deserialized.getValues());
    }

    /**
     * Creates and tests the {@link MegaMessage}.
     */
    @Test
    public void testMega() {
        // first round
        MegaMessage message = new MegaMessage(
                new String[]{"one", "two", "three"},
                new String[]{"first", "second", "third"},
                MegaMessage.Tag.NEW, 2);
        String serialized = messageParserService.serialize(message);
        assertEquals(MessageCharacters.STX + "\\mega\\"
                + "(one:first)" + MessageCharacters.CR + MessageCharacters.LF
                + "(two:second)" + MessageCharacters.CR + MessageCharacters.LF
                + "(three:third)"
                + "\\!NEW[4]!" + MessageCharacters.ETX, serialized);

        serialized = serialized.replace("[4]", "[2]");
        MegaMessage deserialized = messageParserService.deserialize(serialized, MegaMessage.class);
        assertArrayEquals(new String[]{"one", "two", "three"}, deserialized.getKeys());
        assertArrayEquals(new String[]{"first", "second", "third"}, deserialized.getValues());
        assertEquals(MegaMessage.Tag.NEW, deserialized.getTag());
        assertEquals(4, deserialized.getExtra()); // doubled by callback method

        // second round
        message = new MegaMessage(
                new String[]{"one", "two"},
                new String[]{"first", "second"},
                null, 0);
        serialized = messageParserService.serialize(message);
        assertEquals(MessageCharacters.STX + "\\mega\\"
                + "(one:first)" + MessageCharacters.CR + MessageCharacters.LF
                + "(two:second)"
                + "\\" + MessageCharacters.ETX, serialized);

        deserialized = messageParserService.deserialize(serialized, MegaMessage.class);
        assertArrayEquals(new String[]{"one", "two"}, deserialized.getKeys());
        assertArrayEquals(new String[]{"first", "second"}, deserialized.getValues());
        assertNull(deserialized.getTag());
        assertEquals(0, deserialized.getExtra()); // doubled by callback method

        // third round
        message = new MegaMessage(new String[]{}, new String[]{}, null, 0);
        serialized = messageParserService.serialize(message);
        assertEquals(MessageCharacters.STX + "\\mega\\\\" + MessageCharacters.ETX, serialized);

        deserialized = messageParserService.deserialize(serialized.replaceFirst("!.*?!", ""), MegaMessage.class);
        assertNull(deserialized.getKeys()); // is uninitialized because no information available
        assertNull(deserialized.getValues()); // is uninitialized because no information available
        assertNull(deserialized.getTag());
        assertEquals(0, deserialized.getExtra()); // is zero because we removed that part from the message
    }

}

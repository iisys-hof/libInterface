package de.iisys.libinterface.message;

import de.iisys.libinterface.message.annotation.MessageTemplate;
import de.iisys.libinterface.message.interfaces.Message;

/**
 * Class to process message character messages.
 */
@MessageTemplate("/?<ACK>message<CR><LF>")
public class MessageCharacterMessage implements Message {

}

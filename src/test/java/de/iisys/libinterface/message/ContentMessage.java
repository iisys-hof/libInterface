package de.iisys.libinterface.message;

import de.iisys.libinterface.message.annotation.MessageTemplate;
import de.iisys.libinterface.message.interfaces.Message;

/**
 * Class to process content messages.
 */
@MessageTemplate("/?messageContent\\[\\]")
public class ContentMessage implements Message {

}

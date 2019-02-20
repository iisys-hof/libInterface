package de.iisys.libinterface.protocol;

import de.iisys.libinterface.message.interfaces.Message;
import de.iisys.libstate.interfaces.State;
import java.io.IOException;

/**
 * Class to write, read, receive, prepare and process the messages.
 */
public abstract class NetworkProtocolRunner extends ProtocolRunner {

    protected final String PREPARED_MESSAGE = "preparedMessage";
    protected final String RECEIVED_MESSAGE = "receivedMessage";

    /**
     * Abstract method to write message.
     * @param message the message
     * @return message
     * @throws IOException 
     */
    protected abstract Message write(Message message) throws IOException;

    /**
     * Returns {@link State#put(java.lang.Object, java.lang.Object) } to prepare
     * the message.
     * @param state {@link State} reference
     * @param clazz instance of {@link Message}
     * @return {@link State#put(java.lang.Object, java.lang.Object) } 
     * @throws IOException 
     */
    protected Message prepare(State state, Class<? extends Message> clazz) throws IOException {
        return state.put(PREPARED_MESSAGE, askSupplier(clazz));
    }

    /**
     * Gets the prepared message.
     * @param <C> message type
     * @param state {@link State} reference
     * @return 
     */
    protected <C extends Message> C getPreparedMessage(State state) {
        return state.get(PREPARED_MESSAGE);
    }

    /**
     * Sends the Message.
     * @param state {@link State} reference
     * @return message the message
     * @throws IOException 
     */
    protected Message send(State state) throws IOException {
        Message message = getPreparedMessage(state);
        if (message != null) {
            write(message);
        }
        return message;
    }

    /**
     * Prepares and sends the message.
     * @param state {@link State} reference
     * @param clazz instance of {@link Message}
     * @return {@link #send(de.iisys.libstate.interfaces.State) }
     * @throws IOException 
     */
    protected Message prepareAndSend(State state, Class<? extends Message> clazz) throws IOException {
        prepare(state, clazz);
        return send(state);
    }

    /**
     * Abstract method to read messages.
     * @param classes instances of {@link Message}.
     * @return message
     * @throws IOException 
     */
    protected abstract Message read(Class<? extends Message>... classes) throws IOException;

    /**
     * Method for the receive of the messages.
     * @param state {@link State} reference
     * @param classes instances of {@link Message}
     * @return {@link State#put(java.lang.Object, java.lang.Object) }
     * @throws IOException 
     */
    protected Message receive(State state, Class<? extends Message>... classes) throws IOException {
        return state.put(RECEIVED_MESSAGE, read(classes));
    }

    /**
     * Gets the received messages.
     * @param <C> message type
     * @param state {@link State} reference
     * @return {@link State#get(java.lang.Object) }
     */
    protected <C extends Message> C getReceivedMessage(State state) {
        return state.get(RECEIVED_MESSAGE);
    }

    /**
     * Method to process messages.
     * @param state {@link State} reference
     * @return message
     * @throws IOException 
     */
    protected Message process(State state) throws IOException {
        Message message = getReceivedMessage(state);
        if (message != null) {
            processListeners(message);
        }
        return message;
    }

    /**
     * Receives and processes messages.
     * @param state {@link State} reference
     * @param classes instances of {@link Message}
     * @return {@link #process(de.iisys.libstate.interfaces.State) }
     * @throws IOException 
     */
    protected Message receiveAndProcess(State state, Class<? extends Message>... classes) throws IOException {
        receive(state, classes);
        return process(state);
    }

}

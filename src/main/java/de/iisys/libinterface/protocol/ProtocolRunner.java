package de.iisys.libinterface.protocol;

import de.iisys.libinterface.message.interfaces.Message;
import de.iisys.libstate.StateGraph;
import de.iisys.libstate.StateGraphRunner;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to make protocol operations.
 */
public abstract class ProtocolRunner {

    //get logger
    private static final Logger LOGGER = Logger.getLogger(ProtocolRunner.class.getName());

    private StateGraph protocol;
    private StateGraphRunner protocolRunner;

    private Map<Class<? extends Message>, Consumer<Message>> listeners;
    private Map<Class<? extends Message>, Supplier<Message>> suppliers;

    /**
     * Creates new HashMaps for the listeners and suppliers. Calls {@link #registerStates(de.iisys.libstate.StateGraph) },
     * {@link #registerTransitions(de.iisys.libstate.StateGraph) }, {@link #registerListeners() }, {@link #registerSuppliers() }.
     * Initializes {@link #protocol} with {@link #createStateGraph() } and {@link #protocolRunner} with {@link #createStateGraphRunner(de.iisys.libstate.StateGraph) }.
     */
    public ProtocolRunner() {
        listeners = new HashMap<>();
        suppliers = new HashMap<>();

        protocol = createStateGraph();
        registerStates(protocol);
        registerTransitions(protocol);
        registerListeners();
        registerSuppliers();
        protocolRunner = createStateGraphRunner(protocol);
    }

    
    protected void registerStates(StateGraph protocol) {
    }

    protected void registerTransitions(StateGraph protocol) {
    }

    protected void registerListeners() {
    }

    protected void registerSuppliers() {
    }

    /**
     * Returns new {@link StateGraph#StateGraph()}.
     * @return {@link StateGraph#StateGraph()}
     */
    protected StateGraph createStateGraph() {
        return new StateGraph();
    }

    /**
     * Returns new {@link StateGraphRunner#StateGraphRunner(de.iisys.libstate.StateGraph) }.
     * @param protocol the protocol
     * @return {@link StateGraphRunner#StateGraphRunner(de.iisys.libstate.StateGraph) }
     */
    protected StateGraphRunner createStateGraphRunner(StateGraph protocol) {
        return new StateGraphRunner(protocol);
    }

    /**
     * Method to listen to messages.
     * @param <C> message type
     * @param clazz instance of {@link Message}
     * @param listener represents {@link Message} as operation
     */
    public <C extends Message> void listenTo(Class<C> clazz, Consumer<C> listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Consumer must not be null.");
        }

        listeners.put(clazz, (Consumer<Message>) listener);
    }

    /**
     * Method to make supplies for messages.
     * @param <C> message type
     * @param clazz instance of {@link Message}
     * @param supplier  represents a supplier of results of message
     */
    public <C extends Message> void supplyFor(Class<C> clazz, Supplier<C> supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier must not be null.");
        }

        suppliers.put(clazz, (Supplier<Message>) supplier);
    }

    /**
     * Processes the listeners.
     * @param message the message
     */
    protected void processListeners(Message message) {
        Consumer<Message> listener = listeners.get(message.getClass());
        if (listener != null) {
            listener.accept(message);
        }
    }

    /**
     * Method that makes a request for supplier.
     * @param <C> message type
     * @param clazz instance of {@link Message}
     * @return message
     */
    protected <C extends Message> C askSupplier(Class<C> clazz) {
        C message = null;

        Supplier<Message> supplier = suppliers.get(clazz);
        if (supplier != null) {
            message = (C) supplier.get();
        } else {
            try {
                message = clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                LOGGER.log(Level.FINEST, "Could not call default constructor of class '" + clazz.getName() + "'.", ex);
            }
        }

        if (supplier == null || message == null) {
            throw new UnsupportedOperationException("Need a supplier or default constructor for generating '" + clazz.getName() + "' objects.");
        }

        return message;
    }

    /**
     * Runs protocol.
     */
    public void runProtocol() {
        protocolRunner.run();
    }

    /**
     * Stops protocol.
     */
    public void stopProtocol() {
        protocolRunner.stop();
    }

}

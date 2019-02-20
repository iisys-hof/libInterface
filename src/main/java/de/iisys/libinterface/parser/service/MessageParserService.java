package de.iisys.libinterface.parser.service;

import org.parboiled.Rule;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.support.ParsingResult;
import de.iisys.libinterface.message.interfaces.Message;
import de.iisys.libinterface.parser.MessageParser;
import de.iisys.libinterface.parser.node.ParserNode;
import de.iisys.libinterface.service.ReflectionService;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.errors.ParseError;
import org.parboiled.errors.ParserRuntimeException;
import org.parboiled.errors.ParsingException;
import org.parboiled.parserunners.ReportingParseRunner;
import de.iisys.libinterface.message.annotation.Callback;

/**
 * Class that processes the parsing of the messages.
 */
public class MessageParserService {

    private MessageParser parser;

    /**
     * Representation of the {@link MessageParserService} as a Object.
     */
    public MessageParserService() {
        this(MessageParser.class);
    }

    /**
     * Creates new parsers.
     * @param clazz instance of the class {@link MessageParser}
     */
    public MessageParserService(Class<? extends MessageParser> clazz) {
        parser = createParser(clazz);
    }

    /**
     * Returns parboild parser of the instance of the class.
     * @param <V> parsernode type
     * @param <P> baseparser type
     * @param clazz instance of class
     * @return parboiled parser of the given instance of the class
     */
    protected <V extends ParserNode, P extends BaseParser<V>> P createParser(Class<P> clazz) {
        return Parboiled.createParser(clazz);
    }

    /**
     * Creats a new parboiled parse runner.
     * @param <V> parsernode type
     * @param rule parboiled rule
     * @return returns the parboiled parse runner with the given parboiled rule.
     */
    protected <V extends ParserNode> ParseRunner<V> createParseRunner(Rule rule) {
        return new ReportingParseRunner<>(rule);
    }

    /**
     * Builds a error String if the parsing result is a error.
     * @param <V> parser node type
     * @param result the parsing result
     * @return error string
     */
    protected <V extends ParserNode> String buildErrorString(ParsingResult<V> result) {
        StringBuilder errorString = new StringBuilder();
        for (ParseError parseError : result.parseErrors) {
            errorString.append(parseError.getClass().getSimpleName())
                    .append(": ").append('\'').append(parseError.getErrorMessage()).append('\'')
                    .append(" from ").append(parseError.getStartIndex())
                    .append(" to ").append(parseError.getEndIndex())
                    .append(" in ").append('\'').append(parseError.getInputBuffer().extract(0, parseError.getEndIndex())).append('\'').append('\n');
        }
        return errorString.toString();
    }

    /**
     * Serializes the message.
     * @param message the message
     * @return serialized result of the parse
     */
    public String serialize(Message message) {
        ParseRunner<ParserNode> parseRunner = createParseRunner(parser.Parse());
        ParsingResult<ParserNode> result = parseRunner.run(message.getMessageTemplate());
        if (!result.hasErrors()) {
            if (!invokeCallbackMethod(message.getClass(), message)) {
                throw new ParserRuntimeException("Callback method for '" + message.getClass().getName() + "' did return false, so something went wrong.");
            }
            return result.resultValue.serialize(message);
        } else {
            throw new ParsingException(buildErrorString(result));
        }
    }

    /**
     * Returns a new instance of the class if the arguments of the given constructor are null or the
     * length of the arguments is 0. Returns the parameter types if the arguments of the constructor
     * are given and returns the arguments.
     * @param <C> message type
     * @param clazz instance of the class
     * @param constructorArguments the arguments of the constructor
     * @return new instance of the class
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException 
     */
    protected <C extends Message> C instantiate(Class<C> clazz, Object... constructorArguments) throws InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        if (constructorArguments == null || constructorArguments.length == 0) {
            return clazz.newInstance();
        }

        Class<?>[] parameterTypes = new Class<?>[constructorArguments.length];
        for (int i = 0; i < constructorArguments.length; i++) {
            parameterTypes[i] = constructorArguments[i].getClass();
        }

        Constructor<C> constructor = clazz.getConstructor(parameterTypes);
        return constructor.newInstance(constructorArguments);
    }

    /**
     * Deserializes the message.
     * @param <C> message type
     * @param message the message
     * @param clazz instance of the class
     * @return deserialized message
     */
    public <C extends Message> C deserialize(String message, Class<C> clazz) {
        return deserialize(message, clazz, (Object[]) null);
    }

    /**
     * Deserializes with the given message, instance of the class, and arguments objects array.
     * @param <C> message type
     * @param message the message
     * @param clazz instance of the class
     * @param arguments the arguments
     * @return deserialized message
     */
    public <C extends Message> C deserialize(String message, Class<C> clazz, Object[] arguments) {
        return (C) deserialize(message, new Class[]{clazz}, new Object[][]{arguments});
    }

    /**
     * Deserializes the message with the given message and instances of {@link Message}
     * @param message the message
     * @param classes instances of {@link Message}
     * @return 
     */
    public Message deserialize(String message, Class<? extends Message>... classes) {
        return deserialize(message, classes, (Object[][]) null);
    }

    /**
     * Deserializes the message with the given message, instances of {@link  Message} and the object arrays
     * of the arguments of the constructor. Catches the errors in the processing.
     * @param message the message
     * @param classes instances of {@link Message}
     * @param constructorArguments arguments of the constructor
     * @return object
     */
    public Message deserialize(String message, Class<? extends Message>[] classes, Object[][] constructorArguments) {
        StringBuilder errorString = new StringBuilder();

        for (int i = 0; i < classes.length; i++) {
            Class<? extends Message> clazz = classes[i];
            Object[] arguments = constructorArguments != null && constructorArguments[i] != null ? constructorArguments[i] : null;

            try {
                Message object = instantiate(clazz, arguments);

                ParseRunner<ParserNode> parseRunner = createParseRunner(parser.Parse());
                ParsingResult<ParserNode> result = parseRunner.run(object.getMessageTemplate());
                if (!result.hasErrors()) {
                    object = (Message) result.resultValue.deserialize(object, message);
                    if (!invokeCallbackMethod(clazz, object)) {
                        throw new ParserRuntimeException("Callback method for '" + clazz.getName() + "' did return false, so something went wrong.");
                    }
                    return object;
                } else {
                    errorString.append(buildErrorString(result)).append(System.lineSeparator());
                }
            } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException("Given class " + clazz.getName() + " must define an accessible constructor.", ex);
            } catch (ParsingException ex) {
                errorString.append(ex.toString()).append(System.lineSeparator());
            }
        }

        throw new ParsingException(errorString.toString());
    }

    /**
     * Invokes the callback method.
     * @param clazz instance of {@link Message}
     * @param object object
     * @return true
     */
    protected boolean invokeCallbackMethod(Class<? extends Message> clazz, Object object) {
        if (clazz.isAnnotationPresent(Callback.class)) {
            String methodName = clazz.getAnnotation(Callback.class).value();

            try {
                Method method = ReflectionService.getDeclaredMethodWithSuperMethod(clazz, methodName);
                method.setAccessible(true);

                Object response = method.invoke(object);
                if (response != null && response.getClass().equals(Boolean.class)) {
                    return (Boolean) response;
                }
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException ex) {
                throw new ParserRuntimeException("Could not call method '" + methodName + "'.", ex);
            }
        }

        return true;
    }

}

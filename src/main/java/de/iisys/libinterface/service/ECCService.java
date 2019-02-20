package de.iisys.libinterface.service;

import java.util.function.BiFunction;

/**
 * Class that works as ECC service.
 */
public class ECCService {

    /**
     * Exclusive or.
     */
    public static final String XOR = "xor";

    /**
     * Applies function to the given arguments and returns the result.
     * @param <R> the type of the result of the function
     * @param content the content
     * @param result the result
     * @param function BiFunction
     * @return the result
     */
    public static <R> R apply(String content, R result, BiFunction<R, Character, R> function) {
        for (int i = 0; i < content.length(); i++) {
            result = function.apply(result, content.charAt(i));
        }
        return result;
    }

    /**
     * Returns if both operators have the same content.
     * 
     * @param content the content
     * @return {@link #apply(java.lang.String, java.lang.Object, java.util.function.BiFunction) } result XOR character
     */
    public static char xor(String content) {
        return apply(content, Character.MIN_VALUE, (result, character) -> (char) (result ^ character));
    }

}

package de.iisys.libinterface.message.annotation;

import de.iisys.libinterface.parser.service.MessageCharacters;
import de.iisys.libinterface.parser.MessageParser;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Parsable template for message-object serialization and deserialization.<br>
 * <br>
 * For forbidden control characters, see
 * {@link MessageParser#ControlCharacters()}. <br>
 * For the defined escape character, see {@link MessageParser#Escape()}.<br>
 * <br>
 * Template options are:<br>
 * <ul>
 * <li>{@code <CHAR>} - defines an ascii stream control character with a name of
 * any field in {@link MessageCharacters} (f.e. {@code <CR>}, {@code <LF>}).
 * <li>{@code {[length]:field}} - defines a reflectable message part that is
 * filled by or into the given object field named by the field parameter, with
 * the optionally defined fixed length that will cut everything longer or extend
 * with spaces everything shorter (f.e. {@code {name}}, {@code {5:type}}).
 * <li>{@code [optional-template]} - defines an optional message part (f.e.
 * {@code {name}[, {first-name}]}).
 * <li>{@code (template:delimiter)} - defines a repeated or repeatable content
 * seperated zero or more times by given delimiter (f.e.
 * {@code ({key},{value}:;) for "one,two;three,four"}.
 * <li>{@code ~template:reference~} - marks a part of the template as the part
 * to calculate an ecc character for, needs the annotation {@link ECC} and the
 * ecc character defined, too, also defines a reference for the ecc character
 * (f.e. {@code ~message:ecc~!ecc!}).
 * <li>{@code !reference!} - defines the ecc character with reference to the
 * marked template part before, is calculated and compared according to the
 * defined {@link ECC} annotation (f.e. {@code ~message:ecc~!ecc!}).
 * </ul>
  */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MessageTemplate {

    String value();

}

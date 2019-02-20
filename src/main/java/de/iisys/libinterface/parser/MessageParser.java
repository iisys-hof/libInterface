package de.iisys.libinterface.parser;

import de.iisys.libinterface.parser.service.MessageCharacters;
import de.iisys.libinterface.parser.node.ArrayNode;
import de.iisys.libinterface.parser.node.ContentNode;
import de.iisys.libinterface.parser.node.ECCBlockNode;
import de.iisys.libinterface.parser.node.ECCCalculationNode;
import de.iisys.libinterface.parser.node.ReflectionFieldNode;
import de.iisys.libinterface.parser.node.MessageCharacterNode;
import de.iisys.libinterface.parser.node.ParserNode;
import de.iisys.libinterface.parser.node.OptionalNode;
import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.support.StringVar;
import org.parboiled.support.Var;

/**
 * Class to process the message parser.
 */
public class MessageParser extends BaseParser<ParserNode> {

    /**
     * Returns {@link BaseParser#Sequence(java.lang.Object[]) }
     * @return {@link BaseParser#Sequence(java.lang.Object[]) }
     */
    public Rule Parse() {
        return Sequence(
                processValueStack(),
                Sequence(Message(), EOI)
        );
    }

    public boolean processValueStack() {
        return true;
    }

    /**
     * Creates new {@link ParserNode} and returns {@link #Sequence(java.lang.Object[]) }.
     * @return {@link #Sequence(java.lang.Object[]) }
     */
    protected Rule Message() {
        Var<ParserNode> parserNode = new Var<>(new ParserNode());
        return Sequence(
                OneOrMore(
                        Part(),
                        processPart(parserNode)
                ),
                push(parserNode.getAndClear())
        );
    }

    /**
     * Processes the parser node children nodes.
     * @param parserNode {@link ParserNode}
     * @return true
     */
    public boolean processPart(Var<ParserNode> parserNode) {
        parserNode.get().addChild(parserNode.get().getChildren().size(), pop());
        return true;
    }

    /**
     * Returns {@link #FirstOf(java.lang.Object, java.lang.Object, java.lang.Object...) }
     * @return {@link #FirstOf(java.lang.Object, java.lang.Object, java.lang.Object...) }
     */
    protected Rule Part() {
        return FirstOf(
                ECCBlock(),
                ECCCalculation(),
                OptionalExpression(),
                ArrayExpression(),
                ReflectionFieldExpression(),
                MessageCharacterExpression(),
                Content()
        );
    }

    /**
     * Return {@link #Sequence(java.lang.Object, java.lang.Object, java.lang.Object...)} .
     * @return {@link #Sequence(java.lang.Object, java.lang.Object, java.lang.Object...)} 
     */
    protected Rule ECCBlock() {
        return Sequence(
                '~',
                Message(),
                ControlCharacter(':'),
                OneOrMore(NonControlCharacter('~')),
                processECCBlock(pop(), match()),
                '~'
        );
    }

    /**
     * Returns {@link #push(int, java.lang.Object) }
     * @param messageNode message node
     * @param reference reference
     * @return {@link #push(int, java.lang.Object) }
     */
    public boolean processECCBlock(ParserNode messageNode, String reference) {
        return push(new ECCBlockNode(reference, messageNode.getChildren().toArray(new ParserNode[messageNode.getChildren().size()])));
    }

    /**
     * Returns {@link #Sequence(java.lang.Object, java.lang.Object, java.lang.Object...) }.
     * @return {@link #Sequence(java.lang.Object, java.lang.Object, java.lang.Object...) }
     */
    protected Rule ECCCalculation() {
        return Sequence(
                ControlCharacter('!'),
                OneOrMore(NonControlCharacter('!')),
                processECCCalculation(match()),
                ControlCharacter('!')
        );
    }

    /**
     * Returns {@link #push(java.lang.Object) }.
     * @param reference the reference
     * @return {@link #push(java.lang.Object) }
     */
    public boolean processECCCalculation(String reference) {
        return push(new ECCCalculationNode(reference));
    }

    /**
     * Returns {@link #Sequence(java.lang.Object, java.lang.Object, java.lang.Object...) }.
     * @return {@link #Sequence(java.lang.Object, java.lang.Object, java.lang.Object...) }
     */
    protected Rule OptionalExpression() {
        return Sequence(
                ControlCharacter('['),
                Message(),
                processOptionalExpression(pop()),
                ControlCharacter(']')
        );
    }

    /**
     * Returns {@link #push(int, java.lang.Object) }.
     * @param parserNode parser node
     * @return {@link #push(int, java.lang.Object) }
     */
    public boolean processOptionalExpression(ParserNode parserNode) {
        return push(new OptionalNode(parserNode.getChildren().toArray(new ParserNode[parserNode.getChildren().size()])));
    }

    /**
     * Returns {@link #Sequence(java.lang.Object, java.lang.Object, java.lang.Object...) }.
     * @return {@link #Sequence(java.lang.Object, java.lang.Object, java.lang.Object...) }
     */
    protected Rule ArrayExpression() {
        return Sequence(
                ControlCharacter('('),
                Message(),
                ControlCharacter(':'),
                OneOrMore(NonControlCharacter(')')),
                processArrayExpression(pop(), match()),
                ControlCharacter(')')
        );
    }

    /**
     * Returns {@link #push(int, java.lang.Object) }.
     * @param parserNode parser node
     * @param delimiter delimiter
     * @return {@link #push(int, java.lang.Object) }
     */
    public boolean processArrayExpression(ParserNode parserNode, String delimiter) {
        return push(new ArrayNode(MessageCharacters.parseString(delimiter), parserNode.getChildren().toArray(new ParserNode[parserNode.getChildren().size()])));
    }

    /**
     * Returns {@link #Sequence(java.lang.Object, java.lang.Object, java.lang.Object...) }.
     * @return {@link #Sequence(java.lang.Object, java.lang.Object, java.lang.Object...) }
     */
    protected Rule ReflectionFieldExpression() {
        Var<Integer> length = new Var(-1);
        return Sequence(
                ControlCharacter('{'),
                Length(length),
                FieldName(),
                processReflectionField(match(), length.get()),
                ControlCharacter('}')
        );
    }

    /**
     * Returns {@link #push(int, java.lang.Object) }.
     * @param fieldName the field name
     * @param fieldLength field length
     * @return {@link #push(int, java.lang.Object) }
     */
    public boolean processReflectionField(String fieldName, int fieldLength) {
        return push(new ReflectionFieldNode(fieldName, fieldLength));
    }

    /**
     * Returns {@link #Optional(java.lang.Object) }.
     * @param length var length
     * @return {@link #push(int, java.lang.Object) }
     */
    protected Rule Length(Var<Integer> length) {
        return Optional(
                Sequence(
                        OneOrMore(CharRange('0', '9')),
                        length.set(Integer.parseInt(match())),
                        ControlCharacter(':')
                )
        );
    }

    /**
     * Returns {@link #Sequence(java.lang.Object, java.lang.Object, java.lang.Object...) }.
     * @return {@link #Sequence(java.lang.Object, java.lang.Object, java.lang.Object...) }
     */
    protected Rule FieldName() {
        return Sequence(
                ANY,
                Character.isJavaIdentifierStart(matchedChar()),
                ZeroOrMore(
                        Sequence(
                                ANY,
                                Character.isJavaIdentifierPart(matchedChar())
                        )
                )
        );
    }
    
    /**
     * Returns {@link #Sequence(java.lang.Object, java.lang.Object, java.lang.Object...) }.
     * @return {@link #Sequence(java.lang.Object, java.lang.Object, java.lang.Object...) }
     */
    protected Rule MessageCharacterExpression() {
        return Sequence(
                ControlCharacter('<'),
                FirstOf(MessageCharacters.allNamesArray()),
                processMessageCharacter(match()),
                ControlCharacter('>')
        );
    }

    /**
     * Returns {@link #push(java.lang.Object) }.
     * @param messageCharacterName message character name
     * @return {@link #push(java.lang.Object) }
     */
    public boolean processMessageCharacter(String messageCharacterName) {
        return push(new MessageCharacterNode(messageCharacterName));
    }

    /**
     * Creates new {@link StringVar} and returns {@link #Sequence(java.lang.Object, java.lang.Object, java.lang.Object...) }.
     * @return {@link #Sequence(java.lang.Object, java.lang.Object, java.lang.Object...) }
     */
    protected Rule Content() {
        StringVar content = new StringVar();
        return Sequence(
                OneOrMore(
                        NonControlCharacter(),
                        content.append(match().charAt(matchLength() - 1))
                ),
                processContent(content.getAndClear())
        );
    }

    /**
     * Returns {@link #push(java.lang.Object) }.
     * @param content
     * @return 
     */
    public boolean processContent(String content) {
        return push(new ContentNode(content));
    }

    /**
     * Returns {@link #FirstOf(java.lang.Object, java.lang.Object, java.lang.Object...) }.
     * @param character the character
     * @return {@link #FirstOf(java.lang.Object, java.lang.Object, java.lang.Object...) }
     */
    protected Rule NonControlCharacter(char character) {
        return FirstOf(
                NoneOf(Character.toString(character) + Escape()),
                Sequence(
                        Escape(),
                        ANY
                )
        );
    }

    /**
     * Returns {@link #FirstOf(java.lang.Object, java.lang.Object, java.lang.Object...) }.
     * @return {@link #FirstOf(java.lang.Object, java.lang.Object, java.lang.Object...) }
     */
    protected Rule NonControlCharacter() {
        return FirstOf(
                NoneOf(ControlCharacters() + Escape()),
                Sequence(
                        Escape(),
                        ANY
                )
        );
    }

    /**
     * Returns {@link #Sequence(java.lang.Object, java.lang.Object, java.lang.Object...) }.
     * @param character the character
     * @return {@link #Sequence(java.lang.Object, java.lang.Object, java.lang.Object...) }
     */
    protected Rule ControlCharacter(char character) {
        return Sequence(
                TestNot(Escape()),
                character
        );
    }

    /**
     * Returns {@link #Sequence(java.lang.Object, java.lang.Object, java.lang.Object...) }.
     * @return {@link #Sequence(java.lang.Object, java.lang.Object, java.lang.Object...) }
     */
    protected Rule ControlCharacter() {
        return Sequence(
                TestNot(Escape()),
                AnyOf(ControlCharacters())
        );
    }

    /**
     * Defines the escape character to use for escaping control or escape
     * characters, is defined as: &#92;
     *
     * @return the escape character
     * @see #ControlCharacters()
     */
    protected char Escape() {
        return '\\';
    }

    /**
     * Defines the available control characters which are:<br>
     * ()[]{}&lt;&gt;:~!
     *
     * @return the control characters
     * @see #Escape()
     */
    protected String ControlCharacters() {
        return "()[]{}<>:~!";
    }

}

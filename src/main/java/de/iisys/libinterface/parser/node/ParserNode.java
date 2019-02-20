package de.iisys.libinterface.parser.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.parboiled.trees.MutableTreeNodeImpl;

/**
 * Class to process the parser node.
 */
public class ParserNode extends MutableTreeNodeImpl<ParserNode> {

    /**
     * Default constructor.
     */
    public ParserNode() {
    }

    /**
     * Fills the children array with nodes.
     * 
     * @param children 
     */
    public ParserNode(ParserNode... children) {
        for (int i = 0; i < children.length; i++) {
            if (children[i] != null) {
                addChild(i, children[i]);
            }
        }

        reset();
    }

    /**
     * Calls {@link #resetChildren() }
     */
    public void reset() {
        resetChildren();
    }

    /**
     * Resets the parser node childs.
     */
    protected void resetChildren() {
        for (ParserNode child : getChildren()) {
            child.reset();
        }
    }

    /**
     * Returns true.
     * @return true
     */
    protected boolean isDefined() {
        return true;
    }

    /**
     * Returns whether the instance of {@link ParserNode} has context.
     * @param contextClass istance of {@link ParserNode}
     * @return whether has context or not
     */
    protected boolean hasContext(Class<? extends ParserNode> contextClass) {
        return findParent(contextClass) != null;
    }

    /**
     * Looks for the parents of the nodes of {@link ParserNode}. If not returns null.
     * @param <T> Parser node type
     * @param parentFilter parentfilter
     * @return current {@link ParserNode} node
     */
    protected <T extends ParserNode> T findParent(Class<T> parentFilter) {
        ParserNode currentNode = this;
        while (currentNode != null) {
            if (parentFilter.isAssignableFrom(currentNode.getClass())) {
                return (T) currentNode;
            }

            currentNode = currentNode.getParent();
        }

        return null;
    }

    /**
     * Gets the current filtered nodes.
     * @param <T> parser node type
     * @param filterClass instance of {@link ParserNode}
     * @return current filtered nodes
     */
    protected <T extends ParserNode> List<T> getFilteredNodes(Class<T> filterClass) {
        ParserNode current = this;
        while (current.getParent() != null) {
            current = current.getParent();
        }

        return current.getFilteredChildren(filterClass);
    }

    /**
     * Gets the filtered children nodes and adds them in a array list.
     * @param <T> parser node type
     * @param filterClass instance of {@link ParserNode}
     * @return children array list
     */
    protected <T extends ParserNode> List<T> getFilteredChildren(Class<T> filterClass) {
        List<T> children = new ArrayList<>();
        for (ParserNode child : getChildren()) {
            if (filterClass != null && filterClass.isAssignableFrom(child.getClass())) {
                children.add((T) child);
            }
            if (child.getChildren().size() > 0) {
                children.addAll(child.getFilteredChildren(filterClass));
            }
        }
        return children;
    }

    /**
     * Gets the parent child index.
     * @return index
     */
    protected int getParentChildIndex() {
        if (getParent() != null) {
            for (int i = 0; i < getParent().getChildren().size(); i++) {
                if (getParent().getChildren().get(i) == this) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    /**
     * Processes the left sibling nodes.
     * @return left sibling
     */
    protected List<ParserNode> leftSiblings() {
        int parentChildIndex = getParentChildIndex();
        if (parentChildIndex > 0) {
            return getParent().getChildren().subList(0, parentChildIndex);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Processes the right sibling nodes.
     * @return right sibling
     */
    protected List<ParserNode> rightSiblings() {
        int parentChildIndex = getParentChildIndex();
        if (parentChildIndex + 1 < getParent().getChildren().size()) {
            return getParent().getChildren().subList(parentChildIndex + 1, getParent().getChildren().size());
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Gets the position of the left siblings and adds the length.
     * @return length
     */
    protected int getPosition() {
        int length = 0;
        for (ParserNode node : leftSiblings()) {
            length += node.getLength();
        }
        return length;
    }

    /**
     * Returns {@link #getFollowingNode(java.lang.Class) }
     * @return {@link #getFollowingNode(java.lang.Class) }
     */
    protected ParserNode getFollowingNode() {
        return getFollowingNode(null);
    }

    /**
     * Gets the following right node.
     * @param skipClass instance of {@link ParserNode}
     * @return right siblings
     */
    protected ParserNode getFollowingNode(Class<? extends ParserNode> skipClass) {
        List<ParserNode> rightSiblings = rightSiblings();
        for (int i = 0; i < rightSiblings.size(); i++) {
            ParserNode rightSibling = rightSiblings.get(i);
            if (skipClass == null || !skipClass.isAssignableFrom(rightSibling.getClass())) {
                return rightSibling;
            }
        }
        return null;
    }

    /**
     * Returns {@link #getFollowingContent(java.lang.Class) }
     * @return  {@link #getFollowingContent(java.lang.Class) }
     */
    protected String getFollowingContent() {
        return getFollowingContent(null);
    }

    /**
     * Gets the following content if the following node is not null.
     * @param skipClass instance of {@link ParserNode}
     * @return content of the following node
     */
    protected String getFollowingContent(Class<? extends ParserNode> skipClass) {
        ParserNode followingNode = getFollowingNode(skipClass);
        return followingNode != null
                ? followingNode.getContent()
                : null;
    }

    /**
     * Returns {@link #getLastPreviousNode(java.lang.Class) }
     * @return {@link #getLastPreviousNode(java.lang.Class) }
     */
    protected ParserNode getLastPreviousNode() {
        return getLastPreviousNode(null);
    }

    /**
     * Gets the last previous child node.
     * @param skipClass instance of {@link ParserNode}
     * @return child node
     */
    protected ParserNode getLastPreviousNode(Class<? extends ParserNode> skipClass) {
        for (int i = getChildren().size() - 1; i >= 0; i--) {
            ParserNode child = getChildren().get(i);
            if (skipClass == null || !skipClass.isAssignableFrom(child.getClass())) {
                return child;
            }
        }
        return null;
    }

    /**
     * Returns {@link #getLastPreviousContent(java.lang.Class) }
     * @return {@link #getLastPreviousContent(java.lang.Class) }
     */
    protected String getLastPreviousContent() {
        return getLastPreviousContent(null);
    }

    /**
     * Gets the last previous content if the last previous node is null.
     * @param skipClass instance of {@link ParserNode}
     * @return content of the last prevois node
     */
    protected String getLastPreviousContent(Class<? extends ParserNode> skipClass) {
        ParserNode lastPreviousNode = getLastPreviousNode(skipClass);
        return lastPreviousNode != null
                ? lastPreviousNode.getContent()
                : null;
    }

    /**
     * Returns {@link #getEnd(java.lang.String, int) }
     * @param message the message
     * @return {@link #getEnd(java.lang.String, int) }
     */
    protected int getEnd(String message) {
        return getEnd(message, message.length());
    }

    /**
     * Returns {@link #getEnd(java.lang.String, int, java.lang.String) }
     * @param message the message
     * @param position position of the message
     * @return {@link #getEnd(java.lang.String, int, java.lang.String) }
     */
    protected int getEnd(String message, int position) {
        return getEnd(message, position, getFollowingContent());
    }

    /**
     * Returns {@link #getEnd(java.lang.String, int, java.lang.String, boolean) }
     * @param message the message
     * @param position the position of the message
     * @param searchFor what will be searched for
     * @return {@link #getEnd(java.lang.String, int, java.lang.String, boolean) }
     */
    protected int getEnd(String message, int position, String searchFor) {
        return getEnd(message, position, searchFor, true);
    }

    /**
     * Returns the end or the position if the end value is over 0.
     * @param message the message
     * @param position the position of the message
     * @param searchFor what will be searched for
     * @param reverseChildSearch whether the child search is reversed or not
     * @return the position/length of the message
     */
    protected int getEnd(String message, int position, String searchFor, boolean reverseChildSearch) {
        if (searchFor != null) {
            if (searchFor.length() > 0) {
                int end = message.indexOf(searchFor, position);
                return end >= 0 ? end : position;
            } else {
                return position;
            }
        } else if (reverseChildSearch && getChildren().size() > 0) {
            String lastPreviousContent = getLastPreviousContent();
            return getEnd(message, position, lastPreviousContent, false) + lastPreviousContent.length();
        }

        return message.length();
    }

    /**
     * Extracts the delimtered message of the given message.
     * @param message the message
     * @return extracted message
     */
    protected String extractDelimiteredMessage(String message) {
        int position = getPosition();
        return message.substring(position, getEnd(message, position));
    }

    /**
     * Returns the content of the child node.
     *
     * @return the content of the child node
     */
    public String getContent() {
        StringBuilder string = new StringBuilder();
        for (ParserNode child : getChildren()) {
            if (!child.isDefined()) {
                break;
            }
            string.append(Objects.toString(child.getContent(), ""));
        }
        return string.toString();
    }

    /**
     * Returns the length of the children nodes.
     *
     * @return the length of the children nodes
     */
    public int getLength() {
        int length = 0;
        for (ParserNode child : getChildren()) {
            length += child.getLength();
        }
        return length;
    }

    /**
     * Serializes the children nodes.
     * @param object the object
     * @return serialized children nodes
     */
    public String serializeChildren(Object object) {
        StringBuilder string = new StringBuilder();
        for (ParserNode child : getChildren()) {
            string.append(child.serialize(object));
        }
        return string.toString();
    }

    /**
     * 
     * Returns {@link #serializeChildren(java.lang.Object) }.
     * @param object the object
     * @return {@link #serializeChildren(java.lang.Object) }
     */
    public String serialize(Object object) {
        return serializeChildren(object);
    }

    /**
     * Deserializes the children nodes.
     * @param object the object
     * @param message the message
     * @return deserialized children nodes
     */
    public Object deserializeChildren(Object object, String message) {
        for (ParserNode child : getChildren()) {
            object = child.deserialize(object, message);
        }
        return object;
    }

    /**
     * Returns {@link #deserializeChildren(java.lang.Object, java.lang.String) }
     *
     * @param object the object
     * @param message the message
     * @return {@link #deserializeChildren(java.lang.Object, java.lang.String) }
     */
    public Object deserialize(Object object, String message) {
        return deserializeChildren(object, message);
    }
    
    /**
     * ToString method for the children nodes.
     * @return toString for children nodes
     */
    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < getChildren().size(); i++) {
            ParserNode child = getChildren().get(i);
            string.append("Child ").append(i).append(": ").append(child.getContent());
            if (i + 1 < getChildren().size()) {
                string.append("; ");
            }
        }
        return string.toString();
    }

}

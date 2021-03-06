package com.evernym.verity.sdk.handlers;

import com.evernym.verity.sdk.exceptions.InvalidMessageTypeException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.utils.DbcUtil;
import org.json.JSONObject;

/**
 * Defines how to handle a message of a certain type and optionally with a particular status
 */
public class MessageHandler {
    private final MessageFamily messageFamily;
    private final Handler messageHandler;

    public interface Handler {
        void handle(String msgName, JSONObject message);
    }

    /**
     * Associate a handler with a particular message type
     * @param family the type of message to be handled
     * @param messageHandler the handler function itself
     */
    MessageHandler(MessageFamily family, Handler messageHandler) {
        DbcUtil.requireNotNull(family, "family");
        DbcUtil.requireNotNull(messageHandler, "messageHandler");

        this.messageFamily = family;
        this.messageHandler = messageHandler;
    }

    /**
     * Checks to see if this MessageHandler handles a particular agent message
     * @param message the JSON structure of the agent message
     * @return whether or not this MessageHandler handles the given message
     */
    public boolean handles(JSONObject message) {
        if(this.messageFamily == null) return false;

        return this.messageFamily.matches(message.optString("@type"));
    }

    /**
     * Calls the handler function on the agent message
     * @param message the JSON structure of the agent message
     */
    public void handle(JSONObject message) throws InvalidMessageTypeException {
        String msgType = message.getString("@type");
        String msgName = this.messageFamily.messageName(msgType);

        this.messageHandler.handle(msgName, message);
    }
}

package pl.kamjer.shoppinglist.websocketconnect.message;

public enum Command {
    /**
     * Message for establishing connection with service
     */
    CONNECT,
    /**
     * Response message for informing client that connection was established, header id holds information what session id this client is assigned too
     */
    CONNECTED,
    /**
     * General purpose messaging commend used for sending a body to a service, header are: id - of a source session, dest - to what topic a message is aimed at, body - payload of a message
     */
    MESSAGE,
    /**
     * Message serving a way for a client do subscribe to a specific topic
     */
    SUBSCRIBE,
    /**
     * Message for confirming to a client that subscription was successful
     */
    SUBSCRIBED,
    /**
     * Message for unsubscribing a topic
     */
    UNSUBSCRIBE,
    /**
     * Message for confirming unsubscribing
     */
    UNSUBSCRIBED,
    /**
     * Message for sending error to a client
     */
    ERROR
}

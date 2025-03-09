package org.exchange;

public class Action implements MessageExchange {
    private final Commands command;
    private Clients  message;

    public Action(Commands command) {
        this.command = command;
    }

    public Action(Commands command, Clients message) {
        this.command = command;
        this.message = message;
    }

    @Override
    public Commands getCommands() {
        return command;
    }

    @Override
    public Clients getMessage() {
        return message;
    }
}

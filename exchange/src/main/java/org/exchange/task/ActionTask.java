package org.exchange.task;

import org.exchange.Commands;
import org.exchange.MessageExchange;

import java.util.List;

public class ActionTask implements MessageExchange {
    private final Commands commands;
    private final ExchangeTask exchangeTask;

    public ActionTask(Commands commands, ExchangeTask exchangeTask) {
        this.commands = commands;
        this.exchangeTask = exchangeTask;
    }

    @Override
    public Commands getCommands() {
        return this.commands;
    }

    @Override
    public ExchangeTask getMessage() {
        return this.exchangeTask;
    }
}

package org.exchange.calendar;

import org.exchange.Commands;
import org.exchange.MessageExchange;

public class ActionCalendar implements MessageExchange {
    private final Commands command;
    private final ExchangeCalendar calendar;

    public ActionCalendar(Commands command, ExchangeCalendar calendar) {
        this.command = command;
        this.calendar = calendar;
    }

    @Override
    public Commands getCommands() {
        return command;
    }

    @Override
    public ExchangeCalendar getMessage() {
        return calendar;
    }
}

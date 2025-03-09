package org.exchange;

import java.io.Serializable;

public interface MessageExchange extends Serializable {
    public Commands getCommands();
    public Object getMessage();
}

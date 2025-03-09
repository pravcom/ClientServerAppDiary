module exchange {
    requires static lombok;
    requires java.sql;
    requires jdk.compiler;
    exports org.exchange;
    exports org.exchange.calendar;
    exports org.exchange.task;
}
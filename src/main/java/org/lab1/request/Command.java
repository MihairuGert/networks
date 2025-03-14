package org.lab1.request;

public sealed interface Command permits Connect {
    CommandName commandName = null;
}

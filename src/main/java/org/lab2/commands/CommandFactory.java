package org.lab2.commands;

public class CommandFactory {
    public Command newCommand(String args) {
        String[] parsedArgs = args.split(" ");
        return switch (parsedArgs[0]) {
            case "DHCPDISCOVER" -> new Discover(parsedArgs);
            case "DHCPREQUEST" -> new Request(parsedArgs);
            default -> null;
        };
    }
}

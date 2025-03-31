package org.lab2.commands;

public class CommandFactory {
    public Command newCommand(String args) {
        String[] parsedArgs = args.split(" ");
        switch (parsedArgs[0]) {
            case "Discover":
                return new Discover(parsedArgs);
            case "Request":
                return new Request(parsedArgs);
            case "Offer":
                return new Offer(parsedArgs);
            case "Acknowledge":
                return new Acknowledge(parsedArgs);
        }
        return null;
    }
}

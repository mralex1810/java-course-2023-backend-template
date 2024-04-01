package edu.java.scrapper.exceptions;

public class LinkNotFoundException extends ResourceNotFoundException {
    public LinkNotFoundException(String s) {
        super(s);
    }
}

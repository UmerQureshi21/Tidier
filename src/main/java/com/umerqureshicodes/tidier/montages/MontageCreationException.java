package com.umerqureshicodes.tidier.montages;

// Thrown when a montage can't be created, the message is shown to the user
public class MontageCreationException extends RuntimeException {
    public MontageCreationException(String message) {
        super(message);
    }
}

package br.apae.ged.application.exceptions;

public class ActiveUserException extends RuntimeException{

    public ActiveUserException(String message){
        super(message);
    }
}

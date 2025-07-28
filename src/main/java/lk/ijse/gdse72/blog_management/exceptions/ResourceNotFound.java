package lk.ijse.gdse72.back_end.exceptions;

public class ResourceNotFound extends RuntimeException{
    public ResourceNotFound(String message) {
        super(message);// This will pass the message to the parent class RuntimeException
    }



}

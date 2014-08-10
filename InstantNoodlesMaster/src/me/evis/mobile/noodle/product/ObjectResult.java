package me.evis.mobile.noodle.product;

public class ObjectResult<T> {
    private boolean isSuccess;
    private String message;
    private T result;

    public ObjectResult(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
    
    public ObjectResult(boolean isSuccess, String message, T result) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.result = result;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
    
    public String getMessage() {
        return message;
    }

    public T getResult() {
        return result;
    }
}

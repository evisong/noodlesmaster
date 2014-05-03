package me.evis.mobile.noodle.product;

public class StringResult {
    private boolean isSuccess;
    private String result;

    public StringResult(boolean isSuccess, String result) {
        this.isSuccess = isSuccess;
        this.result = result;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getResult() {
        return result;
    }
}

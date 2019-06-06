package io.github.keep2iron.app;

/**
 * Created by keep2iron on ${Date}.
 * write the powerful code ！
 * website : keep2iron.github.io
 */
public class BaseResponse<T> {

    private int code;
    private String message;
    private T value;

    public BaseResponse(T value) {
        this.message = "successful";
        this.code = 200;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}

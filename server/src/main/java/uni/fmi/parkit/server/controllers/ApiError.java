package uni.fmi.parkit.server.controllers;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class ApiError {

    private int status;

    private String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> messages;

    public ApiError(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public ApiError(int status, String message, List<String> messages) {
        this.status = status;
        this.message = message;
        this.messages = messages;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}

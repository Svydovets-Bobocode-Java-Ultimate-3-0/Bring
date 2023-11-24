package svydovets.web.dto;

public enum HttpStatus {

    OK(200),
    CREATED(201),
    BAD_REQUEST(400),
    NOT_FOUND(404),
    SERVER_ERROR(500);

    int status;
    HttpStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

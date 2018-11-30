import lombok.Getter;
import lombok.ToString;

@ToString
class Request {
    Request(User user, String message) {
        this.user = user;
        this.message = message;
    }

    @Getter
    private User user;
    @Getter
    private String message;
}

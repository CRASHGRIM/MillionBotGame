import lombok.Getter;
import lombok.ToString;

@ToString
class Request {
    @Getter
    private User user;
    @Getter
    private String message;
    Request(User user, String message) {
        this.user = user;
        this.message = message;
    }
}

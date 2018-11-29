import lombok.Getter;
import lombok.ToString;

@ToString
class Request {
    Request(User user, String request) {
        this.user = user;
        this.request = request;
    }

    @Getter
    User user;
    @Getter
    String request;
}

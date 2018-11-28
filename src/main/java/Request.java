import lombok.Getter;
import lombok.ToString;

@ToString
public class Request {
    public Request(Integer userId, String request) {
        this.userId = userId;
        this.request = request;
    }

    @Getter
    Integer userId;
    @Getter
    String request;
}

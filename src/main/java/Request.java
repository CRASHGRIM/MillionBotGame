import lombok.Getter;
import lombok.ToString;

@ToString
class Request {
    @Getter
    private int userID;
    @Getter
    private User.Platform platform;
    @Getter
    private String message;
    @Getter
    private User user;
    Request(User user, String message) {
        this.user = user;
        this.userID = user.getId();
        this.platform = user.getPlatform();
        this.message = message;
    }
    Request(int id, User.Platform platform, String message) {
        this.userID = id;
        this.platform = platform;
        this.message = message;
    }
}

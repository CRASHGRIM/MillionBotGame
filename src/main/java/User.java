import lombok.Getter;
import lombok.ToString;

@ToString
class User {
    @Getter
    private Platform platform;
    @Getter
    private Integer id;

    User(Platform platform, Integer id) {
        this.platform = platform;
        this.id = id;
    }
    enum Platform{CONSOLE, VK, TELEGRAM}
}

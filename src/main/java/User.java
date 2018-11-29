import lombok.Getter;
import lombok.ToString;

@ToString
public class User {
    @Getter
    private Platform platform;
    @Getter
    private Integer id;

    public User(Platform platform, Integer id) {
        this.platform = platform;
        this.id = id;
    }

    enum Platform{CONSOLE, VK, TELEGRAM}
}

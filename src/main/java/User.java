import lombok.Getter;
import lombok.ToString;

@ToString
class User {
    @Getter
    private Platform platform;
    @Getter
    private Integer id;
    @Getter
    private Integer score;

    User(Platform platform, Integer id) {
        this.platform = platform;
        this.id = id;
        this.score = 0;
    }

    public void scorePoints(Integer points)
    {
        score+=points;
    }

    enum Platform{CONSOLE, VK, TELEGRAM}
}

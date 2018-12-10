import lombok.Getter;
import lombok.ToString;

@ToString
class User {
    @Getter
    private Platform platform;
    @Getter
    private Integer id;
    @Getter
    private int score;
    @Getter
    private String name;

    User(Platform platform, Integer id) {
        this.platform = platform;
        this.id = id;
        this.score = 0;
        this.name = id.toString();
    }

    User(Platform platform, Integer id, String name) {
        this.platform = platform;
        this.id = id;
        this.score = 0;
        this.name = name;
    }

    void addScore(int points) {
        score += points;
    }

    void bankrupt() {score = 0;}

    enum Platform {CONSOLE, VK, TELEGRAM}
}

import lombok.Getter;
import lombok.Setter;
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
    @Getter
    @Setter
    private Integer currentGameIdentifier;

    User(Platform platform, Integer id) {
        this.platform = platform;
        this.id = id;
        this.score = 0;
        this.name = id.toString();
        this.currentGameIdentifier = null;
    }

    User(Platform platform, Integer id, String name) {
        this.platform = platform;
        this.id = id;
        this.score = 0;
        this.name = name;
        this.currentGameIdentifier = null;
    }

    void addScore(int points) {
        score += points;
    }

    void bankrupt() {score = 0;}

    String getTag(){
        return platform + ":" + id;
    }

    enum Platform {CONSOLE, VK, TELEGRAM}
}

import lombok.Getter;
import lombok.Setter;

public class Question {
    @Getter
    private String question;
    @Getter
    private String answer;

    Question(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
}

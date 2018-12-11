import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
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

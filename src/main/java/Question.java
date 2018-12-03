import lombok.Getter;
import lombok.Setter;

public class Question {
    @Getter
    private String question;
    @Getter
    @Setter
    private String answer;

    Question(String question)
    {
        this.question = question;
    }
}

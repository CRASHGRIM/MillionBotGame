import lombok.Getter;

public class Question {
    @Getter
    private String question;
    @Getter
    private String answerA;
    @Getter
    private String answerB;
    @Getter
    private String answerC;
    @Getter
    private String answerD;
    @Getter
    private answerVariant trueAnswer;

    public Question(String question, String answerA, String answerB, String answerC, String answerD, answerVariant trueAnswer) {
        this.question = question;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
        this.trueAnswer = trueAnswer;
    }

    public boolean checkAnswer(answerVariant answerVariant) {
        return answerVariant == trueAnswer;
    }

    enum answerVariant {A, B, C, D}
}

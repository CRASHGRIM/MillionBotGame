import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


class FileUtils {

    static ArrayList<Question> questionsFileToQuestionsList(String fileName) {
        ArrayList<Question> questions = new ArrayList<>();
        try (FileReader fileReader = new java.io.FileReader(fileName);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] questionAndAnswer = line.split("\\|");
                questions.add(new Question(questionAndAnswer[0], questionAndAnswer[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questions;
    }

    static String readToken(String fileWithToken) {
        try (java.io.FileReader fileReader = new java.io.FileReader(fileWithToken);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            return bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

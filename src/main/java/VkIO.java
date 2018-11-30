import com.petersamokhin.bots.sdk.clients.Group;
import com.petersamokhin.bots.sdk.objects.Message;

import java.io.BufferedReader;
import java.io.IOException;

class VkIO {
    private static Group group = new Group(Config.VK_GROUD_ID, readToken());
    private final User.Platform platform = User.Platform.VK;

    private IOMultiplatformProcessor IOMultiplatformProcessor;

    VkIO(IOMultiplatformProcessor IOMultiplatformProcessor) {
        this.IOMultiplatformProcessor = IOMultiplatformProcessor;

        group.onSimpleTextMessage(message ->
                this.IOMultiplatformProcessor.pushRequest(new Request(new User(platform, message.authorId()), message.getText()))
        );
    }

    void sendMessage(int id, String message) {
        new Message()
                .from(group)
                .to(id)
                .text(message)
                .send();
    }

    private static String readToken() {
        try (java.io.FileReader fileReader = new java.io.FileReader(Config.FILE_NAME_VK_TOKEN);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            return bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

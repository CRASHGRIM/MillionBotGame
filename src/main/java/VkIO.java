import com.petersamokhin.bots.sdk.clients.Group;
import com.petersamokhin.bots.sdk.objects.Message;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

class VkIO implements IOInterface{
    private static Group group = new Group(Config.VK_GROUD_ID, FileUtils.readToken(Config.FILE_NAME_VK_TOKEN));
    private final User.Platform platform = User.Platform.VK;

    private IOMultiplatformProcessor IOMultiplatformProcessor;

    VkIO(IOMultiplatformProcessor IOMultiplatformProcessor) {
        this.IOMultiplatformProcessor = IOMultiplatformProcessor;

        group.onSimpleTextMessage(message ->
                this.IOMultiplatformProcessor.pushRequest(new Request(new User(platform, message.authorId()), message.getText()))
        );
    }
    public void sendMessage(int id, String message) {
            new Message()
                .from(group)
                .to(id)
                .text(message)
                .send();
    }
}

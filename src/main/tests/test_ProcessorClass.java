import java.util.ArrayList;

public class test_ProcessorClass extends IOMultiplatformProcessor{

    public ArrayList<Request> messages;

    test_ProcessorClass(){
        messages = new ArrayList<Request>();
        System.out.println("Hello");
    }

    void sendMes(Request request){
        messages.add(request);
    }

}

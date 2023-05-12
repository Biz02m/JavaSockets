import java.io.IOException;
import java.net.Socket;

public class Client implements Runnable{
    private Socket clientSock;
    private String nickname;
    private Integer n;

    @Override
    public void run() {
        try (Socket client = new Socket("127.0.0.1",9999)){
            this.clientSock = client;

        } catch (IOException e){
            //TODO: handle exception
        }
    }


    public static void main(String[] args) {

    }
}

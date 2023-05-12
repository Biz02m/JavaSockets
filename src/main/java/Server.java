import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable{
    private ServerSocket serverSock;
    private boolean done;

    public Server(){
        done = false;
    }

    //main server logic
    @Override
    public void run() {
        try (ServerSocket tmp = new ServerSocket(9999)){
            this.serverSock = tmp;
            try (Socket socket = serverSock.accept()){

            }
        } catch (IOException e) {
            //TODO: handle exception
        }
    }

    class connectionHandler implements Runnable{
        private Socket client;
        private String nickname;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        //just save the client socket
        public connectionHandler(Socket socket){
            this.client = socket;
        }

        //single connection handler logic
        @Override
        public void run() {
            System.out.println("Server started");
            try ( ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                  ObjectInputStream ois = new ObjectInputStream(client.getInputStream())
                    ){
                    System.out.println("Awaiting Connections...");
                    this.out = oos;
                    this.in = ois;
                    sendMessage("Put in nickname: ");
                    in.readObject();

            }catch (IOException e){
                //TODO: handle IO exception
            } catch (ClassNotFoundException e) {
                //TODO: handle ClassNotFoundException exception
            }
        }

        public void sendMessage(Object message) throws IOException{
            this.out.writeObject(message);
        }
    }

    public static void main(String[] args) {

    }
}

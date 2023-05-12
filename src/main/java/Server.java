import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable{
    private List<connectionHandler> connections;
    private ExecutorService threadPool; //don't know what thread-pooling is,  I have no time to research
    private ServerSocket serverSock;
    private boolean done;

    public Server(){
        done = false;
        connections = new ArrayList<>();
    }

    //main server logic
    @Override
    public void run() {
        try (ServerSocket tmp = new ServerSocket(9999)){
            System.out.println("Server started");
            this.serverSock = tmp;
            threadPool = Executors.newCachedThreadPool();
            while(!done) {
                try (Socket client = serverSock.accept()) {
                    connectionHandler conhand = new connectionHandler(client);
                    connections.add(conhand);
                    threadPool.execute(conhand);
                }
            }
        } catch (IOException e) {
            shutDown();
        }
    }

    public void shutDown(){
        this.done = true;
        if(!serverSock.isClosed()){
            try {
                serverSock.close();
            } catch (IOException e) {
                //ignore exception
            }
        }

        for (connectionHandler con: this.connections) {
            con.shutDown();
        }
    }

    class connectionHandler implements Runnable{
        private Socket client;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        //just save the client socket
        public connectionHandler(Socket socket){
            this.client = socket;
        }

        //single connection handler logic
        @Override
        public void run() {
            try ( ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                  ObjectInputStream ois = new ObjectInputStream(client.getInputStream())
                    ){
                System.out.println("Awaiting Connections...");
                this.out = oos;
                this.in = ois;
                sendMessage("Put in nickname: ");
                String nick = (String) in.readObject();
                System.out.println("Connected to: " + nick);
                sendMessage("ready...");
                Integer n = (Integer) in.readObject();
                sendMessage("ready for " + n + " messages...");

                for(int i = 0; i < n; i++){
                    Message message = (Message) in.readObject();
                    //TODO: print received Messages?
                    // Do something with the messages
                }

                sendMessage("finished");
                System.out.println("Processed " + nick +"`s request. closing connection");
                shutDown();

            }catch (IOException e){
                e.printStackTrace();
                shutDown();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                shutDown();
            }
        }

        public void sendMessage(Object message) throws IOException{
            this.out.writeObject(message);
        }

        public void shutDown(){
            try{
                this.in.close();
                this.out.close();
                if(!client.isClosed()){
                    client.close();
                }
                connections.remove(this);
            } catch (IOException e){
               //ignore exception
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable{
    private List<connectionHandler> connections;
    private ExecutorService threadPool;
    private ServerSocket serverSock;
    private boolean done;

    public Server(){
        done = false;
        connections = new ArrayList<>();
    }

    //main server logic
    @Override
    public void run() {
        try {
            this.serverSock = new ServerSocket(9999);
            System.out.println("Server started");
            threadPool = Executors.newCachedThreadPool();
            System.out.println("Awaiting Connections...");
            Thread handler = new Thread(new serverHandler());
            handler.start();
            while(!done) {
                try {
                    Socket client = serverSock.accept();
                    connectionHandler conhand = new connectionHandler(client);
                    connections.add(conhand);
                    threadPool.execute(conhand);
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    class serverHandler implements Runnable{
        private Scanner sc;

        @Override
        public void run() {
            System.out.println("Server ready to be closed.");
            this.sc = new Scanner(System.in);
            String input;
            while(!done){
                input = sc.nextLine();
                if(input.equals("quit")){
                    shutDown();
                }
            }
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
            try {
                ObjectOutputStream oos = new ObjectOutputStream(this.client.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(this.client.getInputStream());
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
                    System.out.println(nick + " -> " + message.getNumber() + ": " + message.getContent());
                }

                sendMessage("finished");
                System.out.println("Processed " + nick +"`s request. closing connection");
                shutDown();

            }catch (IOException | ClassNotFoundException e){
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

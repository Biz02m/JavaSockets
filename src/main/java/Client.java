import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable{
    private Socket clientSock;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Scanner sc;
    private String nickname;
    private Integer n;

    public Client(){
        sc = new Scanner(System.in);
    }

    @Override
    public void run() {
        try (Socket client = new Socket("127.0.0.1",9999)){
            try(ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(client.getInputStream())) {
                this.clientSock = client;
                this.in = ois;
                this.out = oos;
                String msg = (String) in.readObject();
                System.out.println(msg);
                Integer n = sc.nextInt();
                out.writeObject(sc);
                msg = (String) in.readObject();
                System.out.println(msg);

                for (int i = 0; i < n; i++) {
                    msg = sc.nextLine();
                    Message message = new Message(i, msg);
                    out.writeObject(message);
                }

                msg = (String) in.readObject();
                System.out.println(msg);
                System.out.println("finished sending contents to server, closing connection");
                shutDown();
            }

        } catch (IOException e){
            shutDown();
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            shutDown();
            e.printStackTrace();
        }
    }

    public void shutDown() {
        try{
            if(this.in != null) {
                this.in.close();
            }
            if(this.out != null) {
                this.out.close();
            }
            if(!clientSock.isClosed()){
                clientSock.close();
            }
        }catch (IOException e){
            //ignore exception
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}

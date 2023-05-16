import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
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
        try{
            Socket socket = new Socket(InetAddress.getLocalHost(), 9999);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            this.clientSock = socket;
            //receive put in nickname
            String msg = (String) in.readObject();
            System.out.println(msg);

            //send nickname
            String nickname = sc.nextLine();
            out.writeObject(nickname);

            //receive ready msg
            msg = (String) in.readObject();
            System.out.println(msg);

            //send the integer value
            Integer n = sc.nextInt();
            sc.nextLine();
            out.writeObject(n);

            //receive ready for n msg
            msg = (String) in.readObject();
            System.out.println(msg);
            out.flush();

            //send n msg
            for (int i = 0; i < n; i++) {
                msg = sc.nextLine();
                Message message = new Message(i, msg);
                out.writeObject(message);
            }

            //receive finished
            msg = (String) in.readObject();
            System.out.println(msg);
            System.out.println("finished sending contents to server, closing connection");
            shutDown();
        } catch (IOException | ClassNotFoundException e){
            //e.printStackTrace();
            System.out.println("Connection was closed");
            shutDown();
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

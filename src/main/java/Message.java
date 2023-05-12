import java.io.Serializable;

public class Message implements Serializable {
    static final long serialVersionUID = 2048;
    private int number;
    private String content;

    public Message(int number, String content){
        this.number = number;
        this.content = content;
    }

    public int getNumber() {
        return number;
    }

    public String getContent() {
        return content;
    }
}

package ng.transfer.support.bean;

/**
 * Created by Joe on 2014/6/16.
 */
public class SMSBean {

    private String number;
    private String body;
    private String type;
    private String date;
    private String read;

    public SMSBean(String number, String body, String type, String date, String read) {
        this.number = number;
        this.body = body;
        this.type = type;
        this.date = date;
        this.read = read;
    }

}

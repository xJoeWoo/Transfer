package ng.transfer.support.bean;

/**
 * Created by Joe on 2014/6/16.
 */
public class CallRecordBean {

    private String number;
    private String name;
    private String type;
    private String date;
    private String duration;

    public CallRecordBean(String number, String name, String type, String date, String duration) {
        this.number = number;
        this.name = name;
        this.type = type;
        this.date = date;
        this.duration = duration;

    }

}

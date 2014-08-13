package ng.transfer.support.bean;

import java.util.List;

/**
 * Created by Joe on 2014/6/16.
 */
public class InfoBean {

    private String uuid;
    private String brand;
    private String model;
    private List<CallRecordBean> callRecord;
    private List<ContactsBean> contacts;
    private List<SMSBean> sms;

    public InfoBean(String uuid, String brand, String model) {
        this.uuid = uuid;
        this.brand = brand;
        this.model = model;
    }

    public void setCallRecord(List<CallRecordBean> callRecord) {
        this.callRecord = callRecord;
    }

    public void setContacts(List<ContactsBean> contacts) {
        this.contacts = contacts;
    }

    public void setSms(List<SMSBean> sms) {
        this.sms = sms;
    }
}

package model;

public class Appointment {
    private int id;
    private String date;
    private String time;
    private String service;
    private String customerName;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}
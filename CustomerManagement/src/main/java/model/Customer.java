package model;

import java.sql.Date;

/**
 *
 * @author ADMIN
 */
public class Customer {
    private int id; 
    private String name;
    private String gender;
    private Date dob;

    // Constructor không tham số
    public Customer() {}

    // Constructor không có id
    public Customer(String name, String gender, Date dob) {
        this.name = name;
        this.gender = gender;
        this.dob = dob;
    }

    // Constructor đầy đủ tham số
    public Customer(int id, String name, String gender, Date dob) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.dob = dob;
    }

    // Getter và Setter
    public int getId() {
        return id;
    }

    public void setId(int id) { 
        this.id = id; 
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    @Override
    public String toString() {
        return "Customer{" + "id=" + id + ", name=" + name + ", gender=" + gender + ", dob=" + dob + '}';
    }
}

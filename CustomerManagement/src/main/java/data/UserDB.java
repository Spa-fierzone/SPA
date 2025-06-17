/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

import common.Constant;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import model.User;

/**
 *
 * @author ADMIN
 */
public class UserDB {
    public ArrayList<User> getAll(){
        ArrayList<User> uList = new ArrayList<>();
        try(PreparedStatement ps = DBContext.getConnection().prepareStatement(Constant.USER_GET_ALL)){
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int id = rs.getInt("userID");
                String username = rs.getString("username");
                String password = rs.getString("password");
                uList.add(new User(id, username, password));
            }
            return uList;
        }catch(Exception e){
            System.out.println("Error get all user");
            e.printStackTrace();
        }
        return uList;
    }
    public boolean add(User c) {
        boolean check = false;
        try (PreparedStatement ps = DBContext
                .getConnection()
                .prepareStatement(Constant.USER_ADD)) {
            ps.setString(1, c.getUsername());
            ps.setString(2, c.getPassword());
            int result = ps.executeUpdate();
            if (result > 0) {
                check = true;
            }
        } catch (Exception e) {
            System.out.println("Error add");
            e.printStackTrace();
        }
        return check;
    }
        public static void main(String[] args) {
            UserDB u = new UserDB();
            for(User ui: u.getAll()){
                System.out.println(ui.toString());
            }
        }
}

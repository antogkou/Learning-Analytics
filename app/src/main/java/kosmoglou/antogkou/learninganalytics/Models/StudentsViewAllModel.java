package kosmoglou.antogkou.learninganalytics.Models;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class StudentsViewAllModel {
    private String fullname;
    private String user_id;
    private String username;
    private String usertype;

    public StudentsViewAllModel() {
    }

    public StudentsViewAllModel(String fullname, String user_id, String username, String usertype) {
        this.fullname = fullname;
        this.user_id = user_id;
        this.username = username;
        this.usertype = usertype;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String name) {
        this.fullname = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

}

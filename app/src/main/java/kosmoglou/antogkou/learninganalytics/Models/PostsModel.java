package kosmoglou.antogkou.learninganalytics.Models;


import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class PostsModel {
    @ServerTimestamp
    Date postdate;
    private String title;
    private String description;
    private String creator_userid;
    private String documentId;

    public PostsModel() {
    }

    public PostsModel(String title, String description, String creator_userid, String documentId) {
        this.title = title;
        this.description = description;
        this.creator_userid = creator_userid;
        this.documentId = documentId;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String fullname) {
        this.title = fullname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String username) {
        this.description = username;
    }


    public String getCreator_userid() { return creator_userid;
    }

    public void setCreator_userid(String creator_userid) { this.creator_userid = creator_userid;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }


    public Date getPostdate() {
        return postdate;
    }

    public void setPostdate(Date postdate) {
        this.postdate = postdate;
    }

    }


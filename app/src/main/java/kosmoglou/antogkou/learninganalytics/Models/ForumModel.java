package kosmoglou.antogkou.learninganalytics.Models;

import android.text.format.DateFormat;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.text.SimpleDateFormat;
import java.util.Date;

@IgnoreExtraProperties
public class ForumModel {
    @ServerTimestamp
    Date postdate;

    private String description;
    private String title;
    private String currentUserID;
    private String documentId;
    private String creator_userid;

    public ForumModel() {
    }

    public ForumModel(String description, String time, String title, String currentUserID, String documentId, String creator_userid) {
        this.description = description;
        this.title = title;
        this.currentUserID = currentUserID;
        this.documentId = documentId;
        this.creator_userid = creator_userid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCurrentUserID() {
        return currentUserID;
    }

    public void setCurrentUserID(String currentUserID) {
        this.currentUserID = currentUserID;
    }


    public Date getPostdate() {
        return postdate;
    }

    public void setPostdate(Date postdate) {
        this.postdate = postdate;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }


    public String getCreator_userid() {
        return creator_userid;
    }

    public void setCreator_userid(String creator_userid) {
        this.creator_userid = creator_userid;
    }

}

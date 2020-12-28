package kosmoglou.antogkou.learninganalytics.Models;

import android.support.annotation.Nullable;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class CommentsModel {
    @ServerTimestamp
    Date comment_date;

    private String description;
    private String title;
    private String currentUserID;
    private String comment;
    private String fullname;
    private String user_id;
    private String commentID;


    public CommentsModel() {
    }
//to String comment_date δείχνει το timestamp του comment
    public CommentsModel(String description, String title, String currentUserID, String comment, String fullname, String user_id, String commentID) {
        this.description = description;
        this.title = title;
        this.currentUserID = currentUserID;
        this.comment = comment;
        this.fullname = fullname;
        this.user_id = user_id;
        this.commentID = commentID;
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



    public Date getComment_date() {
        return comment_date;
    }
    public void setComment_date(Date comment_date) {
        this.comment_date = comment_date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }


}

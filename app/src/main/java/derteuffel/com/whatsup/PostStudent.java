package derteuffel.com.whatsup;


import java.util.Date;

public class PostStudent {

    public String image_url, title, user_id, image_thumb;

    public Date timeStamp;


    public PostStudent() {
    }

    public PostStudent(String image_url, String title, String user_id, String image_thumb, Date timeStamp) {
        this.image_url = image_url;
        this.title = title;
        this.user_id = user_id;
        this.image_thumb = image_thumb;
        this.timeStamp = timeStamp;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}

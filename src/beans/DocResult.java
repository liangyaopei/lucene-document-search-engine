package beans;

/**
 * Created by Peter on 2017/6/11 0011.
 */
public class DocResult {
    private String title;
    private String body;
    private String time;
    private String apartment;
    private String attachment;
    private String click;

    public void setClick(String click) {
        this.click = click;
    }

    public String getClick() {
        return click;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getTime() {
        return time;
    }

    public String getApartment() {
        return apartment;
    }

    public String getAttachment() {
        return attachment;
    }
}

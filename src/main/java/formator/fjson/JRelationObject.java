package formator.fjson;


// update2

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class JRelationObject {

    // 存储各个实体之间关系的信息

    private int from;
    private int to;
    private String category;
    private JLocation location;

    public int getFrom() {
        return from;
    }

    @XmlAttribute(name = "from")
    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    @XmlAttribute(name = "to")
    public void setTo(int to) {
        this.to = to;
    }

    public String getCategory() {
        return category;
    }

    @XmlAttribute(name = "category")
    public void setCategory(String category) {
        this.category = category;
    }

    @XmlElement
    public JLocation getLocation() {
        return location;
    }

    public void setLocation(JLocation location) {
        this.location = location;
    }
}

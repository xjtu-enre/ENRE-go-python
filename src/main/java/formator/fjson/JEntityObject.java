package formator.fjson;

// update2

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

@XmlRootElement(name = "entities")
public class JEntityObject {

    // 存储实体的信息

    private int id;
    private String name;

    // update 2.2
    private String qualifyname;
    // end

    private int parent;
    private String category;
    private Map<String,JLocation> identifier;

    public String getQualifyname() {
        return qualifyname;
    }

    public void setQualifyname(String qualifyname) {
        this.qualifyname = qualifyname;
    }

    public int getId() {
        return id;
    }

    @XmlAttribute(name = "id")
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @XmlAttribute(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    public int getParent() {
        return parent;
    }

    @XmlAttribute(name = "parent")
    public void setParent(int parent) {
        this.parent = parent;
    }

    public String getCategory() {
        return category;
    }

    @XmlAttribute(name = "category")
    public void setCategory(String category) {
        this.category = category;
    }

    @XmlElement
    public Map<String, JLocation> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Map<String, JLocation> identifier) {
        this.identifier = identifier;
    }
}

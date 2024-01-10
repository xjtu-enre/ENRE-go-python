package formator.fjson;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

// update2

@XmlRootElement(name = "Entity_level")
public class NewJDepObject {

    private ArrayList<JEntityObject> entities;
    private ArrayList<JRelationObject> relations;

    public ArrayList<JEntityObject> getEntities() {
        return entities;
    }

    @XmlElement
    public void setEntities(ArrayList<JEntityObject> entities) {
        this.entities = entities;
    }

    public ArrayList<JRelationObject> getRelations() {
        return relations;
    }

    @XmlElement
    public void setRelations(ArrayList<JRelationObject> relations) {
        this.relations = relations;
    }
}

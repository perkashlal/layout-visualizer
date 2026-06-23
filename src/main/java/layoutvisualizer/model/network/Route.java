package layoutvisualizer.model.network;
import java.util.List;
import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class Route {
    @XmlAttribute(name="id")
    private String id;

    @XmlAttribute(name="source")
    private String source;

    @XmlAttribute(name="destination")
    private String destination;

    @XmlAttribute(name="dir")
    private String dir;

    @XmlElement(name="condition")
    private List<Condition> condition;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public List<Condition> getCondition() {
        return condition;
    }

    public void setCondition(List<Condition> condition) {
        this.condition = condition;
    }

    public void pulisciRoute() {
        if(condition!=null){
            condition.clear();
        }
    }
}

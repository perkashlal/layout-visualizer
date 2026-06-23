package layoutvisualizer.model.network;
import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class Neighbor {
    @XmlAttribute(name="ref")
    private String ref;

    @XmlAttribute(name="side")
    private String side;
    
    public String getRef() {
        return ref;
    }
    public void setRef(String ref) {
        this.ref = ref;
    }
    public String getSide() {
        return side;
    }
    public void setSide(String side) {
        this.side = side;
    }

    public Neighbor(String ref, String side){
        this.ref = ref;
        this.side = side;
    }

    public Neighbor(){
        this.ref = "";
        this.side = "";
    }
    public static Neighbor deepCopy(Neighbor old) {
        Neighbor newN = new Neighbor();
        newN.setRef(old.getRef());
        newN.setSide(old.getSide());
        return newN;
    }
}

package layoutvisualizer.model.network;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class Condition {
    @XmlAttribute(name="type")
    private String type;

    @XmlAttribute(name="val")
    private String val;

    @XmlAttribute(name="ref")
    private String ref;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

}

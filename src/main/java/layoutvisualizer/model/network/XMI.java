package layoutvisualizer.model.network;
import jakarta.xml.bind.annotation.*;

@XmlRootElement(name="XMI", namespace = "http://www.omg.org/spec/XMI/2.4.1")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMI {

    @XmlElement(name="Documentation")
    private XmiDocumentation xmiDoc;

    @XmlElement(name="interlocking")
    private Interlocking interlocking;

    public XmiDocumentation getXmiDoc() {
        return xmiDoc;
    }

    public void setXmiDoc(XmiDocumentation xmiDoc) {
        this.xmiDoc = xmiDoc;
    }

    public Interlocking getInterlocking() {
        return interlocking;
    }

    public void setInterlocking(Interlocking interlocking) {
        this.interlocking = interlocking;
    }

    public void pulisciXMI(){
        xmiDoc = null;
        if(interlocking != null)
            interlocking.pulisciInterlocking();
    }

}

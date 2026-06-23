package layoutvisualizer.model.network;
import jakarta.xml.bind.annotation.*;

@XmlRootElement(name="interlocking")
@XmlAccessorType(XmlAccessType.FIELD)
public class Interlocking {
    @XmlAttribute(name="id")
    private String id;

    @XmlElement(name="network")
    private Network net;

    @XmlElement(name="routetable")
    private RouteTable routeTable;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Network getNet() {
        return net;
    }
    public void setNet(Network net) {
        this.net = net;
    }
    public RouteTable getRouteTable() {
        return routeTable;
    }
    public void setRouteTable(RouteTable routeTable) {
        this.routeTable = routeTable;
    }
    public void pulisciInterlocking() {
        if(net!=null){
            net.pulisciNet();
        }
        if(routeTable!=null){
            routeTable.pulisciRouteTable();
        }
    }
}

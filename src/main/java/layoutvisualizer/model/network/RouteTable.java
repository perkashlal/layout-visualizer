package layoutvisualizer.model.network;
import java.util.List;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class RouteTable {
    @XmlAttribute(name="id")
    private String id;

    @XmlAttribute(name="network")
    private String netId;

    @XmlElement(name="route")
    private List<Route> routes;
    
    public List<Route> getRoutes() {
        return routes;
    }
    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getNetId() {
        return netId;
    }
    public void setNetId(String netId) {
        this.netId = netId;
    }
    public void pulisciRouteTable() {
        if(routes != null){
            for(Route route : routes){
                if(route != null)
                    route.pulisciRoute();
            }
        }
        if(routes != null){
            routes.clear();
        }

    }
}


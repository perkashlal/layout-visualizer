package layoutvisualizer.model.network;
import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class MarkerBoard {
    @XmlAttribute(name="id")
    private String id;

    @XmlAttribute(name="distance")
    private String distance;

    @XmlAttribute(name="mounted")
    private String mounted = "";

    @XmlAttribute(name="track")
    private String track;

    @XmlTransient
    public String signal = "";

    public void assignSignal(){
        if(mounted.equalsIgnoreCase("down")){
            signal = "downsig.png";
        }else if(mounted.equalsIgnoreCase("up")){
            signal = "upsig.png";
        }
        if(mounted.length() == 0 ){
            if(id!=null && id.trim().length() > 0)
                signal = "nosig.png";
        }
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getDistance() {
        return distance;
    }
    public void setDistance(String distance) {
        this.distance = distance;
    }
    public String getMounted() {
        return mounted;
    }
    public void setMounted(String mounted) {
        this.mounted = mounted;
    }
    public String getTrack() {
        return track;
    }
    public void setTrack(String track) {
        this.track = track;
    }

    public static MarkerBoard deepCopy(MarkerBoard old) {
        MarkerBoard m = new MarkerBoard();
        if(old == null){
            return null;
        }
        m.setDistance(old.getDistance());
        m.setId(old.getId());
        m.setMounted(old.getMounted());
        m.setTrack(old.getTrack());
        return m;
    }
}

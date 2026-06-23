package layoutvisualizer.model.network;
import java.util.*;
import jakarta.xml.bind.annotation.*;

@XmlRootElement(name="network")
@XmlAccessorType(XmlAccessType.FIELD)
public class Network {
    @XmlAttribute(name="id")
    private String id;

    @XmlElement(name="trackSection")
    private List<TrackSection> tracks;

    @XmlTransient
    private HashMap<String, TrackSection> trackSections = new HashMap<String, TrackSection>();

    @XmlElement(name="markerboard")
    private List<MarkerBoard> markerBoards;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public HashMap<String, TrackSection> getTrackSections() {
        return trackSections;
    }
    public void setTrackSections() {
        this.trackSections.clear();
        if(this.tracks == null){
            return;
        }
        for (TrackSection trackSection : this.tracks) {
             this.trackSections.put(trackSection.getId(), trackSection);
        }
    }

    public List<TrackSection> getTracks() {
        return tracks;
    }
   
   public void setTracks(List<TrackSection> tracks) {
        this.tracks = tracks;
    }

    public List<MarkerBoard> getMarkerBoards() {
        return markerBoards;
    }
    public void setMarkerBoards(List<MarkerBoard> markerBoards) {
        this.markerBoards = markerBoards;
    }

    //TODO: added to reload TrackSection neighbors when Downloading XML document
    public void reloadNeighbors(){
        //iterate over trackSection and reassign neighbors
        //based on modified behaviour
        for (TrackSection trackSection : trackSections.values()) {
            trackSection.assignNeighbors();
        }
    }

    public void buildNetwork(){
        if(markerBoards == null){
            markerBoards = new ArrayList<MarkerBoard>();
        }
        for (MarkerBoard markerBoard : markerBoards) {
            TrackSection track = trackSections.get(markerBoard.getTrack());
            markerBoard.assignSignal(); 
            if(track == null)
                continue;
            if(markerBoard.getMounted().equals("down"))
                track.setLeftMarker(markerBoard);
            if(markerBoard.getMounted().equals("up")){
                track.setRightMarker(markerBoard);
            }
        }
        for (TrackSection trackSection : trackSections.values()) {
            trackSection.assignNeighbors();
            if(trackSection.isPoint()){
                MarkerBoard m = new MarkerBoard();
                m.setId(""); 
                m.setDistance(trackSection.getLength());
                m.setTrack(trackSection.getId());
                m.signal = "nosig.png";
                trackSection.setLeftMarker(m);
            }
        }
    }

    public void pulisciNet(){
        for (TrackSection trackSection : trackSections.values()) {
            trackSection.pulisciTrack();
        }
        trackSections.clear();
        if(this.tracks != null){
            for (TrackSection trackSection : this.tracks) {
                trackSection.pulisciTrack();
            }
            tracks.clear();
        }
        if(markerBoards != null){
            markerBoards.clear();
        }
    }
}

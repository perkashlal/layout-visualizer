package layoutvisualizer.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.scene.transform.Scale;
import layoutvisualizer.model.network.Neighbor;
import layoutvisualizer.model.network.Network;
import layoutvisualizer.model.network.RouteTable;
import layoutvisualizer.model.network.TrackSection;
import layoutvisualizer.model.network.XMI;
import layoutvisualizer.model.network.Interlocking;
import layoutvisualizer.model.network.MarkerBoard;


public class ModelComponent {

    private Map<String, Point> pointsBase = new HashMap<String, Point>();
    private Map<String, Linear> compsBase = new HashMap<String, Linear>();
    private XMI xmi;
    private Network net;  
    private LinkedHashMap<String,TaglioRete> tagliDiRete = new LinkedHashMap<String, TaglioRete>();

    public void parseXML(XMI xmi, AnchorPane anchorPane, Group group, ScrollPane scroller, Slider slider, String user_input){
        try{
            this.xmi = xmi;
            Interlocking interlocking = xmi.getInterlocking();
            //String id = interlocking.getId();
            //System.out.println(id);
            Network net = interlocking.getNet();
            this.net = net;
            net.setTrackSections();
            net.buildNetwork();

            Double width = Double.valueOf(0);
            Double height = Double.valueOf(0);
            Double posX = Double.valueOf(0);
            Double posY = Double.valueOf(0);

            slider.setValue(10.0);
            Double altezzaDiPartenza = 120.0;

            Map<String, Linear> comps = new HashMap<String, Linear>();
            Map<String, Point> points = new HashMap<String, Point>();
            this.tagliDiRete.clear();

            DraggableMaker draggableMaker = new DraggableMaker();
            Image baseIm = null;
            try (InputStream righInput = getClass().getResourceAsStream("/images/upsig.png")) {
                baseIm = new Image(righInput, 70, 70, true, false);
                width = baseIm.getWidth();
                height = baseIm.getHeight();
                righInput.close();
            } catch (Exception e) {
                System.out.println(e);
            }
            posY = altezzaDiPartenza + height/2;
            List<TrackSection> startTracks = new ArrayList<TrackSection>();
            for(TrackSection t : net.getTrackSections().values()){
                if(!t.isPoint() && (t.getDown() == null || t.getDown().getRef().length() == 0)){
                    startTracks.add(t);
                    //break;
                }
            }
            if(startTracks.size() == 0){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Nessuna border section valida trovata nel file XML.");
                alert.showAndWait();
                return;
            }
            anchorPane.getChildren().clear();
            draggableMaker.setScrollPane(scroller);
            TrackSection start = startTracks.get(0);
            if(user_input.length() > 0){
                start = net.getTrackSections().get(user_input);
                if(start == null){
                    start = startTracks.get(0);
                }
            }

            UtilityDraw utilityDraw = new UtilityDraw();
            utilityDraw.drawNetwork(net, start, posX, posY, width, height, altezzaDiPartenza, anchorPane, comps, points, draggableMaker);  
            for(TrackSection border : startTracks){
                if(utilityDraw.notDrawn(border, comps, points)){
                    posY = posY + 3*height;
                    utilityDraw.drawNetwork(net, border, posX, posY, width, height, altezzaDiPartenza, anchorPane, comps, points, draggableMaker);
                }
            }

            double minValueX = scroller.getContent().getLayoutBounds().getMinX();
            if(minValueX < 0){
                for(Linear compShift : comps.values()){
                    compShift.shiftOrizzontale(minValueX);
                }
                for(Point pointShift : points.values()){
                    pointShift.shiftOrizzontale(minValueX);
                }
            }

            utilityDraw.controllaRete(net, points, comps, anchorPane, height, width);

            utilityDraw.togliSovrapposizioni(comps, points, net.getTrackSections());
            
            Scale scale = new Scale();
            scale.xProperty().bind((slider.valueProperty()).divide(10.0));
            scale.yProperty().bind((slider.valueProperty()).divide(10.0));
            anchorPane.getTransforms().add(scale);
            group.getTransforms().add(scale);

            for(Linear c : comps.values()){
                compsBase.put(c.getId(), c);
            }
            for(Point p : points.values()){
                pointsBase.put(p.id, p);
            }
        }catch(Exception e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Errore durante l'upload!");
            alert.showAndWait();
        }
    }

    private List<XMI> dividePaneMultipleCuts(HashMap<String, TaglioRete> tagliDiRete) {
        List<XMI> results = new ArrayList<XMI>();
        HashMap <String,TrackSection> netTracks = net.getTrackSections();
        List<XMI> results_from_last_cut = new ArrayList<XMI>();
        int i = 0;
        for(TaglioRete taglio : tagliDiRete.values()){
            i++;
            if(results_from_last_cut.size()>0){
                netTracks = findLatoPerTaglio(results_from_last_cut, taglio);
            }
            results_from_last_cut = new ArrayList<XMI>();
            results_from_last_cut = dividePaneSingleCut(netTracks, taglio, i);
            if(results_from_last_cut.size() == 0){
                break;
            }
            for(int j = 0; j < results_from_last_cut.size(); j++){
                results.add(results_from_last_cut.get(j));
            }

        }
        return results;
    }

    private HashMap<String, TrackSection> findLatoPerTaglio(List<XMI> resultsList, TaglioRete taglio) {
        HashMap<String, TrackSection> remainingTracks = new HashMap<String, TrackSection>();
        String[] primaTraccia = taglio.getPrimoTaglio().split("-");
        //String[] secondaTraccia = taglio.getSecondoTaglio().split("-");
        boolean found = false;
        for(XMI res : resultsList){
            List<TrackSection> appoggioTracks = res.getInterlocking().getNet().getTracks();
            for(TrackSection t : appoggioTracks){
                if(t.getId().equals(primaTraccia[0])){
                    found = true;
                    break;
                }
            }
            if(found){
                for(TrackSection app : appoggioTracks){
                    remainingTracks.put(app.getId(), app);
                }
                break;
            }
        }
        return remainingTracks;
    }

    private List<XMI> dividePaneSingleCut(HashMap<String,TrackSection> netTracks, TaglioRete taglio, Integer count) {
        List<XMI> resultsAdded = new ArrayList<XMI>();
        HashMap<String,TrackSection> intersected = new HashMap<String,TrackSection>();

        TrackSection parte = new TrackSection();
        TrackSection seconda_parte = new TrackSection();
        TrackSection prima_parte = new TrackSection();

        boolean treXML = false;

        String[] primaTraccia = taglio.getPrimoTaglio().split("-");
        String[] secondaTraccia = taglio.getSecondoTaglio().split("-");

        parte = TrackSection.deepCopy(netTracks.get(primaTraccia[0]));

        ArrayList<TrackSection> listOfValues = new ArrayList<TrackSection>();
        listOfValues.add(0, parte);

        intersected.put(parte.getId(), parte);
        boolean taglioDoppio = secondaTraccia.length > 1;
        if(taglioDoppio){
            if(primaTraccia[1].equals(secondaTraccia[1]) && netTracks.get(secondaTraccia[1]).isPoint()){ //suppongo di aver tagliato i lati entranti in un punto
                seconda_parte = TrackSection.deepCopy(netTracks.get(secondaTraccia[0]));
                if(!parte.isPoint() && seconda_parte.isPoint()){
                    intersected.remove(parte.getId());
                    listOfValues.remove(0);
                    seconda_parte = TrackSection.deepCopy(netTracks.get(primaTraccia[0]));
                    parte = TrackSection.deepCopy(netTracks.get(secondaTraccia[0]));
                    listOfValues.add(0, parte);
                    intersected.put(parte.getId(), parte);
                }
                intersected.put(seconda_parte.getId(), seconda_parte);
                listOfValues.add(1, seconda_parte);
                treXML = true;
            }else if((primaTraccia[0].equals(secondaTraccia[0])) && parte.isPoint()){ //suppongo di aver tagliato i lati di un punto di separazione
                seconda_parte = TrackSection.deepCopy(netTracks.get(secondaTraccia[1]));
                prima_parte = TrackSection.deepCopy(netTracks.get(primaTraccia[1])); 
                intersected.put(seconda_parte.getId(), seconda_parte);
                intersected.put(prima_parte.getId(), prima_parte);
                listOfValues.add(1, prima_parte);
                listOfValues.add(2, seconda_parte);
                treXML = true;
            }
        }

        count++;
        XMI newXML = new XMI();
        newXML.setXmiDoc(this.xmi.getXmiDoc());
        Interlocking inter = new Interlocking();
        inter.setId(this.xmi.getInterlocking().getId() + "_" + taglio.getDefinition() + "_left");
        RouteTable rt = new RouteTable();
        rt.setId(this.xmi.getInterlocking().getRouteTable().getId() + "_" + taglio.getDefinition() +  "_left");
        rt.setNetId(this.xmi.getInterlocking().getRouteTable().getNetId() + "_" + taglio.getDefinition() +  "_left");
        inter.setRouteTable(rt);
        Network newNet = new Network();
        newNet.setId(this.xmi.getInterlocking().getNet().getId() + "_" + taglio.getDefinition() +  "_left");
        HashMap<String,TrackSection> tracks = new HashMap<String,TrackSection>();
        List<TrackSection> tracksList = new ArrayList<TrackSection>();
        List<MarkerBoard> markers = new ArrayList<MarkerBoard>();

        ArrayList<String> tagliandi = new ArrayList<String>(); 
        if(parte.isPoint()){
            tagliandi.add(0, primaTraccia[1]);
        }else{
            if(taglioDoppio){
                if(primaTraccia[1].equals(secondaTraccia[1]) && netTracks.get(secondaTraccia[1]).isPoint()){
                    tagliandi.add(0, primaTraccia[1]);
                }
            }
        }

        tracks.put(parte.getId(), parte);
        if(taglioDoppio){
            tracks.put(seconda_parte.getId(), seconda_parte);
            if(primaTraccia[0].equals(secondaTraccia[0]) && parte.isPoint()){
                tracks.put(prima_parte.getId(), prima_parte);
                tagliandi.add(1, secondaTraccia[1]);
            }
        }

        TrackSection down = null;
        if(parte.isPoint()){
            if(parte.punto_di_separazione){
                TrackSection appoggio = netTracks.get(parte.getStem().getRef());
                if(appoggio != null){
                    down = TrackSection.deepCopy(netTracks.get(parte.getStem().getRef()));
                    goDown(netTracks, down, tracks, intersected, parte);
                }
                TrackSection track_tagliata = netTracks.get(tagliandi.get(0));
                String relazione = parte.relazioneTrackPunto(track_tagliata);
                if(relazione.equals("plus")){
                    down = TrackSection.deepCopy(netTracks.get(parte.getMinus().getRef()));
                    goUp(netTracks, down, tracks, intersected, parte);
                }
                if(relazione.equals("minus")){
                    down = TrackSection.deepCopy(netTracks.get(parte.getPlus().getRef()));
                    goUp(netTracks, down, tracks, intersected, parte);
                }
            
            }else{
                TrackSection appoggio = netTracks.get(parte.getPlus().getRef());
                if(appoggio != null && (!appoggio.isPoint() && appoggio.getUp().getRef().equals(parte.getId()) || appoggio.isPoint())){
                    down = TrackSection.deepCopy(netTracks.get(parte.getPlus().getRef()));
                    goDown(netTracks, down, tracks, intersected, parte);
                    down = null;
                }
                appoggio =netTracks.get(parte.getMinus().getRef());
                if(appoggio != null && (!appoggio.isPoint() && appoggio.getUp().getRef().equals(parte.getId()) || appoggio.isPoint())){
                    down = TrackSection.deepCopy(netTracks.get(parte.getMinus().getRef()));
                    goDown(netTracks, down, tracks, intersected, parte);
                    down = null;
                }
            }
        }else{
            down = TrackSection.deepCopy(netTracks.get(parte.getDown().getRef()));
            goDown(netTracks, down, tracks, intersected, parte);
        }

        if(taglioDoppio && primaTraccia[1].equals(secondaTraccia[1]) && netTracks.get(secondaTraccia[1]).isPoint()){
            if(parte.isPoint()){
                if(seconda_parte.isPoint()){
                    TrackSection track_tagliata = netTracks.get(tagliandi.get(0));
                    String relazione = seconda_parte.relazioneTrackPunto(track_tagliata);
                    boolean avanza = false;
                    if(relazione.equals("plus")){
                        if(tracks.containsKey(seconda_parte.getMinus().getRef()) || tracks.containsKey(seconda_parte.getStem().getRef())){
                            avanza = true;
                        }
                    }
                    if(relazione.equals("minus")){
                        if(tracks.containsKey(seconda_parte.getPlus().getRef()) || tracks.containsKey(seconda_parte.getStem().getRef())){
                            avanza = true;
                        }
                    }
                    if(relazione.equals("stem")){
                        if(tracks.containsKey(seconda_parte.getMinus().getRef()) || tracks.containsKey(seconda_parte.getPlus().getRef())){
                            avanza = true;
                        }
                    }
                    if(avanza){
                        if(seconda_parte.punto_di_separazione){
                            TrackSection appoggio = netTracks.get(seconda_parte.getStem().getRef());
                            if(appoggio != null){
                                down = TrackSection.deepCopy(netTracks.get(seconda_parte.getStem().getRef()));
                                goDown(netTracks, down, tracks, intersected, seconda_parte);
                            }
                            if(relazione.equals("plus")){
                                down = TrackSection.deepCopy(netTracks.get(seconda_parte.getMinus().getRef()));
                                goUp(netTracks, down, tracks, intersected, seconda_parte);
                            }
                            if(relazione.equals("minus")){
                                down = TrackSection.deepCopy(netTracks.get(seconda_parte.getPlus().getRef()));
                                goUp(netTracks, down, tracks, intersected, seconda_parte);
                            }
                        
                        }else{
                            TrackSection appoggio = netTracks.get(seconda_parte.getPlus().getRef());
                            if(appoggio != null && (!appoggio.isPoint() && appoggio.getUp().getRef().equals(seconda_parte.getId()) || appoggio.isPoint())){
                                down = TrackSection.deepCopy(netTracks.get(seconda_parte.getPlus().getRef()));
                                goDown(netTracks, down, tracks, intersected, seconda_parte);
                                down = null;
                            }
                            appoggio =netTracks.get(seconda_parte.getMinus().getRef());
                            if(appoggio != null && (!appoggio.isPoint() && appoggio.getUp().getRef().equals(seconda_parte.getId()) || appoggio.isPoint())){
                                down = TrackSection.deepCopy(netTracks.get(seconda_parte.getMinus().getRef()));
                                goDown(netTracks, down, tracks, intersected, seconda_parte);
                                down = null;
                            }
                        }
                    }
                }else{
                    down = TrackSection.deepCopy(netTracks.get(seconda_parte.getDown().getRef()));
                    if(tracks.containsKey(down.getId())){
                        goDown(netTracks, down, tracks, intersected, seconda_parte);
                    }
                }
            }
        }

        if(tracks.size() == netTracks.size()){
            return resultsAdded;
        }

        tracks.remove(parte.getId());
        if(taglioDoppio){
            tracks.remove(seconda_parte.getId());
            if(primaTraccia[0].equals(secondaTraccia[0])  && parte.isPoint()){
                tracks.remove(prima_parte.getId());
            }
        }

        if(treXML){
            HashMap<String, TrackSection> remainingTracks = new HashMap<String, TrackSection>();
            for(TrackSection t : netTracks.values()){
                if(!tracks.containsKey(t.getId())){
                    remainingTracks.put(t.getId(),TrackSection.deepCopy(t));
                }
            }
            if((primaTraccia[0].equals(secondaTraccia[0])) && parte.isPoint()){
                HashMap<String, TrackSection> found = Utility.checkTreXml(remainingTracks, parte.getId(), tagliandi.get(0));
                boolean checkTreXml = false;
                if(found != null && found.size() > 0 && found.size() < remainingTracks.size()){
                    checkTreXml = true;
                }
                if(checkTreXml){
                    resultsAdded = Utility.computeTagliPuntoDiSeparazione(parte, netTracks, tagliandi, intersected, listOfValues, tracks, markers, remainingTracks,this.xmi, count, found, taglio);
                    return resultsAdded;
                } 
            }else if(primaTraccia[1].equals(secondaTraccia[1]) && netTracks.get(secondaTraccia[1]).isPoint()){
                boolean checkTreXml = Utility.checkTreXmlPuntoUnione(tracks, parte, seconda_parte, netTracks, netTracks.get(primaTraccia[1]));
                if(checkTreXml){
                    resultsAdded = Utility.computeTagliPuntoUnione(parte, seconda_parte, netTracks, tagliandi, intersected, listOfValues, tracks, markers, remainingTracks,this.xmi, taglio);
                    return resultsAdded;
                } 
            }
        }

        List<TrackSection> addedTracks = new ArrayList<TrackSection>();
        TrackSection vecchio_vicino_down = parte.isPoint() ? new TrackSection() : TrackSection.deepCopy(netTracks.get(parte.getDown().getRef()));

        if((taglioDoppio && primaTraccia[0].equals(secondaTraccia[0])) && parte.isPoint()){
            intersected.remove(seconda_parte.getId());
            listOfValues.remove(1);
            intersected.remove(prima_parte.getId());
            listOfValues.remove(1);
        }

        for(int j = 0; j < intersected.size(); j++){
            TrackSection parte_j = listOfValues.get(j);
            TrackSection track_b = new TrackSection();
            track_b.setType("linear");
            track_b.setLength("52");
            TrackSection parte_sx = TrackSection.deepCopy(parte_j);
            List<Neighbor> listNeighbor = new ArrayList<Neighbor>();

            if(parte_j.isPoint()){
                for(int k = 0; k < tagliandi.size(); k++){
                    
                    track_b = new TrackSection();
                    track_b.setType("linear");
                    track_b.setLength("52");
                    TrackSection track_tagliata = netTracks.get(tagliandi.get(k));
                    String relazione = parte_j.relazioneTrackPunto(track_tagliata);

                    TrackSection nuova_track = new TrackSection();
                    nuova_track.setType("linear");
                    nuova_track.setLength("52");
                    nuova_track.setId(parte_j.getId() + "_ex_" + relazione);

                    MarkerBoard marker_right = new MarkerBoard();
                    marker_right.setMounted("up");
                    marker_right.setId("mb_exit_"+nuova_track.getId());
                    marker_right.setTrack(nuova_track.getId());
                    marker_right.setDistance("51");
                    if(!checkInserimentoMarker(marker_right, markers))
                        markers.add(marker_right);

                    nuova_track.setRightMarker(marker_right);
                    
                    if(track_tagliata.isPoint()){
                        track_b.setId(parte_j.getId() + "_ex_" + relazione + "_b");
                    }else{
                        track_b.setId(track_tagliata.getId());
                    }
                    Neighbor neigh_b = new Neighbor();
                    neigh_b.setRef(nuova_track.getId());
                    neigh_b.setSide("down");

                    track_b.getNeighbors().add(neigh_b);
                    track_b.setDown(neigh_b);

                    MarkerBoard marker_left = new MarkerBoard();
                    marker_left.setMounted("down");
                    if(track_tagliata.isPoint()){
                        marker_left.setId("mb_entry_"+nuova_track.getId());
                    }else{
                        if(track_tagliata.getLeftMarker()!= null && track_tagliata.getLeftMarker().getId()!= null && track_tagliata.getLeftMarker().getId().length() > 0 && track_tagliata.getLeftMarker().getMounted()!=null && track_tagliata.getLeftMarker().getMounted().length() > 0 ){
                            marker_left.setId(track_tagliata.getLeftMarker().getId());
                        }else{
                            marker_left.setId("mb_entry_"+nuova_track.getId());
                        }
                    }
                    marker_left.setTrack(track_b.getId());
                    marker_left.setDistance("51");
                    if(!checkInserimentoMarker(marker_left, markers))
                        markers.add(marker_left);

                    track_b.setLeftMarker(marker_left);

                    Neighbor dx_neigh_nuova_track = new Neighbor();
                    dx_neigh_nuova_track.setRef(track_b.getId());
                    dx_neigh_nuova_track.setSide("up");

                    nuova_track.getNeighbors().add(dx_neigh_nuova_track);
                    nuova_track.setUp(dx_neigh_nuova_track);

                    Neighbor sx_neigh_nuova_track = new Neighbor();
                    sx_neigh_nuova_track.setRef(parte_j.getId());
                    sx_neigh_nuova_track.setSide("down");

                    nuova_track.getNeighbors().add(sx_neigh_nuova_track);
                    nuova_track.setDown(sx_neigh_nuova_track);

                    tracks.put(nuova_track.getId(), nuova_track);

                    Neighbor neigh_parte_sx = new Neighbor();
                    neigh_parte_sx.setRef(nuova_track.getId());
                    neigh_parte_sx.setSide(relazione);
                    parte_sx.setNeighbors(new ArrayList<Neighbor>());
                    parte_sx.getNeighbors().add(neigh_parte_sx);
                    
                    if(relazione.equals("plus")){
                        parte_sx.setPlus(neigh_parte_sx);
                        parte_sx.getNeighbors().add(Neighbor.deepCopy(parte_sx.getMinus()));
                        parte_sx.getNeighbors().add(Neighbor.deepCopy(parte_sx.getStem()));
                    }
                    if(relazione.equals("minus")){
                        parte_sx.setMinus(neigh_parte_sx);
                        parte_sx.getNeighbors().add(Neighbor.deepCopy(parte_sx.getPlus()));
                        parte_sx.getNeighbors().add(Neighbor.deepCopy(parte_sx.getStem()));
                    }
                    if(relazione.equals("stem")){
                        parte_sx.setStem(neigh_parte_sx);
                        parte_sx.getNeighbors().add(Neighbor.deepCopy(parte_sx.getMinus()));
                        parte_sx.getNeighbors().add(Neighbor.deepCopy(parte_sx.getPlus()));
                    }
                    if(!checkInserimentoTrackList(track_b, tracksList))
                        tracksList.add(track_b);
                }
                
                tracks.put(parte_sx.getId(), parte_sx);
                addedTracks.add(parte_sx);

            }else{
                sistemaTaglioLatoSinistro(parte_j, parte_sx, vecchio_vicino_down, listNeighbor, tracks, markers, track_b);
                
                if(vecchio_vicino_down.isPoint()){
                    tracks.remove(vecchio_vicino_down.getId());
                    tracks.put(vecchio_vicino_down.getId(), vecchio_vicino_down);
                    if(taglioDoppio){
                        vecchio_vicino_down = TrackSection.deepCopy(vecchio_vicino_down);
                    }
                }    
                if(!checkInserimentoTrackList(track_b, tracksList))
                    tracksList.add(track_b);
                tracks.put(parte_sx.getId(), parte_sx);
                addedTracks.add(parte_sx);        
            }   
        }

        for(TrackSection t : tracks.values()){
            if(!checkInserimentoTrackList(t, tracksList))
                tracksList.add(t);
            if(!t.isPoint()){
                MarkerBoard l = MarkerBoard.deepCopy(t.getLeftMarker());
                if(l!= null && l.getMounted() != null && l.getMounted().length() > 0){
                    if(!checkInserimentoMarker(l, markers))
                        markers.add(l);
                }
                MarkerBoard r = MarkerBoard.deepCopy(t.getRightMarker());
                if(r!= null && r.getMounted() != null && r.getMounted().length() > 0){
                    if(!checkInserimentoMarker(r, markers))
                        markers.add(r);
                }
            }
        }
        newNet.setTracks(tracksList);
        newNet.setMarkerBoards(markers);
        inter.setNet(newNet);
        newXML.setInterlocking(inter);
        if(tracksList.size() > 0){
            resultsAdded.add(newXML);
        }
        //lato destro
        XMI newXML_dx = new XMI();
        newXML_dx.setXmiDoc(this.xmi.getXmiDoc());
        Interlocking inter_dx = new Interlocking();
        inter_dx.setId(this.xmi.getInterlocking().getId() + "_" + taglio.getDefinition() +  "_right");
        RouteTable rt_dx = new RouteTable();
        rt_dx.setId(this.xmi.getInterlocking().getRouteTable().getId() + "_" + taglio.getDefinition() + "_right");
        rt_dx.setNetId(this.xmi.getInterlocking().getRouteTable().getNetId() + "_" + taglio.getDefinition() +  "_right");
        inter_dx.setRouteTable(rt_dx);
        Network newNet_dx = new Network();
        newNet_dx.setId(this.xmi.getInterlocking().getNet().getId() + "_" + taglio.getDefinition() +  "_right");
        List<TrackSection> tracks_dx = new ArrayList<TrackSection>();
        List<MarkerBoard> markers_dx = new ArrayList<MarkerBoard>();

        if(parte.isPoint() && !taglioDoppio){
            TrackSection track_appoggio = TrackSection.deepCopy(netTracks.get(tagliandi.get(0)));
            if(!track_appoggio.isPoint()){
                intersected.remove(parte.getId());
                listOfValues.remove(0);
                intersected.put(tagliandi.get(0), track_appoggio);
                listOfValues.add(track_appoggio);
                tracks.put(track_appoggio.getId(), track_appoggio);
            }
        }
        
        boolean puntoPuntoPunto = false;
        if(taglioDoppio  && parte.isPoint()){
            if(primaTraccia[0].equals(secondaTraccia[0])){            
                if(!seconda_parte.isPoint() || !prima_parte.isPoint()){
                    tagliandi.clear();
                    if(prima_parte.isPoint()){
                        tagliandi.add(prima_parte.getId());
                        intersected.put(seconda_parte.getId(), seconda_parte);
                        listOfValues.add(seconda_parte);
                    }else if(seconda_parte.isPoint()){
                        tagliandi.add(seconda_parte.getId());
                        intersected.put(prima_parte.getId(), prima_parte);
                        listOfValues.add(prima_parte);                    
                    }
                    else if(!seconda_parte.isPoint() && !prima_parte.isPoint()){
                        tagliandi.clear();
                        intersected.remove(parte.getId());
                        listOfValues.remove(0);
                        intersected.put(seconda_parte.getId(), seconda_parte);
                        listOfValues.add(seconda_parte);
                        intersected.put(prima_parte.getId(), prima_parte);
                        listOfValues.add(prima_parte);        
                    }
                }else{
                    puntoPuntoPunto = true;
                }
            }
        }

        TrackSection vecchio_vicino_up = parte.isPoint() ? new TrackSection() : TrackSection.deepCopy(netTracks.get(parte.getUp().getRef()));
        if(taglioDoppio && primaTraccia[1].equals(secondaTraccia[1]) && netTracks.get(secondaTraccia[1]).isPoint()){ //suppongo di aver tagliato i lati entranti in un punto
            vecchio_vicino_up = TrackSection.deepCopy(netTracks.get(tagliandi.get(0)));
        }else if(taglioDoppio && parte.isPoint()){
            if(!seconda_parte.isPoint()){
                vecchio_vicino_up = TrackSection.deepCopy(netTracks.get(seconda_parte.getUp().getRef()));
            }else if(!prima_parte.isPoint()){
                vecchio_vicino_up = TrackSection.deepCopy(netTracks.get(prima_parte.getUp().getRef()));
            }    
        }
        
        for(int j = 0; j < intersected.size(); j++){
            TrackSection parte_j = listOfValues.get(j);
            for(TrackSection added : addedTracks){
                tracks.remove(added.getId());
            }
            vecchio_vicino_down = parte_j.isPoint() ? new TrackSection() : TrackSection.deepCopy(netTracks.get(parte_j.getDown().getRef()));

            TrackSection track_ex = new TrackSection();
            track_ex.setType("linear");
            track_ex.setLength("52");
            TrackSection parte_dx = TrackSection.deepCopy(parte_j);
            if((!vecchio_vicino_down.isPoint() && !parte_j.isPoint()) || (!parte_j.isPoint() && (parte_j.getRightMarker() == null || (parte_j.getRightMarker()!=null && (parte_j.getRightMarker().getMounted()== null || parte_j.getRightMarker().getMounted().length() == 0))))){
                String relazione = "";
                if(parte.isPoint()){
                    if(!parte_j.isPoint()){
                        vecchio_vicino_up = TrackSection.deepCopy(netTracks.get(parte_j.getUp().getRef()));
                    }
                }
                if(vecchio_vicino_up.isPoint()){
                    relazione = vecchio_vicino_up.relazioneTrackPunto(parte_j);
                    track_ex.setId(parte_j.getUp().getRef() + "_ex_" + relazione );
                }else{
                    track_ex.setId(parte_j.getUp().getRef() + "_ex");
                }

                Neighbor neigh_ex_down = new Neighbor();
                neigh_ex_down.setRef(parte_j.getId());
                neigh_ex_down.setSide("down");
                track_ex.getNeighbors().add(neigh_ex_down);
                track_ex.setDown(neigh_ex_down);

                Neighbor neigh_ex_up = Neighbor.deepCopy(parte_j.getUp());
                track_ex.getNeighbors().add(neigh_ex_up);
                track_ex.setUp(neigh_ex_up);

                MarkerBoard marker_ex_down = new MarkerBoard();
                marker_ex_down.setMounted("down");
                marker_ex_down.setId("mb_exit_"+track_ex.getId());
                marker_ex_down.setTrack(track_ex.getId());
                marker_ex_down.setDistance("51");
                if(!checkInserimentoMarker(marker_ex_down, markers_dx))                
                    markers_dx.add(marker_ex_down);

                if(!netTracks.get(parte_j.getUp().getRef()).isPoint()){
                    MarkerBoard marker_ex_up = new MarkerBoard();
                    marker_ex_up.setMounted("up");
                    marker_ex_up.setId("mb_entry_"+track_ex.getId());
                    marker_ex_up.setTrack(track_ex.getId());
                    marker_ex_up.setDistance("51");
                    if(!checkInserimentoMarker(marker_ex_up, markers_dx))    
                        markers_dx.add(marker_ex_up);
                    track_ex.setRightMarker(marker_ex_up);
                }

                track_ex.setLeftMarker(marker_ex_down);

                Neighbor neigh_parte_ex = new Neighbor();
                neigh_parte_ex.setRef(track_ex.getId());
                neigh_parte_ex.setSide("up");

                parte_dx.setUp(neigh_parte_ex);
                List<Neighbor> listNeighbor_parte_dx = new ArrayList<Neighbor>();
                listNeighbor_parte_dx.add(neigh_parte_ex);

                parte_dx.setNeighbors(listNeighbor_parte_dx);

                MarkerBoard parte_dx_marker = MarkerBoard.deepCopy(parte_dx.getRightMarker());
                if(!checkInserimentoMarker(parte_dx_marker, markers_dx))
                    markers_dx.add(parte_dx_marker);

                parte_dx.setLeftMarker(new MarkerBoard());

                if(!parte_j.isPoint() && (parte_j.getRightMarker() == null || (parte_j.getRightMarker()!=null && (parte_j.getRightMarker().getMounted()== null || parte_j.getRightMarker().getMounted().length() == 0)))){
                    MarkerBoard marker_b_dx = new MarkerBoard();
                    marker_b_dx.setMounted("up");
                    marker_b_dx.setId("mb_entry_"+track_ex.getId());
                    marker_b_dx.setTrack(parte_dx.getId());
                    marker_b_dx.setDistance("51");
                    if(!checkInserimentoMarker(marker_b_dx, markers_dx))
                        markers_dx.add(marker_b_dx);
                    parte_dx.setRightMarker(marker_b_dx);
                }
            
                List<Neighbor> nbs = new ArrayList<Neighbor>();
                if(!vecchio_vicino_up.isPoint()){
                    nbs.add(Neighbor.deepCopy(vecchio_vicino_up.getUp()));
                    Neighbor nuovo_neigh = new Neighbor(track_ex.getId(), "down");
                    nbs.add(nuovo_neigh);
                    MarkerBoard l = MarkerBoard.deepCopy(vecchio_vicino_up.getLeftMarker());
                    if(l!= null && l.getMounted() != null && l.getMounted().length() > 0){
                        if(!checkInserimentoMarker(l, markers_dx))
                            markers_dx.add(l);
                    }
                    MarkerBoard r = MarkerBoard.deepCopy(vecchio_vicino_up.getRightMarker());
                    if(r!= null && r.getMounted() != null && r.getMounted().length() > 0){
                        if(!checkInserimentoMarker(r, markers_dx))
                            markers_dx.add(r);
                    }
                }else{
                    Neighbor nuovo_neigh = new Neighbor(track_ex.getId(), relazione);
                    nbs.add(nuovo_neigh);
                    if(relazione.equals("plus")){
                        vecchio_vicino_up.setPlus(nuovo_neigh);
                        nbs.add(Neighbor.deepCopy(vecchio_vicino_up.getMinus()));
                        nbs.add(Neighbor.deepCopy(vecchio_vicino_up.getStem()));
                    }
                    if(relazione.equals("minus")){
                        vecchio_vicino_up.setMinus(nuovo_neigh);
                        nbs.add(Neighbor.deepCopy(vecchio_vicino_up.getPlus()));
                        nbs.add(Neighbor.deepCopy(vecchio_vicino_up.getStem()));
                    }
                    if(relazione.equals("stem")){
                        vecchio_vicino_up.setStem(nuovo_neigh);
                        nbs.add(Neighbor.deepCopy(vecchio_vicino_up.getMinus()));
                        nbs.add(Neighbor.deepCopy(vecchio_vicino_up.getPlus()));
                    }

                }

                vecchio_vicino_up.setNeighbors(nbs);
                
                if(vecchio_vicino_up.isPoint()){
                    tracks.remove(vecchio_vicino_up.getId());
                    if(taglioDoppio){
                        vecchio_vicino_up = TrackSection.deepCopy(vecchio_vicino_up);
                    }
                }    
                if(taglioDoppio && primaTraccia[1].equals(secondaTraccia[1]) && netTracks.get(secondaTraccia[1]).isPoint()){
                
                }else{   
                    tracks_dx.add(vecchio_vicino_up);
                }
                tracks.put(vecchio_vicino_up.getId(), vecchio_vicino_up);
                tracks_dx.add(track_ex);
                tracks_dx.add(parte_dx);

            }else if(!parte_j.isPoint()){
                track_ex.setId(parte_j.getId() + "_b");

                Neighbor neigh_b = new Neighbor();
                neigh_b.setRef(parte_j.getId());
                neigh_b.setSide("up");

                track_ex.getNeighbors().add(neigh_b);
                track_ex.setUp(neigh_b);

                MarkerBoard marker_b = new MarkerBoard();
                marker_b.setMounted("up");
                marker_b.setId("mb_entry_"+parte_j.getId());
                marker_b.setTrack(track_ex.getId());
                marker_b.setDistance("51");
                if(!checkInserimentoMarker(marker_b, markers_dx))
                    markers_dx.add(marker_b);

                track_ex.setRightMarker(marker_b);

                Neighbor neigh_parte = new Neighbor();
                neigh_parte.setRef(track_ex.getId());
                neigh_parte.setSide("down");

                parte_dx.setDown(neigh_parte);

                List<Neighbor> newListNeighbors = new ArrayList<Neighbor>();
                newListNeighbors.add(neigh_parte);
                newListNeighbors.add(parte_dx.getUp());
                parte_dx.setNeighbors(newListNeighbors);

                MarkerBoard parte_dx_marker = MarkerBoard.deepCopy(parte_dx.getRightMarker());
                if(!checkInserimentoMarker(parte_dx_marker, markers_dx))
                    markers_dx.add(parte_dx_marker);
                MarkerBoard parte_dx_marker_left = MarkerBoard.deepCopy(parte_dx.getLeftMarker());
                if(!checkInserimentoMarker(parte_dx_marker_left, markers_dx))    
                    markers_dx.add(parte_dx_marker_left);
                tracks_dx.add(track_ex);
                tracks_dx.add(parte_dx);
            }else{     
                if(!puntoPuntoPunto){
                    TrackSection track_tagliata = TrackSection.deepCopy(netTracks.get(tagliandi.get(0)));
                    
                    if(taglioDoppio && primaTraccia[1].equals(secondaTraccia[1]) && netTracks.get(secondaTraccia[1]).isPoint()){
                        track_tagliata = TrackSection.deepCopy(vecchio_vicino_up);
                    }
                    String relazione_vicino_up = track_tagliata.relazioneTrackPunto(parte_j);
                    track_ex.setId(track_tagliata.getId() + "_ex_" + relazione_vicino_up + "_b");

                    MarkerBoard marker_b = new MarkerBoard();
                    marker_b.setMounted("up");
                    marker_b.setId("mb_entry_"+ track_tagliata.getId() + "_ex_" + relazione_vicino_up);
                    marker_b.setTrack(track_ex.getId());
                    marker_b.setDistance("51");
                    if(!checkInserimentoMarker(marker_b, markers_dx))
                        markers_dx.add(marker_b);

                    track_ex.setRightMarker(marker_b);

                    TrackSection nuova_track_dx = new TrackSection();
                    nuova_track_dx.setId(track_tagliata.getId() + "_ex_" + relazione_vicino_up);
                    nuova_track_dx.setLength("52");
                    nuova_track_dx.setType("linear");

                    MarkerBoard marker_nuova_track_dx = new MarkerBoard();
                    marker_nuova_track_dx.setMounted("down");
                    marker_nuova_track_dx.setId("mb_exit_"+ nuova_track_dx.getId());
                    marker_nuova_track_dx.setTrack(nuova_track_dx.getId());
                    marker_nuova_track_dx.setDistance("51");
                    if(!checkInserimentoMarker(marker_nuova_track_dx, markers_dx))
                        markers_dx.add(marker_nuova_track_dx);

                    nuova_track_dx.setLeftMarker(marker_nuova_track_dx);

                    Neighbor neigh_track_ex = new Neighbor(nuova_track_dx.getId(), "up");
                    track_ex.setUp(neigh_track_ex);
                    track_ex.setNeighbors(new ArrayList<Neighbor>());
                    track_ex.getNeighbors().add(neigh_track_ex);

                    Neighbor neigh_nuova_track_dx = new Neighbor(track_ex.getId(), "down");
                    nuova_track_dx.setDown(neigh_nuova_track_dx);
                    nuova_track_dx.setNeighbors(new ArrayList<Neighbor>());
                    nuova_track_dx.getNeighbors().add(neigh_nuova_track_dx);

                    Neighbor neigh_nuova_track_up = new Neighbor(track_tagliata.getId(), "up");
                    nuova_track_dx.setUp(neigh_nuova_track_up);
                    nuova_track_dx.getNeighbors().add(neigh_nuova_track_up);

                    tracks_dx.add(nuova_track_dx);

                    parte_dx = TrackSection.deepCopy(track_tagliata);

                    Neighbor neigh_parte_sx = new Neighbor();
                    neigh_parte_sx.setRef(nuova_track_dx.getId());
                    neigh_parte_sx.setSide(relazione_vicino_up);
                    parte_dx.setNeighbors(new ArrayList<Neighbor>());
                    parte_dx.getNeighbors().add(neigh_parte_sx);
                    if(relazione_vicino_up.equals("plus")){
                        parte_dx.setPlus(neigh_parte_sx);
                        parte_dx.getNeighbors().add(Neighbor.deepCopy(track_tagliata.getMinus()));
                        parte_dx.getNeighbors().add(Neighbor.deepCopy(track_tagliata.getStem()));
                    }
                    if(relazione_vicino_up.equals("minus")){
                        parte_dx.setMinus(neigh_parte_sx);
                        parte_dx.getNeighbors().add(Neighbor.deepCopy(track_tagliata.getPlus()));
                        parte_dx.getNeighbors().add(Neighbor.deepCopy(track_tagliata.getStem()));
                    }
                    if(relazione_vicino_up.equals("stem")){
                        parte_dx.setStem(neigh_parte_sx);
                        parte_dx.getNeighbors().add(Neighbor.deepCopy(track_tagliata.getMinus()));
                        parte_dx.getNeighbors().add(Neighbor.deepCopy(track_tagliata.getPlus()));
                    }


                    if(taglioDoppio && primaTraccia[1].equals(secondaTraccia[1]) && netTracks.get(secondaTraccia[1]).isPoint()){
                        vecchio_vicino_up = TrackSection.deepCopy(parte_dx);
                        tracks.remove(track_tagliata.getId());
                    }else{
                        
                        tracks_dx.add(parte_dx);
                    }

                    tracks.put(track_tagliata.getId(), track_tagliata);
                    tracks_dx.add(track_ex);

                }else if(puntoPuntoPunto){
                    for(int k = 0; k < tagliandi.size(); k++){
                        TrackSection track_ex_ppp = new TrackSection();
                        track_ex_ppp.setType("linear");
                        track_ex_ppp.setLength("52");
                        TrackSection track_tagliata = TrackSection.deepCopy(netTracks.get(tagliandi.get(k)));

                        String relazione_vicino_up = track_tagliata.relazioneTrackPunto(parte_j);
                        track_ex_ppp.setId(track_tagliata.getId() + "_ex_" + relazione_vicino_up + "_b");

                        MarkerBoard marker_b = new MarkerBoard();
                        marker_b.setMounted("up");
                        marker_b.setId("mb_entry_"+ track_tagliata.getId() + "_ex_" + relazione_vicino_up);
                        marker_b.setTrack(track_ex_ppp.getId());
                        marker_b.setDistance("51");
                        if(!checkInserimentoMarker(marker_b, markers_dx))
                            markers_dx.add(marker_b);

                        track_ex_ppp.setRightMarker(marker_b);

                        TrackSection nuova_track_dx = new TrackSection();
                        nuova_track_dx.setId(track_tagliata.getId() + "_ex_" + relazione_vicino_up);
                        nuova_track_dx.setLength("52");
                        nuova_track_dx.setType("linear");

                        MarkerBoard marker_nuova_track_dx = new MarkerBoard();
                        marker_nuova_track_dx.setMounted("down");
                        marker_nuova_track_dx.setId("mb_exit_"+ nuova_track_dx.getId());
                        marker_nuova_track_dx.setTrack(nuova_track_dx.getId());
                        marker_nuova_track_dx.setDistance("51");
                        if(!checkInserimentoMarker(marker_nuova_track_dx, markers_dx))
                            markers_dx.add(marker_nuova_track_dx);

                        nuova_track_dx.setLeftMarker(marker_nuova_track_dx);

                        Neighbor neigh_track_ex = new Neighbor(nuova_track_dx.getId(), "up");
                        track_ex_ppp.setUp(neigh_track_ex);
                        track_ex_ppp.setNeighbors(new ArrayList<Neighbor>());
                        track_ex_ppp.getNeighbors().add(neigh_track_ex);

                        Neighbor neigh_nuova_track_dx = new Neighbor(track_ex_ppp.getId(), "down");
                        nuova_track_dx.setDown(neigh_nuova_track_dx);
                        nuova_track_dx.setNeighbors(new ArrayList<Neighbor>());
                        nuova_track_dx.getNeighbors().add(neigh_nuova_track_dx);

                        Neighbor neigh_nuova_track_up = new Neighbor(track_tagliata.getId(), "up");
                        nuova_track_dx.setUp(neigh_nuova_track_up);
                        nuova_track_dx.getNeighbors().add(neigh_nuova_track_up);

                        tracks_dx.add(nuova_track_dx);

                        parte_dx = TrackSection.deepCopy(track_tagliata);

                        Neighbor neigh_parte_sx = new Neighbor();
                        neigh_parte_sx.setRef(nuova_track_dx.getId());
                        neigh_parte_sx.setSide(relazione_vicino_up);
                        parte_dx.setNeighbors(new ArrayList<Neighbor>());
                        parte_dx.getNeighbors().add(neigh_parte_sx);
                        if(relazione_vicino_up.equals("plus")){
                            parte_dx.setPlus(neigh_parte_sx);
                            parte_dx.getNeighbors().add(Neighbor.deepCopy(track_tagliata.getMinus()));
                            parte_dx.getNeighbors().add(Neighbor.deepCopy(track_tagliata.getStem()));
                        }
                        if(relazione_vicino_up.equals("minus")){
                            parte_dx.setMinus(neigh_parte_sx);
                            parte_dx.getNeighbors().add(Neighbor.deepCopy(track_tagliata.getPlus()));
                            parte_dx.getNeighbors().add(Neighbor.deepCopy(track_tagliata.getStem()));
                        }
                        if(relazione_vicino_up.equals("stem")){
                            parte_dx.setStem(neigh_parte_sx);
                            parte_dx.getNeighbors().add(Neighbor.deepCopy(track_tagliata.getMinus()));
                            parte_dx.getNeighbors().add(Neighbor.deepCopy(track_tagliata.getPlus()));
                        }

                        tracks.put(track_tagliata.getId(), track_tagliata);
                        tracks_dx.add(track_ex_ppp);
                        tracks_dx.add(parte_dx);
                    }
                }
            }
        }
        tracks.put(parte.getId(), parte);
        if(taglioDoppio){
            tracks.put(seconda_parte.getId(), seconda_parte);
            if(primaTraccia[0].equals(secondaTraccia[0])  && parte.isPoint()){
                tracks.put(prima_parte.getId(), prima_parte);
            }
        }
        
        if(taglioDoppio && primaTraccia[1].equals(secondaTraccia[1]) && netTracks.get(secondaTraccia[1]).isPoint()){
            tracks.put(vecchio_vicino_up.getId(), vecchio_vicino_up);
            tracks_dx.add(vecchio_vicino_up);
        }

        for(TrackSection t : netTracks.values()){
            if(!tracks.containsKey(t.getId())){
                tracks_dx.add(TrackSection.deepCopy(t));
                if(!t.isPoint()){
                    MarkerBoard l = MarkerBoard.deepCopy(t.getLeftMarker());
                    if(l!= null && l.getMounted() != null && l.getMounted().length() > 0){
                        if(!checkInserimentoMarker(l, markers_dx))
                            markers_dx.add(l);
                    }
                    MarkerBoard r = MarkerBoard.deepCopy(t.getRightMarker());
                    if(r!= null && r.getMounted() != null && r.getMounted().length() > 0){
                        if(!checkInserimentoMarker(r, markers_dx))
                            markers_dx.add(r);
                    }
                }
            }
        }
        newNet_dx.setTracks(tracks_dx);
        newNet_dx.setMarkerBoards(markers_dx);
        inter_dx.setNet(newNet_dx);
        newXML_dx.setInterlocking(inter_dx);
        if(tracks_dx.size() > 0){
            resultsAdded.add(newXML_dx);
        }
        return resultsAdded;
    }

    private boolean checkInserimentoTrackList(TrackSection t, List<TrackSection> tracksList) {
        for(TrackSection into : tracksList){
            if(t.getId() !=null && into.getId() !=null && into.getId().equals(t.getId()))
                return true;
        }
        return false; 
    }

    public static boolean checkInserimentoMarker(MarkerBoard m, List<MarkerBoard> markers) {
        if(m.getId() == null || (m.getId() !=null && m.getId().length() == 0)){
            return true;
        }
        for(MarkerBoard into : markers){
            if(m.getId() !=null && into.getId() !=null && into.getId().equals(m.getId()))
                return true;
        }
        return false; 
    }

    public static void sistemaTaglioLatoSinistro(TrackSection parte, TrackSection parte_sx, TrackSection vecchio_vicino_down, List<Neighbor> listNeighbor, HashMap<String, TrackSection> tracks, List<MarkerBoard> markers, TrackSection track_b) {
        if(!vecchio_vicino_down.isPoint() || parte.getRightMarker() == null || (parte.getRightMarker()!=null && (parte.getRightMarker().getMounted()== null || parte.getRightMarker().getMounted().length() == 0))){
            track_b.setId(parte.getId() + "_b");

            Neighbor neigh_b = new Neighbor();
            neigh_b.setRef(parte.getId());
            neigh_b.setSide("down");

            track_b.getNeighbors().add(neigh_b);
            track_b.setDown(neigh_b);

            MarkerBoard marker_b = new MarkerBoard();
            marker_b.setMounted("down");
            marker_b.setId("mb_entry_"+parte.getId());
            marker_b.setTrack(track_b.getId());
            marker_b.setDistance("51");
            if(!checkInserimentoMarker(marker_b, markers))
                markers.add(marker_b);

            track_b.setLeftMarker(marker_b);

            Neighbor neigh_parte = new Neighbor();
            neigh_parte.setRef(track_b.getId());
            neigh_parte.setSide("up");

            parte_sx.setUp(neigh_parte);
            
            listNeighbor.add(neigh_parte);
            listNeighbor.add(parte_sx.getDown());
            parte_sx.setNeighbors(listNeighbor);
            if(parte.getRightMarker() == null || (parte.getRightMarker()!=null && (parte.getRightMarker().getMounted()== null || parte.getRightMarker().getMounted().length() == 0))){
                MarkerBoard marker_b_dx = new MarkerBoard();
                marker_b_dx.setMounted("up");
                marker_b_dx.setId("mb_exit_"+parte.getId());
                marker_b_dx.setTrack(parte_sx.getId());
                marker_b_dx.setDistance("51");
                if(!checkInserimentoMarker(marker_b_dx, markers))
                    markers.add(marker_b_dx);
                parte_sx.setRightMarker(marker_b_dx);
            }
        }else{
            String relazione = vecchio_vicino_down.relazioneTrackPunto(parte);
            track_b.setId(vecchio_vicino_down.getId() + "_ex_" + relazione);
            Neighbor neigh_b = new Neighbor();
            neigh_b.setRef(parte.getId());
            neigh_b.setSide("up");

            track_b.getNeighbors().add(neigh_b);
            track_b.setUp(neigh_b);

            MarkerBoard marker_right = new MarkerBoard();
            marker_right.setMounted("up");
            marker_right.setId("mb_exit_"+track_b.getId());
            marker_right.setTrack(track_b.getId());
            marker_right.setDistance("51");
            if(!checkInserimentoMarker(marker_right, markers))
                markers.add(marker_right);

            track_b.setRightMarker(marker_right);
            Neighbor nuovo_neigh = new Neighbor(track_b.getId(), relazione);
            listNeighbor.add(nuovo_neigh);

            if(relazione.equals("plus")){
                listNeighbor.add(Neighbor.deepCopy(vecchio_vicino_down.getMinus()));
                listNeighbor.add(Neighbor.deepCopy(vecchio_vicino_down.getStem()));
                vecchio_vicino_down.setPlus(nuovo_neigh);
            }
            if(relazione.equals("minus")){
                listNeighbor.add(Neighbor.deepCopy(vecchio_vicino_down.getPlus()));
                listNeighbor.add(Neighbor.deepCopy(vecchio_vicino_down.getStem()));
                vecchio_vicino_down.setMinus(nuovo_neigh);
            }
            if(relazione.equals("stem")){
                listNeighbor.add(Neighbor.deepCopy(vecchio_vicino_down.getMinus()));
                listNeighbor.add(Neighbor.deepCopy(vecchio_vicino_down.getPlus()));
                vecchio_vicino_down.setStem(nuovo_neigh);
            }

            vecchio_vicino_down.setNeighbors(listNeighbor);


            Neighbor neigh_b_down = new Neighbor();
            neigh_b_down.setRef(vecchio_vicino_down.getId());
            neigh_b_down.setSide("down");
            track_b.getNeighbors().add(neigh_b_down);

            Neighbor neigh_parte_sx = new Neighbor();
            neigh_parte_sx.setRef(track_b.getId());
            neigh_parte_sx.setSide("down");
            parte_sx.setNeighbors(new ArrayList<Neighbor>());
            parte_sx.getNeighbors().add(neigh_parte_sx);
            parte_sx.setDown(neigh_parte_sx);
            parte_sx.setRightMarker(new MarkerBoard());

        }
    }

    public Boolean goDown(HashMap<String,TrackSection> netTracks, TrackSection parte, HashMap<String,TrackSection> tracks, HashMap<String,TrackSection>intersected, TrackSection track_di_provenienza){
        if(parte == null || (parte!=null && (intersected.containsKey(parte.getId()) || tracks.containsKey(parte.getId())))){
            return true;
        }
        boolean arrivatoInCima = false;
        tracks.put(parte.getId(),TrackSection.deepCopy(parte));
        while(!arrivatoInCima){
            TrackSection down = null;
            if(parte.isPoint()){
                if(parte.punto_di_separazione){
                    TrackSection appoggio = netTracks.get(parte.getStem().getRef());
                    if(appoggio != null){
                        down = TrackSection.deepCopy(netTracks.get(parte.getStem().getRef()));
                        goDown(netTracks, down, tracks, intersected, parte);
                    }
                    String relazione = parte.relazioneTrackPunto(track_di_provenienza);
                    if(relazione.equals("plus")){
                        down = TrackSection.deepCopy(netTracks.get(parte.getMinus().getRef()));
                        goUp(netTracks, down, tracks, intersected, parte);
                    }
                    if(relazione.equals("minus")){
                        down = TrackSection.deepCopy(netTracks.get(parte.getPlus().getRef()));
                        goUp(netTracks, down, tracks, intersected, parte);
                    }
                    
                }else{
                    TrackSection appoggio = netTracks.get(parte.getPlus().getRef());
                    if(appoggio != null && (!appoggio.isPoint() && appoggio.getUp().getRef().equals(parte.getId()) || appoggio.isPoint()) ){
                        down = TrackSection.deepCopy(netTracks.get(parte.getPlus().getRef()));
                        goDown(netTracks, down, tracks, intersected, parte);
                        down = null;
                    }
                    appoggio = netTracks.get(parte.getMinus().getRef());
                    if(appoggio != null && (!appoggio.isPoint() && appoggio.getUp().getRef().equals(parte.getId()) || appoggio.isPoint())){
                        down = TrackSection.deepCopy(netTracks.get(parte.getMinus().getRef()));
                        goDown(netTracks, down, tracks, intersected, parte);
                        down = null;
                    }
                }
            }else{
                down = TrackSection.deepCopy(netTracks.get(parte.getDown().getRef()));
            }
            if(down!=null){
                if(intersected.containsKey(down.getId()) || tracks.containsKey(down.getId())){
                    arrivatoInCima = true;  
                }else{
                    track_di_provenienza = TrackSection.deepCopy(parte);
                }
                tracks.put(down.getId(),down);
                parte = TrackSection.deepCopy(down);
            }else{
              arrivatoInCima = true;  
            }
        }
        return arrivatoInCima;
    }

    private boolean goUp(HashMap<String,TrackSection> netTracks, TrackSection parte, HashMap<String, TrackSection> tracks, HashMap<String, TrackSection> intersected, TrackSection track_di_provenienza) {
        if(parte == null || (parte!=null && (intersected.containsKey(parte.getId()) || tracks.containsKey(parte.getId())))){
            return true;
        }
        boolean arrivatoInCima = false;
        tracks.put(parte.getId(),TrackSection.deepCopy(parte));
        while(!arrivatoInCima){
            TrackSection up = null;
            if(parte.isPoint()){
                if(!parte.punto_di_separazione){
                    TrackSection appoggio = netTracks.get(parte.getStem().getRef());
                    if(appoggio != null && (!appoggio.isPoint() && appoggio.getDown().getRef().equals(parte.getId()) || appoggio.isPoint())){
                        up = TrackSection.deepCopy(netTracks.get(parte.getStem().getRef()));
                        goUp(netTracks, up, tracks, intersected, parte);
                    }
                    String relazione = parte.relazioneTrackPunto(track_di_provenienza);
                    if(relazione.equals("plus")){
                        up = TrackSection.deepCopy(netTracks.get(parte.getMinus().getRef()));
                        goDown(netTracks, up, tracks, intersected, parte);
                    }
                    if(relazione.equals("minus")){
                        up = TrackSection.deepCopy(netTracks.get(parte.getPlus().getRef()));
                        goDown(netTracks, up, tracks, intersected, parte);
                    }
                }else{
                    TrackSection appoggio = netTracks.get(parte.getPlus().getRef());
                    if(appoggio != null && (!appoggio.isPoint() && appoggio.getDown().getRef().equals(parte.getId()) || appoggio.isPoint()) ){
                        up = TrackSection.deepCopy(netTracks.get(parte.getPlus().getRef()));
                        goUp(netTracks, up, tracks, intersected, parte);
                        up = null;
                    }
                    appoggio = netTracks.get(parte.getMinus().getRef());
                    if(appoggio != null && (!appoggio.isPoint() && appoggio.getDown().getRef().equals(parte.getId()) || appoggio.isPoint())){
                        up = TrackSection.deepCopy(netTracks.get(parte.getMinus().getRef()));
                        goUp(netTracks, up, tracks, intersected, parte);
                        up = null;
                    }
                }
            }else{
                up = TrackSection.deepCopy(netTracks.get(parte.getUp().getRef()));
            }
            if(up!=null){
                if(intersected.containsKey(up.getId()) || tracks.containsKey(up.getId())){
                    arrivatoInCima = true;  
                }else{
                    track_di_provenienza =  TrackSection.deepCopy(parte);
                }
                tracks.put(up.getId(),up);
                parte = TrackSection.deepCopy(up);
            }else{
              arrivatoInCima = true;  
            }
        }
        return arrivatoInCima;
    }

    public void addCut(String user_input, AnchorPane pane) {
        if(!tagliDiRete.containsKey(user_input)){
            String[] tracceTagliate = user_input.split(";");
            TaglioRete taglioRete = new TaglioRete();
            taglioRete.setPrimoTaglio(tracceTagliate[0]);
            Boolean taglioDoppio = tracceTagliate.length > 1;
            String definition = TaglioRete.createDefinition(tracceTagliate);
            taglioRete.setDefinition(definition);
            if(taglioDoppio){
                taglioRete.setSecondoTaglio(tracceTagliate[1]);
            }
            String[] tagli = tracceTagliate[0].split("-"); 
            Linear c = compsBase.get(tagli[0]);
            Point2D start;
            Point2D end;
            if(c == null){
                Point p = pointsBase.get(tagli[0]);
                start = new Point2D(p.labelLine.getLayoutX() + 30, p.labelLine.getLayoutY() - 30.0) ;
                end = new Point2D(p.labelLine.getLayoutX() + 30, p.labelLine.getLayoutY() + 30.0) ;
            }else{
                start = new Point2D(c.imvRight.getLayoutX() + c.imvRight.getImage().getWidth()/2, c.imvRight.getLayoutY() - c.imvRight.getImage().getHeight()/2) ;
                end = new Point2D(c.imvRight.getLayoutX() + c.imvRight.getImage().getWidth()/2, c.imvRight.getLayoutY() + c.imvRight.getImage().getHeight() + c.imvRight.getImage().getHeight()/2) ;
            }
            if(taglioDoppio){
                String[] secondo_taglio = tracceTagliate[1].split("-"); 
                Linear c2 = compsBase.get(secondo_taglio[0]);
                if(c2 == null){
                    Point p2 = pointsBase.get(secondo_taglio[0]);
                    end = new Point2D(p2.labelLine.getLayoutX() + 30, p2.labelLine.getLayoutY() + 30) ;
                }else{
                    end = new Point2D(c2.labelLine.getLayoutX() + 30, c2.labelLine.getLayoutY() + 30) ;
                }
            }
            Line cut = new Line(start.getX(), start.getY(), end.getX(), end.getY());
            cut.setStyle("-fx-stroke: green;");
            cut.setStrokeWidth(2.0);
            taglioRete.setCut(cut);
            pane.getChildren().addAll(cut);
            this.tagliDiRete.put(user_input, taglioRete);
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Taglio già presente");
            alert.showAndWait();
        }
    }

    public void removeCut(AnchorPane pane, String user_input) {
        if(tagliDiRete.containsKey(user_input)){
            TaglioRete cutRemoved = this.tagliDiRete.get(user_input);
            this.tagliDiRete.remove(user_input);
            pane.getChildren().remove(cutRemoved.getCut());
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Taglio non presente");
            alert.showAndWait();
        }
    }

    public List<XMI> download() {
        //TODO: added build net to reassign neighbors [EDITED BY ZECCHI]
        net.reloadNeighbors();

        List<XMI> results = new ArrayList<XMI>();
        if(tagliDiRete.size() > 0){
            results = dividePaneMultipleCuts(this.tagliDiRete);
        }else{
            results.add(xmi);
        }
        return results;
    }

    public void reload(String user_input, AnchorPane pane, Group group, ScrollPane scroller, Slider slider) {
        if(compsBase.size() > 0){
            for(Linear c : compsBase.values()){
                c.clean();
            }
            compsBase.clear();
        }
        if(pointsBase.size() > 0){
            pointsBase.clear();
        }
        if(this.tagliDiRete.size() > 0){
            tagliDiRete.clear();
        }
        parseXML(this.xmi, pane, group, scroller, slider, user_input);
    }

    public void rotatePoint(String pointId) {
        Point point = pointsBase.get(pointId);
        if(point != null){
            point.rotateBranch();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Scambio non trovato: " + pointId);
            alert.showAndWait();
        }
    }

    public void clear() {
        if(compsBase.size() > 0){
            for(Linear c : compsBase.values()){
                c.clean();
            }
            compsBase.clear();
        }
        if(pointsBase.size() > 0){
            for(Point p : pointsBase.values()){
                p.clean();
            }
            pointsBase.clear();
        }
        if(this.tagliDiRete.size() > 0){
            tagliDiRete.clear();
        }
        if(this.xmi!=null){
            this.xmi.pulisciXMI();
        }
        this.xmi = null;
        if(this.net != null){
            this.net.pulisciNet();
        }
        this.net = null;
    }

    public void preReUpload() {
        if(compsBase.size() > 0){
            for(Linear c : compsBase.values()){
                c.clean();
            }
            compsBase.clear();
        }
        if(pointsBase.size() > 0){
            for(Point p : pointsBase.values()){
                p.clean();
            }
            pointsBase.clear();
        }
        if(this.tagliDiRete.size() > 0){
            tagliDiRete.clear();
        }
        if(this.net != null){
            this.net.pulisciNet();
        }
        this.net = null;
    }
}

package layoutvisualizer.model;

import java.util.*;

import layoutvisualizer.model.network.Neighbor;
import layoutvisualizer.model.network.Network;
import layoutvisualizer.model.network.RouteTable;
import layoutvisualizer.model.network.TrackSection;
import layoutvisualizer.model.network.XMI;
import layoutvisualizer.model.network.Interlocking;
import layoutvisualizer.model.network.MarkerBoard;

public class Utility {
    public Utility(){}

    public static HashMap<String, TrackSection> checkTreXml(HashMap<String, TrackSection> remainingTracks, String id, String lato_da_tagliare) {
        HashMap<String,TrackSection> intersected = new HashMap<String,TrackSection>();
        HashMap<String,TrackSection> found = new HashMap<String,TrackSection>();
        TrackSection parte = remainingTracks.get(id);
        TrackSection down = null;
        if(parte.isPoint()){
            if(parte.punto_di_separazione){
                TrackSection appoggio = remainingTracks.get(parte.getStem().getRef());
                if(appoggio != null){
                    down = TrackSection.deepCopy(remainingTracks.get(parte.getStem().getRef()));
                    goDown(remainingTracks, down, found, intersected, parte);
                }
                TrackSection track_tagliata = remainingTracks.get(lato_da_tagliare);
                String relazione = parte.relazioneTrackPunto(track_tagliata);
                if(relazione.equals("plus")){
                    down = TrackSection.deepCopy(remainingTracks.get(parte.getMinus().getRef()));
                    goUp(remainingTracks, down, found, intersected, parte);
                }
                if(relazione.equals("minus")){
                    down = TrackSection.deepCopy(remainingTracks.get(parte.getPlus().getRef()));
                    goUp(remainingTracks, down, found, intersected, parte);
                }            
            }else{
                TrackSection appoggio = remainingTracks.get(parte.getPlus().getRef());
                if(appoggio != null && (!appoggio.isPoint() && appoggio.getUp().getRef().equals(parte.getId()) || appoggio.isPoint())){
                    down = TrackSection.deepCopy(remainingTracks.get(parte.getPlus().getRef()));
                    goDown(remainingTracks, down, found, intersected, parte);
                    down = null;
                }
                appoggio =remainingTracks.get(parte.getMinus().getRef());
                if(appoggio != null && (!appoggio.isPoint() && appoggio.getUp().getRef().equals(parte.getId()) || appoggio.isPoint())){
                    down = TrackSection.deepCopy(remainingTracks.get(parte.getMinus().getRef()));
                    goDown(remainingTracks, down, found, intersected, parte);
                    down = null;
                }
            }
        }else{
            down = TrackSection.deepCopy(remainingTracks.get(parte.getDown().getRef()));
            goDown(remainingTracks, down, found, intersected, parte);
        }
        return found;
    }

    public static Boolean goDown(HashMap<String,TrackSection> netTracks, TrackSection parte, HashMap<String,TrackSection> tracks, HashMap<String,TrackSection>intersected, TrackSection track_di_provenienza){
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

    private static boolean goUp(HashMap<String,TrackSection> netTracks, TrackSection parte, HashMap<String, TrackSection> tracks, HashMap<String, TrackSection> intersected, TrackSection track_di_provenienza) {
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

    public static List<XMI> computeTagliPuntoDiSeparazione(TrackSection parte, HashMap<String, TrackSection> netTracks, ArrayList<String> tagliandi,
     HashMap<String, TrackSection> intersected, ArrayList<TrackSection> listOfValues, HashMap<String, 
     TrackSection> tracks, List<MarkerBoard> markers, HashMap<String, TrackSection> remainingTracks, XMI xmiBase, int count, HashMap<String,TrackSection> found, TaglioRete taglio) {
     
        List<XMI> resultsAdded = new ArrayList<XMI>();
        List<TrackSection> tracksList = new ArrayList<TrackSection>();
        List<TrackSection> addedTracks = new ArrayList<TrackSection>();
        TrackSection vecchio_vicino_down = parte.isPoint() ? new TrackSection() : TrackSection.deepCopy(netTracks.get(parte.getDown().getRef()));
        TrackSection track_b = new TrackSection();
        track_b.setType("linear");
        track_b.setLength("52");
        TrackSection parte_sx = TrackSection.deepCopy(parte);
        for(int k = 0; k < tagliandi.size(); k++){
            track_b = new TrackSection();
            track_b.setType("linear");
            track_b.setLength("52");
            TrackSection track_tagliata = netTracks.get(tagliandi.get(k));
            String relazione = parte.relazioneTrackPunto(track_tagliata);

            TrackSection nuova_track = new TrackSection();
            nuova_track.setType("linear");
            nuova_track.setLength("52");
            nuova_track.setId(parte.getId() + "_ex_" + relazione);

            MarkerBoard marker_right = new MarkerBoard();
            marker_right.setMounted("up");
            marker_right.setId("mb_exit_"+nuova_track.getId());
            marker_right.setTrack(nuova_track.getId());
            marker_right.setDistance("51");
            if(!ModelComponent.checkInserimentoMarker(marker_right, markers))
                markers.add(marker_right);

            nuova_track.setRightMarker(marker_right);
            
            track_b.setId(parte.getId() + "_ex_" + relazione + "_b");
            Neighbor neigh_b = new Neighbor();
            neigh_b.setRef(nuova_track.getId());
            neigh_b.setSide("down");

            track_b.getNeighbors().add(neigh_b);
            track_b.setDown(neigh_b);

            MarkerBoard marker_left = new MarkerBoard();
            marker_left.setMounted("down");
            marker_left.setId("mb_entry_"+nuova_track.getId());
            marker_left.setTrack(track_b.getId());
            marker_left.setDistance("51");
            if(!ModelComponent.checkInserimentoMarker(marker_left, markers))
                markers.add(marker_left);

            track_b.setLeftMarker(marker_left);

            Neighbor dx_neigh_nuova_track = new Neighbor();
            dx_neigh_nuova_track.setRef(track_b.getId());
            dx_neigh_nuova_track.setSide("up");

            nuova_track.getNeighbors().add(dx_neigh_nuova_track);
            nuova_track.setUp(dx_neigh_nuova_track);

            Neighbor sx_neigh_nuova_track = new Neighbor();
            sx_neigh_nuova_track.setRef(parte.getId());
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
            tracksList.add(track_b);
        }
        tracks.put(parte_sx.getId(), parte_sx);
        addedTracks.add(parte_sx);

        for(TrackSection t : tracks.values()){
            tracksList.add(t);
            if(!t.isPoint()){
                MarkerBoard l = MarkerBoard.deepCopy(t.getLeftMarker());
                if(l!= null && l.getMounted() != null && l.getMounted().length() > 0){
                    if(!ModelComponent.checkInserimentoMarker(l, markers))
                        markers.add(l);
                }
                MarkerBoard r = MarkerBoard.deepCopy(t.getRightMarker());
                if(r!= null && r.getMounted() != null && r.getMounted().length() > 0){
                    if(!ModelComponent.checkInserimentoMarker(r, markers))
                        markers.add(r);
                }
            }
        }
        XMI newXML = new XMI();
        newXML.setXmiDoc(xmiBase.getXmiDoc());
        Interlocking inter = new Interlocking();
        inter.setId(xmiBase.getInterlocking().getId() + "_" + taglio.getDefinition() + "_left");
        RouteTable rt = new RouteTable();
        rt.setId("rt_" + xmiBase.getInterlocking().getRouteTable().getId() + "_" + taglio.getDefinition() + "_left");
        rt.setNetId("net_" + xmiBase.getInterlocking().getRouteTable().getNetId() + "_" + taglio.getDefinition() + "_left");
        inter.setRouteTable(rt);
        Network newNet = new Network();
        newNet.setId(xmiBase.getInterlocking().getNet().getId() + "_" + taglio.getDefinition() + "_left");
        newNet.setTracks(tracksList);
        newNet.setMarkerBoards(markers);
        inter.setNet(newNet);
        newXML.setInterlocking(inter);
        if(tracksList.size() > 0){
            resultsAdded.add(newXML);
        }

        HashMap<String, TrackSection> remaining_found = new HashMap<String, TrackSection>();
        remainingTracks.remove(parte.getId());
        for(TrackSection t : remainingTracks.values()){
            if(!found.containsKey(t.getId())){
                remaining_found.put(t.getId(), TrackSection.deepCopy(t));
            }
        }

        XMI newXML_dx = new XMI();
        newXML_dx.setXmiDoc(newXML.getXmiDoc());
        Interlocking inter_dx = new Interlocking();
        inter_dx.setId(xmiBase.getInterlocking().getId()  + "_" + taglio.getDefinition() + "_right");
        RouteTable rt_dx = new RouteTable();
        rt_dx.setId("rt_" + xmiBase.getInterlocking().getRouteTable().getId()  + "_" + taglio.getDefinition() + "_right");
        rt_dx.setNetId("net_" + xmiBase.getInterlocking().getRouteTable().getNetId()  + "_" + taglio.getDefinition() + "_right");
        inter_dx.setRouteTable(rt_dx);
        Network newNet_dx = new Network();
        newNet_dx.setId(xmiBase.getInterlocking().getNet().getId() + "_" + taglio.getDefinition() + "_right");
        List<TrackSection> tracks_dx = new ArrayList<TrackSection>();
        List<MarkerBoard> markers_dx = new ArrayList<MarkerBoard>();
        TrackSection parte_0 = TrackSection.deepCopy(netTracks.get(tagliandi.get(0)));
        String latoDaTagliareZero = tagliandi.get(0);
        if(!found.containsKey(tagliandi.get(0))){
            parte_0 = TrackSection.deepCopy(netTracks.get(tagliandi.get(1)));
            latoDaTagliareZero = tagliandi.get(1);
        }
        vecchio_vicino_down = parte_0.isPoint() ? new TrackSection() : TrackSection.deepCopy(netTracks.get(parte_0.getDown().getRef()));

        TrackSection track_ex = new TrackSection();
        track_ex.setType("linear");
        track_ex.setLength("52");
        TrackSection parte_dx = TrackSection.deepCopy(parte_0);
        if((!vecchio_vicino_down.isPoint() && !parte_0.isPoint()) || (!parte_0.isPoint() && (parte_0.getRightMarker() == null || (parte_0.getRightMarker()!=null && (parte_0.getRightMarker().getMounted()== null || parte_0.getRightMarker().getMounted().length() == 0))))){
            TrackSection vecchio_vicino_up = TrackSection.deepCopy(netTracks.get(parte_0.getUp().getRef()));
            String relazione = "";
            if(vecchio_vicino_up.isPoint()){
                relazione = vecchio_vicino_up.relazioneTrackPunto(parte_0);
                track_ex.setId(parte_0.getUp().getRef() + "_ex_" + relazione );
            }else{
                track_ex.setId(parte_0.getUp().getRef() + "_ex");
            }

            Neighbor neigh_ex_down = new Neighbor();
            neigh_ex_down.setRef(parte_0.getId());
            neigh_ex_down.setSide("down");
            track_ex.getNeighbors().add(neigh_ex_down);
            track_ex.setDown(neigh_ex_down);

            Neighbor neigh_ex_up = Neighbor.deepCopy(parte_0.getUp());
            track_ex.getNeighbors().add(neigh_ex_up);
            track_ex.setUp(neigh_ex_up);

            MarkerBoard marker_ex_down = new MarkerBoard();
            marker_ex_down.setMounted("down");
            marker_ex_down.setId("mb_exit_"+track_ex.getId());
            marker_ex_down.setTrack(track_ex.getId());
            marker_ex_down.setDistance("51");
            if(!ModelComponent.checkInserimentoMarker(marker_ex_down, markers_dx))
                markers_dx.add(marker_ex_down);

            if(!netTracks.get(parte_0.getUp().getRef()).isPoint()){
                MarkerBoard marker_ex_up = new MarkerBoard();
                marker_ex_up.setMounted("up");
                marker_ex_up.setId("mb_entry_"+track_ex.getId());
                marker_ex_up.setTrack(track_ex.getId());
                marker_ex_up.setDistance("51");
                if(!ModelComponent.checkInserimentoMarker(marker_ex_up, markers_dx))
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
            if(!ModelComponent.checkInserimentoMarker(parte_dx_marker, markers_dx))
                markers_dx.add(parte_dx_marker);
            parte_dx.setLeftMarker(new MarkerBoard());

            if(!parte_0.isPoint() && (parte_0.getRightMarker() == null || (parte_0.getRightMarker()!=null && (parte_0.getRightMarker().getMounted()== null || parte_0.getRightMarker().getMounted().length() == 0)))){
                MarkerBoard marker_b_dx = new MarkerBoard();
                marker_b_dx.setMounted("up");
                marker_b_dx.setId("mb_entry_"+track_ex.getId());
                marker_b_dx.setTrack(parte_dx.getId());
                marker_b_dx.setDistance("51");
                if(!ModelComponent.checkInserimentoMarker(marker_b_dx, markers_dx))
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
                    if(!ModelComponent.checkInserimentoMarker(l, markers_dx))
                        markers_dx.add(l);
                }
                MarkerBoard r = MarkerBoard.deepCopy(vecchio_vicino_up.getRightMarker());
                if(r!= null && r.getMounted() != null && r.getMounted().length() > 0){
                    if(!ModelComponent.checkInserimentoMarker(r, markers_dx))
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
            tracks_dx.add(vecchio_vicino_up);
            found.remove(vecchio_vicino_up.getId());
            tracks_dx.add(track_ex);
            tracks_dx.add(parte_dx);
            found.remove(parte_dx.getId());
        }else if(!parte_0.isPoint()){
            track_ex.setId(parte_0.getId() + "_b");
            Neighbor neigh_b = new Neighbor();
            neigh_b.setRef(parte_0.getId());
            neigh_b.setSide("up");
            track_ex.getNeighbors().add(neigh_b);
            track_ex.setUp(neigh_b);
            MarkerBoard marker_b = new MarkerBoard();
            marker_b.setMounted("up");
            marker_b.setId("mb_entry_"+parte_0.getId());
            marker_b.setTrack(track_ex.getId());
            marker_b.setDistance("51");
            if(!ModelComponent.checkInserimentoMarker(marker_b, markers_dx))
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
            if(!ModelComponent.checkInserimentoMarker(parte_dx_marker, markers_dx))
                markers_dx.add(parte_dx_marker);
            MarkerBoard parte_dx_marker_left = MarkerBoard.deepCopy(parte_dx.getLeftMarker());
            if(!ModelComponent.checkInserimentoMarker(parte_dx_marker_left, markers_dx))
                markers_dx.add(parte_dx_marker_left);
            tracks_dx.add(track_ex);
            tracks_dx.add(parte_dx);
            found.remove(parte_dx.getId());
        }else{
            TrackSection track_tagliata = TrackSection.deepCopy(netTracks.get(latoDaTagliareZero));
            String relazione_vicino_up = track_tagliata.relazioneTrackPunto(parte);
            track_ex.setId(track_tagliata.getId() + "_ex_" + relazione_vicino_up + "_b");
            MarkerBoard marker_b = new MarkerBoard();
            marker_b.setMounted("up");
            marker_b.setId("mb_entry_"+ track_tagliata.getId() + "_ex_" + relazione_vicino_up);
            marker_b.setTrack(track_ex.getId());
            marker_b.setDistance("51");
            if(!ModelComponent.checkInserimentoMarker(marker_b, markers_dx))
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
            if(!ModelComponent.checkInserimentoMarker(marker_nuova_track_dx, markers_dx))
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
            found.remove(track_tagliata.getId());    
            tracks_dx.add(track_ex);
            tracks_dx.add(parte_dx);
        }
        for(TrackSection t : found.values()){
            tracks_dx.add(TrackSection.deepCopy(t));
            if(!t.isPoint()){
                MarkerBoard l = MarkerBoard.deepCopy(t.getLeftMarker());
                if(l!= null && l.getMounted() != null && l.getMounted().length() > 0){
                    if(!ModelComponent.checkInserimentoMarker(l, markers_dx))
                        markers_dx.add(l);
                }
                MarkerBoard r = MarkerBoard.deepCopy(t.getRightMarker());
                if(r!= null && r.getMounted() != null && r.getMounted().length() > 0){
                    if(!ModelComponent.checkInserimentoMarker(r, markers_dx))
                        markers_dx.add(r);
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
        XMI terzo = new XMI();
        terzo.setXmiDoc(newXML.getXmiDoc());
        Interlocking inter_terzo = new Interlocking();
        inter_terzo.setId(xmiBase.getInterlocking().getId()  + "_" + taglio.getDefinition() + "_third");
        RouteTable rt_terzo = new RouteTable();
        rt_terzo.setId("rt_" + xmiBase.getInterlocking().getRouteTable().getId()  + "_" + taglio.getDefinition() + "_third");
        rt_terzo.setNetId("net_" + xmiBase.getInterlocking().getRouteTable().getNetId()  + "_" + taglio.getDefinition() + "_third");
        inter_terzo.setRouteTable(rt_terzo);
        Network newNet_terzo = new Network();
        newNet_terzo.setId(xmiBase.getInterlocking().getNet().getId() + "_" + taglio.getDefinition() + "__third");
        List<TrackSection> tracks_terzo = new ArrayList<TrackSection>();
        List<MarkerBoard> markers_terzo = new ArrayList<MarkerBoard>();
        TrackSection parte_3 = TrackSection.deepCopy(netTracks.get(tagliandi.get(1)));
        String latoDaTagliareUno = tagliandi.get(1);
        if(!remaining_found.containsKey(latoDaTagliareUno)){
            parte_3 = TrackSection.deepCopy(netTracks.get(tagliandi.get(0)));
            latoDaTagliareUno = tagliandi.get(0);
        }
        vecchio_vicino_down = parte_3.isPoint() ? new TrackSection() : TrackSection.deepCopy(netTracks.get(parte_3.getDown().getRef()));
        TrackSection track_3 = new TrackSection();
        track_3.setType("linear");
        track_3.setLength("52");
        TrackSection altro_terzo = TrackSection.deepCopy(parte_3);
        sistemaLatoDestro(vecchio_vicino_down, parte_3, track_3, netTracks, markers_terzo, altro_terzo, tracks_terzo, remaining_found, latoDaTagliareUno, parte);
        for(TrackSection t : remaining_found.values()){
            tracks_terzo.add(TrackSection.deepCopy(t));
            if(!t.isPoint()){
                MarkerBoard l = MarkerBoard.deepCopy(t.getLeftMarker());
                if(l!= null && l.getMounted() != null && l.getMounted().length() > 0){
                    if(!ModelComponent.checkInserimentoMarker(l, markers_terzo))
                        markers_terzo.add(l);
                }
                MarkerBoard r = MarkerBoard.deepCopy(t.getRightMarker());
                if(r!= null && r.getMounted() != null && r.getMounted().length() > 0){
                    if(!ModelComponent.checkInserimentoMarker(r, markers_terzo))
                        markers_terzo.add(r);
                }
            }
        }
        newNet_terzo.setTracks(tracks_terzo);
        newNet_terzo.setMarkerBoards(markers_terzo);
        inter_terzo.setNet(newNet_terzo);
        terzo.setInterlocking(inter_terzo);
        if(tracks_terzo.size() > 0){
            resultsAdded.add(terzo);
        }
        return resultsAdded;
    }

    private static void sistemaLatoDestro(TrackSection vecchio_vicino_down, TrackSection parte_0, TrackSection track_ex, HashMap<String, TrackSection> netTracks, List<MarkerBoard> markers_dx,
        TrackSection parte_dx, List<TrackSection> tracks_dx, HashMap<String,TrackSection> found, String latoTagliato, TrackSection parte) {
        if((!vecchio_vicino_down.isPoint() && !parte_0.isPoint()) || (!parte_0.isPoint() && (parte_0.getRightMarker() == null || (parte_0.getRightMarker()!=null && (parte_0.getRightMarker().getMounted()== null || parte_0.getRightMarker().getMounted().length() == 0))))){
            TrackSection vecchio_vicino_up = TrackSection.deepCopy(netTracks.get(parte_0.getUp().getRef()));
            String relazione = "";
            if(vecchio_vicino_up.isPoint()){
                relazione = vecchio_vicino_up.relazioneTrackPunto(parte_0);
                track_ex.setId(parte_0.getUp().getRef() + "_ex_" + relazione );
            }else{
                track_ex.setId(parte_0.getUp().getRef() + "_ex");
            }

            Neighbor neigh_ex_down = new Neighbor();
            neigh_ex_down.setRef(parte_0.getId());
            neigh_ex_down.setSide("down");
            track_ex.getNeighbors().add(neigh_ex_down);
            track_ex.setDown(neigh_ex_down);

            Neighbor neigh_ex_up = Neighbor.deepCopy(parte_0.getUp());
            track_ex.getNeighbors().add(neigh_ex_up);
            track_ex.setUp(neigh_ex_up);

            MarkerBoard marker_ex_down = new MarkerBoard();
            marker_ex_down.setMounted("down");
            marker_ex_down.setId("mb_exit_"+track_ex.getId());
            marker_ex_down.setTrack(track_ex.getId());
            marker_ex_down.setDistance("51");
            if(!ModelComponent.checkInserimentoMarker(marker_ex_down, markers_dx))
                markers_dx.add(marker_ex_down);

            if(!netTracks.get(parte_0.getUp().getRef()).isPoint()){
                MarkerBoard marker_ex_up = new MarkerBoard();
                marker_ex_up.setMounted("up");
                marker_ex_up.setId("mb_entry_"+track_ex.getId());
                marker_ex_up.setTrack(track_ex.getId());
                marker_ex_up.setDistance("51");
                if(!ModelComponent.checkInserimentoMarker(marker_ex_up, markers_dx))
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
            if(!ModelComponent.checkInserimentoMarker(parte_dx_marker, markers_dx))
                markers_dx.add(parte_dx_marker);

            parte_dx.setLeftMarker(new MarkerBoard());

            if(!parte_0.isPoint() && (parte_0.getRightMarker() == null || (parte_0.getRightMarker()!=null && (parte_0.getRightMarker().getMounted()== null || parte_0.getRightMarker().getMounted().length() == 0)))){
                MarkerBoard marker_b_dx = new MarkerBoard();
                marker_b_dx.setMounted("up");
                marker_b_dx.setId("mb_entry_"+track_ex.getId());
                marker_b_dx.setTrack(parte_dx.getId());
                marker_b_dx.setDistance("51");
                if(!ModelComponent.checkInserimentoMarker(marker_b_dx, markers_dx))
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
                    if(!ModelComponent.checkInserimentoMarker(l, markers_dx))
                        markers_dx.add(l);
                }
                MarkerBoard r = MarkerBoard.deepCopy(vecchio_vicino_up.getRightMarker());
                if(r!= null && r.getMounted() != null && r.getMounted().length() > 0){
                    if(!ModelComponent.checkInserimentoMarker(r, markers_dx))
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
            tracks_dx.add(vecchio_vicino_up);
            found.remove(vecchio_vicino_up.getId());
            tracks_dx.add(track_ex);
            tracks_dx.add(parte_dx);
            found.remove(parte_dx.getId());

        }else if(!parte_0.isPoint()){
            track_ex.setId(parte_0.getId() + "_b");
            Neighbor neigh_b = new Neighbor();
            neigh_b.setRef(parte_0.getId());
            neigh_b.setSide("up");
            track_ex.getNeighbors().add(neigh_b);
            track_ex.setUp(neigh_b);
            MarkerBoard marker_b = new MarkerBoard();
            marker_b.setMounted("up");
            marker_b.setId("mb_entry_"+parte_0.getId());
            marker_b.setTrack(track_ex.getId());
            marker_b.setDistance("51");
            if(!ModelComponent.checkInserimentoMarker(marker_b, markers_dx))
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
            if(!ModelComponent.checkInserimentoMarker(parte_dx_marker, markers_dx))
                markers_dx.add(parte_dx_marker);
            MarkerBoard parte_dx_marker_left = MarkerBoard.deepCopy(parte_dx.getLeftMarker());
            if(!ModelComponent.checkInserimentoMarker(parte_dx_marker_left, markers_dx))
                markers_dx.add(parte_dx_marker_left);
            tracks_dx.add(track_ex);
            tracks_dx.add(parte_dx);
            found.remove(parte_dx.getId());
        }else{
            TrackSection track_tagliata = TrackSection.deepCopy(netTracks.get(latoTagliato));
            String relazione_vicino_up = track_tagliata.relazioneTrackPunto(parte);
            track_ex.setId(track_tagliata.getId() + "_ex_" + relazione_vicino_up + "_b");

            MarkerBoard marker_b = new MarkerBoard();
            marker_b.setMounted("up");
            marker_b.setId("mb_entry_"+ track_tagliata.getId() + "_ex_" + relazione_vicino_up);
            marker_b.setTrack(track_ex.getId());
            marker_b.setDistance("51");
            if(!ModelComponent.checkInserimentoMarker(marker_b, markers_dx))
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
            if(!ModelComponent.checkInserimentoMarker(marker_nuova_track_dx, markers_dx))
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
            found.remove(track_tagliata.getId());    

            tracks_dx.add(track_ex);
            tracks_dx.add(parte_dx);
        }
    }

    public static boolean checkTreXmlPuntoUnione(HashMap<String, TrackSection> tracks, TrackSection parte,
            TrackSection seconda_parte, HashMap<String, TrackSection> netTracks, TrackSection puntoUnione) {
        boolean treXml = false;
        tracks.put(parte.getId(), parte);
        if(seconda_parte.isPoint()){
            String relazione = seconda_parte.relazioneTrackPunto(puntoUnione);
            if(relazione.equals("plus")){
                if(!(tracks.containsKey(seconda_parte.getMinus().getRef())) && !(tracks.containsKey(seconda_parte.getStem().getRef()))){
                    treXml = true;
                }
            }
            if(relazione.equals("minus")){
                if(!(tracks.containsKey(seconda_parte.getPlus().getRef())) && !(tracks.containsKey(seconda_parte.getStem().getRef()))){
                    treXml = true;
                }
            }
            if(relazione.equals("stem")){
                if(!(tracks.containsKey(seconda_parte.getMinus().getRef())) && !(tracks.containsKey(seconda_parte.getPlus().getRef()))){
                    treXml = true;
                }
            }
        }else{
            if(!tracks.containsKey(seconda_parte.getDown().getRef())){
                treXml = true;
            }
        }
        tracks.remove(parte.getId());
        return treXml;
    }

    public static List<XMI> computeTagliPuntoUnione(TrackSection parte, TrackSection seconda_parte, HashMap<String, TrackSection> netTracks,
            ArrayList<String> tagliandi, HashMap<String, TrackSection> intersected,
            ArrayList<TrackSection> listOfValues, HashMap<String, TrackSection> tracks, List<MarkerBoard> markers,
            HashMap<String, TrackSection> remainingTracks, XMI xmiBase, TaglioRete taglio) {
        
        List<XMI> resultsAdded = new ArrayList<XMI>();
        List<TrackSection> tracksList = new ArrayList<TrackSection>();
        List<TrackSection> addedTracks = new ArrayList<TrackSection>();

        TrackSection vecchio_vicino_down = parte.isPoint() ? new TrackSection() : TrackSection.deepCopy(netTracks.get(parte.getDown().getRef()));

        TrackSection track_b = new TrackSection();
        track_b.setType("linear");
        track_b.setLength("52");
        TrackSection parte_sx = TrackSection.deepCopy(parte);
        List<Neighbor> listNeighbor = new ArrayList<Neighbor>();

        if(parte.isPoint()){
            TrackSection track_tagliata = netTracks.get(tagliandi.get(0));
            String relazione = parte.relazioneTrackPunto(track_tagliata);

            TrackSection nuova_track = new TrackSection();
            nuova_track.setType("linear");
            nuova_track.setLength("52");
            nuova_track.setId(parte.getId() + "_ex_" + relazione);

            MarkerBoard marker_right = new MarkerBoard();
            marker_right.setMounted("up");
            marker_right.setId("mb_exit_"+nuova_track.getId());
            marker_right.setTrack(nuova_track.getId());
            marker_right.setDistance("51");
            if(!ModelComponent.checkInserimentoMarker(marker_right, markers))
                markers.add(marker_right);

            nuova_track.setRightMarker(marker_right);
            
            track_b.setId(parte.getId() + "_ex_" + relazione + "_b");
            Neighbor neigh_b = new Neighbor();
            neigh_b.setRef(nuova_track.getId());
            neigh_b.setSide("down");

            track_b.getNeighbors().add(neigh_b);
            track_b.setDown(neigh_b);

            MarkerBoard marker_left = new MarkerBoard();
            marker_left.setMounted("down");
            marker_left.setId("mb_entry_"+nuova_track.getId());
            marker_left.setTrack(track_b.getId());
            marker_left.setDistance("51");
            if(!ModelComponent.checkInserimentoMarker(marker_left, markers))
                markers.add(marker_left);

            track_b.setLeftMarker(marker_left);

            Neighbor dx_neigh_nuova_track = new Neighbor();
            dx_neigh_nuova_track.setRef(track_b.getId());
            dx_neigh_nuova_track.setSide("up");

            nuova_track.getNeighbors().add(dx_neigh_nuova_track);
            nuova_track.setUp(dx_neigh_nuova_track);

            Neighbor sx_neigh_nuova_track = new Neighbor();
            sx_neigh_nuova_track.setRef(parte.getId());
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
            tracksList.add(track_b);

            tracks.put(parte_sx.getId(), parte_sx);
            addedTracks.add(parte_sx);

        }else{
            ModelComponent.sistemaTaglioLatoSinistro(parte, parte_sx, vecchio_vicino_down, listNeighbor, tracks, markers, track_b);
            if(vecchio_vicino_down.isPoint()){
                tracks.remove(vecchio_vicino_down.getId());
                tracks.put(vecchio_vicino_down.getId(), vecchio_vicino_down);
                vecchio_vicino_down = TrackSection.deepCopy(vecchio_vicino_down);
            }    
            tracksList.add(track_b);
            tracks.put(parte_sx.getId(), parte_sx);
            addedTracks.add(parte_sx);        
        }

        for(TrackSection t : tracks.values()){
            tracksList.add(t);
            if(!t.isPoint()){
                MarkerBoard l = MarkerBoard.deepCopy(t.getLeftMarker());
                if(l!= null && l.getMounted() != null && l.getMounted().length() > 0){
                    if(!ModelComponent.checkInserimentoMarker(l, markers))
                        markers.add(l);
                }
                MarkerBoard r = MarkerBoard.deepCopy(t.getRightMarker());
                if(r!= null && r.getMounted() != null && r.getMounted().length() > 0){
                    if(!ModelComponent.checkInserimentoMarker(r, markers))
                        markers.add(r);
                }
            }
        }
        XMI newXML = new XMI();
        newXML.setXmiDoc(xmiBase.getXmiDoc());
        Interlocking inter = new Interlocking();
        inter.setId(xmiBase.getInterlocking().getId() + "_" + taglio.getDefinition() + "_left");
        RouteTable rt = new RouteTable();
        rt.setId("rt_" + xmiBase.getInterlocking().getRouteTable().getId() + "_" + taglio.getDefinition() + "_left");
        rt.setNetId("net_" + xmiBase.getInterlocking().getRouteTable().getNetId() + "_" + taglio.getDefinition() + "_left");
        inter.setRouteTable(rt);
        Network newNet = new Network();
        newNet.setId(xmiBase.getInterlocking().getNet().getId() + "_" + taglio.getDefinition() + "_left");
        newNet.setTracks(tracksList);
        newNet.setMarkerBoards(markers);
        inter.setNet(newNet);
        newXML.setInterlocking(inter);
        if(tracksList.size() > 0){
            resultsAdded.add(newXML);
        }


        HashMap<String,TrackSection> tracks_terzo = new HashMap<String,TrackSection>();
        tracks_terzo.put(seconda_parte.getId(), seconda_parte);

        TrackSection down = null;
        if(seconda_parte.isPoint()){
            if(seconda_parte.punto_di_separazione){
                TrackSection appoggio = netTracks.get(seconda_parte.getStem().getRef());
                if(appoggio != null){
                    down = TrackSection.deepCopy(netTracks.get(seconda_parte.getStem().getRef()));
                    goDown(netTracks, down, tracks_terzo, intersected, seconda_parte);
                }
                TrackSection track_tagliata = netTracks.get(tagliandi.get(0));
                String relazione = seconda_parte.relazioneTrackPunto(track_tagliata);
                if(relazione.equals("plus")){
                    down = TrackSection.deepCopy(netTracks.get(seconda_parte.getMinus().getRef()));
                    goUp(netTracks, down, tracks_terzo, intersected, seconda_parte);
                }
                if(relazione.equals("minus")){
                    down = TrackSection.deepCopy(netTracks.get(seconda_parte.getPlus().getRef()));
                    goUp(netTracks, down, tracks_terzo, intersected, seconda_parte);
                }
            }else{
                TrackSection appoggio = netTracks.get(seconda_parte.getPlus().getRef());
                if(appoggio != null && (!appoggio.isPoint() && appoggio.getUp().getRef().equals(seconda_parte.getId()) || appoggio.isPoint())){
                    down = TrackSection.deepCopy(netTracks.get(seconda_parte.getPlus().getRef()));
                    goDown(netTracks, down, tracks_terzo, intersected, seconda_parte);
                    down = null;
                }
                appoggio =netTracks.get(seconda_parte.getMinus().getRef());
                if(appoggio != null && (!appoggio.isPoint() && appoggio.getUp().getRef().equals(seconda_parte.getId()) || appoggio.isPoint())){
                    down = TrackSection.deepCopy(netTracks.get(seconda_parte.getMinus().getRef()));
                    goDown(netTracks, down, tracks_terzo, intersected, seconda_parte);
                    down = null;
                }
            }
        }else{
            down = TrackSection.deepCopy(netTracks.get(seconda_parte.getDown().getRef()));
            goDown(netTracks, down, tracks_terzo, intersected, seconda_parte);
        }

        tracks_terzo.remove(seconda_parte.getId());

        List<TrackSection> tracksList_terzo = new ArrayList<TrackSection>();
        List<TrackSection> addedTracks_terzo = new ArrayList<TrackSection>();
        List<MarkerBoard> markers_terzo = new ArrayList<MarkerBoard>();
        //TrackSection vecchio_vicino_down_terzo = seconda_parte.isPoint() ? new TrackSection() : TrackSection.deepCopy(netTracks.get(seconda_parte.getDown().getRef()));
        sistemaLatoSinistroTotale(seconda_parte, netTracks, tagliandi, tracks_terzo, markers_terzo, tracksList_terzo, addedTracks_terzo);

        for(TrackSection t : tracks_terzo.values()){
            tracksList_terzo.add(t);
            if(!t.isPoint()){
                MarkerBoard l = MarkerBoard.deepCopy(t.getLeftMarker());
                if(l!= null && l.getMounted() != null && l.getMounted().length() > 0){
                    if(!ModelComponent.checkInserimentoMarker(l, markers_terzo))
                        markers_terzo.add(l);
                }
                MarkerBoard r = MarkerBoard.deepCopy(t.getRightMarker());
                if(r!= null && r.getMounted() != null && r.getMounted().length() > 0){
                    if(!ModelComponent.checkInserimentoMarker(r, markers_terzo))
                        markers_terzo.add(r);
                }
            }
        }
        XMI terzo = new XMI();
        terzo.setXmiDoc(newXML.getXmiDoc());
        Interlocking inter_terzo = new Interlocking();
        inter_terzo.setId(xmiBase.getInterlocking().getId()  + "_" + taglio.getDefinition() + "_third");
        RouteTable rt_terzo = new RouteTable();
        rt_terzo.setId("rt_" + xmiBase.getInterlocking().getRouteTable().getId()  + "_" + taglio.getDefinition() + "_third");
        rt_terzo.setNetId("net_" + xmiBase.getInterlocking().getRouteTable().getNetId()  + "_" + taglio.getDefinition() + "_third");
        inter_terzo.setRouteTable(rt_terzo);
        Network newNet_terzo = new Network();
        newNet_terzo.setId(xmiBase.getInterlocking().getNet().getId() + "_" + taglio.getDefinition() + "_third");
        newNet_terzo.setTracks(tracksList_terzo);
        newNet_terzo.setMarkerBoards(markers_terzo);
        inter_terzo.setNet(newNet_terzo);
        terzo.setInterlocking(inter_terzo);
        if(tracksList_terzo.size() > 0){
            resultsAdded.add(terzo);
        }
        
        HashMap<String, TrackSection> rightTracks = new HashMap<String, TrackSection>();
        for(TrackSection t : netTracks.values()){
            if(!tracks.containsKey(t.getId()) && !tracks_terzo.containsKey(t.getId())){
                rightTracks.put(t.getId(), TrackSection.deepCopy(t));
            }
        }
        List<TrackSection> tracksList_dx = new ArrayList<TrackSection>();
        List<MarkerBoard> markers_dx = new ArrayList<MarkerBoard>();
        TrackSection vecchio_vicino_up = TrackSection.deepCopy(netTracks.get(tagliandi.get(0)));
        TrackSection vecchio_vicino_down_1 = parte.isPoint() ? new TrackSection() : TrackSection.deepCopy(netTracks.get(parte.getDown().getRef()));
        TrackSection nuovo_vicino_up = sistemaLatoDestroUnione(vecchio_vicino_up, vecchio_vicino_down_1, parte, markers_dx, tracksList_dx, netTracks, rightTracks);
        TrackSection vecchio_vicino_down_2 = seconda_parte.isPoint() ? new TrackSection() : TrackSection.deepCopy(netTracks.get(seconda_parte.getDown().getRef()));
        nuovo_vicino_up = sistemaLatoDestroUnione(nuovo_vicino_up, vecchio_vicino_down_2, seconda_parte, markers_dx, tracksList_dx, netTracks, rightTracks);

        XMI newXML_dx = new XMI();
        newXML_dx.setXmiDoc(newXML.getXmiDoc());
        Interlocking inter_dx = new Interlocking();
        inter_dx.setId(xmiBase.getInterlocking().getId()  + "_" + taglio.getDefinition() + "_right");
        RouteTable rt_dx = new RouteTable();
        rt_dx.setId("rt_" + xmiBase.getInterlocking().getRouteTable().getId()  + "_" + taglio.getDefinition() + "_right");
        rt_dx.setNetId("net_" + xmiBase.getInterlocking().getRouteTable().getNetId()  + "_" + taglio.getDefinition() + "_right");
        inter_dx.setRouteTable(rt_dx);
        Network newNet_dx = new Network();
        newNet_dx.setId(xmiBase.getInterlocking().getNet().getId() + "_" + taglio.getDefinition() + "_right");

        for(TrackSection t : rightTracks.values()){
            tracksList_dx.add(t);
            if(!t.isPoint()){
                MarkerBoard l = MarkerBoard.deepCopy(t.getLeftMarker());
                if(l!= null && l.getMounted() != null && l.getMounted().length() > 0){
                    if(!ModelComponent.checkInserimentoMarker(l, markers_dx))
                        markers_dx.add(l);
                }
                MarkerBoard r = MarkerBoard.deepCopy(t.getRightMarker());
                if(r!= null && r.getMounted() != null && r.getMounted().length() > 0){
                    if(!ModelComponent.checkInserimentoMarker(r, markers_dx))
                        markers_dx.add(r);
                }
            }
        }
        newNet_dx.setTracks(tracksList_dx);
        newNet_dx.setMarkerBoards(markers_dx);
        inter_dx.setNet(newNet_dx);
        newXML_dx.setInterlocking(inter_dx);
        if(tracksList_dx.size() > 0){
            resultsAdded.add(newXML_dx);
        }
        return resultsAdded;
    }

    private static TrackSection sistemaLatoDestroUnione(TrackSection vecchio_vicino_up, TrackSection vecchio_vicino_down, TrackSection parte_j, List<MarkerBoard> markers_dx, List<TrackSection> tracks_dx
                ,HashMap<String, TrackSection> netTracks, HashMap<String, TrackSection> tracks) {
        TrackSection track_ex = new TrackSection();
        track_ex.setType("linear");
        track_ex.setLength("52");
        TrackSection parte_dx = TrackSection.deepCopy(parte_j);
        if((!vecchio_vicino_down.isPoint() && !parte_j.isPoint()) || (!parte_j.isPoint() && (parte_j.getRightMarker() == null || (parte_j.getRightMarker()!=null && (parte_j.getRightMarker().getMounted()== null || parte_j.getRightMarker().getMounted().length() == 0))))){
            String relazione = "";
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
            if(!ModelComponent.checkInserimentoMarker(marker_ex_down, markers_dx))
                markers_dx.add(marker_ex_down);

            if(!netTracks.get(parte_j.getUp().getRef()).isPoint()){
                MarkerBoard marker_ex_up = new MarkerBoard();
                marker_ex_up.setMounted("up");
                marker_ex_up.setId("mb_entry_"+track_ex.getId());
                marker_ex_up.setTrack(track_ex.getId());
                marker_ex_up.setDistance("51");
                if(!ModelComponent.checkInserimentoMarker(marker_ex_up, markers_dx))
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
            if(!ModelComponent.checkInserimentoMarker(parte_dx_marker, markers_dx))
                markers_dx.add(parte_dx_marker);

            parte_dx.setLeftMarker(new MarkerBoard());

            if(!parte_j.isPoint() && (parte_j.getRightMarker() == null || (parte_j.getRightMarker()!=null && (parte_j.getRightMarker().getMounted()== null || parte_j.getRightMarker().getMounted().length() == 0)))){
                MarkerBoard marker_b_dx = new MarkerBoard();
                marker_b_dx.setMounted("up");
                marker_b_dx.setId("mb_entry_"+track_ex.getId());
                marker_b_dx.setTrack(parte_dx.getId());
                marker_b_dx.setDistance("51");
                if(!ModelComponent.checkInserimentoMarker(marker_b_dx, markers_dx))
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
                    if(!ModelComponent.checkInserimentoMarker(l, markers_dx))
                        markers_dx.add(l);
                }
                MarkerBoard r = MarkerBoard.deepCopy(vecchio_vicino_up.getRightMarker());
                if(r!= null && r.getMounted() != null && r.getMounted().length() > 0){
                    if(!ModelComponent.checkInserimentoMarker(r, markers_dx))
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
                vecchio_vicino_up = TrackSection.deepCopy(vecchio_vicino_up);
            }    
            tracks.put(vecchio_vicino_up.getId(), vecchio_vicino_up);
            tracks_dx.add(track_ex);
            tracks_dx.add(parte_dx);
            return vecchio_vicino_up;

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
            if(!ModelComponent.checkInserimentoMarker(marker_b, markers_dx))
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
            if(!ModelComponent.checkInserimentoMarker(parte_dx_marker, markers_dx))
                markers_dx.add(parte_dx_marker);
            MarkerBoard parte_dx_marker_left = MarkerBoard.deepCopy(parte_dx.getLeftMarker());
            if(!ModelComponent.checkInserimentoMarker(parte_dx_marker_left, markers_dx))
                markers_dx.add(parte_dx_marker_left);
            tracks_dx.add(track_ex);
            tracks_dx.add(parte_dx);
            return vecchio_vicino_up;
        }else{
            TrackSection track_tagliata = TrackSection.deepCopy(vecchio_vicino_up);
            String relazione_vicino_up = track_tagliata.relazioneTrackPunto(parte_j);
            track_ex.setId(track_tagliata.getId() + "_ex_" + relazione_vicino_up + "_b");
            MarkerBoard marker_b = new MarkerBoard();
            marker_b.setMounted("up");
            marker_b.setId("mb_entry_"+ track_tagliata.getId() + "_ex_" + relazione_vicino_up);
            marker_b.setTrack(track_ex.getId());
            marker_b.setDistance("51");
            if(!ModelComponent.checkInserimentoMarker(marker_b, markers_dx))
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
            if(!ModelComponent.checkInserimentoMarker(marker_nuova_track_dx, markers_dx))
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
            vecchio_vicino_up = TrackSection.deepCopy(parte_dx);
            tracks.remove(track_tagliata.getId());
            tracks_dx.add(track_ex);
            tracks.put(parte_dx.getId(), parte_dx);
            return vecchio_vicino_up;
        }        
    }

    private static void sistemaLatoSinistroTotale(TrackSection parte, HashMap<String, TrackSection> netTracks, 
        ArrayList<String> tagliandi, HashMap<String, TrackSection> tracks, List<MarkerBoard> markers, List<TrackSection> tracksList, List<TrackSection> addedTracks) {
        TrackSection vecchio_vicino_down = parte.isPoint() ? new TrackSection() : TrackSection.deepCopy(netTracks.get(parte.getDown().getRef()));
        TrackSection track_b = new TrackSection();
        track_b.setType("linear");
        track_b.setLength("52");
        TrackSection parte_sx = TrackSection.deepCopy(parte);
        List<Neighbor> listNeighbor = new ArrayList<Neighbor>();
        if(parte.isPoint()){
            TrackSection track_tagliata = netTracks.get(tagliandi.get(0));
            String relazione = parte.relazioneTrackPunto(track_tagliata);
            TrackSection nuova_track = new TrackSection();
            nuova_track.setType("linear");
            nuova_track.setLength("52");
            nuova_track.setId(parte.getId() + "_ex_" + relazione);
            MarkerBoard marker_right = new MarkerBoard();
            marker_right.setMounted("up");
            marker_right.setId("mb_exit_"+nuova_track.getId());
            marker_right.setTrack(nuova_track.getId());
            marker_right.setDistance("51");
            if(!ModelComponent.checkInserimentoMarker(marker_right, markers))
                markers.add(marker_right);
            nuova_track.setRightMarker(marker_right);
        
            track_b.setId(parte.getId() + "_ex_" + relazione + "_b");
            Neighbor neigh_b = new Neighbor();
            neigh_b.setRef(nuova_track.getId());
            neigh_b.setSide("down");

            track_b.getNeighbors().add(neigh_b);
            track_b.setDown(neigh_b);

            MarkerBoard marker_left = new MarkerBoard();
            marker_left.setMounted("down");
            marker_left.setId("mb_entry_"+nuova_track.getId());
            marker_left.setTrack(track_b.getId());
            marker_left.setDistance("51");
            if(!ModelComponent.checkInserimentoMarker(marker_left, markers))
                markers.add(marker_left);

            track_b.setLeftMarker(marker_left);

            Neighbor dx_neigh_nuova_track = new Neighbor();
            dx_neigh_nuova_track.setRef(track_b.getId());
            dx_neigh_nuova_track.setSide("up");

            nuova_track.getNeighbors().add(dx_neigh_nuova_track);
            nuova_track.setUp(dx_neigh_nuova_track);

            Neighbor sx_neigh_nuova_track = new Neighbor();
            sx_neigh_nuova_track.setRef(parte.getId());
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
            tracksList.add(track_b);
            tracks.put(parte_sx.getId(), parte_sx);
            addedTracks.add(parte_sx);
        }else{
            ModelComponent.sistemaTaglioLatoSinistro(parte, parte_sx, vecchio_vicino_down, listNeighbor, tracks, markers, track_b);
            if(vecchio_vicino_down.isPoint()){
                tracks.remove(vecchio_vicino_down.getId());
                tracks.put(vecchio_vicino_down.getId(), vecchio_vicino_down);
                vecchio_vicino_down = TrackSection.deepCopy(vecchio_vicino_down);
            }    
            tracksList.add(track_b);
            tracks.put(parte_sx.getId(), parte_sx);
            addedTracks.add(parte_sx);        
        }
    }
}

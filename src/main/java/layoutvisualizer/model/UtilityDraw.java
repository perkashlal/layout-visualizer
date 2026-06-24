package layoutvisualizer.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import layoutvisualizer.model.network.Neighbor;
import layoutvisualizer.model.network.Network;
import layoutvisualizer.model.network.TrackSection;
import layoutvisualizer.model.network.MarkerBoard;

public class UtilityDraw {

    public void drawNetwork(Network net, TrackSection start, Double posX, Double posY, Double width, Double height,
            Double altezzaDiPartenza, AnchorPane anchorPane, Map<String, Linear> comps,
            Map<String, Point> points, DraggableMaker draggableMaker) {

        Image upIm = null;
        try (InputStream righInput = getClass().getResourceAsStream("/images/upsig.png")) {
            upIm = new Image(righInput, 70, 70, true, false);
            width = upIm.getWidth();
            height = upIm.getHeight();
            righInput.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        Image downIm = null;
        try (InputStream righInput = getClass().getResourceAsStream("/images/downsig.png")) {
            downIm = new Image(righInput, 70, 70, true, false);
            width = downIm.getWidth();
            height = downIm.getHeight();
            righInput.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        Image voidIm = null;
        try (InputStream righInput = getClass().getResourceAsStream("/images/nosig2.png")) {
            voidIm = new Image(righInput, 70, 70, true, false);
            width = voidIm.getWidth();
            height = voidIm.getHeight();
            righInput.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        Circle startPoint = new Circle();
        posX = 5.0;
        startPoint.setCenterX(posX);
        startPoint.setCenterY(posY + height / 2);
        startPoint.setRadius(2);
        anchorPane.getChildren().add(startPoint);

        drawTrackSection(net, start, posX, posY, width, height, altezzaDiPartenza, anchorPane, false, comps, points, draggableMaker, upIm, downIm, voidIm);
        Linear startC = comps.get(start.getId());
        startC.setCircle(startPoint);
        startC.bindCircle(true);
        drawRoute(start, posX, posY, width, height, altezzaDiPartenza, anchorPane, net, start.getUp(), comps, points, draggableMaker, upIm, downIm, voidIm);          
    }

    public void drawRoute(TrackSection ts, Double posX, Double posY, Double width, Double height, Double altezzaDiPartenza, AnchorPane pane, Network net, Neighbor neighbor, Map<String, Linear> components, Map<String, Point> pointComponents, DraggableMaker draggableMaker, Image upIm, Image downIm, Image voidIm){
        TrackSection next =  ts!=null ? net.getTrackSections().get(ts.getUp().getRef()) : null;
        Double layoutX = posX;
        Double layoutY = posY;
        if(ts.isPoint()){
            next = net.getTrackSections().get(neighbor.getRef());
            Point pc = pointComponents.get(ts.getId());
            layoutX = pc != null ? pc.coordEndX : posX;
            layoutY = pc != null ? pc.coordEndY : posY;
        }else{
            next =  net.getTrackSections().get(ts.getUp().getRef());
            Linear tsCo = components.get(ts.getId());
            layoutX = tsCo != null ? tsCo.coordEndX : posX;
            layoutY = tsCo != null ? tsCo.coordEndY : posY;
        }
        if(next != null && next.getId().length() > 0 && (notDrawn(next, components, pointComponents) || next.isPoint())){
            if(next.isPoint()){
                    drawPointTrackNew2(ts, next, layoutX, layoutY, width, height, altezzaDiPartenza, pane, net, components, pointComponents, draggableMaker, upIm, downIm, voidIm);
            }else{
                Linear nextComp = drawTrackSection(net, next, layoutX, layoutY, width, height, altezzaDiPartenza, pane, ts.isPoint(), components, pointComponents, draggableMaker, upIm, downIm, voidIm);
                drawRoute(next, nextComp.coordEndX, nextComp.coordEndY, width, height, altezzaDiPartenza, pane, net, next.getUp(), components, pointComponents, draggableMaker, upIm, downIm, voidIm);
                if(!ts.isPoint()){
                    Linear tsComp = components.get(ts.getId());
                    if(nextComp != null){
                        nextComp.setDown(tsComp);
                        tsComp.setUp(nextComp);
                    }
                }
            }
        }else{
            if(next == null || next.getId().length() == 0){
                Circle endPoint = new Circle();
                endPoint.setCenterX(posX);
                endPoint.setCenterY(posY + height / 2);
                endPoint.setRadius(2);
                pane.getChildren().add(endPoint);
                Linear ending = components.get(ts.getId());
                ending.setCircle(endPoint);
                ending.bindCircle(false);
            }
        }
    }

    public boolean notDrawn(TrackSection next, Map<String, Linear> components, Map<String, Point> pointComponents) {    
        if(components.containsKey(next.getId()) || pointComponents.containsKey(next.getId())){
            return false;
        }else{
            return true;
        }
    }

    private void drawRouteDown(TrackSection ts, Double posX, Double posY, Double width, Double height, Double altezzaDiPartenza, AnchorPane anch, Network net, Neighbor neighbor, Map<String, Linear> components, Map<String, Point> pointComponents, DraggableMaker draggableMaker, Image upIm, Image downIm, Image voidIm) {
        TrackSection next =  ts!=null ? net.getTrackSections().get(ts.getDown().getRef()) : null;
        Double layoutX = posX;
        Double layoutY = posY;
        if(ts.isPoint()){
            next = net.getTrackSections().get(neighbor.getRef());
            Point pc = pointComponents.get(ts.getId());
            layoutX = pc != null ? pc.coordX : posX;
            layoutY = pc != null ? pc.coordY : posY;
        }else{
            next =  net.getTrackSections().get(ts.getDown().getRef());
            Linear tsCo = components.get(ts.getId());
            layoutX = tsCo != null ? tsCo.coordX : posX;
            layoutY = tsCo != null ? tsCo.coordY : posY;
        }
        if(next != null && next.getId().length() > 0 && (notDrawn(next, components, pointComponents) || next.isPoint())){
            if(next.isPoint()){
                drawPointTrackDown2(ts, next, layoutX, layoutY, width, height, altezzaDiPartenza, anch, net, components, pointComponents, draggableMaker, upIm, downIm, voidIm);
            }else{
                Linear nextDown = drawTrackSectionDown2(net, next, layoutX, layoutY, width, height, altezzaDiPartenza, anch, ts.isPoint(), components, pointComponents, draggableMaker, upIm, downIm, voidIm);
                drawRouteDown(next, nextDown.coordX, nextDown.coordY, width, height, altezzaDiPartenza, anch, net, next.getDown(), components, pointComponents, draggableMaker, upIm, downIm, voidIm);
                if(!ts.isPoint()){
                    Linear tsComp = components.get(ts.getId());
                    if(nextDown != null){
                        tsComp.setDown(nextDown);
                        nextDown.setUp(tsComp);
                    }
                }
            }
        }else{
            if(next == null || next.getId().length() == 0){
                Circle endPoint = new Circle();
                endPoint.setCenterX(posX);
                endPoint.setCenterY(posY);
                endPoint.setRadius(2);
                anch.getChildren().add(endPoint);
                Linear ending = components.get(ts.getId());
                ending.setCircle(endPoint);
                ending.bindCircle(true);
            }
        }
    }

    public Linear drawTrackSection(Network net, TrackSection ts, Double posX, Double posY, Double imgWidth, Double imgHeight, Double altezzaPartenza, AnchorPane pane, boolean vengoDaPoint, Map<String, Linear> components, Map<String, Point> pointComponents, DraggableMaker draggableMaker, Image upIm, Image downIm, Image voidIm ){
        Linear trackPane = new Linear(0,0);
        if(ts!= null){
           trackPane.setId(ts.getId());
           components.put(ts.getId(), trackPane);
           trackPane.coordX = posX;
           trackPane.coordY = posY;
           MarkerBoard leftMarker = ts.getLeftMarker();
           if(leftMarker!=null && leftMarker.signal.length() > 0 && !leftMarker.signal.equals("nosig.png")){
                ImageView imageView2 = new ImageView();
                imageView2.setImage(downIm);
                if(!vengoDaPoint){
                    posX = posX - imgWidth;
                    trackPane.coordX = posX;
                }
                imageView2.setLayoutX(posX);
                imageView2.setLayoutY(posY);
                Label labelLeft = new Label(leftMarker.getId());
                labelLeft.relocate(posX + imgWidth/2, posY - imgHeight / 2);

                trackPane.setImvLeftAndLabel(imageView2, labelLeft);
                pane.getChildren().addAll(trackPane.getImvLeft(),labelLeft);
                posX += imgWidth;
            }else{
                TrackSection previous = net.getTrackSections().get(ts.getDown().getRef());
                if(previous != null){
                    ImageView imageView2 = new ImageView();
                    imageView2.setImage(voidIm);

                    if(!vengoDaPoint){
                        posX = posX - imgWidth;
                        trackPane.coordX = posX;
                    }
                    imageView2.setLayoutX(posX);
                    imageView2.setLayoutY(posY);
                    Label labelLeft = new Label("");
                    labelLeft.relocate(posX + imgWidth/2, posY - imgHeight / 2);
                    trackPane.setImvLeftAndLabel(imageView2, labelLeft);
                    pane.getChildren().addAll(trackPane.getImvLeft(),labelLeft);
                    posX += imgWidth;
                }
            }
            posY += imgHeight / 2;
            Line connector = new Line();
            connector.setStartX(posX);
            connector.setStartY(posY);
            Label label = new Label(ts.getId());
            label.relocate(posX, posY);

            Double length = Double.parseDouble("0" + (ts.getLength() != null ? ts.getLength() : ""));
            length = 100.0;
            posX += length.compareTo(0.0) > 0 ? length : 100.0;
            trackPane.setLengthLine(length);
            connector.setEndX(posX);
            connector.setEndY(posY);

            posY = posY - imgHeight /2;    

            trackPane.setLine(connector,label);
            pane.getChildren().addAll(connector, label);
            
            MarkerBoard rightMarker = ts.getRightMarker();
            TrackSection next = net.getTrackSections().get(ts.getUp().getRef());
            if(rightMarker!=null && rightMarker.signal.length() > 0 && !rightMarker.signal.equals("nosig.png")){
                ImageView imageView3 = new ImageView();
                imageView3.setImage(upIm);
                imageView3.setLayoutX(posX);
                imageView3.setLayoutY(posY);
                Label labelRight =  new Label(rightMarker.getId());
                labelRight.relocate(posX, posY + imgHeight);

                trackPane.setImvRightAndLabel(imageView3, labelRight);
                pane.getChildren().addAll(trackPane.getImvRight(), labelRight);
                posX += imgWidth;
            }else{
                if(next != null){
                    ImageView imageView3 = new ImageView();
                    imageView3.setImage(voidIm);

                    imageView3.setLayoutX(posX);
                    imageView3.setLayoutY(posY);
                    Label labelRight = new Label("");
                    labelRight.relocate(posX, posY + imgHeight);

                    trackPane.setImvRightAndLabel(imageView3, labelRight);
                    pane.getChildren().addAll(trackPane.getImvRight(), labelRight);
                    posX += imgWidth;
                }
            }
            trackPane.coordEndX = posX;
            trackPane.coordEndY = posY;
        }
        draggableMaker.makeDraggable2(trackPane);
        return trackPane;
    }

    private Linear drawTrackSectionDown2(Network net, TrackSection ts, Double posX, Double posY, Double imgWidth, Double imgHeight, Double altezzaDiPartenza, AnchorPane pane, boolean vengoDaPoint, Map<String, Linear> components, Map<String, Point> pointComponents, DraggableMaker draggableMaker, Image upIm, Image downIm, Image voidIm) {
        Linear trackPane = new Linear(0,0);
        if(ts!= null){
            trackPane.setId(ts.getId());
            components.put(ts.getId(), trackPane);
            trackPane.coordEndX = posX;
            posY = posY - imgHeight/2;
            trackPane.coordEndY = posY;
            MarkerBoard rightMarker = ts.getRightMarker();
            Image righImage = null;
            if(rightMarker!=null && rightMarker.signal.length() > 0 && !rightMarker.signal.equals("nosig.png")){
                try (InputStream righInput = getClass().getResourceAsStream("/images/" + rightMarker.signal)) {
                    righImage = new Image(righInput, 70, 70, true, false);
                    imgWidth = righImage.getWidth();
                    imgHeight = righImage.getHeight();
                    righInput.close();
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    ImageView imageView2 = new ImageView();
                    imageView2.setImage(righImage);
                    if(!vengoDaPoint){
                        posX = posX + imgWidth;
                        trackPane.coordEndX = posX;
                    }
                    imageView2.setLayoutX(posX - imgWidth);
                    imageView2.setLayoutY(posY);
                    Label labelRight = new Label(rightMarker.getId());
                    labelRight.relocate(posX - imgWidth, posY + imgHeight);
                    trackPane.setImvRightAndLabel(imageView2, labelRight);
                    pane.getChildren().addAll(trackPane.getImvRight(), labelRight);
                    posX -= imgWidth;
                }else{
                    try (InputStream righInput = getClass().getResourceAsStream("/images/nosig2.png")) {
                        righImage = new Image(righInput, 70, 70, true, false);
                        imgWidth = righImage.getWidth();
                        imgHeight = righImage.getHeight();
                        righInput.close();
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    ImageView imageView2 = new ImageView();
                    imageView2.setImage(righImage);
                    if(!vengoDaPoint){
                        posX = posX + imgWidth;
                        trackPane.coordEndX = posX;
                    }
                    imageView2.setLayoutX(posX - imgWidth);
                    imageView2.setLayoutY(posY);
                    Label labelRight = new Label("");
                    labelRight.relocate(posX - imgWidth, posY + imgHeight);
                    trackPane.setImvRightAndLabel(imageView2, labelRight);
                    pane.getChildren().addAll(trackPane.getImvRight(), labelRight);
                    posX -= imgWidth;
                }
            posY += imgHeight / 2;
            Line connector = new Line();
            connector.setEndX(posX);
            connector.setEndY(posY);
            Double length = Double.parseDouble("0" + (ts.getLength() != null ? ts.getLength() : ""));
            length = 100.0;
            trackPane.setLengthLine(length);
            posX -= length.compareTo(0.0) > 0 ? length : 100.0;
            Label label = new Label(ts.getId());
            label.relocate(posX, posY);
            connector.setStartX(posX);
            connector.setStartY(posY);
            posY = posY - imgHeight /2;    
            trackPane.setLine(connector,label);
            pane.getChildren().addAll(connector, label);
            MarkerBoard leftMarker = ts.getLeftMarker();
            TrackSection next = net.getTrackSections().get(ts.getDown().getRef());
            Image leftImage = null;
            if(leftMarker!=null && leftMarker.signal.length() > 0 && !leftMarker.signal.equals("nosig.png")){
                    try (InputStream leftInput = getClass().getResourceAsStream("/images/" + leftMarker.signal) ) {
                        leftImage = new Image(leftInput, 70, 70, true, false);
                        imgWidth = leftImage.getWidth();
                        imgHeight = leftImage.getHeight();
                        leftInput.close();
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                ImageView imageView2 = new ImageView();
                imageView2.setImage(leftImage);
                imageView2.setLayoutX(posX - imgWidth);
                imageView2.setLayoutY(posY);
                Label labelLeft = new Label(leftMarker.getId());
                labelLeft.relocate(posX - imgWidth/2, posY - imgHeight / 2);
                trackPane.setImvLeftAndLabel(imageView2, labelLeft);
                pane.getChildren().addAll(trackPane.getImvLeft(),labelLeft);
                posX -= imgWidth;
            }else{
                if(next!= null){
                    try (InputStream leftInput = getClass().getResourceAsStream("/images/nosig2.png") ) {
                        leftImage = new Image(leftInput, 70, 70, true, false);
                        imgWidth = leftImage.getWidth();
                        imgHeight = leftImage.getHeight();
                        leftInput.close();
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    ImageView imageView2 = new ImageView();
                    imageView2.setImage(leftImage);
                    imageView2.setLayoutX(posX - imgWidth);
                    imageView2.setLayoutY(posY);
                    Label labelLeft = new Label("");
                    labelLeft.relocate(posX - imgWidth/2, posY - imgHeight / 2);
                    trackPane.setImvLeftAndLabel(imageView2, labelLeft);
                    pane.getChildren().addAll(trackPane.getImvLeft(),labelLeft);
                    posX -= imgWidth;
                }
            }
            trackPane.coordX = posX;
            posY = posY + imgHeight/2;
            trackPane.coordY = posY;
        }
        draggableMaker.makeDraggable2(trackPane);
        return trackPane;
    }


    private void drawPointTrackDown2(TrackSection ts, TrackSection point, Double posX, Double posY, Double width, Double height, Double altezzaDiPartenza, AnchorPane anch, Network net, Map<String, Linear> components, Map<String, Point> pointComponents, DraggableMaker draggableMaker, Image upIm, Image downIm, Image voidIm) {
        if(ts.getId().equals(point.getMinus().getRef()) || ts.getId().equals(point.getPlus().getRef())){
            if(ts.getId().equals(point.getPlus().getRef())){
                point.punto_di_separazione = true;
                if(notDrawn(point, components, pointComponents)){
                    posX -= 50.0;
                    Point pointPane = new Point();
                    pointPane.coordEndX = posX;
                    pointPane.coordEndY = posY;
                    Line connector = new Line();
                    connector.setEndX(posX);
                    connector.setEndY(posY);
                    Double length = Double.parseDouble("0" + (point.getLength() != null ? point.getLength() : ""));
                    length = 100.0;
                    posX -= length.compareTo(0.0) > 0 ? length : 100.0;
                    Label label = new Label(point.getId());
                    label.relocate(posX, posY);
                    connector.setStartX(posX);
                    connector.setStartY(posY);
                    anch.getChildren().addAll(connector, label);
                    pointPane.coordX = posX;
                    pointPane.coordY = posY;
                    pointPane.setLinePoint(connector, label);
                    pointPane.setPunto_di_separazione(true);
                    pointPane.setLengthLine(length);
                    draggableMaker.makePointDraggable(pointPane);
                    pointComponents.put(point.getId(),pointPane);
                    //vado  indietro e disegno lo stem
                    drawRouteDown(point, posX, posY, width, height, altezzaDiPartenza, anch, net, point.getStem(), components, pointComponents, draggableMaker, upIm, downIm, voidIm);
                    TrackSection minus = net.getTrackSections().get(point.getMinus().getRef());
                    if(minus!=null){
                        if(notDrawn(minus, components, pointComponents)){
                            posX = pointPane.coordX + 50;
                            posY = pointPane.coordY + height/2 ;
                            drawRoute(point, posX, posY, width, height, altezzaDiPartenza, anch, net, point.getMinus(), components, pointComponents, draggableMaker, upIm, downIm, voidIm);
                        }
                    }
                }else{
                    Point pc = pointComponents.get(point.getId());
                    pc.setPunto_di_separazione(true);
                }
            }else{
                point.punto_di_separazione = true;
                if(notDrawn(point, components, pointComponents)){              
                    Point pc = new Point();
       
                    posY = posY + 2*height;
                    pc.coordEndY = posY;

                    pc.coordEndX = posX;
                    Line connector = new Line();
                    connector.setEndX(posX);
                    connector.setEndY(posY);
                    Double length = Double.parseDouble("0" + (point.getLength() != null ? point.getLength() : ""));
                    length = 100.0;                    
                    posX -= length.compareTo(0.0) > 0 ? length : 100.0;
                    Label label = new Label(point.getId());
                    label.relocate(posX, posY);
                    connector.setStartX(posX);
                    connector.setStartY(posY);
                    anch.getChildren().addAll(connector, label);
                    pc.coordX = posX;
                    pc.coordY = posY;
                    pc.setLinePoint(connector, label);
                    pc.setPunto_di_separazione(true);
                    pc.setLengthLine(length);
                    draggableMaker.makePointDraggable(pc);
                    pointComponents.put(point.getId(),pc);
                    drawRouteDown(point, posX, posY, width, height, altezzaDiPartenza, anch, net, point.getStem(), components, pointComponents, draggableMaker, upIm, downIm, voidIm);
                    pc.coordEndY -= height/2;
                    TrackSection plus = net.getTrackSections().get(point.getPlus().getRef());
                    if(plus!=null){
                        if(notDrawn(plus, components, pointComponents)){
                            // non ancora disegnato
                            if(!plus.isPoint())
                                pc.coordEndX += 50.0;
                            drawRoute(point, posX, posY, width, height, altezzaDiPartenza, anch, net, point.getPlus(), components, pointComponents, draggableMaker, upIm, downIm, voidIm);
                        }
                    }
                }
            }
        }else{
            if(notDrawn(point, components, pointComponents)){
                Point pc = new Point();
                pc.coordEndX = posX;
                pc.coordEndY = posY;
                Line connector = new Line();
                connector.setEndX(posX);
                connector.setEndY(posY);
                Double length = Double.parseDouble("0" + (point.getLength() != null ? point.getLength() : ""));
                length = 100.0;
                posX -= length.compareTo(0.0) > 0 ? length : 100.0;
                connector.setStartX(posX);
                connector.setStartY(posY);
                Label label = new Label(point.getId());
                label.relocate(posX, posY);
                pc.coordX = posX - 50.0;
                pc.coordY = posY;
                anch.getChildren().addAll(connector, label);
                pc.setLinePoint(connector, label);
                pc.setLengthLine(length);
                draggableMaker.makePointDraggable(pc);
                pointComponents.put(point.getId(), pc);
                drawRouteDown(point, posX, posY, width, height, altezzaDiPartenza, anch, net, point.getPlus(), components, pointComponents, draggableMaker, upIm, downIm, voidIm);
                posY = posY + height/2;
                posX = pc.coordEndX - 50.0 - width;
                Line minusLine = new Line();
                minusLine.setStartX(posX);
                minusLine.setStartY(posY);
                posX = posX - 50.0;
                posY =  posY + 2*height;
                minusLine.setEndX(posX);
                minusLine.setEndY(posY);
                posY = posY - height/2;
                pc.coordX = posX;
                pc.coordY = posY;
                drawRouteDown(point, posX, posY, width, height, altezzaDiPartenza, anch, net, point.getMinus(), components, pointComponents, draggableMaker, upIm, downIm, voidIm);
            }
        } 
    }
        
    public void drawPointTrackNew2(TrackSection ts, TrackSection point, Double posX, Double posY, Double width, Double height, Double altezzaDiPartenza, AnchorPane anch, Network net, Map<String, Linear> components, Map<String, Point> pointComponents, DraggableMaker draggableMaker, Image upIm, Image downIm, Image voidIm){

        if(ts.getId().equals(point.getMinus().getRef()) || ts.getId().equals(point.getPlus().getRef())){
            /* punto di giunzione di due tracce : posso disegnare il punto e poi disegno dritto */
            if(ts.getId().equals(point.getPlus().getRef())){
                /* Vengo da plus, disegno solo il punto */
                if(notDrawn(point, components, pointComponents)){
                    posX += 50.0;
                    Point pointPane = new Point();
                    pointPane.coordX = posX;
                    pointPane.coordY = posY;
                    posY += height / 2;
                    Line connector = new Line();
                    connector.setStartX(posX);
                    connector.setStartY(posY);
        
                    Label label = new Label(point.getId());
                    label.relocate(posX, posY);
        
                    Double length = Double.parseDouble("0" + (point.getLength() != null ? point.getLength() : ""));
                    length = 100.0;
                    posX += length.compareTo(0.0) > 0 ? length : 100.0;
                    connector.setEndX(posX);
                    connector.setEndY(posY);
                    posY = posY - height /2;    
                    anch.getChildren().addAll(connector, label);
                    pointPane.coordEndX = posX;
                    pointPane.coordEndY = posY;
                    pointPane.setLinePoint(connector, label);
                    pointPane.setLengthLine(length);
                    draggableMaker.makePointDraggable(pointPane);
                    pointComponents.put(point.getId(),pointPane);
                    drawRoute(point, posX, posY, width, height, altezzaDiPartenza, anch, net, point.getStem(), components, pointComponents, draggableMaker, upIm, downIm, voidIm);
                    //torno indietro e disegno il minus
                    TrackSection minus = net.getTrackSections().get(point.getMinus().getRef());
                    if(minus!=null){
                        if(notDrawn(minus, components, pointComponents)){
                            posX = pointPane.coordX - 50;
                            posY += 2*height + height/2 ;
                            pointPane.coordX = posX;
                            pointPane.coordY = posY;
                            drawRouteDown(point, posX, posY, width, height, altezzaDiPartenza, anch, net, point.getMinus(), components, pointComponents, draggableMaker, upIm, downIm, voidIm);
                        }
                    }
                }
            }else{
                posY += height / 2;
                if(notDrawn(point, components, pointComponents)){                          
                    if(point.getPlus()!=null){
                        TrackSection plus = net.getTrackSections().get(point.getPlus().getRef());
                        if(plus!=null){
                            if(plus.getRightMarker() == null || plus.getRightMarker().signal.length() == 0 || plus.getRightMarker().signal.equals("nosig.png")){

                            }else{
                                if(!ts.isPoint())
                                    posX += width;
                            }
                        }
                    }
                    Point pc = new Point();
                    posY = posY + 2*height;
                    pc.coordY = posY;
                    pc.coordX = posX;
                    Line connector = new Line();
                    connector.setStartX(posX);
                    connector.setStartY(posY);
                    Label label = new Label(point.getId());
                    label.relocate(posX, posY);
                    Double length = Double.parseDouble("0" + (point.getLength() != null ? point.getLength() : ""));
                    length = 100.0;
                    posX += length.compareTo(0.0) > 0 ? length : 100.0;
                    connector.setEndX(posX);
                    connector.setEndY(posY);
                    posY = posY - height /2;    
                    anch.getChildren().addAll(connector, label);
                    pc.coordEndX = posX;
                    pc.coordEndY = posY;
                    pc.setLinePoint(connector, label);
                    pc.setLengthLine(length);
                    draggableMaker.makePointDraggable(pc);
                    pointComponents.put(point.getId(),pc);

                    drawRoute(point, posX, posY, width, height, altezzaDiPartenza, anch, net, point.getStem(), components, pointComponents, draggableMaker, upIm, downIm, voidIm);
                    //torno indietro e disegno il plus
                    TrackSection plus = net.getTrackSections().get(point.getPlus().getRef());
                    if(plus!=null){
                        if(notDrawn(plus, components, pointComponents)){
                            // non ancora disegnato
                            pc.coordX -= 75.0;
                            drawRouteDown(point, posX, posY, width, height, altezzaDiPartenza, anch, net, point.getPlus(), components, pointComponents, draggableMaker, upIm, downIm, voidIm);
                            pc.coordX += 75.0;
                        }
                    }
                }
            }
        }else{
            /* punto di separazione di due tracce : disegno il punto e i due lati uscenti */
            if(notDrawn(point, components, pointComponents)){
                point.punto_di_separazione = true;
                Point pc = new Point();
                pc.coordX = posX;
                pc.coordY = posY;
                posY += height / 2;
                
                Line connector = new Line();
                connector.setStartX(posX);
                connector.setStartY(posY);

                Label label = new Label(point.getId());
                label.relocate(posX, posY);

                Double length = Double.parseDouble("0" + (point.getLength() != null ? point.getLength() : ""));
                length = 100.0;
                posX += length.compareTo(0.0) > 0 ? length : 100.0;
                connector.setEndX(posX);
                connector.setEndY(posY);
                posY = posY - height /2;  
                pc.coordEndX = posX;
                pc.coordEndY = posY;
                anch.getChildren().addAll(connector, label);
                pc.setLinePoint(connector, label);
                pc.setPunto_di_separazione(true);
                pc.setLengthLine(length);
                pointComponents.put(point.getId(), pc);    
                drawRoute(point, posX, posY, width, height, altezzaDiPartenza, anch, net, point.getPlus(), components, pointComponents, draggableMaker, upIm, downIm, voidIm);
                draggableMaker.makePointDraggable(pc);

                posY = posY + height/2;
                posX = pc.coordEndX - 50.0 -width;

                posX = posX + 125.0;
                posY =  posY + 2*height;

                posY = posY - height/2;

                pc.coordEndX = posX;
                pc.coordEndY = posY;
                drawRoute(point, posX, posY, width, height, altezzaDiPartenza, anch, net, point.getMinus(), components, pointComponents, draggableMaker, upIm, downIm, voidIm);
            }
        }
    }

    public void controllaRete(Network net, Map<String, Point> points, Map<String, Linear> comps, AnchorPane anchorPane, Double height, Double width){
        for(TrackSection trackAdjust : net.getTrackSections().values()){
            Point pointAdjust = points.get(trackAdjust.getId());
            Linear plus = comps.get(trackAdjust.getPlus().getRef()); 
            if(trackAdjust.isPoint() && trackAdjust.punto_di_separazione){
                setPointComponents(trackAdjust, comps, points, anchorPane);
                if(plus != null){
                    if(!(plus.line.getStartY() == pointAdjust.line.getStartY())){
                        double offSet = pointAdjust.line.getStartY();
                        aggiustaAltezza(net.getTrackSections(), plus, offSet, height/2, comps, points);
                    }
                    if((plus.imvLeft != null && plus.imvLeft.getLayoutX() <= pointAdjust.line.getStartX()) || (plus.line != null && plus.line.getStartX() <= pointAdjust.line.getStartX())){
                        double offSet = pointAdjust.line.getEndX() + 50;
                        aggiustaTracce(net.getTrackSections(), plus, offSet, width, comps, points);
                    }
                }else if(points.get(trackAdjust.getPlus().getRef()) != null){
                    Point plusPoint = points.get(trackAdjust.getPlus().getRef()); 
                    String relazione = net.getTrackSections().get(trackAdjust.getPlus().getRef()).relazioneTrackPunto(trackAdjust);
                    if(!(plusPoint.line.getStartY() == pointAdjust.line.getStartY()) && !relazione.equals("minus")){
                        double offSet = pointAdjust.line.getEndY();
                        aggiustaAltezzaPunti(net.getTrackSections(), plusPoint, offSet, height/2, comps, points);
                    } 
                    if(plusPoint.line != null && plusPoint.line.getStartX() <= pointAdjust.line.getStartX()){
                        double offSet = pointAdjust.line.getEndX() + 50;
                        aggiustaPunti(net.getTrackSections(), plusPoint, offSet, width, comps, points);
                    }
                }
                Linear min = comps.get(trackAdjust.getMinus().getRef()); 
                if(min != null){
                    if((min.imvLeft != null && min.imvLeft.getLayoutX() <= pointAdjust.line.getStartX()) || (min.line != null && min.line.getStartX() <= pointAdjust.line.getStartX())){
                        double offSet = pointAdjust.line.getEndX() + 50;
                        aggiustaTracce(net.getTrackSections(), min, offSet, width, comps, points);
                    }
                }else if(points.get(trackAdjust.getMinus().getRef()) != null){
                    Point minPoint = points.get(trackAdjust.getMinus().getRef()); 
                    if(minPoint.line != null && minPoint.line.getStartX() <= pointAdjust.line.getStartX()){
                        double offSet = pointAdjust.line.getEndX() + 50;
                        aggiustaPunti(net.getTrackSections(), minPoint, offSet, width, comps, points);
                    }
                }
            } else if(trackAdjust.isPoint() && !trackAdjust.punto_di_separazione){
                setPointComponents(trackAdjust, comps, points, anchorPane);
                if(plus != null){
                    if(!(plus.line.getStartY() == pointAdjust.line.getStartY())){
                        double offSet = plus.line.getEndY();
                        aggiustaAltezzaPunti(net.getTrackSections(), pointAdjust, offSet, height/2, comps, points);
                    }
                    if((plus.imvRight != null && (plus.imvRight.getLayoutX() + width) >= pointAdjust.line.getStartX()) || (plus.line != null && plus.line.getEndX() >= pointAdjust.line.getStartX())){
                        double offSet = plus.imvRight != null ? (plus.imvRight.getLayoutX() + width + 50.0) : (plus.line.getEndX() + 50.0);
                        aggiustaPunti(net.getTrackSections(), pointAdjust, offSet, width, comps, points);
                    }
                }else if(points.get(trackAdjust.getPlus().getRef()) != null){
                    Point plusPoint = points.get(trackAdjust.getPlus().getRef());
                    String relazione = net.getTrackSections().get(trackAdjust.getPlus().getRef()).relazioneTrackPunto(trackAdjust); 
                    if(!(plusPoint.line.getStartY() == pointAdjust.line.getStartY()) && !relazione.equals("minus")){
                        double offSet = plusPoint.line.getEndY();
                        aggiustaAltezzaPunti(net.getTrackSections(), pointAdjust, offSet, height/2, comps, points);
                    }
                    if(plusPoint.line != null && plusPoint.line.getEndX() >= pointAdjust.line.getStartX()){
                        double offSet = plusPoint.line.getEndX() + 50.0;
                        aggiustaPunti(net.getTrackSections(), pointAdjust, offSet, width, comps, points);
                    }
                }
                Linear min = comps.get(trackAdjust.getMinus().getRef()); 
                if(min != null){
                    if((min.imvRight != null && (min.imvRight.getLayoutX() + width) >= pointAdjust.line.getStartX()) || (min.line != null && min.line.getEndX() >= pointAdjust.line.getStartX())){
                        double offSet = min.imvRight != null ? (min.imvRight.getLayoutX() + width + 50.0) : (min.line.getEndX() + 50.0);
                        aggiustaPunti(net.getTrackSections(), pointAdjust, offSet, width, comps, points);
                    }
                }else if(points.get(trackAdjust.getMinus().getRef()) != null){
                    Point minPoint = points.get(trackAdjust.getMinus().getRef()); 
                    if(minPoint.line != null && minPoint.line.getEndX() >= pointAdjust.line.getStartX()){
                        double offSet = minPoint.line.getEndX() + 50.0;
                        aggiustaPunti(net.getTrackSections(), pointAdjust, offSet, width, comps, points);
                    }
                }
            } else if(!trackAdjust.isPoint()){
                setComponents(trackAdjust, comps, points);
            }
        }
    }

    public void setPointComponents(TrackSection point, Map<String, Linear> components, Map<String, Point> pointComponents, AnchorPane anch){
        //TODO: bind TrackSection to Point (inheriths GRaphicTrack) [EDITED BY ZECCHI]
        Point pc = pointComponents.get(point.getId());
        if(point.punto_di_separazione){
            pc.setPunto_di_separazione(true);
        }else{
            pc.setPunto_di_separazione(false);
        }
        GraphicTrack min = components.get(point.getMinus().getRef());
        if(min == null){
            min = pointComponents.get(point.getMinus().getRef());
        }
        pc.setMinus(min);
        
        GraphicTrack plus = components.get(point.getPlus().getRef());
        if(plus == null){
            plus = pointComponents.get(point.getPlus().getRef());
        }
        pc.setPlus(plus);
        
        GraphicTrack stem = components.get(point.getStem().getRef());
        if(stem == null){
            stem = pointComponents.get(point.getStem().getRef());
        }
        pc.setStem(stem);

        Boolean minusAbovePlus = point.shouldPlaceMinusAbovePlus();
        if(minusAbovePlus != null){
            pc.setMinusBranchAbove(minusAbovePlus);
        }
        
        pc.rightClickOnLabel();
        pc.setLines(anch);

        point.associatedPoint = pc;
    }

    private void setComponents(TrackSection comp, Map<String, Linear> components, Map<String, Point> pointComponents) {
        Linear c = components.get(comp.getId());
        if(c!=null){
            GraphicTrack down = components.get(comp.getDown().getRef());
            if(down == null){
                down = pointComponents.get(comp.getDown().getRef());
            }
            c.setDown(down); 
            GraphicTrack up = components.get(comp.getUp().getRef());
            if(up == null){
                up = pointComponents.get(comp.getUp().getRef());
            }
            c.setUp(up);
        }
    }
    private void aggiustaAltezzaPunti(HashMap<String, TrackSection> trackSections, Point pointAdjust, double off, Double height, Map<String, Linear> components, Map<String, Point> pointComponents) {
        pointAdjust.line.setStartY(off);
        pointAdjust.line.setEndY(off);
        pointAdjust.labelLine.setLayoutY(off);
        TrackSection questa = trackSections.get(pointAdjust.id);
        if( questa!= null ){
            if(questa.punto_di_separazione){
                TrackSection plus = trackSections.get(questa.getPlus().getRef());
                if(!plus.isPoint()){     
                    Linear cp = components.get(plus.getId());
                    if(!(pointAdjust.line.getEndY() == (cp.line.getEndX())))
                        aggiustaAltezza(trackSections, cp, off, height, components, pointComponents);
                }else{
                    Point pointProssima = pointComponents.get(plus.getId());
                    if(pointProssima != null && !(pointAdjust.line.getEndY() == pointProssima.line.getStartY()))
                        aggiustaAltezzaPunti(trackSections, pointProssima, off,height, components, pointComponents);
                }
            }else{
                TrackSection stem = trackSections.get(questa.getStem().getRef());
                if(!stem.isPoint()){     
                    Linear cpm = components.get(stem.getId());
                    if(cpm !=null && !(pointAdjust.line.getEndY() == (cpm.line.getStartY())))
                        aggiustaAltezza(trackSections, cpm, off, height, components, pointComponents);
                }else{
                    Point pointProssima = pointComponents.get(stem.getId());
                    if(pointProssima != null && !(pointAdjust.line.getEndY() == pointProssima.line.getStartY()))
                        aggiustaAltezzaPunti(trackSections, pointProssima, off, height, components, pointComponents);
                }
            }
        }
    }

    private void aggiustaAltezza(HashMap<String, TrackSection> trackSections, Linear primo, double offSet, Double height, Map<String, Linear> components, Map<String, Point> pointComponents) {
        if(primo.imvLeft != null){
            primo.imvLeft.setLayoutY(offSet - height);
            primo.labelLeft.setLayoutY(offSet - 2*height);
        }
        primo.line.setStartY(offSet);
        primo.line.setEndY(offSet);
        primo.labelLine.setLayoutY(offSet);
        if(primo.imvRight != null){
            primo.imvRight.setLayoutY(offSet - height);
            primo.labelRight.setLayoutY(offSet + height);
        }
        TrackSection questa = trackSections.get(primo.labelLine.getText());
        if( questa!= null ){
            Linear prossima = components.get(questa.getUp().getRef());
            if(prossima != null && !(prossima.line.getStartY() == primo.line.getStartY())){     
                aggiustaAltezza(trackSections, prossima, offSet, height, components, pointComponents);
            }else{
                Point compProssima = pointComponents.get(questa.getUp().getRef());
                if(compProssima != null && !(primo.line.getStartY() == compProssima.line.getStartY())){
                    aggiustaAltezzaPunti(trackSections, compProssima, offSet, height, components, pointComponents);
                }
            }
        }
    }

    private void aggiustaTracce(HashMap<String, TrackSection> trackSections, Linear min, Double off, Double width, Map<String, Linear> components, Map<String, Point> pointComponents) {
        if(min.imvLeft != null){
            min.imvLeft.setLayoutX(off);
            min.labelLeft.setLayoutX(off  + width/2);
            min.line.setStartX(off + width);
            min.line.setEndX(off + width + min.lengthLine);
        }else{
            min.line.setStartX(off);
            min.line.setEndX(off + min.lengthLine);
        }
        if(min.imvRight != null){
            min.imvRight.setLayoutX(min.line.getEndX());
            min.labelRight.setLayoutX(min.imvRight.getLayoutX());
        }

        min.labelLine.setLayoutX(min.line.getStartX());
        TrackSection questa = trackSections.get(min.labelLine.getText());
        off = min.imvRight != null ? min.imvRight.getLayoutX() : min.line.getEndX();
        if( questa!= null ){
            Linear prossima = components.get(questa.getUp().getRef());
            if(prossima != null && (prossima.imvLeft.getLayoutX() <= (min.imvRight.getLayoutX()+width))){     
                aggiustaTracce(trackSections, prossima, off, width, components, pointComponents);
            }else{
                Point compProssima = pointComponents.get(questa.getUp().getRef());
                if(compProssima != null){
                    if(min.imvRight != null)
                        off += width;
                    if(compProssima.line.getStartX() <= (min.imvRight.getLayoutX()+width))
                        aggiustaPunti(trackSections, compProssima, off, width, components, pointComponents);
                }
            }
        }
    }

    private void aggiustaPunti(HashMap<String, TrackSection> trackSections, Point minP, Double off, Double width, Map<String, Linear> components, Map<String, Point> pointComponents) {
        minP.line.setStartX(off);
        minP.line.setEndX(minP.lengthLine + off);
        minP.labelLine.setLayoutX(minP.line.getStartX());
        TrackSection questa = trackSections.get(minP.id);
        off = minP.line.getEndX();
        if( questa!= null ){
            if(questa.punto_di_separazione){
                TrackSection plus = trackSections.get(questa.getPlus().getRef());
                if(!plus.isPoint()){     
                    Linear cp = components.get(plus.getId());
                    if(minP.line.getEndX() >= (cp.imvLeft.getLayoutX()))
                        aggiustaTracce(trackSections, cp, off, width, components, pointComponents);
                }else{
                    Point pointProssima = pointComponents.get(plus.getId());
                    if(pointProssima != null && (minP.line.getEndX() >= pointProssima.line.getStartX()))
                        aggiustaPunti(trackSections, pointProssima, off+ 50.0 ,width, components, pointComponents);
                }
                TrackSection minus = trackSections.get(questa.getMinus().getRef());
                off = minP.line.getEndX();
                if(!minus.isPoint()){     
                    Linear cpm = components.get(minus.getId());
                    if((cpm.imvLeft != null && cpm.imvLeft.getLayoutX() <= minP.line.getStartX()) || (cpm.line != null && cpm.line.getStartX() <= minP.line.getStartX()))
                        aggiustaTracce(trackSections, cpm, off, width, components, pointComponents);
                }else{
                    Point pointProssima = pointComponents.get(minus.getId());
                    if(pointProssima.line != null && pointProssima.line.getStartX() <= minP.line.getStartX())
                        aggiustaPunti(trackSections, pointProssima, off + 50.0, width, components, pointComponents);
                }
            }else{
                TrackSection stem = trackSections.get(questa.getStem().getRef());
                if(!stem.isPoint()){     
                    Linear cpm = components.get(stem.getId());
                    if(minP.line.getEndX() >= (cpm.imvLeft.getLayoutX()))
                        aggiustaTracce(trackSections, cpm, off, width, components, pointComponents);
                }else{
                    Point pointProssima = pointComponents.get(stem.getId());
                    if(pointProssima != null && (minP.line.getEndX() >= pointProssima.line.getStartX()))
                        aggiustaPunti(trackSections, pointProssima, off + 50.0,width, components, pointComponents);
                }
            }
        }
    }

    public void togliSovrapposizioni(Map<String, Linear> comps, Map<String, Point> points, HashMap<String, TrackSection> trackSections) {                
        HashMap<Double, ArrayList<String>> quote = new HashMap<>();
        for(Linear c : comps.values()){
            if(c.line!=null ){
                Double levelY = c.line.getStartY();
                if(!quote.containsKey(levelY)){
                    ArrayList<String> altezze = new ArrayList<>();
                    altezze.add(c.getId());
                    quote.put(levelY, altezze);
                }else{
                    quote.get(levelY).add(c.getId());
                }
            }
        }

        for(Point p : points.values()){
            if(p.line!=null ){
                Double levelY = p.line.getStartY();
                if(!quote.containsKey(levelY)){
                    ArrayList<String> altezze = new ArrayList<>();
                    altezze.add(p.id);
                    quote.put(levelY, altezze);
                }else{
                    quote.get(levelY).add(p.id);
                }
            }
        } 

        ArrayList<Double> keys = new ArrayList<>(quote.keySet());
        java.util.Collections.sort(keys);

        for(int k = 0; k < keys.size(); k++){
            ArrayList<String> listaComp = quote.get(keys.get(k));
            if(listaComp == null){
                continue;
            }
            for(int myObj = 0; myObj >= 0 && myObj < listaComp.size(); myObj++){
                String myId = listaComp.get(myObj);
                Linear c = comps.get(myId);
                Double startX = 0.0;
                Double endX = 0.0;
                if(c != null){
                    startX = c.getStartPositionX();
                    endX = c.getEndPositionX();
                }else{
                    Point p = points.get(myId);
                    startX = p.getStartPositionX();
                    endX = p.getEndPositionX();
                }
                for(int otherObj = 0; otherObj < listaComp.size(); otherObj++){
                    String id = listaComp.get(otherObj);
                    if(!myId.equals(id)){
                        Linear comparato = comps.get(id);
                        Double dueX = 0.0;
                        Double dueEndX = 0.0;
                        if(comparato != null){
                            dueX = comparato.getStartPositionX();
                            dueEndX = comparato.getEndPositionX();
                        }else{
                            Point pointComparato = points.get(id);
                            dueX = pointComparato.line.getStartX();
                            dueEndX = pointComparato.line.getEndX();
                        }
                        if(startX > dueEndX && endX > dueEndX)
                            continue;
                        if(startX < dueX && endX < dueX)
                            continue;
                        if(startX < dueEndX && endX > dueEndX){
                            ArrayList<String> daRimuovere = aggiustaY(keys, k, myId, comps, points, listaComp, quote);
                            for(int v=0; v < daRimuovere.size(); v++){
                                listaComp.remove(daRimuovere.get(v));
                            }
                            break;
                        }
                        if(startX < dueX && endX > dueX){
                            ArrayList<String> daRimuovere = aggiustaY(keys, k, myId, comps, points, listaComp, quote);
                            for(int v=0; v < daRimuovere.size(); v++){
                                listaComp.remove(daRimuovere.get(v));
                            }
                            break;
                        } 
                    }
                }
            }
        }
    }

    private ArrayList<String> aggiustaY(List<Double> keys, int k, String myId,  Map<String, Linear> comps, Map<String, Point> points, ArrayList<String> listaComp, HashMap<Double, ArrayList<String>> quote) {
        ArrayList<String> daRimuovere = new ArrayList<>();
        Double primo = keys.get(k);
        Double secondo = 0.0;
        if((k + 1) < keys.size()){
            secondo = keys.get(k + 1);
            if(secondo < primo)
                secondo = 0.0;
        }
        Double newHeight = primo + (secondo - primo)/2 ;
        while(keys.contains(newHeight)){
            newHeight += 50.0; 
        }
        keys.add(newHeight);
        Linear c = comps.get(myId);
        HashMap<String, String> mapDaRimuovere = new HashMap<>();
        if(c!=null){
            mapDaRimuovere = c.cambiaAltezza(newHeight, mapDaRimuovere, listaComp);
        }else{
            Point p = points.get(myId);
            mapDaRimuovere = p.cambiaAltezza(newHeight, mapDaRimuovere, listaComp);
        }
        daRimuovere = new ArrayList<>(mapDaRimuovere.keySet());
        return daRimuovere;
    }

}

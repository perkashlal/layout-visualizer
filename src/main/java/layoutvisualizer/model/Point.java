package layoutvisualizer.model;

import java.util.*;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

public class Point extends GraphicTrack{
    public GraphicTrack minus;
    public GraphicTrack plus;
    public GraphicTrack stem;
    private Line toStem = new Line();
    private Line toPlus = new Line();
    private Line toMinus = new Line();

    public Point(){}

    public Point(Line l, Label b){
        id = b.getText();
        line = l;
        labelLine = b;
    }

    public void setLinePoint(Line l, Label b){
        id = b.getText();
        line = l;
        labelLine = b;
    }

    public GraphicTrack getMinus() {
        return minus;
    }
    public void setMinus(GraphicTrack minus) {
        this.minus = minus;
    }
    public GraphicTrack getPlus() {
         return plus;
    }
    public void setPlus(GraphicTrack plus) {
        this.plus = plus;
    }
    public GraphicTrack getStem() {
        return stem;
    }
    public void setStem(GraphicTrack stem) {
        this.stem = stem;
    }

    @Override
    public double getStartPositionX() {
        if(plus != null && plus.getEndPositionXForPoint() < line.getStartX() && plus.line.getStartY() == this.line.getStartY() ){
            return plus.getEndPositionXForPoint();
        }
        if(stem != null && stem.getEndPositionXForPoint() < line.getStartX() && stem.line.getStartY() == this.line.getStartY() ){
            return stem.getEndPositionXForPoint();
        }
        if(minus != null && minus.getEndPositionXForPoint() < line.getStartX() && minus.line.getStartY() == this.line.getStartY() ){
            return minus.getEndPositionXForPoint();
        }
        return line.getStartX();
    }

    @Override
    public double getEndPositionX() {
        if(plus != null && plus.getStartPositionX() > line.getEndX() && plus.line.getStartY() == this.line.getStartY() ){
            return plus.getStartPositionX();
        }
        if(stem != null && stem.getStartPositionX() > line.getEndX() && stem.line.getStartY() == this.line.getStartY() ){
            return stem.getStartPositionX();
        }
        if(minus != null && minus.getStartPositionX() > line.getEndX() && minus.line.getStartY() == this.line.getStartY() ){
            return minus.getStartPositionX();
        }
        return line.getEndX();
    }

    @Override
    public void clean() {
        if(line != null){
            line.startXProperty().unbind();
            line.endXProperty().unbind();
        }
        toStem.startXProperty().unbind();
        toStem.startYProperty().unbind();
        toStem.endXProperty().unbind();
        toStem.endXProperty().unbind();
        toMinus.startXProperty().unbind();
        toMinus.startYProperty().unbind();
        toMinus.endXProperty().unbind();
        toMinus.endXProperty().unbind();
        toPlus.startXProperty().unbind();
        toPlus.startYProperty().unbind();
        toPlus.endXProperty().unbind();
        toPlus.endXProperty().unbind();
        this.line = null;
        this.labelLine = null;
        minus = null;
        plus = null;
        stem = null;
    }

    public void setLines(AnchorPane pane) {
        if(punto_di_separazione){
            toStem.endXProperty().bind(line.startXProperty());
            toStem.endYProperty().bind(line.startYProperty());
            if(stem!=null){
                toStem.startXProperty().bind(stem.getEndXProperty());
                toStem.startYProperty().bind(stem.getEndYProperty());
                pane.getChildren().add(toStem);                
            }
            toPlus.startXProperty().bind(line.endXProperty());
            toPlus.startYProperty().bind(line.endYProperty());
            if(plus!=null){
                toPlus.endXProperty().bind(plus.getStartXProperty());
                toPlus.endYProperty().bind(plus.getStartYProperty());
                if(!plus.punto_di_separazione)
                    pane.getChildren().add(toPlus);
            }
            toMinus.startXProperty().bind(line.startXProperty());
            toMinus.startYProperty().bind(line.startYProperty());
            if(minus!=null){
                toMinus.endXProperty().bind(minus.getStartXProperty());
                toMinus.endYProperty().bind(minus.getStartYProperty());
                if(!minus.punto_di_separazione)
                    pane.getChildren().add(toMinus);
            }   
        }else{
            toStem.startXProperty().bind(line.endXProperty());
            toStem.startYProperty().bind(line.endYProperty());
            if(stem!=null){
                toStem.endXProperty().bind(stem.getStartXProperty());
                toStem.endYProperty().bind(stem.getStartYProperty());                
                if(stem instanceof Linear){
                    pane.getChildren().add(toStem);
                }
            }
            toMinus.endXProperty().bind(line.startXProperty());
            toMinus.endYProperty().bind(line.startYProperty());
            if(minus!=null){
                toMinus.startXProperty().bind(minus.getEndXProperty());
                toMinus.startYProperty().bind(minus.getEndYProperty());     
                if(!minus.punto_di_separazione)
                    pane.getChildren().add(toMinus);           
            }
            toPlus.endXProperty().bind(line.startXProperty());
            toPlus.endYProperty().bind(line.startYProperty());
            if(plus!=null){
                toPlus.startXProperty().bind(plus.getEndXProperty());
                toPlus.startYProperty().bind(plus.getEndYProperty());      
                if(!plus.punto_di_separazione)
                    pane.getChildren().add(toPlus);          
            }
        }
    }

    public void rightClickOnLabel(){
        if(this.labelLine != null){
            this.labelLine.setOnMouseClicked(mouseEvent ->{
                if(mouseEvent.getButton() == MouseButton.SECONDARY){
                    rotateBranch();
                }
            });
        }
    }

    public void rotateBranch(){
        if(this.minus != null && this.minus.line != null && this.line != null){
            Double livelloY = this.minus.line.getStartY();
            Double difference = Math.abs(this.line.getStartY() - livelloY);
            if(difference == 0.0){
                difference = 70.0;
            }
            Double newHeight = livelloY - 2*difference;
            if(livelloY < this.line.getStartY()){
                newHeight = livelloY + 2*difference;
            }
            this.minus.setNewHeight(newHeight,this.punto_di_separazione);
        }
    }

    public void setMinusBranchAbove(boolean minusAbovePlus){
        Boolean current = isMinusBranchAbovePlus();
        if(current != null && current.booleanValue() != minusAbovePlus){
            rotateBranch();
        }
    }

    private Boolean isMinusBranchAbovePlus(){
        if(this.minus == null || this.plus == null || this.minus.line == null || this.plus.line == null){
            return null;
        }
        return this.minus.line.getStartY() < this.plus.line.getStartY();
    }

    @Override
    public void rotate(double degrees){
        if(line == null){
            return;
        }
        double pivotX = (line.getStartX() + line.getEndX()) / 2.0;
        double pivotY = (line.getStartY() + line.getEndY()) / 2.0;
        rotateLineAround(line, pivotX, pivotY, degrees);
        rotateNodeAround(labelLine, pivotX, pivotY, degrees);
    }

    @Override
    public void setNewHeight(Double newHeight, boolean fromLeft) {
        this.line.setStartY(newHeight);
        this.line.setEndY(newHeight);
        this.labelLine.setLayoutY(newHeight);
        if(this.stem != null && stem instanceof Linear){
            this.stem.setNewHeight(newHeight, !this.punto_di_separazione);
        }
        if(this.plus != null && plus instanceof Linear){
            this.plus.setNewHeight(newHeight, this.punto_di_separazione);
        }
    }

    @Override
    public HashMap<String, String> cambiaAltezza(Double newHeight, HashMap<String, String> mapDaRimuovere, ArrayList<String> listaComp) {
        this.line.setStartY(newHeight);
        this.line.setEndY(newHeight);
        this.labelLine.setLayoutY(newHeight);
        mapDaRimuovere.put(this.id,this.id);
        //System.out.println(id);
        if(plus != null){
            if(!mapDaRimuovere.containsKey(plus.getId()) && listaComp.contains(plus.getId())){
                plus.cambiaAltezza(newHeight, mapDaRimuovere, listaComp);
            }
        }
        if(stem != null){
            if(!mapDaRimuovere.containsKey(stem.getId())  && listaComp.contains(stem.getId())){
                stem.cambiaAltezza(newHeight, mapDaRimuovere, listaComp);
            }
        }
        return mapDaRimuovere;
    }
}

package layoutvisualizer.model;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;

public class GraphicTrack {
    public Line line;
    public Label labelLine;
    public String id = "";
    public boolean punto_di_separazione;
    public Double lengthLine = 0.0;
    public Double coordX;
    public Double coordY;
    public Double coordEndX;
    public Double coordEndY;

    public double getLengthLine() {
        return lengthLine;
    }

    public void setLengthLine(double lengthLine) {
        this.lengthLine = lengthLine;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line, Label lab) {
        this.line = line;
        this.labelLine = lab;
    }
    
    public boolean isPunto_di_separazione() {
        return punto_di_separazione;
    }

    public void setPunto_di_separazione(boolean punto_di_separazione) {
        this.punto_di_separazione = punto_di_separazione;
    }

    public ObservableValue<? extends Number> getEndXProperty(){
        return line.endXProperty();
    }

    public ObservableValue<? extends Number> getEndYProperty() {
        return line.endYProperty();
    }

    public ObservableValue<? extends Number> getStartXProperty() {
        return line.startXProperty();
    }

    public ObservableValue<? extends Number> getStartYProperty() {
        return line.startYProperty();
    }

    public void shiftOrizzontale(double minValueX){
        line.setStartX(line.getStartX() + Math.abs(minValueX));
        line.setEndX(line.getEndX() + Math.abs(minValueX));
        labelLine.setLayoutX(labelLine.getLayoutX() + Math.abs(minValueX));
    }

    public void setNewHeight(Double newHeight, boolean b){
        this.line.setStartY(newHeight);
        this.line.setEndY(newHeight);
        this.labelLine.setLayoutY(newHeight);
    }

    public void clean(){
        if(line != null){
            line.startXProperty().unbind();
            line.endXProperty().unbind();
        }
        this.line = null;
        this.labelLine = null;
        id = "";
    }

    public HashMap<String, String> cambiaAltezza(Double newHeight, HashMap<String, String> mapDaRimuovere, ArrayList<String> listaComp) {
        this.line.setStartY(newHeight);
        this.line.setEndY(newHeight);
        this.labelLine.setLayoutY(newHeight);
        mapDaRimuovere.put(this.id,this.id);
        return mapDaRimuovere;
    }

    public double getStartPositionX() {
        return line.getStartX();
    }

    public double getEndPositionXForPoint() {
        return line.getEndX();
    }

    public double getEndPositionX() {
        return line.getEndX();
    }

    public void moveDown() {
    
    }

    public void moveUp() {
    
    }

    public void moveNeighborUp(ImageView imv, Line l){
    }

    public void moveNeighborDown(ImageView imv, Line l){
    }

    public void moveDownWithValues(double newPosX, double newPosY, double heightSX, double lunghezza, Node node, double widthSX, boolean left, double widthDX, double heightDX){
        
    }

    public void moveUpWithValues(double newPosX, double newPosY, double heightSX, double lunghezza, Node node, double widthSX, boolean left, double widthDX, double heightDX) {
    }
}

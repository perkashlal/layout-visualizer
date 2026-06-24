package layoutvisualizer.model;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
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

    public void rotate(double degrees){
        if(line == null){
            return;
        }
        double pivotX = (line.getStartX() + line.getEndX()) / 2.0;
        double pivotY = (line.getStartY() + line.getEndY()) / 2.0;
        rotateLineAround(line, pivotX, pivotY, degrees);
        rotateNodeAround(labelLine, pivotX, pivotY, degrees);
    }

    protected void rotateLineAround(Line lineToRotate, double pivotX, double pivotY, double degrees){
        if(lineToRotate == null){
            return;
        }
        double[] start = rotatePoint(lineToRotate.getStartX(), lineToRotate.getStartY(), pivotX, pivotY, degrees);
        double[] end = rotatePoint(lineToRotate.getEndX(), lineToRotate.getEndY(), pivotX, pivotY, degrees);
        lineToRotate.setStartX(start[0]);
        lineToRotate.setStartY(start[1]);
        lineToRotate.setEndX(end[0]);
        lineToRotate.setEndY(end[1]);
    }

    protected void rotateNodeAround(Node node, double pivotX, double pivotY, double degrees){
        if(node == null){
            return;
        }
        Bounds bounds = node.getBoundsInParent();
        double centerX = (bounds.getMinX() + bounds.getMaxX()) / 2.0;
        double centerY = (bounds.getMinY() + bounds.getMaxY()) / 2.0;
        double[] rotatedCenter = rotatePoint(centerX, centerY, pivotX, pivotY, degrees);
        node.setLayoutX(node.getLayoutX() + rotatedCenter[0] - centerX);
        node.setLayoutY(node.getLayoutY() + rotatedCenter[1] - centerY);
        node.setRotate(node.getRotate() + degrees);
    }

    protected double[] rotatePoint(double x, double y, double pivotX, double pivotY, double degrees){
        double radians = Math.toRadians(degrees);
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);
        double translatedX = x - pivotX;
        double translatedY = y - pivotY;
        double rotatedX = translatedX * cos - translatedY * sin;
        double rotatedY = translatedX * sin + translatedY * cos;
        return new double[]{rotatedX + pivotX, rotatedY + pivotY};
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

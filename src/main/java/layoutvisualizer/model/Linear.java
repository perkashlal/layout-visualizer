package layoutvisualizer.model;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Linear extends GraphicTrack{
    public Label labelLeft;
    public Label labelRight;
    public ImageView imvLeft;
    public ImageView imvRight;
    public double startPositionX;
    public double startPositionY;
    public double endPositionY;
    public double endPositionX;

    public GraphicTrack up;
    public GraphicTrack down;

    private Circle circle;

    public Linear(int startPositionX, int startPositionY) {
        this.startPositionX = startPositionX;
        this.startPositionY = startPositionY;
        punto_di_separazione = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ImageView getImvLeft() {
        return imvLeft;
    }

    public void setImvLeft(ImageView imvLeft) {
        this.imvLeft = imvLeft;
    }

    public void setImvLeftAndLabel(ImageView imvLeft, Label l) {
        this.imvLeft = imvLeft;
        this.labelLeft = l;
    }

    public void setImvRightAndLabel(ImageView imvRight, Label l) {
        this.imvRight = imvRight;
        this.labelRight = l;
    }

    public ImageView getImvRight() {
        return imvRight;
    }

    public void setImvRight(ImageView imvRight) {
        this.imvRight = imvRight;
    }

    public void setStartPositionX(Double startPositionX) {
        this.startPositionX = startPositionX;
    }

    public void setStartPositionY(Double startPositionY) {
        this.startPositionY = startPositionY;
    }

    public GraphicTrack getUp() {
        return up;
    }

    public void setUp(GraphicTrack up) {
        this.up = up;
    }

    public GraphicTrack getDown() {
        return down;
    }

    public void setDown(GraphicTrack down) {
        this.down = down;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }

    public void setNewHeightBase(Double newHeight) {
        Double heigthHalf = imvLeft!=null ? imvLeft.getImage().getHeight()/2 : imvRight != null ? imvRight.getImage().getHeight()/2 : 0.0;
        if(imvLeft!=null){
            imvLeft.setLayoutY(newHeight - heigthHalf);
            labelLeft.setLayoutY(newHeight - 2*heigthHalf);
        }
        this.line.setStartY(newHeight);
        this.line.setEndY(newHeight);
        this.labelLine.setLayoutY(newHeight);
        if(imvRight != null){
            imvRight.setLayoutY(newHeight - heigthHalf);
            labelRight.setLayoutY(newHeight + heigthHalf);
        }
    }

    @Override
    public HashMap<String, String>  cambiaAltezza(Double newHeight, HashMap<String, String> mapDaRimuovere, ArrayList<String> listaComp) {
        mapDaRimuovere.put(this.getId(),this.getId());
        this.setNewHeightBase(newHeight);
        if(this.up!=null){
            if(!mapDaRimuovere.containsKey(up.getId()) && listaComp.contains(up.getId())){
                up.cambiaAltezza(newHeight, mapDaRimuovere, listaComp);
            }
        }
        if(this.down!=null){
            if(!mapDaRimuovere.containsKey(down.getId()) && listaComp.contains(down.getId())){
                down.cambiaAltezza(newHeight, mapDaRimuovere, listaComp);
            }
        }
        return mapDaRimuovere;
    }

    @Override
    public double getStartPositionX() {
        if(imvLeft != null){
            this.setStartPositionX(imvLeft.getLayoutX());
        }else{
            this.setStartPositionX(line.getStartX() - 2);
        }
        return startPositionX;
    }

    public double getStartPositionY() {
        return startPositionY;
    }

    @Override
    public double getEndPositionX() {
        if(imvRight != null){
            this.endPositionX = imvRight.getLayoutX();
        }else{
            this.endPositionX = line.getEndX() + 2;
        }
        return endPositionX;
    }

    @Override
    public double getEndPositionXForPoint() {
        if(imvRight != null){
            return imvRight.getLayoutX() + imvRight.getImage().getWidth();
        }else{
            return line.getEndX() + 2;
        }
    }

    @Override
    public void moveNeighborUp(ImageView imv, Line l){
        if(imvLeft != null){
            imvLeft.relocate(imv.getLayoutX(), imv.getLayoutY());
            labelLeft.relocate(imv.getLayoutX(), imv.getLayoutY() - imv.getImage().getHeight()/2);
        }
        line.setStartX(imv.getLayoutX() + imv.getImage().getWidth());
        line.setEndX(imv.getLayoutX() + imv.getImage().getWidth() + lengthLine);
        line.setStartY(l.getStartY());
        line.setEndY(l.getStartY());
        labelLine.relocate(line.getStartX(), l.getStartY());
        if(imvRight != null){
            imvRight.relocate(line.getEndX(), imv.getLayoutY());
            labelRight.relocate(line.getEndX() + imv.getImage().getWidth()/2, imv.getLayoutY() + imv.getImage().getHeight());
        }
    }

    @Override
    public void moveUp() {
        if(this.up != null && imvRight != null){
            this.up.moveNeighborUp(imvRight, line);
            this.up.moveUp();
        }
    }

    @Override
    public void moveDownWithValues(double newPosX, double newPosY, double heightSX, double lunghezza, Node node, double widthSX, boolean left, double widthDX, double heightDX){
        if(left){
            if(imvRight != null){
                imvRight.relocate(newPosX, newPosY);
                labelRight.relocate(newPosX, newPosY + heightSX);
            }
            line.setEndX(newPosX);
            line.setStartX(node.getLayoutX() - lunghezza);
            line.setStartY(node.getLayoutY() + heightSX/2);
            line.setEndY(line.getStartY());
            labelLine.relocate(line.getStartX(), line.getStartY());
            if(imvLeft != null){
                imvLeft.relocate(newPosX - lunghezza - widthSX, newPosY);
                labelLeft.relocate(newPosX - lunghezza - widthSX, newPosY - heightSX/2);
            }
        }else{
            if(imvRight != null){
                imvRight.relocate(newPosX - lunghezza - widthDX, newPosY);
                labelRight.relocate(newPosX - lunghezza - widthDX, newPosY + heightDX);
            }
            line.setEndX(newPosX - lunghezza - widthDX);
            line.setStartX(node.getLayoutX() - lunghezza - widthDX - lunghezza);
            line.setStartY(node.getLayoutY() + heightDX/2);
            line.setEndY(line.getStartY());
            labelLine.relocate(line.getStartX(), line.getStartY());
            if(imvLeft != null){
                imvLeft.relocate(newPosX - 2*lunghezza - 2*widthDX, newPosY);
                labelLeft.relocate(newPosX - 2*lunghezza - 2*widthDX, newPosY - heightDX/2);
            }
        }
    }

    @Override
    public void moveUpWithValues(double newPosX, double newPosY, double heightSX, double lunghezza, Node node, double widthSX, boolean left, double widthDX, double heightDX) {
        if(left){
            if(imvLeft != null){
                imvLeft.relocate(newPosX + widthSX + lunghezza, newPosY);
                labelLeft.relocate(newPosX + widthSX + lunghezza + widthSX/2, newPosY - heightSX/2);
            }
            line.setStartX(newPosX + 2*widthSX + lunghezza);
            line.setEndX(newPosX + 2*widthSX + 2*lunghezza);

            line.setStartY(node.getLayoutY() + heightSX/2);
            line.setEndY(line.getStartY());
            labelLine.relocate(line.getStartX(), line.getStartY());
            if(imvRight != null){
                imvRight.relocate(newPosX + 2*widthSX + 2*lunghezza, newPosY);
                labelRight.relocate(newPosX + 2*widthSX + 2*lunghezza + widthSX/2, newPosY + heightSX);
            }
        }else{
            if(imvLeft != null){
                imvLeft.relocate(newPosX, newPosY);
                labelLeft.relocate(newPosX + widthDX/2, newPosY - heightDX/2);
            }
            line.setStartX(newPosX + widthDX);
            line.setEndX(newPosX + widthDX + lunghezza);

            line.setStartY(node.getLayoutY() + heightDX/2);
            line.setEndY(line.getStartY());
            labelLine.relocate(line.getStartX(), line.getStartY());
            if(imvRight != null){
                imvRight.relocate(newPosX + widthDX + lunghezza, newPosY);
                labelRight.relocate(newPosX + widthDX + lunghezza, newPosY + heightDX);
            }
        }
    }

    @Override
    public void moveDown() { 
        if(this.down != null){
            down.moveNeighborDown(imvLeft, line);
            this.down.moveDown();
        }
    }

    @Override
    public void moveNeighborDown(ImageView imv, Line l) {
        if(imvRight != null){
            imvRight.relocate(imv.getLayoutX(), imv.getLayoutY());
            labelRight.relocate(imv.getLayoutX(), imv.getLayoutY() + imv.getImage().getHeight());
        }
        line.setStartX(imv.getLayoutX() - lengthLine);            
        line.setEndX(imv.getLayoutX());
        line.setStartY(l.getStartY());
        line.setEndY(l.getStartY());
        labelLine.relocate(line.getStartX(), l.getStartY());
        if(imvLeft != null){
            imvLeft.relocate(line.getStartX() - imv.getImage().getWidth(), imv.getLayoutY());
            labelLeft.relocate(line.getStartX() - imv.getImage().getWidth(), imv.getLayoutY() - imv.getImage().getHeight()/2);
        }
    }

    public void bindCircle(boolean partenza) {
        if(partenza){
            if(line != null){
                circle.centerXProperty().bind(line.startXProperty());
                circle.centerYProperty().bind(line.startYProperty());
            }
        }else{
            if(line != null){
                circle.centerXProperty().bind(line.endXProperty());
                circle.centerYProperty().bind(line.endYProperty());
            }
        }
    }

    @Override
    public void shiftOrizzontale(double minValueX){
        if(imvLeft != null){
            imvLeft.setLayoutX(imvLeft.getLayoutX() + Math.abs(minValueX));
            labelLeft.setLayoutX(labelLeft.getLayoutX() + Math.abs(minValueX));
        } 
        if(imvRight != null){
            imvRight.setLayoutX(imvRight.getLayoutX() + Math.abs(minValueX));
            labelRight.setLayoutX(labelRight.getLayoutX() + Math.abs(minValueX));
        }
        line.setStartX(line.getStartX() + Math.abs(minValueX));
        line.setEndX(line.getEndX() + Math.abs(minValueX));
        labelLine.setLayoutX(labelLine.getLayoutX() + Math.abs(minValueX));
    }

    @Override
    public void clean() {
        if(imvLeft != null){
            imvLeft.layoutXProperty().unbind();
            imvLeft.layoutYProperty().unbind();
        }
        if(imvRight != null){
            imvRight.layoutXProperty().unbind();
            imvRight.layoutYProperty().unbind();
        }
        if(line != null){
            line.startXProperty().unbind();
            line.endXProperty().unbind();
        }
        if(circle != null){
            circle.centerXProperty().unbind();
            circle.centerYProperty().unbind();
        }
        this.up = null;
        this.down = null;
        this.imvLeft = null;
        this.imvRight = null;
        this.labelLeft = null;
        this.labelRight = null;
        this.line = null;
        this.labelLine = null;
        this.circle = null;
    }
    
    @Override
    public  ObservableValue<? extends Number> getEndXProperty(){
        if(imvRight!=null){
            return imvRight.layoutXProperty().add(imvRight.getImage().getWidth());
        }else{
            return line.endXProperty();
        }
    }

    @Override
    public ObservableValue<? extends Number> getEndYProperty() {
        if(imvRight!=null){
            return imvRight.layoutYProperty().add(imvRight.getImage().getHeight()/2);
        }else{
            return line.endYProperty();
        }
    }

    @Override
    public  ObservableValue<? extends Number> getStartXProperty(){
        if(imvLeft!=null){
            return imvLeft.layoutXProperty();
        }else{
            return line.startXProperty();
        }
    }

    @Override
    public ObservableValue<? extends Number> getStartYProperty() {
        if(imvLeft!=null){
            return imvLeft.layoutYProperty().add(imvLeft.getImage().getHeight()/2);
        }else{
            return line.startYProperty();
        }
    }

    @Override
    public void setNewHeight(Double newHeight, boolean fromLeft) {
        Double heigthHalf = imvLeft!=null ? imvLeft.getImage().getHeight()/2 : imvRight != null ? imvRight.getImage().getHeight()/2 : 0.0;
        if(imvLeft!=null){
            imvLeft.setLayoutY(newHeight - heigthHalf);
            labelLeft.setLayoutY(newHeight - 2*heigthHalf);
        }
        this.line.setStartY(newHeight);
        this.line.setEndY(newHeight);
        this.labelLine.setLayoutY(newHeight);
        if(imvRight != null){
            imvRight.setLayoutY(newHeight - heigthHalf);
            labelRight.setLayoutY(newHeight + heigthHalf);
        }
        if(fromLeft && up!=null && up instanceof Linear){
            up.setNewHeight(newHeight, fromLeft);
        }
        if(!fromLeft && down!=null && down instanceof Linear){
            down.setNewHeight(newHeight, fromLeft);
        }
    }
}

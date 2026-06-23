package layoutvisualizer.model;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Line;

public class DraggableMaker {
    private double mouseAnchorX;
    private double mouseAnchorY;
    private ScrollPane scrollPane;

    public void setScrollPane(ScrollPane sp){
        this.scrollPane = sp;
    }

    public void makeDraggable3(Node node, Node n2, Line line, boolean left, Linear c){
        Label sx = c.labelLeft;
        Label dx = c.labelRight;
        Label edge = c.labelLine;
        double widthSX = c.imvLeft != null ? c.imvLeft.getImage().getWidth() : 0.0;
        double heightSX = c.imvLeft != null ? c.imvLeft.getImage().getHeight() : 0.0;
        double widthDX = c.imvRight != null ? c.imvRight.getImage().getWidth() : 0.0;
        double heightDX = c.imvRight != null ? c.imvRight.getImage().getHeight() : 0.0;
        double lineLength = c.line != null ? c.line.getEndX() - c.line.getStartX() : 0.0;
        
        node.setOnMousePressed(mouseEvent -> {
            mouseAnchorX = mouseEvent.getX();
            mouseAnchorY = mouseEvent.getY();
        });

        node.setOnMouseDragged(mouseEvent -> {
            double horizOffset = (scrollPane.getContent().getBoundsInLocal().getWidth() - scrollPane.getViewportBounds().getWidth())*scrollPane.getHvalue();
            double vertiOffset = (scrollPane.getContent().getBoundsInLocal().getHeight() - scrollPane.getViewportBounds().getHeight())*scrollPane.getVvalue(); 
            double newPosX = mouseEvent.getSceneX() + horizOffset - mouseAnchorX ;
            double newPosY = mouseEvent.getSceneY() + vertiOffset - mouseAnchorY ;
            node.setLayoutX(newPosX);
            node.setLayoutY(newPosY);
            if(left){
                if(n2!=null && line != null){
                    n2.setLayoutX(newPosX + widthSX + lineLength );
                    n2.setLayoutY(newPosY);
                    if(dx!=null){
                      dx.relocate(n2.getLayoutX(), n2.getLayoutY() + heightDX);
                    }
                }
                if(line !=null){
                    line.setStartX(node.getLayoutX() + widthSX);
                    line.setStartY(node.getLayoutY() + heightSX/2);
                    line.setEndX(line.getStartX() + lineLength);
                    line.setEndY(line.getStartY());
                    if(edge != null)
                        edge.relocate(line.getStartX(), line.getStartY());
                }
                if(sx!=null){
                   sx.relocate(newPosX + widthSX/2, newPosY - heightSX/2 );
                }

                if(c.down!=null){
                    c.down.moveDownWithValues(newPosX, newPosY, heightSX, lineLength, node, widthSX, left, widthDX, heightDX);
                    c.down.moveDown();
                }
                if(c.up!=null){
                    c.up.moveUpWithValues(newPosX, newPosY, heightSX, lineLength, node, widthSX, left, widthDX, heightDX);
                    c.up.moveUp();
                }
            }else{
                if(n2!=null && line != null){
                    n2.setLayoutX(newPosX - lineLength - widthSX);
                    n2.setLayoutY(newPosY);
                    if(sx!=null){
                        sx.relocate(n2.getLayoutX() + widthSX/2, n2.getLayoutY() - heightSX/2 );
                    }
                }
                if(line !=null){
                    line.setEndX(node.getLayoutX());
                    line.setEndY(node.getLayoutY() + heightDX/2);
                    line.setStartX(line.getEndX() - lineLength);
                    line.setStartY(line.getEndY());
                    if(edge != null)
                        edge.relocate(line.getStartX(), line.getStartY());
                }
                if(dx!=null){
                    dx.relocate(newPosX, newPosY + heightDX);
                }
                if(c.down!=null){
                    c.down.moveDownWithValues(newPosX, newPosY, heightSX, lineLength, node, widthSX, left, widthDX, heightDX);
                    c.down.moveDown();
                }
                if(c.up!=null){
                    c.up.moveUpWithValues(newPosX, newPosY, heightSX, lineLength, node, widthSX, left, widthDX, heightDX);
                    c.up.moveUp();
                }
            }
            mouseEvent.consume();

        });
    }

    public void makeDraggable2(Linear component){
        if(component!=null && component.getImvLeft()!= null){
            makeDraggable3(component.getImvLeft(), component.getImvRight(), component.line, true, component);
        }
        if(component!=null && component.getImvRight() != null){
            makeDraggable3(component.getImvRight(), component.getImvLeft(), component.line, false, component);
        }
        if(component!=null &&  component.getImvLeft()== null && component.getImvRight()== null && component.labelLine!= null){
            makeLabelDraggable(component.line, component.labelLine);
        }
    }

    public void makePointDraggable(Point point){
        if(point.labelLine != null){
            makeLabelDraggable(point.line, point.labelLine, point);
        }
    }

    private void makeLabelDraggable(Line pointLine, Label pointLabel, Point point) {
        pointLabel.setOnMousePressed(mouseEvent -> {
            if(mouseEvent.getButton() == MouseButton.PRIMARY){
                mouseAnchorX = mouseEvent.getX();
                mouseAnchorY = mouseEvent.getY();
            }
        });

        pointLabel.setOnMouseDragged(mouseEvent -> {
            if(mouseEvent.getButton() == MouseButton.PRIMARY){
                double horizOffset = (scrollPane.getContent().getBoundsInLocal().getWidth() - scrollPane.getViewportBounds().getWidth())*scrollPane.getHvalue(); 
                double vertiOffset = (scrollPane.getContent().getBoundsInLocal().getHeight() - scrollPane.getViewportBounds().getHeight())*scrollPane.getVvalue(); 
                double newPosX = mouseEvent.getSceneX() + horizOffset - mouseAnchorX ;
                double newPosY = mouseEvent.getSceneY() + vertiOffset - mouseAnchorY ;
                pointLabel.setLayoutX(newPosX);
                pointLabel.setLayoutY(newPosY);
                pointLine.setStartX(newPosX);
                pointLine.setStartY(newPosY);
                pointLine.setEndX(newPosX+100.0);
                pointLine.setEndY(newPosY);
            }
        });
    }
    
    public void makeLabelDraggable(Line line, Label label){

        label.setOnMousePressed(mouseEvent -> {
            mouseAnchorX = mouseEvent.getX();
            mouseAnchorY = mouseEvent.getY();
        });

        label.setOnMouseDragged(mouseEvent -> {
            double horizOffset = (scrollPane.getContent().getBoundsInLocal().getWidth() - scrollPane.getViewportBounds().getWidth())*scrollPane.getHvalue(); 
            double vertiOffset = (scrollPane.getContent().getBoundsInLocal().getHeight() - scrollPane.getViewportBounds().getHeight())*scrollPane.getVvalue(); 
            double newPosX = mouseEvent.getSceneX() + horizOffset  - mouseAnchorX;
            double newPosY = mouseEvent.getSceneY() + vertiOffset - mouseAnchorY;
            label.setLayoutX(newPosX);
            label.setLayoutY(newPosY);
            line.setStartX(newPosX);
            line.setStartY(newPosY);
            line.setEndX(newPosX+100.0);
            line.setEndY(newPosY);
        });
    }
}


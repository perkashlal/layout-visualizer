package layoutvisualizer;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import java.util.List;

import layoutvisualizer.model.ModelComponent;
import layoutvisualizer.model.network.XMI;

import javax.imageio.ImageIO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;

public class Controller {
    @FXML 
    private AnchorPane pane;

    @FXML 
    public SplitPane splitPane;

    @FXML 
    private Group group;

    @FXML 
    private ScrollPane scroller;

    @FXML 
    private Slider slider;

    @FXML
    private TextField cutText;

    @FXML
    private TextField orderText;

    private ModelComponent model = new ModelComponent();

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Slider getSlider() {
        return slider;
    }

    public void setSlider(Slider slider) {
        this.slider = slider;
    }

    public ScrollPane getScroller() {
        return scroller;
    }

    public void setScroller(ScrollPane scroller) {
        this.scroller = scroller;
    }

    public AnchorPane getAnchorPane() {
        return pane;
    }

    public void setAnchorPane(AnchorPane pane) {
        this.pane = pane;
    }

    @FXML
    private void addCut(){
        String user_input = this.cutText.getText();
        if(user_input!=null && user_input.length()>0)
            model.addCut(user_input, pane);
    }

    @FXML
    private void removeCut(){
        String user_input = this.cutText.getText();
        if(user_input!=null && user_input.length()>0)
            model.removeCut(pane, user_input);
    }

    @FXML
    public void clear(){
        this.cutText.clear();
        this.orderText.clear();
        this.getAnchorPane().getChildren().clear();
        this.splitPane.getItems().clear();
        ScrollPane sp = new ScrollPane();
        this.setScroller(sp);
        Group gp = new Group();
        this.setGroup(gp);
        AnchorPane anch = new AnchorPane();
        this.setAnchorPane(anch);
        gp.getChildren().add(anch);
        sp.setContent(gp);
        slider.valueProperty().unbind();
        splitPane.getItems().add(sp);
        model.clear();
    }

    public void closeAll(){
        clear();
        this.splitPane.getItems().clear();
    }

    @FXML
    private void saveLayout(){
        try{
            for(Node node : this.splitPane.getItems()){
                if(node instanceof ScrollPane){
                    FileChooser fileChooser = new FileChooser();
                    File file = fileChooser.showSaveDialog(splitPane.getScene().getWindow());
                    if(file!=null){
                        try {
                            ScrollPane scr = (ScrollPane) node;
                            Group gp = (Group) scr.getContent();
                            AnchorPane ap = (AnchorPane)  gp.getChildren().get(0);
                            WritableImage writableImage = new WritableImage((int)ap.getWidth() + 20, (int)ap.getHeight() + 20);
                            ap.snapshot(null, writableImage);
                            BufferedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                            ImageIO.write(renderedImage, "png", file);
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setContentText("Layout salvato!");
                            alert.showAndWait();
                        } catch (IOException ex) { 
                            ex.printStackTrace(); 
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setContentText("Errore durante il salvataggio!");
                            alert.showAndWait();
                        }
                    }
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Errore durante il salvataggio!");
            alert.showAndWait();
        }
    }

    @FXML
    private void uploadXML(){
        FileChooser fileChooser = new FileChooser();
        File file = null;
        try{
            file = fileChooser.showOpenDialog(splitPane.getScene().getWindow());
            if(file!=null){
                parseFile(file);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            
        }
    }

    
    @FXML
    private void dragFile(DragEvent event){
        if(event.getDragboard().hasFiles()){
            event.acceptTransferModes(TransferMode.ANY);
        }
        event.consume();
    }

    @FXML
    private void dropFile(DragEvent event){
        List<File> files = event.getDragboard().getFiles();
        try{
            if(files.get(0)!=null){
                parseFile(files.get(0));
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            files.clear();
        }
        event.consume();
    }

    private void parseFile(File file){
        try{
            if(file!=null && checkExtension(file)){
                JAXBContext jaxb = null;
                try{
                    clear();
                    jaxb = JAXBContext.newInstance(XMI.class);
                    Unmarshaller unmarshaller = jaxb.createUnmarshaller();
                    XMI xmi = (XMI) unmarshaller.unmarshal(file);
                    model.parseXML(xmi, pane, group, scroller, slider, "");
                }catch(Exception e){
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Errore durante l'upload!");
                    alert.showAndWait();
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            
        }
    }

    private boolean checkExtension(File file) {
        boolean check = false;
        try{
            if(file!=null){
                String extension = "";
                String fileName = file.getName() != null ? file.getName() : "";
                int i = fileName.lastIndexOf('.');
                if (i > 0 && i < fileName.length() - 1)
                    extension = fileName.substring(i + 1).toLowerCase();
                if(!extension.equalsIgnoreCase("xml")){
                    check = false;
                }else{
                    check = true;
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return check;
    }

    @FXML
    private void downloadXML(){
        JAXBContext jaxb = null;
        try{
            jaxb = JAXBContext.newInstance(XMI.class);
            Marshaller marshaller = jaxb.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            List<XMI> results = model.download();
            if(results.size() > 1){
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File file = directoryChooser.showDialog(scroller.getScene().getWindow());
                if(file!=null){
                    if(!file.isDirectory()){
                        file.mkdirs();
                    }
                    for(XMI parte : results){
                        String fileName = parte.getInterlocking().getNet().getId() + ".xml";
                        try (OutputStream os = new FileOutputStream(file.getAbsolutePath() + "/" + fileName)) {
                            marshaller.marshal(parte, os);
                            os.close();
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }
            }
            else if(results.size() == 1){
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showSaveDialog(scroller.getScene().getWindow());
                if(file!=null){
                    try (OutputStream os = new FileOutputStream(file.getAbsolutePath());) {
                        marshaller.marshal(results.get(0), os);
                        os.close();
                    } catch (Exception e) {
                        System.out.println(e);
                    } 
                }
            }
            Alert alert = null;
            if(results.size() == 0){
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Errore durante il download, il taglio non divide la rete!");
                alert.showAndWait();
            }else{
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Download terminato!");
                alert.showAndWait();
                try{
                    reUpload(this.splitPane, results);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Errore durante il download!");
            alert.showAndWait();
        }

    }

    private void reUpload(SplitPane splitPane, List<XMI> results) {
        model.preReUpload();
        this.getAnchorPane().getChildren().clear();
        slider.valueProperty().unbind();
        splitPane.getItems().clear();
        for(XMI result : results){
            ScrollPane sp = new ScrollPane();
            Group gp = new Group();
            AnchorPane anch = new AnchorPane();
            gp.getChildren().add(anch);
            sp.setContent(gp);
            splitPane.getItems().add(sp);
            model.parseXML(result, anch, gp, sp, slider,"");            
        }
    }
    

    @FXML
    public void reload(){
        String user_input = this.orderText.getText();
        try{
            model.reload(user_input, pane, group, scroller, slider);
        }catch(Exception ex){
            ex.printStackTrace();
            try{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Errore durante l'upload!");
                alert.showAndWait();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void rotatePoint(){
        String userInput = this.orderText.getText();
        if(userInput != null && userInput.trim().length() > 0){
            model.rotatePoint(userInput.trim());
        }
    }

    @FXML
    public void rotateElementClockwise(){
        rotateElement(90.0);
    }

    @FXML
    public void rotateElementCounterClockwise(){
        rotateElement(-90.0);
    }

    private void rotateElement(double degrees){
        String userInput = this.orderText.getText();
        if(userInput != null && userInput.trim().length() > 0){
            model.rotateElement(userInput.trim(), degrees);
        }
    }
    
}

package layoutvisualizer.model;

import javafx.scene.shape.Line;

public class TaglioRete {
    public Line cut;
    public String primoTaglio = "";
    public String secondoTaglio = "";
    public String definition = "";
    
    public String getDefinition() {
        return definition;
    }
    public void setDefinition(String definition) {
        this.definition = definition;
    }
    public Line getCut() {
        return cut;
    }
    public void setCut(Line cut) {
        this.cut = cut;
    }
    public String getPrimoTaglio() {
        return primoTaglio;
    }
    public void setPrimoTaglio(String primaTrack) {
        this.primoTaglio = primaTrack;
    }
    public String getSecondoTaglio() {
        return secondoTaglio;
    }
    public void setSecondoTaglio(String secondaTrack) {
        this.secondoTaglio = secondaTrack;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((primoTaglio == null) ? 0 : primoTaglio.hashCode());
        result = prime * result + ((secondoTaglio == null) ? 0 : secondoTaglio.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TaglioRete other = (TaglioRete) obj;
        if (primoTaglio == null) {
            if (other.primoTaglio != null)
                return false;
        } else if (!primoTaglio.equals(other.primoTaglio))
            return false;
        if (secondoTaglio == null) {
            if (other.secondoTaglio != null)
                return false;
        } else if (!secondoTaglio.equals(other.secondoTaglio))
            return false;
        return true;
    }

    public static String createDefinition(String[] tracceTagliate) {
        String nameOfCut = "";
        nameOfCut = tracceTagliate[0].replaceAll("-", "_");
        if(tracceTagliate.length > 1){
            nameOfCut += "_and_" + tracceTagliate[1].replaceAll("-", "_");
        }
        return nameOfCut;
    }
    
}

package layoutvisualizer.model.network;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name="Documentation")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmiDocumentation {
    @XmlAttribute(name="exporter")
    private String exporter = "";

    @XmlAttribute(name="exporterVersion")
    private String exporterVersion = "";

    public String getExporter() {
        return exporter;
    }

    public void setExporter(String exporter) {
        this.exporter = exporter;
    }

    public String getExporterVersion() {
        return exporterVersion;
    }

    public void setExporterVersion(String exporterVersion) {
        this.exporterVersion = exporterVersion;
    }
}

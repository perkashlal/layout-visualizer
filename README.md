# Layout Visualizer

JavaFX desktop application for visualizing railway interlocking layouts from XML files and generating split XML networks after admissible cuts.

The project is based on Simone Casini's thesis work, "Layout visualizer for railway interlocking systems", and is intended to support compositional verification workflows for large railway interlocking layouts.

## Features

- Load an interlocking network from XML.
- Visualize linear track sections, points/switches, and markerboards.
- Manually adjust the generated layout.
- Rotate a point branch by ID with the `Ruota scambio` control.
- Insert single cuts and cluster cuts.
- Export the displayed layout as PNG.
- Download XML files for the generated subnetworks.

## Requirements

- Java 11 or newer.
- Maven 3.8 or newer.

The Maven project declares JavaFX dependencies, so the easiest development command is:

```bash
mvn clean javafx:run
```

## Build

```bash
mvn clean package
```

The shaded JAR is created under:

```text
target/layout_visualizer-1.0-SNAPSHOT-shaded.jar
```

Run it with:

```bash
java -jar target/layout_visualizer-1.0-SNAPSHOT-shaded.jar
```

If you use an external JavaFX SDK instead, adapt the module path to your installation:

```bash
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar target/layout_visualizer-1.0-SNAPSHOT-shaded.jar
```

## Input XML

The application expects an XMI/XML file containing:

- `trackSection` elements with `id`, `type`, and optional `length`.
- `neighbor` elements with `ref` and `side`.
- `markerboard` elements with `id`, `track`, `distance`, and `mounted`.

The sample `lvr_1.xml` can be used to test the application.

## Layout Controls

- Use `Upload` or drag and drop an XML file into the window.
- Use the first text field to enter a cut such as `533-PM01U`.
- Use `;` for cluster cuts, for example `802-PM04U;801-PM04U`.
- Use the second text field to enter a starting track ID and click `Ricarica`.
- Enter a point ID in the second text field and click `Ruota scambio` to flip the secondary branch orientation.
- Click `Download` to generate XML outputs after applying cuts.

## Current Improvement Focus

Large or complex layouts can still require manual correction because the XML input does not encode all graphical information. The most important missing information is:

- whether the `minus` branch of a point should be above or below the `plus` branch;
- the desired distance between diverging branches;
- explicit rotation or geometry metadata for track elements.

This version starts addressing that limitation by exposing branch rotation directly in the UI and keeping the exported neighbor order aligned with the displayed point orientation.

## Roadmap

- Add automatic point-orientation selection to reduce crossings and overlaps.
- Add configurable branch spacing for dense layouts.
- Add a layout quality score based on crossings, overlaps, and manual edits required.
- Add regression tests using representative XML layouts.
- Remove duplicated legacy resources under `src/main/java` after confirming no IDE workflow depends on them.


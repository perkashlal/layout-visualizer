# Project Context - Layout Visualizer

## Repository

- Local path: `C:\Users\bajaj\git\layout visulizer\layout_visualizer-master\layout_visualizer-master`
- GitHub: `https://github.com/perkashlal/layout-visualizer`
- Main branch: `main`
- Application type: Java 11 / JavaFX desktop application
- Build tool: Maven

## Project Background

This project is based on Simone Casini's thesis work, "Layout visualizer for railway interlocking systems".

The application visualizes railway interlocking layouts from XML files. It supports:

- loading XML railway network descriptions;
- drawing linear track sections, switches/points, and markerboards;
- manually adjusting the generated visualization;
- inserting cuts in the layout;
- exporting generated subnetworks as XML;
- saving the visualized layout as an image.

The professor's feedback identifies the main limitation: complex station layouts are not always displayed optimally because the input XML lacks complete graphical geometry information.

## Official Scope Of My Contribution

Only the following three enhancements should be treated as the project scope:

1. Rotation functionality
   - Add real support for rotating visual track elements.
   - The current application mainly supports dragging/repositioning, but not true rotation.

2. Automatic switch branch direction
   - Current behavior places the `minus` branch of a switch below the `plus` branch by convention.
   - The project should extend XML/model/drawing logic so branch direction can be handled automatically.
   - Goal: reduce manual correction when the expected railway layout has `minus` above `plus`.

3. Improved layout optimization
   - Improve overlap detection and layout adjustment for complex station layouts.
   - Goal: cleaner drawings with fewer overlaps and fewer manual corrections.

Avoid adding unrelated features unless they directly support one of these three points.

## Work Already Completed

The following preparation work has already been completed and pushed to GitHub:

- Created Git repository and pushed it to GitHub.
- Fixed the runnable JAR manifest:
  - old invalid main class: `layoutvisualizer.Main`
  - corrected main class: `layoutvisualizer.Launcher`
- Added GitHub-ready `README.md`.
- Added `.gitignore`.
- Removed duplicate image/FXML resources from `src/main/java`.
- Kept canonical resources under `src/main/resources`.
- Added professor report:
  - `docs/Layout_Visualizer_Change_Report.docx`
- Verified Maven packaging with:

```bash
mvn -q -DskipTests clean package
```

- Verified JavaFX app startup from the shaded JAR.
- Added GitHub Actions workflow:
  - `.github/workflows/maven-build.yml`
  - builds the Maven project on `windows-latest`;
  - verifies the shaded JAR manifest;
  - uploads the generated shaded JAR as a workflow artifact.

## Important Existing Files

- `pom.xml`
  - Maven build configuration.
  - JavaFX dependencies.
  - Shade plugin JAR packaging.

- `src/main/java/layoutvisualizer/App.java`
  - JavaFX application entrypoint.

- `src/main/java/layoutvisualizer/Launcher.java`
  - Launcher used by the shaded JAR.

- `src/main/java/layoutvisualizer/Controller.java`
  - UI event handlers for upload, download, reload, cuts, save image, and rotation controls.

- `src/main/java/layoutvisualizer/model/ModelComponent.java`
  - Core model orchestration.
  - Parses XML, builds network, draws layout, reloads, handles cuts, downloads XML.

- `src/main/java/layoutvisualizer/model/UtilityDraw.java`
  - Main drawing logic for tracks and points.
  - Important target for branch direction and layout optimization work.

- `src/main/java/layoutvisualizer/model/Point.java`
  - Visual representation of a switch/point.
  - Important target for rotation behavior.

- `src/main/java/layoutvisualizer/model/Linear.java`
  - Visual representation of a linear track section.

- `src/main/java/layoutvisualizer/model/network/TrackSection.java`
  - JAXB model for XML track sections.
  - Contains neighbor assignment and point metadata.

- `src/main/java/layoutvisualizer/model/network/Network.java`
  - JAXB model for the railway network.
  - Builds track-section lookup map and associates markerboards.

- `src/main/resources/layoutvisualizer/view.fxml`
  - JavaFX UI layout.

## Current Local State Warning

There may be uncommitted local draft changes related to an early automatic rotation heuristic:

- `README.md`
- `src/main/resources/layoutvisualizer/view.fxml`
- `src/main/java/layoutvisualizer/Controller.java`
- `src/main/java/layoutvisualizer/model/ModelComponent.java`
- `src/main/java/layoutvisualizer/model/Point.java`

These draft changes were not finalized as the official implementation. Before continuing development, inspect them with:

```bash
git status --short
git diff
```

Decide whether to:

- reshape them into the official three-scope implementation; or
- discard/rework them manually after confirming they are not needed.

Do not blindly commit them without review.

## Recommended Implementation Plan

1. Stabilize working tree
   - Review any uncommitted draft changes.
   - Keep only changes that support the official scope.

2. Rotation functionality
   - Define what "rotation" means for each visual element:
     - linear section;
     - point/switch;
     - markerboard image/label;
     - connected lines.
   - Implement rotation around a stable pivot point.
   - Ensure drag behavior and bound connector lines still work.

3. Automatic switch branch direction
   - Decide how branch direction is represented:
     - XML attribute, for example `branchDirection="up"` / `branchDirection="down"`; or
     - derived automatically from topology/layout heuristics.
   - Extend `TrackSection` if XML metadata is used.
   - Modify `UtilityDraw` so point drawing uses the chosen direction.
   - Ensure downloaded XML preserves or correctly derives branch order.

4. Improved layout optimization
   - Improve the current overlap handling in `UtilityDraw.togliSovrapposizioni`.
   - Add a layout score:
     - same-level overlaps;
     - crossings;
     - branches drawn backward;
     - excessive manual adjustment need.
   - Test against `lvr_1.xml` and any complex XML examples.

5. Verification
   - Run:

```bash
mvn -q -DskipTests clean package
java -jar target/layout_visualizer-1.0-SNAPSHOT-shaded.jar
```

   - Confirm app opens.
   - Upload `lvr_1.xml`.
   - Test rotation/branch direction/layout behavior.
   - Confirm XML download still works.

6. GitHub
   - Commit in small logical steps.
   - Push to:

```bash
git push
```

## GitHub Actions

The repository includes a CI workflow:

```text
.github/workflows/maven-build.yml
```

The workflow runs on every push and pull request to `main`.

It performs:

- repository checkout;
- Java 17 setup using Temurin;
- Maven build with `mvn -B -DskipTests clean package`;
- shaded JAR manifest verification;
- shaded JAR upload as an artifact.

The workflow intentionally does not launch the JavaFX desktop UI because GitHub Actions runners are headless build environments. Runtime UI behavior should still be tested locally.

## Notes For Professor Explanation

The key technical idea is that the original tool converts topology into geometry, but the XML contains incomplete geometry. Therefore, the project must bridge that missing information by:

- adding interactive rotation controls;
- representing or inferring switch branch direction;
- improving layout optimization heuristics.

The goal is not to replace the original thesis tool, but to extend it so complex station layouts require fewer manual corrections.

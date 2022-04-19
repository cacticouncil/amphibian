# Amphibian
This plugin integrates the Droplet Editor into an IntelliJ IDEA Plugin. The Droplet Editor is also used in Pencil Code (http://pencilcode.net/) and some code.org exercises. Eventually, we hope to support all languages supported by Droplet and the various tools in the JetBrains IDE family. 

# Installation & Running
Amphibian can be installed like any other IntelliJ plugin through the plugin menu. It is also available on the JetBrains Plugin Repository (https://plugins.jetbrains.com/plugin/15716-amphibian-editor).

# Other Projects Incorporated into Amphibian
Amphibian uses a custom variant of the Droplet Editor from Pencil Code (http://pencilcode.net).

# For Developers
## Dependencies/Resources
Amphibian is compiled using Gradle version 7.1. It was developed for Java 11 and is designed to be compatible with IntelliJ versions 2020.3 and later. It uses the Chromium Embedded Framework via the Jetbrains SDK.
## Project Structure
Amphibian uses the Chromium Embedded Framework (CEF) to run Droplet as an embedded tab inside of IntelliJ. The AmphibianEditor class extends the default FileEditor class in order to add this additional tab's functionality and facilitate switching between editing styles. The AmphibianStartupListener copies the Droplet website files out from the runtime's resource directory on project load, and creates a relation map between file types and extensions which is stored in the AmphibianService.

# Amphibian Features

## Full Use of Droplet

Users are able to use all features that come packaged with Droplet.
Users are able to switch between text and blocks through the tab at the bottom of the 
editor.

AmphibianEditor provides the logic for creating a new Droplet instance on plugin start.
The Droplet instance is created by calling JavaScript functions located in
`resources/org/cacticouncil/amphibian/plugin.js`. This Droplet instance is connected to
JCEF to be rendered in IntelliJ. All events in Droplet are JCEF browser events, and are
handled as such (i.e. downloading).

AmphibianToggle handles the logic for switching between text and blocks.

PaletteManager handles the creation of the Droplet palette.

## Full Use of IntelliJ

Users are able to use IntelliJ and the text editor as normal.

## Image and Animation Export

Users are able to download blocks from the Blocks tab as SVG images. Additionally,
they have the option to download the block as an animation, which will place each
individual block part into a separate SVG image. This enables the user to then construct
an animation out of the resulting sequenced images.

AmphibianContextHandler handles the logic for all context menu events received.
If either of the export options are selected, JavaScript functions in
`resources/org/cacticouncil/amphibian/plugin.js` are called to process the appropriate
block into a new SVG image that is downloaded through a data URL.

AmphibianDownloadHandler handles the logic for all download events. When the JavaScript
calls for a download event, the handler will receive the request and download the SVG
image to the user's current IntelliJ project directory. 
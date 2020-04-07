Java Chromium Embedded Framework (JCEF) Binary Distribution for Windows 64-bit
-------------------------------------------------------------------------------

Date:             February 27, 2020

JCEF Version:     80.0.4.244+gf88de60
JCEF URL:         https://github.com/chromiumembedded/java-cef.git
                  @f88de609b0ef6bcb9abddb0182c075342ba9ff1d

CEF Version:      80.0.4+g74f7b0c+chromium-80.0.3987.122
CEF URL:          https://bitbucket.org/chromiumembedded/cef.git

Chromium Verison: 80.0.3987.122
Chromium URL:     https://chromium.googlesource.com/chromium/src.git

This distribution contains all components necessary to build and distribute a
Java application using JCEF on the Windows 64-bit platform. Please see the LICENSING
section of this document for licensing terms and conditions.


CONTENTS
--------

bin         Contains Java archives, native library files and the MainFrame
            sample application.

docs        Contains documentation for the org.cef package.


USAGE
-----

1. Install a 64-bit version of the Java 7 runtime.
2. Add the Java bin folder to your system PATH.
3. Execute the run.bat script to run the MainFrame sample application.
4. Optionally recompile the sample application and update jcef-tests.jar
   by running the compile.bat script.

Please visit the JCEF Website for additional usage information.

https://bitbucket.org/chromiumembedded/java-cef


REDISTRIBUTION
--------------

This binary distribution contains the below components.

Required components:

The following components are required. JCEF will not function without them.

* Java archives.
    jcef.jar
    gluegen-rt.jar
    gluegen-rt-natives-windows-amd64.jar
    jogl-all.jar
    jogl-all-natives-windows-amd64.jar

* CEF JNI library.
    jcef.dll

* CEF JNI process helper.
    jcef_helper.exe

* CEF core library.
  * libcef.dll

* Unicode support data.
  * icudtl.dat

* V8 snapshot data.
  * natives_blob.bin
  * snapshot_blob.bin

Optional components:

The following components are optional. If they are missing JCEF will continue to
run but any related functionality may become broken or disabled.

* Localized resources.
  Locale file loading can be disabled completely using
  CefSettings.pack_loading_disabled. The locales directory path can be
  customized using CefSettings.locales_dir_path.

  * locales/
    Directory containing localized resources used by CEF, Chromium and Blink. A
    .pak file is loaded from this directory based on the CefSettings.locale
    value. Only configured locales need to be distributed. If no locale is
    configured the default locale of "en-US" will be used. Without these files
    arbitrary Web components may display incorrectly.

* Other resources.
  Pack file loading can be disabled completely using
  CefSettings.pack_loading_disabled. The resources directory path can be
  customized using CefSettings.resources_dir_path.

  * cef.pak
  * cef_100_percent.pak
  * cef_200_percent.pak
    These files contain non-localized resources used by CEF, Chromium and Blink.
    Without these files arbitrary Web components may display incorrectly.

  * cef_extensions.pak
    This file contains non-localized resources required for extension loading.
    Pass the `--disable-extensions` command-line flag to disable use of this
    file. Without this file components that depend on the extension system,
    such as the PDF viewer, will not function.

  * devtools_resources.pak
    This file contains non-localized resources required for Chrome Developer
    Tools. Without this file Chrome Developer Tools will not function.

* Angle and Direct3D support.
  * d3dcompiler_43.dll (required for Windows XP)
  * d3dcompiler_47.dll (required for Windows Vista and newer)
  * libEGL.dll
  * libGLESv2.dll
  Without these files HTML5 accelerated content like 2D canvas, 3D CSS and WebGL
  will not function.


LICENSING
---------

The JCEF project is BSD licensed. Please read the LICENSE.txt files included with
this binary distribution for licensing terms and conditions. Other software
included in this distribution is provided under other licenses. Please visit
"about:credits" in a JCEF-based application for complete Chromium and third-party
licensing information.

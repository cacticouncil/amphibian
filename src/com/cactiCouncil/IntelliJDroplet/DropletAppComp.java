package com.cactiCouncil.IntelliJDroplet;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

/**
 * Created by exlted on 07-Mar-17.
 */
public class DropletAppComp implements ApplicationComponent {

    static String filePath;

    private boolean copyFileFromJar(String copyFrom, String copyTo){
        InputStream in = this.getClass().getResourceAsStream(copyFrom);
        if(in == null) {
            return false;
        }
        Path blah = Paths.get(copyTo);
        try {
            Files.copy(in, blah, StandardCopyOption.REPLACE_EXISTING);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void initComponent() {
        //Deal with Website construction here
        /*
            3. Write the code that copies the website out
            4. Build a preferences file that holds a boolean of whether it has been copied out, and a path to the place in the file structure where it is.
         */
        Path blah = null;
        try {
            blah = Files.createTempDirectory("Droplet");
        } catch (IOException e) {
            e.printStackTrace();
        }
        filePath = blah.toString() + System.getProperty("file.separator");
        blah.toFile().deleteOnExit();

        copyFileFromJar("example.html", filePath + "example.html");
        copyFileFromJar("ace.js", filePath + "ace.js");
        copyFileFromJar("c_c++_palette.coffee", filePath + "c_c++_palette.coffee");
        copyFileFromJar("coffee-script.js", filePath + "coffee-script.js");
        copyFileFromJar("coffeescript_palette.coffee", filePath + "coffeescript_palette.coffee");
        copyFileFromJar("droplet.css", filePath + "droplet.css");
        copyFileFromJar("droplet.min.css", filePath + "droplet.min.css");
        copyFileFromJar("droplet-full.js", filePath + "droplet-full.js");
        copyFileFromJar("droplet-full.min.js", filePath + "droplet-full.min.js");
        copyFileFromJar("example.coffee", filePath + "example.coffee");
        copyFileFromJar("javascript_palette.coffee", filePath + "javascript_palette.coffee");
        copyFileFromJar("jquery.min.js", filePath + "jquery.min.js");
        copyFileFromJar("mode-c_cpp.js", filePath + "mode-s_cpp.js");
        copyFileFromJar("mode-coffee.js", filePath + "mode-coffee.js");
        copyFileFromJar("mode-javascript.js", filePath + "mode-javascript.js");
        copyFileFromJar("mode-python.js", filePath + "mode-python.js");
        copyFileFromJar("python_palette.coffee", filePath + "python_palette.coffee");
        copyFileFromJar("theme-chrome.js", filePath + "theme-chrome.js");
        copyFileFromJar("worker.js", filePath + "worker.js");
        copyFileFromJar("worker-coffee.js", filePath + "worker-coffee.js");
        copyFileFromJar("worker-javascript.js", filePath + "worker-javascript.js");
    }

    @Override
    public void disposeComponent() {
        //???
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "Droplet";
    }
}

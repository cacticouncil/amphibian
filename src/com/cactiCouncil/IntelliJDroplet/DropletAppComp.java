package com.cactiCouncil.IntelliJDroplet;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;

/**
 * Created by exlted on 07-Mar-17.
 * Sets up the plugin to be ready to be used later in the lifetime of IntelliJ
 */
public class DropletAppComp implements ApplicationComponent {

    /**
     * Holds the filePath to be used by anything needing direct access to the Droplet website
     */
    static String filePath;
    /**
     * Holds the relation map to be used for Droplet to both determine if a file should open a Droplet editor and what palette should be used for what file
     */
    static HashMap<String, String> relationMap = new HashMap<>();

    /**
     * Copies a resource out of com.cactiCouncil.IntelliJDroplet package (might be able to change?) to external location
     * @param copyFrom The name of the resource to be copied out, including the file extension
     * @param copyTo The fully defined file path to copy the file out to
     * @param delete Determines whether the file gets deleted on exit or not
     * @return whether the function succeeded or not
     */
    private boolean copyFileFromResources(String copyFrom, String copyTo, boolean delete){
        InputStream in = this.getClass().getResourceAsStream(copyFrom);
        if(in == null) {
            return false;
        }
        Path blah = Paths.get(copyTo);
        try {
            Files.copy(in, blah, StandardCopyOption.REPLACE_EXISTING);
            if(delete){
                blah.toFile().deleteOnExit();
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Initializes the plugin by copying out the website from resources
     * And by building the relationMap
     */
    @Override
    public void initComponent() {
        Path blah = null;
        try {
            blah = Files.createTempDirectory("Droplet");
        } catch (IOException e) {
            e.printStackTrace();
        }
        filePath = blah.toString() + System.getProperty("file.separator");
        blah.toFile().deleteOnExit();

        InputStream in = this.getClass().getResourceAsStream("Manifest.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            while(reader.ready()){
                String file = reader.readLine();
                copyFileFromResources(file, filePath + file, true);
            }
            reader.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        in = this.getClass().getResourceAsStream("Relations.txt");
        reader = new BufferedReader(new InputStreamReader(in));
        try {
            while(reader.ready()){
                String temp = reader.readLine();
                String[] splits = temp.split("\\|");
                relationMap.putIfAbsent(splits[0], splits[1]);
            }
            reader.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called by IntelliJ to clear up memory that needs to be disposed, currently no memory needs to be disposed
     */
    @Override
    public void disposeComponent() {
    }

    /**
     * Called by IntelliJ for unknown reasons
     * @return the internal name of this plugin
     */
    @NotNull
    @Override
    public String getComponentName() {
        return "Droplet";
    }
}

package com.cactiCouncil.IntelliJDroplet;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;

/**
 * Created by exlted on 07-Mar-17.
 */
public class DropletAppComp implements ApplicationComponent {

    static String filePath;
    static HashMap<String, String> relationMap = new HashMap<>();

    private boolean copyFileFromResources(String copyFrom, String copyTo){
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

        InputStream in = this.getClass().getResourceAsStream("Manifest.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            while(reader.ready()){
                String file = reader.readLine();
                copyFileFromResources(file, filePath + file);
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

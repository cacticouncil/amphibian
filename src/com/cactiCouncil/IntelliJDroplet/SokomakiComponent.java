package com.cactiCouncil.IntelliJDroplet;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;

/**
 * Created by exlted on 07-Mar-17.
 * Sets up the plugin to be ready to be used later in the lifetime of IntelliJ
 */
public class SokomakiComponent implements ApplicationComponent {

    /**
     * Holds the pathname to be used by anything needing direct access to the Droplet website
     */
    static String pathname;
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
    private boolean copyFileFromResources(String copyFrom, String copyTo, boolean delete)
    {
        InputStream in = this.getClass().getResourceAsStream(copyFrom);
        if(in == null)
            return false;

        Path destination = Paths.get(copyTo);
        try {
            Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
            if(delete){
                destination.toFile().deleteOnExit();
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
        FileTemplate template = FileTemplateManager.getDefaultInstance().getTemplate("Droplet Palette");
        if(template == null){
            template = FileTemplateManager.getDefaultInstance().addTemplate("Droplet Palette", "coffee");
            template.setText("//DELETE ALL COMMENTS BEFORE ATTEMPTING TO USE GENERATED PALETTES\n" +
                    "({\n" +
                    "//Currently Usable modes, 'javascript', 'coffee', 'python'\n" +
                    "    mode: 'python',\n" +
                    "    modeOptions: {\n" +
                    "        functions: {},\n" +
                    "        },\n" +
                    "    palette: [\n" +
                    "        {\n" +
                    "            name: 'Group Name',\n" +
                    "            color: 'orange', //Group Color\n" +
                    "            blocks: [\n" +
                    "                { block: '# Insert Code Here' },\n" +
                    "            ]\n" +
                    "        },\n" +
                    "        {\n" +
                    "            name: 'Group Name 2',\n" +
                    "            color: 'green',\n" +
                    "            blocks: [\n" +
                    "                { block: '#More code can be here' },\n" +
                    "                { block: '#You can have multiple blocks per group!' },\n" +
                    "            ]\n" +
                    "        }\n" +
                    "    ]\n" +
                    "  })");
        }

        Path tempPath = null;
        try {
            tempPath = Files.createTempDirectory("Droplet");
        } catch (IOException e) {
            e.printStackTrace();
        }
        pathname = tempPath.toString() + System.getProperty("file.separator");
        tempPath.toFile().deleteOnExit();

        InputStream in = this.getClass().getResourceAsStream("Manifest.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            while(reader.ready()){
                String file = reader.readLine();
                Files.createDirectories(Paths.get(pathname + file).getParent());
                copyFileFromResources(file, pathname + file, true);
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

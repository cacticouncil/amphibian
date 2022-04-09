package org.cacticouncil.amphibian;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import com.intellij.ide.AppLifecycleListener;

public class AmphibianStartupListener implements AppLifecycleListener
{
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
    public void appStarted()
    {
        // get the amphibian service
        AmphibianService amphibianService =
                ApplicationManager.getApplication().getService(AmphibianService.class);

        // perform startup tasks
        // all this code is from the old amphibiancomponent class
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
        //pathname = tempPath.toString() + System.getProperty("file.separator"); CHANGED TO:
        amphibianService.setPathname(tempPath.toString() + System.getProperty("file.separator"));
        tempPath.toFile().deleteOnExit();

        InputStream in = this.getClass().getResourceAsStream("Manifest.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            while(reader.ready()){
                String file = reader.readLine();
                Files.createDirectories(Paths.get(amphibianService.getPathname() + file).getParent());
                copyFileFromResources(file, amphibianService.getPathname() + file, true);
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
                //relationMap.putIfAbsent(splits[0], splits[1]); CHANGED TO
                amphibianService.setRelation(splits[0],splits[1]);
            }
            reader.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

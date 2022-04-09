package org.cacticouncil.amphibian;

import java.util.HashMap;

public class AmphibianService
{
    /**
     * Holds the pathname to be used by anything needing direct access to the Droplet website
     */
    static String pathname;
    /**
     * Holds the relation map to be used for Droplet to both determine if a file should open a Droplet editor and what palette should be used for what file
     */
    static HashMap<String, String> relationMap = new HashMap<>();

    public static String getPathname()
    {
        return pathname;
    }

    public void setPathname(String path)
    {
        pathname = path;
    }

    public static HashMap<String, String> getRelationMap()
    {
        return relationMap;
    }

    public void setRelation(String first, String second)
    {
        relationMap.putIfAbsent(first,second);
    }
}

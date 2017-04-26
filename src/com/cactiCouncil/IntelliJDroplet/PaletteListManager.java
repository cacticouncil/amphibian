package com.cactiCouncil.IntelliJDroplet;

/**
 * Created by exlted on 26-Apr-17.
 */
public class PaletteListManager {

    private String basePaletteList = "CoffeeScript|coffeescript_palette.coffee\\n" +
                                 "JavaScript|javascript_palette.coffee\\n" +
                                 "Python|python_palette.coffee";

    private String paletteList = basePaletteList;

    private static PaletteListManager PLM = null;

    private PaletteListManager(){

    }

    void updatePaletteList(){
        StringBuilder newPaletteList = new StringBuilder(basePaletteList);
        //Deal with filling out the new list here
        paletteList = newPaletteList.toString();
    }

    String getPaletteList(){
        return paletteList;
    }

    public static PaletteListManager getPaletteListManager(){
        if(PLM == null){
            PLM = new PaletteListManager();
        }
        return PLM;
    }

}

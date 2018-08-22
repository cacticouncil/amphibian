package com.cactiCouncil.IntelliJDroplet;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

//@Storage("Droplet.xml")
@State(name="DropletPluginState", storages = { @Storage("Droplet.xml")})

public class DropletPluginState implements PersistentStateComponent<DropletPluginState>
{
    public boolean canLog = true;
    public String randomized = null;
    public String gatorlink = null;
    public String ufid = null;
    public int numRuns = 0;
    public LinkedList<String> logList = new LinkedList<String>();

    public DropletPluginState getState()
    {
        return this;
    }

    public void loadState(DropletPluginState state)
    {
        XmlSerializerUtil.copyBean(state, this);
    }

    public static DropletPluginState getInstance()
    {
        DropletPluginState state = ServiceManager.getService(DropletPluginState.class);

        if (state.randomized == null)
            state.randomized = getSaltString();

        return state;
    }

    private static String getSaltString()
    {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }}

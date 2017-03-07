package com.cactiCouncil.IntelliJDroplet;

import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

/**
 * Created by exlted on 07-Mar-17.
 */
public class DropletAppComp implements ApplicationComponent {
    @Override
    public void initComponent() {
        //Deal with Website construction here
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

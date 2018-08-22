package com.cactiCouncil.IntelliJDroplet;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;

public class DropletSettingsDialog extends DialogWrapper
{
    JTextField gatorlink = null;
    JTextField ufid = null;
    JCheckBox permission = null;

    public DropletSettingsDialog()
    {
        super(false);
        init();
    }

    public void doOKAction()
    {
        DropletPluginState settings = DropletPluginState.getInstance();
        settings.gatorlink = (gatorlink.getText().equals("") ? null : gatorlink.getText());
        settings.ufid = (ufid.getText().equals("") ? null : ufid.getText());
        settings.canLog = permission.isSelected();

        gatorlink = null;
        ufid = null;
        permission = null;

        super.doOKAction();
    }

    protected JComponent createCenterPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayoutManager(5, 5, new Insets(0, 0, 0, 0), -1, -1));

        JTextPane blurb = new JTextPane();
        blurb.setText("It looks like this is the first time you've used the Droplet Plugin!\n" + "\n" + "By default, Droplet collects information to identify how the programming modes are being used. This information is used to improve the plugin and identify what helps people learn more effectively.  \n" + "\n" + "In order to see how use connects to student learning, we are requesting student identification information. This will also be used to connect student records for the purposes of assignment of extra credit for study participation and connection to other study information (such as surveys and assessments).  If you are willing to participate in the study, please enter your Gatorlink account and UFID below.");
        blurb.setEditable(false);

        JLabel glLabel = new JLabel("Gatorlink");
        glLabel.setHorizontalAlignment(JLabel.LEFT);
        glLabel.setVerticalAlignment(JLabel.CENTER);

        JLabel ufLabel = new JLabel("UFID");
        ufLabel.setHorizontalAlignment(JLabel.LEFT);
        ufLabel.setVerticalAlignment(JLabel.CENTER);

        permission = new JCheckBox("Droplet may send my log information for research purposes.");
        gatorlink = new JTextField();
        ufid = new JTextField();

        final Spacer spacer1 = new Spacer();
        final Spacer spacer2 = new Spacer();

        DropletPluginState settings = DropletPluginState.getInstance();
        permission.setSelected(settings.canLog);
        gatorlink.setText((settings.gatorlink == null) ? "" : settings.gatorlink);
        ufid.setText((settings.ufid == null) ? "" : settings.ufid);

        panel.add(spacer1, new GridConstraints(0, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        panel.add(spacer2, new GridConstraints(1, 0, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        panel.add(blurb, new GridConstraints(1, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        panel.add(glLabel, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel.add(gatorlink, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        panel.add(permission, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel.add(ufLabel, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel.add(ufid, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));

        return panel;
    }
}

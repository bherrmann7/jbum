package se.datadosen.component;

import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 * A ControlPanel is a JPanel which has "RiverLayout" as default layout manager.
 * This is the preferred panel for making user interfaces in a simple way.
 *
 * @author David Ekholm
 * @version 1.0
 * @see RiverLayout
 */
@SuppressWarnings("serial")
public class ControlPanel extends JPanel implements se.datadosen.util.JComponentHolder {

    /**
     * Create a plain ControlPanel
     */
    public ControlPanel() {
        super(new RiverLayout());
    }

    /**
     * Create a control panel framed with a titled border
     */
    public ControlPanel(String title) {
        this();
        setTitle(title);
    }

    public void setTitle(String title) {
        setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), title));
    }
}
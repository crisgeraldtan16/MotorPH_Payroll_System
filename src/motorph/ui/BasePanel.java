package motorph.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/*
 * This abstract class serves as a base panel for other UI panels.
 * It provides common colors, layout, and reusable UI helper methods.
 */
public abstract class BasePanel extends JPanel {

    // These are shared color constants used for a consistent UI design
    protected static final Color BG = new Color(245, 247, 252);
    protected static final Color CARD_BG = Color.WHITE;
    protected static final Color BORDER = new Color(225, 230, 240);
    protected static final Color TEXT = new Color(28, 35, 50);
    protected static final Color MUTED = new Color(110, 120, 140);

    /*
     * This constructor sets the default layout, background,
     * and padding for panels that inherit from this class.
     */
    protected BasePanel() {
        setLayout(new BorderLayout(14, 14));
        setBackground(BG);
        setBorder(new EmptyBorder(18, 18, 18, 18));
    }

    /*
     * This helper method creates a card-style panel
     * with consistent background, border, and padding.
     */
    protected JPanel cardWrap() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 14, 14, 14)
        ));
        return card;
    }

    /*
     * This helper method creates a label using the muted text color.
     */
    protected JLabel labelMuted(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(MUTED);
        return l;
    }
}
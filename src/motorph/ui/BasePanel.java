package motorph.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public abstract class BasePanel extends JPanel {

    protected static final Color BG = new Color(245, 247, 252);
    protected static final Color CARD_BG = Color.WHITE;
    protected static final Color BORDER = new Color(225, 230, 240);
    protected static final Color TEXT = new Color(28, 35, 50);
    protected static final Color MUTED = new Color(110, 120, 140);

    protected BasePanel() {
        setLayout(new BorderLayout(14, 14));
        setBackground(BG);
        setBorder(new EmptyBorder(18, 18, 18, 18));
    }

    protected JPanel cardWrap() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 14, 14, 14)
        ));
        return card;
    }

    protected JLabel labelMuted(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(MUTED);
        return l;
    }
}
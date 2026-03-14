package com.hotel.view.dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CardPanel extends JPanel {

    private boolean hover = false;

    public CardPanel(String icon, String title, String value, Color accent) {

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        setPreferredSize(new Dimension(220,130));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content,BoxLayout.Y_AXIS));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji",Font.PLAIN,26));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI",Font.PLAIN,14));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI",Font.BOLD,28));
        valueLabel.setForeground(accent);

        content.add(iconLabel);
        content.add(Box.createVerticalStrut(5));
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(5));
        content.add(valueLabel);

        add(content,BorderLayout.CENTER);

        // Hover effect
        addMouseListener(new MouseAdapter(){

            public void mouseEntered(MouseEvent e){
                hover = true;
                repaint();
            }

            public void mouseExited(MouseEvent e){
                hover = false;
                repaint();
            }

        });

    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);   // gọi trước

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        int yOffset = hover ? -4 : 0;

        // shadow
        g2.setColor(new Color(0,0,0,35));
        g2.fillRoundRect(5,8,width-10,height-10,20,20);

        // background card
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0,yOffset,width-10,height-10,20,20);

        g2.dispose();
    }
}
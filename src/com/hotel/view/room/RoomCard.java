package com.hotel.view.room;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class RoomCard extends JPanel {

    private boolean hover = false;

    private String roomName;
    private String type;
    private RoomStatus status;

    public RoomCard(String roomName,String type,RoomStatus status){

        this.roomName = roomName;
        this.type = type;
        this.status = status;

        setPreferredSize(new Dimension(150,120));
        setOpaque(false);
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content,BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20,10,10,10));

        JLabel icon = new JLabel("🛏");
        icon.setFont(new Font("Segoe UI Emoji",Font.PLAIN,22));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel name = new JLabel(roomName);
        name.setFont(new Font("Segoe UI",Font.BOLD,14));
        name.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel typeLabel = new JLabel(type);
        typeLabel.setFont(new Font("Segoe UI",Font.PLAIN,12));
        typeLabel.setForeground(Color.GRAY);
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(icon);
        content.add(Box.createVerticalStrut(6));
        content.add(name);
        content.add(typeLabel);

        add(content,BorderLayout.CENTER);

        addMouseListener(new MouseAdapter(){

            public void mouseEntered(MouseEvent e){
                hover=true;
                repaint();
            }

            public void mouseExited(MouseEvent e){
                hover=false;
                repaint();
            }

            public void mouseClicked(MouseEvent e){

                RoomDetailDialog dialog =
                        new RoomDetailDialog(
                                SwingUtilities.getWindowAncestor(RoomCard.this),
                                roomName,
                                type,
                                status
                        );

                dialog.setVisible(true);
            }
        });
    }

    private Color getStatusColor(){

        switch(status){

            case AVAILABLE:
                return new Color(232,245,233);

            case OCCUPIED:
                return new Color(255,235,238);

            case CLEANING:
                return new Color(255,248,225);
        }

        return Color.WHITE;
    }

    private Color getStatusIconColor(){

        switch(status){

            case AVAILABLE:
                return new Color(56,142,60);

            case OCCUPIED:
                return new Color(211,47,47);

            case CLEANING:
                return new Color(255,152,0);
        }

        return Color.GRAY;
    }

    protected void paintComponent(Graphics g){

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        int y = hover ? -6 : 0;

        g2.setColor(new Color(0,0,0,35));
        g2.fillRoundRect(6,8,w-12,h-12,18,18);

        g2.setColor(getStatusColor());
        g2.fillRoundRect(0,y,w-12,h-12,18,18);

        g2.setColor(getStatusIconColor());
        g2.fillOval(w-30,10,12,12);

        g2.dispose();

        super.paintComponent(g);
    }

    public RoomStatus getStatus(){
        return status;
    }
}
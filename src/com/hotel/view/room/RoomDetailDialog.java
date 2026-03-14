package com.hotel.view.room;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RoomDetailDialog extends JDialog {

    public RoomDetailDialog(Window owner,String room,String type,RoomStatus status){

        super(owner,"Chi tiết phòng",ModalityType.APPLICATION_MODAL);

        setSize(350,250);
        setLocationRelativeTo(owner);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20,20,20,20));

        JLabel title = new JLabel(room);
        title.setFont(new Font("Segoe UI",Font.BOLD,20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel typeLabel = new JLabel("Loại phòng: "+type);
        JLabel statusLabel = new JLabel("Trạng thái: "+status.getText());

        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton close = new JButton("Đóng");

        close.setAlignmentX(Component.CENTER_ALIGNMENT);
        close.addActionListener(e->dispose());

        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        panel.add(typeLabel);
        panel.add(statusLabel);
        panel.add(Box.createVerticalStrut(30));
        panel.add(close);

        add(panel);
    }
}
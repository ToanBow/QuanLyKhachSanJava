package com.hotel.view.room;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RoomMapPanel extends JPanel {

    private JPanel floorsPanel;
    private JComboBox<String> filterBox;

    private List<RoomCard> allRooms = new ArrayList<>();

    public RoomMapPanel(){

        setLayout(new BorderLayout());
        setBackground(new Color(245,247,250));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15,20,10,20));

        JLabel title = new JLabel("Sơ đồ phòng");
        title.setFont(new Font("Segoe UI",Font.BOLD,22));

        String[] filters = {"Tất cả","Trống","Có khách","Đang dọn"};
        filterBox = new JComboBox<>(filters);

        filterBox.addActionListener(e->applyFilter());

        header.add(title,BorderLayout.WEST);
        header.add(filterBox,BorderLayout.EAST);

        add(header,BorderLayout.NORTH);

        floorsPanel = new JPanel();
        floorsPanel.setLayout(new BoxLayout(floorsPanel,BoxLayout.Y_AXIS));
        floorsPanel.setBackground(new Color(245,247,250));
        floorsPanel.setBorder(new EmptyBorder(10,20,20,20));

        floorsPanel.add(createFloor("Tầng 1",101));
        floorsPanel.add(createFloor("Tầng 2",201));
        floorsPanel.add(createFloor("Tầng 3",301));

        JScrollPane scroll = new JScrollPane(floorsPanel);
        scroll.setBorder(null);

        add(scroll,BorderLayout.CENTER);
    }

    private JPanel createFloor(String floorName,int startRoom){

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(10,0,25,0));

        JLabel label = new JLabel(floorName);
        label.setFont(new Font("Segoe UI",Font.BOLD,18));

        container.add(label,BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(1,5,20,20));
        grid.setOpaque(false);

        for(int i=0;i<5;i++){

            int roomNum = startRoom + i;

            RoomStatus status = randomStatus();

            RoomCard card = new RoomCard(
                    "Phòng "+roomNum,
                    "Deluxe",
                    status
            );

            allRooms.add(card);

            grid.add(card);
        }

        container.add(grid,BorderLayout.CENTER);

        return container;
    }

    private RoomStatus randomStatus(){

        RoomStatus[] arr = RoomStatus.values();

        return arr[(int)(Math.random()*arr.length)];
    }

    private void applyFilter(){

        String selected = (String) filterBox.getSelectedItem();

        for(RoomCard card : allRooms){

            if(selected.equals("Tất cả")){

                card.setVisible(true);
                continue;
            }

            if(card.getStatus().getText().equals(selected)){
                card.setVisible(true);
            }else{
                card.setVisible(false);
            }
        }

        revalidate();
        repaint();
    }

    
}
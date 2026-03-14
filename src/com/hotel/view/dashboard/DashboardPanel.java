package com.hotel.view.dashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private final Color BG_COLOR = new Color(245,247,250);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color ACCENT_COLOR = new Color(13,71,161);

    public DashboardPanel(){

        setLayout(new BorderLayout());
        setBackground(BG_COLOR);

        add(createHeader(),BorderLayout.NORTH);
        add(createCenter(),BorderLayout.CENTER);
    }

    private JPanel createHeader(){

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(15,20,15,20));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI",Font.BOLD,20));

        panel.add(title,BorderLayout.WEST);

        return panel;
    }

    private JPanel createCenter(){

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(30,30,30,30));

        panel.add(createCards(),BorderLayout.NORTH);
        // panel.add(createChart(),BorderLayout.CENTER);

        return panel;
    }

    // Cards
   private JPanel createCards(){

    JPanel panel = new JPanel(new GridLayout(1,4,30,30));
    panel.setOpaque(false);

    panel.add(new CardPanel("🛏","Phòng trống","25",new Color(46,125,50)));
    panel.add(new CardPanel("👥","Khách đang ở","42",new Color(13,71,161)));
    panel.add(new CardPanel("📄","Hóa đơn hôm nay","18",new Color(251,192,45)));
    panel.add(new CardPanel("💰","Doanh thu","25M",new Color(198,40,40)));

    return panel;
}

    private JPanel createCard(String title,String value){

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(new EmptyBorder(20,20,20,20));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI",Font.PLAIN,14));

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI",Font.BOLD,26));
        v.setForeground(ACCENT_COLOR);

        card.add(t,BorderLayout.NORTH);
        card.add(v,BorderLayout.CENTER);

        return card;
    }

    // Chart
    // private JPanel createChart(){

    //     return new ChartPanel();
    // }

}
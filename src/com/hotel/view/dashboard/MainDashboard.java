package com.hotel.view.dashboard;

import com.hotel.view.room.RoomMapPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainDashboard extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Đồng bộ với LoginFrame
    private final Color PRIMARY_COLOR = new Color(26,35,126);
    private final Color ACCENT_COLOR = new Color(13,71,161);
    private final Color BG_COLOR = new Color(245,247,250);

    private final Font MENU_FONT = new Font("Segoe UI",Font.PLAIN,15);

    public MainDashboard(){

        setTitle("Hotel Management System");
        setSize(1200,700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createSidebar(),BorderLayout.WEST);
        add(createContentPanel(),BorderLayout.CENTER);
    }

    // SIDEBAR
    private JPanel createSidebar(){

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar,BoxLayout.Y_AXIS));
        sidebar.setBackground(PRIMARY_COLOR);
        sidebar.setPreferredSize(new Dimension(220,getHeight()));

        sidebar.add(createLogo());
        sidebar.add(Box.createVerticalStrut(20));

        sidebar.add(createMenuItem("Dashboard","DASHBOARD"));
        sidebar.add(createMenuItem("Sơ đồ phòng","ROOM"));
        sidebar.add(createMenuItem("Khách hàng","CUSTOMER"));
        sidebar.add(createMenuItem("Hóa đơn","INVOICE"));
        sidebar.add(createMenuItem("Cài đặt","SETTING"));

        return sidebar;
    }

    private JPanel createLogo(){

        JPanel panel = new JPanel();
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(new EmptyBorder(30,10,30,10));

        JLabel label = new JLabel("KHÁCH SẠN ÁNH TRĂNG");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI",Font.BOLD,18));

        panel.add(label);

        return panel;
    }

    private JPanel createMenuItem(String text,String card){

        JPanel panel = new JPanel(new BorderLayout());
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE,45));
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(new EmptyBorder(10,20,10,10));

        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(MENU_FONT);

        panel.add(label,BorderLayout.WEST);

        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover
        panel.addMouseListener(new java.awt.event.MouseAdapter(){

            public void mouseEntered(java.awt.event.MouseEvent evt){
                panel.setBackground(ACCENT_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt){
                panel.setBackground(PRIMARY_COLOR);
            }

            public void mouseClicked(java.awt.event.MouseEvent evt){
                cardLayout.show(contentPanel,card);
            }

        });

        return panel;
    }

    // CONTENT PANEL
    private JPanel createContentPanel(){

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_COLOR);

        contentPanel.add(new DashboardPanel(),"DASHBOARD");
        contentPanel.add(new RoomMapPanel(),"ROOM");
        contentPanel.add(new JPanel(),"CUSTOMER");
        contentPanel.add(new JPanel(),"INVOICE");
        contentPanel.add(new JPanel(),"SETTING");

        return contentPanel;
    }

    public static void main(String[] args){

        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new MainDashboard().setVisible(true);
        });
    }
}
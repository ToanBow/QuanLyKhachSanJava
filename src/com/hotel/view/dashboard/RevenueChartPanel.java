// package com.hotel.view.dashboard;

// import javax.swing.*;
// import java.awt.*;

// public class RevenueChartPanel extends JPanel {

//     int[] revenue = {12,20,15,30,22,18,40,28,35,32,20,25};

//     String[] months = {
//             "Jan","Feb","Mar","Apr","May","Jun",
//             "Jul","Aug","Sep","Oct","Nov","Dec"
//     };

//     protected void paintComponent(Graphics g){

//         super.paintComponent(g);

//         Graphics2D g2 = (Graphics2D) g;

//         int width = getWidth();
//         int height = getHeight();

//         int barWidth = width / revenue.length;

//         for(int i=0;i<revenue.length;i++){

//             int barHeight = revenue[i]*5;

//             int x = i*barWidth + 20;
//             int y = height - barHeight - 30;

//             g2.setColor(new Color(13,71,161));
//             g2.fillRect(x,y,barWidth-25,barHeight);

//             g2.setColor(Color.BLACK);
//             g2.drawString(months[i],x+5,height-10);

//         }

//     }

// }
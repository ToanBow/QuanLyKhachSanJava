// package com.hotel.view.dashboard;

// import javax.swing.*;
// import java.awt.*;

// public class ChartPanel extends JPanel {

//     int[] data = {10,25,18,30,22,40,28};

//     public ChartPanel(){
//         setPreferredSize(new Dimension(500,300));
//         setBackground(Color.WHITE);
//     }

//     protected void paintComponent(Graphics g){

//         super.paintComponent(g);

//         Graphics2D g2 = (Graphics2D) g;

//         int width = getWidth();
//         int height = getHeight();

//         int barWidth = width/data.length;

//         for(int i=0;i<data.length;i++){

//             int barHeight = data[i]*5;

//             int x = i*barWidth + 20;
//             int y = height - barHeight - 30;

//             g2.setColor(new Color(13,71,161));
//             g2.fillRect(x,y,barWidth-40,barHeight);
//         }

//     }

// }
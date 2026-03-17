package com.hotel.view.dashboard;

import javax.swing.*;
import java.awt.*;

public class RevenueChartPanel extends JPanel {

    private double[] revenueData;
    private String[] monthLabels;

    public RevenueChartPanel(double[] revenueData, String[] monthLabels) {
        this.revenueData = revenueData;
        this.monthLabels = monthLabels;
        setOpaque(false);
        setPreferredSize(new Dimension(600, 300));
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int padding = 40;
        int barWidth = (width - padding * 2) / revenueData.length;

        // Tìm doanh thu lớn nhất để scale chiều cao cột
        double maxRev = 0;
        for (double v : revenueData) {
            if (v > maxRev) maxRev = v;
        }
        if (maxRev == 0) maxRev = 1; 

        // Vẽ các cột
        for(int i = 0; i < revenueData.length; i++){
            int barHeight = (int) ((revenueData[i] / maxRev) * (height - padding * 2));
            int x = padding + i * barWidth + (barWidth / 4); 
            int y = height - padding - barHeight;

            // Màu cột
            g2.setColor(new Color(13, 71, 161)); 
            g2.fillRoundRect(x, y, barWidth / 2, barHeight, 10, 10);

            // Chữ (Tháng)
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.drawString(monthLabels[i], x, height - padding / 2);
        }
    }
}
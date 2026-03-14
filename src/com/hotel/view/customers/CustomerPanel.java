package com.hotel.view.customers;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CustomerPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public CustomerPanel(){

        setLayout(new BorderLayout());

        add(createHeader(),BorderLayout.NORTH);
        add(createTable(),BorderLayout.CENTER);
        add(createButtons(),BorderLayout.SOUTH);
    }

    private JPanel createHeader(){

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10,20,10,20));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Quản lý khách hàng");
        title.setFont(new Font("Segoe UI",Font.BOLD,20));

        JTextField search = new JTextField();
        search.setPreferredSize(new Dimension(200,30));

        panel.add(title,BorderLayout.WEST);
        panel.add(search,BorderLayout.EAST);

        return panel;
    }

    private JScrollPane createTable(){

        String[] columns = {"ID","Tên","SĐT","CMND","Email"};

        model = new DefaultTableModel(columns,0);

        table = new JTable(model);

        return new JScrollPane(table);
    }

    private JPanel createButtons(){

        JPanel panel = new JPanel();

        JButton add = new JButton("Thêm");
        JButton edit = new JButton("Sửa");
        JButton delete = new JButton("Xóa");

        panel.add(add);
        panel.add(edit);
        panel.add(delete);

        add.addActionListener(e -> openForm(null));

        edit.addActionListener(e -> {

            int row = table.getSelectedRow();

            if(row==-1) return;

            String name = (String) model.getValueAt(row,1);

            openForm(name);

        });

        delete.addActionListener(e -> {

            int row = table.getSelectedRow();

            if(row!=-1) model.removeRow(row);

        });

        return panel;
    }

    private void openForm(String name){

        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField idField = new JTextField();
        JTextField emailField = new JTextField();

        Object[] fields = {
                "Tên",nameField,
                "SĐT",phoneField,
                "CMND",idField,
                "Email",emailField
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Thông tin khách",
                JOptionPane.OK_CANCEL_OPTION
        );

        if(option==JOptionPane.OK_OPTION){

            model.addRow(new Object[]{
                    model.getRowCount()+1,
                    nameField.getText(),
                    phoneField.getText(),
                    idField.getText(),
                    emailField.getText()
            });

        }

    }

}
package com.hotel.service.crm;

import com.hotel.dao.IInvoiceDAO;
import com.hotel.dao.impl.InvoiceDAOImpl;
import com.hotel.model.Invoice;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CRMService {

    private final IInvoiceDAO invoiceDAO;
    private final Set<String> blacklist = new HashSet<>();

    public CRMService() {
        this.invoiceDAO = new InvoiceDAOImpl() {
            @Override
            public List<Invoice> findAll() {
                return List.of();
            }
        };
    }

    // Tổng chi tiêu của khách
    public double getTotalSpending(String cccd) {

        List<Invoice> invoices = invoiceDAO.findAll()
                .stream()
                .filter(i -> i.getGuestCccd().equals(cccd))
                .collect(Collectors.toList());

        return invoices.stream()
                .mapToDouble(Invoice::getTotalAmount)
                .sum();
    }

    // Số lần lưu trú
    public int getTotalBookings(String cccd) {
        return (int) invoiceDAO.findAll()
                .stream()
                .filter(i -> i.getGuestCccd().equals(cccd))
                .count();
    }

    // Phân hạng thành viên
    public String getMemberLevel(String cccd) {

        int bookings = getTotalBookings(cccd);

        if (bookings >= 20) return "DIAMOND";
        if (bookings >= 10) return "GOLD";
        return "SILVER";
    }

    // Blacklist
    public void addToBlacklist(String cccd) {
        blacklist.add(cccd);
    }

    public boolean isBlacklisted(String cccd) {
        return blacklist.contains(cccd);
    }
}
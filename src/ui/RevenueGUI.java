package ui;

import dao.OrderDAO;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class RevenueGUI extends JFrame
{
    private final Map<Integer, JTextField> cashFields = new LinkedHashMap<>();
    private final int[] denominations = {50000, 10000, 5000, 1000, 500, 100, 50, 10};

    public RevenueGUI()
    {
        setTitle("시재 확인");
        setSize(400, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel header = new JLabel("현금 시재 입력", SwingConstants.CENTER);
        header.setFont(new Font("Dialog", Font.BOLD, 18));
        add(header, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(denominations.length, 2, 10, 10));
        for (int denom : denominations)
        {
            JLabel label = new JLabel(denom + "원:");
            JTextField field = new JTextField("0");
            cashFields.put(denom, field);
            centerPanel.add(label);
            centerPanel.add(field);
        }
        add(centerPanel, BorderLayout.CENTER);

        JButton checkButton = new JButton("시재 확인");
        JButton backButton = new JButton("뒤로가기");

        JTextArea resultArea = new JTextArea(4, 30);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        checkButton.addActionListener(e -> {
            int actualTotal = calculateActualCash();
            OrderDAO dao = new OrderDAO();
            int systemTotal = dao.getTotalRevenue();

            int difference = actualTotal - systemTotal;

            StringBuilder result = new StringBuilder();
            result.append("실제 입력 금액: ").append(actualTotal).append("원\n");
            result.append("시스템 보유 금액: ").append(systemTotal).append("원\n");

            if (difference == 0) {
                result.append("차액 없음 (정상)");
            } else {
                result.append("차액: ").append(difference).append("원 (");
                result.append(difference > 0 ? "초과" : "부족").append(")");
            }

            resultArea.setText(result.toString());
        });

        backButton.addActionListener(e -> {
            dispose();
            new AdminGUI();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        buttonPanel.add(checkButton);
        buttonPanel.add(backButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private int calculateActualCash()
    {
        int total = 0;
        for (int denom : denominations)
        {
            try {
                int count = Integer.parseInt(cashFields.get(denom).getText().trim());
                total += denom * count;
            } catch (NumberFormatException e) {
                // 무시
            }
        }
        return total;
    }

    public static void main(String[] args)
    {
        new RevenueGUI();
    }
}

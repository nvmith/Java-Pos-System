
package ui;

import dao.DiscountDAO;
import model.Item;
import service.ItemService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Vector;
import java.util.Map;

public class DiscountGUI extends JFrame {

    private final DiscountDAO dao = new DiscountDAO();
    private final ItemService itemService = new ItemService();

    private JComboBox<String> discountBox;
    private JTextField barcodeField;
    private JTable discountTable;
    private DefaultTableModel tableModel;

    public DiscountGUI() {
        setTitle("할인 행사 관리");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        // ===== 입력 패널 =====
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        discountBox = new JComboBox<>(new String[]{"1+1", "2+1"});
        barcodeField = new JTextField(10);

        JButton registerButton = new JButton("등록");
        registerButton.addActionListener(e -> {
            String barcode = barcodeField.getText();
            String discount = (String) discountBox.getSelectedItem();

            if (barcode.isEmpty()) {
                JOptionPane.showMessageDialog(this, "바코드를 입력하세요.");
                return;
            }

            if (dao.insertDiscount(barcode, discount)) {
                JOptionPane.showMessageDialog(this, "할인 등록 완료!");
                barcodeField.setText("");
                discountBox.setSelectedIndex(0);
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "등록 실패: 바코드를 확인하세요.");
            }
        });

        JButton deleteButton = new JButton("삭제");
        deleteButton.addActionListener(e -> {
            String barcode = barcodeField.getText();

            if (barcode.isEmpty()) {
                JOptionPane.showMessageDialog(this, "바코드를 입력하세요.");
                return;
            }

            if (dao.deleteByBarcode(barcode)) {
                JOptionPane.showMessageDialog(this, "삭제 성공!");
                barcodeField.setText("");
                discountBox.setSelectedIndex(0);
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "삭제 실패: 바코드를 확인하세요.");
            }
        });

        JButton backButton = new JButton("뒤로가기");
        backButton.addActionListener(e -> {
            dispose();
            new AdminGUI();
        });

        inputPanel.add(new JLabel("바코드:"));
        inputPanel.add(barcodeField);
        inputPanel.add(new JLabel("할인유형:"));
        inputPanel.add(discountBox);
        inputPanel.add(registerButton);
        inputPanel.add(deleteButton);
        inputPanel.add(backButton);

        // ===== 테이블 =====
        String[] columns = {"상품명", "할인유형"};
        tableModel = new DefaultTableModel(columns, 0);
        discountTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(discountTable);

        discountTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = discountTable.getSelectedRow();
                String fullText = tableModel.getValueAt(row, 0).toString();  // "이름 (바코드)"

                // 괄호 안에서 바코드만 추출
                int start = fullText.indexOf("(");
                int end = fullText.indexOf(")");
                if (start != -1 && end != -1) {
                    String barcode = fullText.substring(start + 1, end);
                    barcodeField.setText(barcode);
                }
            }
        });

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        refreshTable();
        setVisible(true);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        Map<String, String> map = dao.getAllDiscounts();  // 수정된 부분

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String barcode = entry.getKey();
            String discount = entry.getValue();
            String menu = itemService.getMenuByBarcode(barcode);

            tableModel.addRow(new Object[]{menu + " (" + barcode + ")", discount});
        }
    }

    public static void main(String[] args) {
        new DiscountGUI();
    }
}

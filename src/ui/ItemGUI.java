package ui;

import model.Item;
import service.ItemService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ItemGUI extends JFrame
{
    private final ItemService service = new ItemService();

    private JTextField nameField;
    private JTextField priceField;
    private JTextField barcodeField;
    private JTextField expirationField;

    private JButton insertButton, updateButton, deleteButton, clearButton;
    private JTable itemTable;
    private DefaultTableModel tableModel;

    public ItemGUI() {
        setTitle("상품 관리");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // 왼쪽 패널
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(400, 0));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        nameField = new JTextField(15);
        priceField = new JTextField(15);
        barcodeField = new JTextField(15);
        expirationField = new JTextField(15);

        leftPanel.add(makeField("이름", nameField));
        leftPanel.add(makeField("가격", priceField));
        leftPanel.add(makeField("바코드", barcodeField));
        leftPanel.add(makeField("유통기한", expirationField));

        // 버튼들 (등록, 수정, 삭제, 초기화)
        insertButton = new JButton("등록");
        updateButton = new JButton("수정");
        deleteButton = new JButton("삭제");
        clearButton = new JButton("초기화");

        insertButton.addActionListener(e -> insertButtonClicked());
        updateButton.addActionListener(e -> updateButtonClicked());
        deleteButton.addActionListener(e -> deleteButtonClicked());
        clearButton.addActionListener(e -> clearFields());

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        buttonPanel.add(insertButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(buttonPanel);

        // 뒤로가기 버튼
        JButton backButton = new JButton("뒤로가기");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> {
            dispose();
            new AdminGUI();
        });
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(backButton);

        // 오른쪽 테이블
        String[] columns = {"이름", "가격", "바코드", "유통기한"};
        tableModel = new DefaultTableModel(columns, 0);
        itemTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(itemTable);

        itemTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = itemTable.getSelectedRow();
                nameField.setText(tableModel.getValueAt(row, 0).toString());
                priceField.setText(tableModel.getValueAt(row, 1).toString());
                barcodeField.setText(tableModel.getValueAt(row, 2).toString());
                expirationField.setText(tableModel.getValueAt(row, 3).toString());
            }
        });

        add(leftPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);

        refreshItemList();
        setVisible(true);
    }

    private JPanel makeField(String labelText, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(80, 30));
        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return panel;
    }


    private void insertButtonClicked()
    {
        try {
            String expirationText = expirationField.getText();
            if (!expirationText.matches("\\d{8}")) {
                JOptionPane.showMessageDialog(this, "유통기한은 8자리 숫자(YYYYMMDD)로 입력해야 합니다.");
                return;
            }

            Item item = new Item(
                    nameField.getText(),
                    Integer.parseInt(priceField.getText()),
                    barcodeField.getText(),
                    Integer.parseInt(expirationField.getText())
            );

            if (!service.addItem(item)) {
                JOptionPane.showMessageDialog(this, "중복된 바코드 또는 이름입니다.");
                return;
            }

            JOptionPane.showMessageDialog(this, "등록 성공!");
            clearFields();
            refreshItemList();
        }
        catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "가격과 유통기한은 숫자로 입력하세요.");
        }
    }

    private void updateButtonClicked()
    {
        try {
            Item item = new Item(
                    nameField.getText(),
                    Integer.parseInt(priceField.getText()),
                    barcodeField.getText(),
                    Integer.parseInt(expirationField.getText())
            );

            if (service.updateItem(item)) {
                JOptionPane.showMessageDialog(this, "수정 성공!");
                clearFields();
                refreshItemList();
            } else {
                JOptionPane.showMessageDialog(this, "해당 바코드를 가진 상품이 없습니다.");
            }
        }
        catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "가격과 유통기한은 숫자로 입력하세요.");
        }
    }

    private void deleteButtonClicked()
    {
        String barcode = barcodeField.getText();
        if (barcode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "삭제할 바코드를 입력하세요.");
            return;
        }

        if (service.deleteItem(barcode)) {
            JOptionPane.showMessageDialog(this, "삭제 성공!");
            clearFields();
            refreshItemList();
        } else {
            JOptionPane.showMessageDialog(this, "삭제 실패: 바코드를 확인하세요.");
        }
    }

    private void clearFields()
    {
        nameField.setText("");
        priceField.setText("");
        barcodeField.setText("");
        expirationField.setText("");
    }

    private void refreshItemList()
    {
        List<Item> items = service.getAllItems();
        tableModel.setRowCount(0); // 기존 데이터 지우기

        for (Item item : items) {
            Object[] row = {
                    item.getMenu(),
                    item.getPrice(),
                    item.getBarcode(),
                    item.getDate()
            };
            tableModel.addRow(row);
        }
    }

    public static void main(String[] args)
    {
        new ItemGUI();
    }
}

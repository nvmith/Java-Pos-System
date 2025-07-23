
package ui;

import model.Item;
import service.ItemService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Vector;

public class OrderGUI extends JFrame {

    private final ItemService service = new ItemService();

    private JTable table;
    private DefaultTableModel model;
    private Vector<String> header = new Vector<>(List.of("메뉴", "가격", "개수"));
    private Vector<Vector<String>> data = new Vector<>();

    private JTextField totalField = new JTextField(10);
    private JButton[] actionButtons;
    private JButton[] menuButtons;

    private int totalPrice = 0;

    public OrderGUI() {
        setTitle("POS: 주문");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initTable();
        initMenuPanel();
        initBottomPanel();

        setVisible(true);
    }

    private void initTable() {
        model = new DefaultTableModel(data, header);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void initMenuPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.setBorder(BorderFactory.createTitledBorder("메뉴"));

        List<Item> itemList = service.getAllItems();
        menuButtons = new JButton[itemList.size()];

        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            JButton btn = new JButton(item.getMenu() + ":" + item.getPrice());
            btn.addActionListener(this::handleMenuClick);
            menuButtons[i] = btn;
            panel.add(btn);
        }

        add(panel, BorderLayout.EAST);
    }

    private void initBottomPanel() {
        JPanel panel = new JPanel();

        String[] actions = {"주문", "취소", "계산"};
        actionButtons = new JButton[actions.length];

        for (int i = 0; i < actions.length; i++) {
            actionButtons[i] = new JButton(actions[i]);
            actionButtons[i].addActionListener(this::handleActionClick);
            panel.add(actionButtons[i]);
        }

        panel.add(new JLabel("합계"));
        panel.add(totalField);
        panel.add(new JLabel("원"));

        JButton backButton = new JButton("뒤로가기");
        backButton.addActionListener(e -> {
            dispose();         // 현재 주문창 닫기
            new MainGUI();     // 메인 메뉴 다시 열기 (MainGUI 클래스 필요)
        });
        panel.add(backButton);

        add(panel, BorderLayout.SOUTH);
    }

    private void reset() {
        data.clear();
        model.setDataVector(data, header);
        totalField.setText("");
    }

    private void handleActionClick(ActionEvent e) {
        Object src = e.getSource();

        if (src == actionButtons[0]) {  // 주문
            totalPrice = 0;
            for (Vector<String> row : data) {
                int price = Integer.parseInt(row.get(1));
                int count = Integer.parseInt(row.get(2));  // 수량도 가져와야 함
                totalPrice += price * count;
            }
            totalField.setText(String.valueOf(totalPrice));
        } else if (src == actionButtons[1]) {  // 취소
            reset();
        } else if (src == actionButtons[2]) {  // 계산
            String cashStr = JOptionPane.showInputDialog("현금을 입력하세요:");
            try {
                int cash = Integer.parseInt(cashStr);
                int change = cash - totalPrice;
                if (change >= 0) {
                    JOptionPane.showMessageDialog(this, "거스름돈: " + change + "원");
                    reset();
                } else {
                    JOptionPane.showMessageDialog(this, (-change) + "원이 부족합니다.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "숫자를 정확히 입력하세요.");
            }
        }
    }

    private void handleMenuClick(ActionEvent e) {
        JButton src = (JButton) e.getSource();
        String[] parts = src.getText().split(":");
        String menuName = parts[0];
        String price = parts[1];

        boolean found = false;
        for (Vector<String> row : data) {
            if (row.get(0).equals(menuName)) {
                int count = Integer.parseInt(row.get(2));
                row.set(2, String.valueOf(count + 1));
                found = true;
                break;
            }
        }

        if (!found) {
            Vector<String> newRow = new Vector<>();
            newRow.add(menuName);
            newRow.add(price);
            newRow.add("1");
            data.add(newRow);
        }

        model.setDataVector(data, header);
    }

    public static void main(String[] args) {
        new OrderGUI();
    }
}

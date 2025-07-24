
package ui;

import model.Item;
import service.ItemService;
import dao.OrderDAO;
import dao.DiscountDAO;

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
            JButton btn = new JButton(item.getMenu());  // 버튼엔 메뉴명만
            btn.addActionListener(this::handleMenuClick);
            menuButtons[i] = btn;
            panel.add(btn);
        }

        add(panel, BorderLayout.EAST);
    }

    private void initBottomPanel() {
        JPanel panel = new JPanel();

        String[] actions = {"취소", "계산"};
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
            dispose();
            new MainGUI();
        });
        panel.add(backButton);

        add(panel, BorderLayout.SOUTH);
    }

    private void reset() {
        data.clear();
        model.setDataVector(data, header);
        totalField.setText("");
    }

    private void updateTotal() {
        totalPrice = 0;
        DiscountDAO discountDAO = new DiscountDAO();

        for (Vector<String> row : data) {
            String menu = row.get(0).split(" ")[0];
            int price = Integer.parseInt(row.get(1));
            int count = Integer.parseInt(row.get(2));
            String barcode = service.getBarcodeByMenu(menu);
            String discount = discountDAO.getDiscountType(barcode);

            if (discount.equals("1+1") && count >= 2) {
                int payCount = count - (count / 2);
                totalPrice += payCount * price;
            } else if (discount.equals("2+1") && count >= 3) {
                int payCount = count - (count / 3);
                totalPrice += payCount * price;
            } else {
                totalPrice += count * price;
            }
        }
        totalField.setText(String.valueOf(totalPrice));
    }

    private void handleActionClick(ActionEvent e) {
        Object src = e.getSource();

        if (src == actionButtons[0]) {  // 취소
            reset();
        } else if (src == actionButtons[1]) {  // 계산
            // 1. 할인 조건 누락 체크
            StringBuilder warning = new StringBuilder();
            DiscountDAO discountDAO = new DiscountDAO();

            for (Vector<String> row : data) {
                String menu = row.get(0).split(" ")[0];
                int count = Integer.parseInt(row.get(2));
                String barcode = service.getBarcodeByMenu(menu);
                String discount = discountDAO.getDiscountType(barcode);

                if (discount.equals("1+1") && count == 1) {
                    warning.append("- ").append(menu).append(" (1개, 1+1)").append("\n");
                } else if (discount.equals("2+1") && count == 2) {
                    warning.append("- ").append(menu).append(" (2개, 2+1)").append("\n");
                }
            }

            // 2. 경고 메시지가 있다면 팝업 띄우고 선택지 제공
            if (warning.length() > 0) {
                int option = JOptionPane.showOptionDialog(
                        this,
                        "다음 상품은 할인 조건을 충족하지 않습니다:\n" + warning + "\n추가 등록 후 결제를 진행하시겠습니까?",
                        "할인 조건 누락",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        new Object[]{"추가등록", "결제진행"},
                        "추가등록"
                );

                if (option != 1) return;  // '추가등록' 선택 시 계산 중단
            }

            // 3. 정상 결제 진행
            String cashStr = JOptionPane.showInputDialog("현금을 입력하세요:");
            try {
                int cash = Integer.parseInt(cashStr);
                updateTotal();
                int change = cash - totalPrice;

                if (change >= 0) {
                    OrderDAO dao = new OrderDAO();
                    for (Vector<String> row : data) {
                        String menuName = row.get(0).split(" ")[0];
                        int price = Integer.parseInt(row.get(1));
                        int quantity = Integer.parseInt(row.get(2));
                        dao.insertOrder(menuName, price, quantity);
                    }

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
        String menuName = src.getText();
        Item item = service.getItemByMenu(menuName);
        int price = item.getPrice();
        String barcode = item.getBarcode();

        DiscountDAO discountDAO = new DiscountDAO();
        String discount = discountDAO.getDiscountType(barcode);
        String menuLabel = menuName;
        if (!discount.equals("NONE")) {
            menuLabel += " [" + discount + "]";
        }

        boolean found = false;
        for (Vector<String> row : data) {
            if (row.get(0).equals(menuLabel)) {
                int count = Integer.parseInt(row.get(2)) + 1;
                row.set(2, String.valueOf(count));
                found = true;
                break;
            }
        }

        if (!found) {
            Vector<String> newRow = new Vector<>();
            newRow.add(menuLabel);
            newRow.add(String.valueOf(price));
            newRow.add("1");
            data.add(newRow);
        }

        model.setDataVector(data, header);
        updateTotal();
    }

    public static void main(String[] args) {
        new OrderGUI();
    }
}

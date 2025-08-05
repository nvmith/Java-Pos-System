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
    private Vector<String> header = new Vector<>(List.of("메뉴", "단가", "개수", "합계"));
    private Vector<Vector<String>> data = new Vector<>();

    private JTextField totalField = new JTextField(10);
    private JButton[] actionButtons; // {"전체삭제", "계산"}
    private JButton incBtn, decBtn, delBtn; // +, -, 삭제
    private JButton[] menuButtons;

    private int totalPrice = 0;

    public OrderGUI() {
        setTitle("POS: 주문");
        setSize(900, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initTable();
        initMenuPanel();
        initBottomPanel();

        setVisible(true);
    }

    private void initTable() {
        model = new DefaultTableModel(data, header) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 직접 편집 방지 (버튼으로만 조작)
            }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

        // 수량 조절/삭제 버튼
        incBtn = new JButton("+");
        decBtn = new JButton("-");
        delBtn = new JButton("삭제");

        incBtn.addActionListener(e -> adjustSelectedQuantity(+1));
        decBtn.addActionListener(e -> adjustSelectedQuantity(-1));
        delBtn.addActionListener(e -> deleteSelectedRow());

        panel.add(incBtn);
        panel.add(decBtn);
        panel.add(delBtn);

        // 전체삭제 / 계산
        String[] actions = {"전체삭제", "계산"};
        actionButtons = new JButton[actions.length];

        for (int i = 0; i < actions.length; i++) {
            actionButtons[i] = new JButton(actions[i]);
            actionButtons[i].addActionListener(this::handleActionClick);
            panel.add(actionButtons[i]);
        }

        panel.add(new JLabel("합계"));
        totalField.setEditable(false);
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
        refreshModelKeepSelection(-1, null);
        totalField.setText("");
        totalPrice = 0;
    }

    private void updateTotal() {
        totalPrice = 0;
        DiscountDAO discountDAO = new DiscountDAO();

        int selectedRow = table.getSelectedRow();

        for (Vector<String> row : data) {
            String menu = row.get(0).split(" ")[0];
            int price = Integer.parseInt(row.get(1));
            int count = Integer.parseInt(row.get(2));
            String barcode = service.getBarcodeByMenu(menu);
            String discount = discountDAO.getDiscountType(barcode);

            int payCount;
            if (discount.equals("1+1") && count >= 2) {
                payCount = count - (count / 2);
            } else if (discount.equals("2+1") && count >= 3) {
                payCount = count - (count / 3);
            } else {
                payCount = count;
            }

            int lineTotal = payCount * price;
            row.set(3, String.valueOf(lineTotal));
            totalPrice += lineTotal;
        }

        totalField.setText(String.valueOf(totalPrice));
        model.setDataVector(data, header);

        if (selectedRow != -1 && selectedRow < table.getRowCount()) {
            table.setRowSelectionInterval(selectedRow, selectedRow);
        }
    }

    private void handleActionClick(ActionEvent e) {
        Object src = e.getSource();

        if (src == actionButtons[0]) {  // 전체삭제
            reset();

        } else if (src == actionButtons[1]) {  // 계산
            // 1. 할인 조건 누락 체크
            StringBuilder warning = new StringBuilder();
            DiscountDAO discountDAO = new DiscountDAO();

            for (Vector<String> row : data) {
                String menu = row.get(0).split(" \\[")[0];
                int count = Integer.parseInt(row.get(2));
                String barcode = service.getBarcodeByMenu(menu);
                String discount = discountDAO.getDiscountType(barcode);

                if ("1+1".equals(discount) && count == 1) {
                    warning.append("- ").append(menu).append(" (1개, 1+1)").append("\n");
                } else if ("2+1".equals(discount) && count == 2) {
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
                        String menuName = row.get(0).split(" \\[")[0];
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

    private void adjustSelectedQuantity(int delta) {
        int rowIdx = table.getSelectedRow();
        if (rowIdx < 0) {
            JOptionPane.showMessageDialog(this, "수정할 상품을 테이블에서 선택하세요.");
            return;
        }
        String preferredKey = data.get(rowIdx).get(0); // 현재 선택된 행의 키(메뉴라벨)

        try {
            int count = Integer.parseInt(data.get(rowIdx).get(2)) + delta;
            if (count <= 0) {
                data.remove(rowIdx);
                // 제거된 경우, 같은 인덱스에 남은 행을 다시 선택 시도
                int nextIndex = Math.min(rowIdx, data.size() - 1);
                refreshModelKeepSelection(nextIndex, null);
            } else {
                data.get(rowIdx).set(2, String.valueOf(count));
                // 같은 항목 유지 선택
                refreshModelKeepSelection(rowIdx, preferredKey);
            }
            updateTotal();
            table.requestFocusInWindow();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "수량 값이 올바르지 않습니다.");
        }
    }

    private void deleteSelectedRow() {
        int rowIdx = table.getSelectedRow();
        if (rowIdx < 0) {
            JOptionPane.showMessageDialog(this, "삭제할 상품을 테이블에서 선택하세요.");
            return;
        }
        data.remove(rowIdx);
        int nextIndex = Math.min(rowIdx, data.size() - 1);
        refreshModelKeepSelection(nextIndex, null);
        updateTotal();
        table.requestFocusInWindow();
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
        if (!"NONE".equals(discount)) {
            menuLabel += " [" + discount + "]";
        }

        boolean found = false;
        for (Vector<String> row : data) {
            if (row.get(0).equals(menuLabel)) {
                int count = Integer.parseInt(row.get(2)) + 1;
                row.set(2, String.valueOf(count));
                row.set(3, String.valueOf(count * Integer.parseInt(row.get(1))));
                found = true;
                break;
            }
        }

        if (!found) {
            Vector<String> newRow = new Vector<>();
            newRow.add(menuLabel);                     // 메뉴
            newRow.add(String.valueOf(price));         // 단가
            newRow.add("1");                           // 수량
            newRow.add(String.valueOf(price));         // 합계 (초기값 = 단가)
            data.add(newRow);
        }

        // 방금 추가/증가한 항목을 선택 상태로 유지
        int idx = findRowIndexByLabel(menuLabel);
        refreshModelKeepSelection(idx, menuLabel);

        updateTotal();
        table.requestFocusInWindow();
    }

    // ----- 유틸리티 -----

    // data → model 반영 후, 가능한 경우 선택 유지/복원
    private void refreshModelKeepSelection(int preferredIndex, String preferredKey) {
        model.setDataVector(data, header);

        int rowToSelect = -1;

        // 우선 키(메뉴라벨)로 찾기
        if (preferredKey != null) {
            rowToSelect = findRowIndexByLabel(preferredKey);
        }

        // 키로 못 찾았으면 인덱스로 시도
        if (rowToSelect < 0 && preferredIndex >= 0 && preferredIndex < data.size()) {
            rowToSelect = preferredIndex;
        }

        // 그래도 못 정하면 마지막 행 선택(데이터가 있다면)
        if (rowToSelect < 0 && !data.isEmpty()) {
            rowToSelect = data.size() - 1;
        }

        if (rowToSelect >= 0) {
            try {
                table.setRowSelectionInterval(rowToSelect, rowToSelect);
                table.scrollRectToVisible(table.getCellRect(rowToSelect, 0, true));
            } catch (Exception ignored) {}
        }
    }

    private int findRowIndexByLabel(String label) {
        for (int i = 0; i < data.size(); i++) {
            if (label.equals(data.get(i).get(0))) return i;
        }
        return -1;
    }

    public static void main(String[] args) {
        new OrderGUI();
    }
}

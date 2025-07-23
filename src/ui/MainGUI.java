package ui;

import javax.swing.*;
import java.awt.*;

public class MainGUI extends JFrame {

    public MainGUI() {
        setTitle("POS 시스템");
        setSize(400, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(2, 1, 10, 10));

        JButton orderBtn = new JButton("주문하기");
        JButton adminBtn = new JButton("관리자 화면");

        orderBtn.addActionListener(e -> {
            dispose();  // 현재 창 닫고
            new ui.OrderGUI();  // 주문 창 실행
        });

        adminBtn.addActionListener(e -> {
            JPasswordField pwField = new JPasswordField();
            int option = JOptionPane.showConfirmDialog(
                    this, pwField, "관리자 비밀번호를 입력하세요", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String pw = new String(pwField.getPassword());
                if ("1234".equals(pw)) {
                    dispose();
                    new ItemGUI();
                } else {
                    JOptionPane.showMessageDialog(this, "비밀번호가 틀렸습니다!");
                }
            }
        });

        add(orderBtn);
        add(adminBtn);
        setVisible(true);
    }

    public static void main(String[] args) {
        new MainGUI();
    }
}

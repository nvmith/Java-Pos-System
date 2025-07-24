package ui;

import javax.swing.*;
import java.awt.*;
import dao.OrderDAO;
import dao.DiscountDAO;

public class AdminGUI extends JFrame
{
    public AdminGUI()
    {
        setTitle("관리자 메뉴");
        setSize(500, 120);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        // 메뉴 버튼들
        JButton btnItem = new JButton("물품 관리");
        JButton btnDiscount = new JButton("할인 행사 관리");
        JButton btnRevenue = new JButton("시재 확인");
        JButton btnBack = new JButton("뒤로가기");
        JButton btnReset = new JButton("POS 초기화");

        // 버튼 이벤트
        btnItem.addActionListener(e -> {
            dispose();         // 현재 AdminGUI 닫기
            new ItemGUI();     // 상품 관리 화면 열기
        });

        btnDiscount.addActionListener(e -> {
            dispose();             // AdminGUI 닫기
            new DiscountGUI();     // 할인 행사 화면 열기
        });

        btnRevenue.addActionListener(e -> {
            dispose();             // AdminGUI 닫기
            new RevenueGUI();      // 시재 확인 화면 열기 (팝업이 아닌 새 창)
        });

        btnBack.addActionListener(e -> {
            dispose();         // AdminGUI 닫기
            new MainGUI();     // 메인 화면으로 돌아가기
        });

        btnReset.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                    "모든 주문 내역과 할인 정보를 삭제합니다. 계속하시겠습니까?",
                    "초기화 확인",
                    JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                OrderDAO orderDAO = new OrderDAO();
                DiscountDAO discountDAO = new DiscountDAO();

                boolean orderCleared = orderDAO.clearAll();
                boolean discountCleared = discountDAO.clearAll();

                if (orderCleared && discountCleared) {
                    JOptionPane.showMessageDialog(this, "POS 데이터가 초기화되었습니다.");
                } else {
                    JOptionPane.showMessageDialog(this, "일부 데이터를 초기화하지 못했습니다.");
                }
            }
        });

        // 버튼 배치
        add(btnItem);
        add(btnDiscount);
        add(btnRevenue);
        add(btnReset);
        add(btnBack);

        setVisible(true);
    }
}

# 🧾 Java POS 시스템 (GUI 기반)

Java Swing을 활용하여 구현한 **간단한 포스(Point of Sale) 시스템**입니다.  
상품 주문부터 할인 적용, 매출 조회까지 기본적인 매장 POS 기능을 포함하고 있으며,  
유지보수가 용이하도록 구성했습니다.

---

## ✅ 목표

- Java 기반 GUI 애플리케이션 개발 능력 강화
- 실사용 시나리오 기반의 주문 흐름, 할인 정책, 매출 통계 구현
- DAO/Service/UI 구조 분리

---

## 🔧 개발 환경

- 언어: Java 17+
- GUI: Swing (JFrame, JPanel, etc.)
- DB: MySQL (`posdb`)
- IDE: IntelliJ IDEA

---

## 🧠 개발 의도 및 철학

단순한 입력/출력 프로그램을 넘어,  
**실제 매장에서 사용할 수 있는 POS 시스템의 흐름을 구성**해보고자 기획되었습니다.  
UI와 데이터 처리를 분리하여 **확장성과 유지보수성**을 고려하였고,  
**할인/매출 시스템 등 로직 중심의 기능** 구현을 중심으로 개발했습니다.

---

## 🗂️ 주요 기능

### 🛒 주문/결제 기능

- 주문화면에서 상품 리스트를 확인하고 수량을 조절하여 담기 가능
- 총합 계산 및 실시간 할인 적용 (예: 1+1, 2+1)
- 결제 완료 시 주문 내역 DB 저장

### 🧑‍💼 관리자 기능

- 관리자 전용 비밀번호 입력 시 진입 가능
- 상품 추가/삭제/수정, 할인 설정 가능
- 정산 화면 제공

---

## 🗃️ 폴더 구조

| 폴더 | 역할 |
|------|------|
| `ui/` | Swing 기반 GUI 클래스 모음 (`MainGUI`, `OrderGUI`, `AdminGUI` 등) |
| `model/` | `Item`, `Order`, `Revenue` 등 데이터 모델 |
| `dao/` | DB 연결 및 CRUD 쿼리 담당 |
| `service/` | 비즈니스 로직 처리 (할인 적용, 매출 정산 등) |
| `util/` | 공통 기능 유틸 클래스 |

---

## 💡 주요 기술적 도전 과제

- ✅ 할인 정책 (1+1, 2+1) 적용 시 조건 분기 및 계산 최적화
- ✅ DB 연동 기반의 주문/매출 흐름 구성
- ✅ GUI 간 화면 전환 흐름 정리 및 상태 유지
- ✅ 유지보수 고려한 계층 분리 및 네이밍 정리

---

## 📷 실행 화면

| 상품 관리 화면 | 주문 화면 |
|:--:|:--:|
| <img width="666" height="371" alt="Image" src="https://github.com/user-attachments/assets/95a4910b-1f5f-467a-9320-6ac1c5be7dea" /> | <img width="666" height="333" alt="Image" src="https://github.com/user-attachments/assets/ca118ffb-0f04-4bee-9869-db3e367c72c1" /> |

| 시재 확인 화면 | 할인 행사 관리 화면 |
|:--:|:--:|
| <img width="291" height="408" alt="Image" src="https://github.com/user-attachments/assets/7a1f1218-ea00-43ae-bb02-4dd53f48a996" /> | <img width="441" height="296" alt="Image" src="https://github.com/user-attachments/assets/14bea7bd-4c84-4368-b1de-4b60b6cda63e" /> |

---

## 🚀 실행 방법

1. MySQL에서 `posdb` 데이터베이스 생성 및 테이블 세팅
2. 프로젝트에서 JDBC 연결 설정 확인 (`ItemDBConn`)
3. `MainGUI` 실행

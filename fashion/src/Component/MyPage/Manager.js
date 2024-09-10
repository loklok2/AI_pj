import React, { useState, useEffect } from 'react';
import { Line, Doughnut } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import '../../CSS/Manager.css';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  ArcElement,
  Title,
  Tooltip,
  Legend
);

const Manager = () => {
  const [totalVisits, setTotalVisits] = useState(0);
  const [inquiries, setInquiries] = useState(0);
  const [inquiryReplies, setInquiryReplies] = useState(0);
  const [orderList, setOrderList] = useState([]);
  const [selectedOrders, setSelectedOrders] = useState([]); // 선택된 주문 리스트
  const [chartType, setChartType] = useState('realtime'); // 'realtime' or 'cumulative'

  // 목업 실시간 월별 데이터
  const realTimeData = {
    labels: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월'],
    datasets: [
      {
        label: '매출',
        data: [200, 250, 300, 320, 400, 450, 480, 500, 520],
        fill: false,
        borderColor: 'rgba(75,192,192,1)',
        tension: 0.1,
      },
    ],
  };

  // 목업 누적 월별 데이터
  const cumulativeData = {
    labels: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월'],
    datasets: [
      {
        label: '판매율',
        data: [200, 450, 750, 1070, 1470, 1920, 2400, 2900, 3420],
        fill: false,
        borderColor: 'rgba(255,99,132,1)',
        tension: 0.1,
      },
    ],
  };

  // 목업 카테고리별 판매율 데이터 (도넛 차트)
  const doughnutData = {
    labels: ['캐주얼', '포멀', '스포티', '빈티지', '보헤미안', '미니멀리즘', '스트릿웨어'],
    datasets: [
      {
        label: '카테고리별 판매율',
        data: [300, 500, 100, 200, 400, 250, 333],
        backgroundColor: [
          'rgba(54, 162, 235, 0.5)',
          'rgba(75, 192, 192, 0.5)',
          'rgba(255, 206, 86, 0.5)',
          'rgba(153, 102, 255, 0.5)',
          'rgba(255, 99, 132, 0.5)',
          'rgba(255, 159, 64, 0.5)',
          'rgba(128, 0, 128, 0.5)',
        ],
        borderColor: [
          'rgba(54, 162, 235, 1)',
          'rgba(75, 192, 192, 1)',
          'rgba(255, 206, 86, 1)',
          'rgba(153, 102, 255, 1)',
          'rgba(255, 99, 132, 1)',
          'rgba(255, 159, 64, 1)',
          'rgba(128, 0, 128, 0.5)',
        ],
        borderWidth: 1,
      },
    ],
  };

  const doughnutOptions = {
    plugins: {
      legend: {
        position: 'right', // 레전드를 차트 오른쪽에 배치
        align: 'center', // 가운데 정렬
        labels: {
          boxWidth: 10, // 레전드 상자의 크기
          padding: 20, // 레전드 텍스트와 상자 간격
        },
      },
    },
    maintainAspectRatio: false, // 차트의 비율 유지 설정 끔
  };

  // 배송 상태를 변경할 때 사용하는 함수
  const handleStatusChange = (orderId, newStatus) => {
    setOrderList((prevOrders) =>
      prevOrders.map((order) =>
        order.orderId === orderId ? { ...order, deliveryStatus: newStatus } : order
      )
    );
  };

  // 체크박스 선택 시 실행되는 함수
  const handleCheckboxChange = (orderId) => {
    setSelectedOrders((prevSelected) =>
      prevSelected.includes(orderId)
        ? prevSelected.filter((id) => id !== orderId) // 이미 선택된 주문이면 제거
        : [...prevSelected, orderId] // 새로 선택된 주문이면 추가
    );
  };

  // 삭제 버튼 클릭 시 선택된 주문 삭제
  const handleDeleteClick = () => {
    const updatedOrderList = orderList.filter(order => !selectedOrders.includes(order.orderId));
    setOrderList(updatedOrderList);
    setSelectedOrders([]); // 선택 목록 초기화
  };

  // 저장 버튼을 눌렀을 때 실행되는 함수
  const handleSaveClick = () => {
    console.log('저장된 주문 상태:', orderList);
    alert('저장되었습니다.');
  };

  useEffect(() => {
    // 목업 데이터로 상태값 설정
    setTotalVisits(3420); // 목업으로 총 방문자 수 설정
    setOrderList([
      { orderId: '76015-1', userId: 'user001', productInfo: '와일드 퀘스트 바람막이 자켓 with 팀킷 리플 다비셜', totalPrice: '29,900원', orderDate: '2024. 9. 10', deliveryStatus: '배송 중' },
      { orderId: '76015-2', userId: 'user002', productInfo: '블랙 헬릭스 셔츠', totalPrice: '39,900원', orderDate: '2024. 9. 10', deliveryStatus: '배송 중' },
      { orderId: '76015-3', userId: 'user003', productInfo: '바디크림 헬리티 디저트', totalPrice: '39,800원', orderDate: '2024. 9. 10', deliveryStatus: '배송 중' },
      { orderId: '76015-4', userId: 'user004', productInfo: '플로팅 로트리 스마트', totalPrice: '37,900원', orderDate: '2024. 9. 10', deliveryStatus: '배송 중' },
      { orderId: '76015-5', userId: 'user005', productInfo: '다이렉트 스텝 커버 레드 플로', totalPrice: '52,900원', orderDate: '2024. 9. 10', deliveryStatus: '배송 중' },
    ]);
  }, []);

  return (
    <div className="manager-dashboard-container">
      <h1 className="manager-dashboard-title">DashBoard</h1>
      <p className="manager-dashboard-welcome-text">이 페이지는 관리자만 이용 가능합니다.</p>

      <div className="manager-dashboard-stats">
        <div className="manager-dashboard-stat-box">
          <p>총 회원수</p>
          <p>{totalVisits}</p>
        </div>
        <div className="manager-dashboard-stat-box">
          <p>총 방문자</p>
          <p>{totalVisits}</p>
        </div>
        <div className="manager-dashboard-stat-box">
          <p>고객 문의</p>
          <p>{inquiries}</p>
        </div>
        <div className="manager-dashboard-stat-box">
          <p>문의 답변</p>
          <p>{inquiryReplies}</p>
        </div>
      </div>

      {/* 실시간/누적 차트 선택 버튼 */}
      <div className="chart-type-buttons">
        <button onClick={() => setChartType('realtime')}>매출</button>
        <button onClick={() => setChartType('cumulative')}>판매율</button>
      </div>

      <div className="manager-dashboard-charts">
        <div className="manager-dashboard-chart">
          <p>{chartType === 'realtime' ? '매출' : '판매율'}</p>
          <Line data={chartType === 'realtime' ? realTimeData : cumulativeData} />
        </div>
        <div className="manager-dashboard-chart">
          <p>카테고리별 판매율</p>
          <Doughnut data={doughnutData} options={doughnutOptions} />
        </div>
      </div>

      <div className="manager-dashboard-order">
        <div className="manager-dashboard-order-header">
          <h2>주문 목록</h2>
          <div className="manager-dashboard-order-actions">
            <button className="save-button" onClick={handleSaveClick}>저장</button>
            <button className="delete-button" onClick={handleDeleteClick}>삭제</button>
          </div>
        </div>
      </div>
      <table className="manager-dashboard-order-table">
        <thead>
          <tr>
            <th></th> {/* 체크박스 칸 */}
            <th>주문 번호</th>
            <th>사용자 아이디</th>
            <th>상품 정보</th>
            <th>총 가격</th>
            <th>주문 일자</th>
            <th>배송 상태</th>
          </tr>
        </thead>
        <tbody>
          {orderList.length > 0 ? (
            orderList.map(order => (
              <tr key={order.orderId}>
                <td>
                  <input
                    type="checkbox"
                    checked={selectedOrders.includes(order.orderId)}
                    onChange={() => handleCheckboxChange(order.orderId)}
                  />
                </td>
                <td>{order.orderId}</td>
                <td>{order.userId}</td>
                <td>{order.productInfo}</td>
                <td>{order.totalPrice}</td>
                <td>{order.orderDate}</td>
                <td>
                  <select
                    value={order.deliveryStatus}
                    onChange={(e) => handleStatusChange(order.orderId, e.target.value)}
                  >
                    <option value="배송 준비">배송 준비</option>
                    <option value="배송 중">배송 중</option>
                    <option value="배송 완료">배송 완료</option>
                  </select>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="7" style={{ textAlign: 'center' }}>주문 목록이 없습니다.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default Manager;
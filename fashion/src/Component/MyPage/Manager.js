import React, { useState, useEffect } from 'react';
import DatePicker, { registerLocale } from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import { format } from 'date-fns';
import { ko } from 'date-fns/locale';
import { Line, Doughnut } from 'react-chartjs-2';
import Admheader from '../Admins/Admheader';

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

// 한국어 로케일 등록
registerLocale('ko', ko);

const Manager = () => {
  const [totalMembers, setTotalMembers] = useState(0);
  const [dailyVisitors, setDailyVisitors] = useState(0);
  const [inquiries, setInquiries] = useState(0);
  const [inquiryReplies, setInquiryReplies] = useState(0);
  const [orderList, setOrderList] = useState([]);
  const [selectedOrders, setSelectedOrders] = useState([]);
  const [chartType, setChartType] = useState('realtime');
  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);
  const [filteredData, setFilteredData] = useState({
    labels: [],
    datasets: [{ label: '일별 매출', data: [] }],
  });
  const [doughnutData, setDoughnutData] = useState({
    labels: [],
    datasets: [{
      label: '카테고리별 판매율',
      data: [],
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
        'rgba(128, 0, 128, 1)',
      ],
      borderWidth: 1,
    }],
  });

  const realTimeData = {
    labels: ['9월 1일', '9월 2일', '9월 3일', '9월 4일', '9월 5일', '9월 6일', '9월 7일', '9월 8일', '9월 9일'],
    datasets: [
      {
        label: '일별 매출',
        data: [200, 250, 220, 320, 200, 450, 380, 500, 420],
        fill: false,
        borderColor: 'rgba(75,192,192,1)',
        tension: 0.1,
      },
    ],
  };

  const cumulativeData = {
    labels: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월'],
    datasets: [
      {
        label: '월별 누적 매출',
        data: [200, 700, 2450, 1970, 2470, 3000, 2700, 4500, 3200],
        fill: false,
        borderColor: 'rgba(255,99,132,1)',
        tension: 0.1,
      },
    ],
  };

  const doughnutOptions = {
    plugins: {
      legend: {
        position: 'right',
        align: 'center',
        labels: {
          boxWidth: 10,
          padding: 20,
        },
      },
    },
    maintainAspectRatio: false,
  };

  const API_BASE_URL = 'http://10.125.121.188:8080/api/admin';
  const VISITOR_API_URL = 'http://10.125.121.188:8080/api/visitors';

  // 데이터 가져오기
  useEffect(() => {
    const fetchTotalMembers = async () => {
      try {
        const response = await fetch(`${API_BASE_URL}/members/count`);
        if (response.ok) {
          const data = await response.json();
          setTotalMembers(data);
        }
      } catch (error) {
        console.error('총 회원 수를 가져오는 중 오류 발생:', error);
      }
    };

    const fetchDailyVisitors = async () => {
      try {
        const response = await fetch(`${VISITOR_API_URL}/count`);
        if (response.ok) {
          const data = await response.json();
          setDailyVisitors(data);
        }
      } catch (error) {
        console.error('일일 방문자 수를 가져오는 중 오류 발생:', error);
      }
    };

    const fetchInquiriesCount = async () => {
      try {
        const response = await fetch(`${API_BASE_URL}/qboards/count`);
        if (response.ok) {
          const data = await response.json();
          setInquiries(data);
        }
      } catch (error) {
        console.error('문의 건수를 가져오는 중 오류 발생:', error);
      }
    };

    const fetchInquiryRepliesCount = async () => {
      try {
        const response = await fetch(`${API_BASE_URL}/comments/admin/count`);
        if (response.ok) {
          const data = await response.json();
          setInquiryReplies(data);
        }
      } catch (error) {
        console.error('답변된 문의 건수를 가져오는 중 오류 발생:', error);
      }
    };

    const fetchOrders = async () => {
      try {
        const response = await fetch(`${API_BASE_URL}/orders`);
        if (response.ok) {
          const data = await response.json();
          setOrderList(data);
        }
      } catch (error) {
        console.error('주문 목록을 가져오는 중 오류 발생:', error);
      }
    };

    const fetchCategorySalesPercentage = async () => {
      try {
        const response = await fetch(`${API_BASE_URL}/sales/category-percentage`);
        if (response.ok) {
          const data = await response.json();
          const labels = Object.keys(data);
          const salesData = Object.values(data);
          setDoughnutData({
            labels: labels,
            datasets: [{
              label: '카테고리별 판매율',
              data: salesData,
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
                'rgba(128, 0, 128, 1)',
              ],
              borderWidth: 1,
            }],
          });
        }
      } catch (error) {
        console.error('카테고리별 판매율을 가져오는 중 오류 발생:', error);
      }
    };

    fetchTotalMembers();
    fetchDailyVisitors();
    fetchInquiriesCount();
    fetchInquiryRepliesCount();
    fetchOrders();
    fetchCategorySalesPercentage();
  }, []);

  const handleStatusChange = async (orderId, newStatus) => {
    try {
      const response = await fetch(`${API_BASE_URL}/orders/${orderId}/status`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ newStatus }),
      });

      if (response.ok) {
        setOrderList((prevOrders) =>
          prevOrders.map((order) =>
            order.orderId === orderId ? { ...order, deliveryStatus: newStatus } : order
          )
        );
      } else {
        console.error('주문 상태 업데이트 중 오류 발생:', response.statusText);
      }
    } catch (error) {
      console.error('주문 상태 업데이트 중 오류 발생:', error);
    }
  };

  const handleDeleteClick = async () => {
    try {
      await Promise.all(
        selectedOrders.map(async (orderId) => {
          const response = await fetch(`${API_BASE_URL}/orders/${orderId}/cancel`, {
            method: 'PATCH',
          });
          if (!response.ok) {
            console.error(`주문 취소 중 오류 발생 (Order ID: ${orderId}):`, response.statusText);
          }
        })
      );

      setOrderList(orderList.filter(order => !selectedOrders.includes(order.orderId)));
      setSelectedOrders([]);
    } catch (error) {
      console.error('주문 취소 중 오류 발생:', error);
    }
  };

  const handleSaveClick = () => {
    alert('저장되었습니다.');
  };

  const handleSearchClick = () => {
    console.log("조회 시작 날짜: ", startDate ? format(startDate, 'yyyy.MM.dd') : "선택 안 됨");
    console.log("조회 종료 날짜: ", endDate ? format(endDate, 'yyyy.MM.dd') : "선택 안 됨");
  };

  useEffect(() => {
    if (chartType === 'realtime') {
      setFilteredData(realTimeData);
    } else {
      setFilteredData(cumulativeData);
    }
  }, [chartType]);

  const handleCheckboxChange = (orderId) => {
    setSelectedOrders((prevSelected) =>
      prevSelected.includes(orderId)
        ? prevSelected.filter((id) => id !== orderId)
        : [...prevSelected, orderId]
    );
  };

  return (
    <div>
      <Admheader />
      <div className="manager-dashboard-container">
        <h1 className="manager-dashboard-title">DashBoard</h1>
        <p className="manager-dashboard-welcome-text">이 페이지는 관리자만 이용 가능합니다.</p>

        <div className="manager-dashboard-stats">
          <div className="manager-dashboard-stat-box">
            <p>총 회원 수</p>
            <p>{totalMembers}</p>
          </div>
          <div className="manager-dashboard-stat-box">
            <p>총 방문자 수</p>
            <p>{dailyVisitors}</p>
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

        <div className="chart-type-and-filter">
          <div className="chart-type-buttons">
            <button onClick={() => setChartType('realtime')}>일일 매출 보고서</button>
            <button onClick={() => setChartType('cumulative')}>월별 매출 보고서</button>
          </div>
          <div className="search-filter-inline">
            <DatePicker
              locale="ko"
              selected={startDate}
              onChange={(date) => setStartDate(date)}
              dateFormat="yyyy.MM.dd"
              placeholderText="시작 날짜"
              selectsStart
              startDate={startDate}
              endDate={endDate}
            />
            <span> ~ </span>
            <DatePicker
              locale="ko"
              selected={endDate}
              onChange={(date) => setEndDate(date)}
              dateFormat="yyyy.MM.dd"
              placeholderText="종료 날짜"
              selectsEnd
              startDate={startDate}
              endDate={endDate}
              minDate={startDate}
            />
            <button onClick={handleSearchClick}>조회</button>
          </div>
        </div>

        <div className="manager-dashboard-charts">
          <div className="manager-dashboard-chart">
            <p>{chartType === 'realtime' ? '일일 매출' : '월별 매출'}</p>
            <Line data={filteredData} />
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
        
        <div className="table-container" style={{ maxHeight: '300px', overflowY: 'auto' }}>
          <table className="manager-dashboard-order-table">
            <thead style={{ position: 'sticky', top: 0, backgroundColor: 'white' }}>
              <tr>
                <th></th>
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
                        <option value="배송 취소">주문 취소</option>
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
      </div>
    </div>
  );
};

export default Manager;
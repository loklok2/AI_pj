import React, { useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faUser, faCalendar } from '@fortawesome/free-solid-svg-icons';
import { Link } from 'react-router-dom'; // Link 컴포넌트 가져오기
import DatePicker, { registerLocale } from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import ko from 'date-fns/locale/ko';
import '../../CSS/MyOrder.css';

// 한국어 locale 등록
registerLocale('ko', ko);

const MyOrder = () => {
  // 예시 주문 데이터
  const orders = [
    {
      id: 204858,
      product: '알렉산드로 플립 화이트 With 링크 리본 디테일',
      price: '29,900원',
      date: '2024-08-05',
      status: '발송',
    },
    {
      id: 204859,
      product: '알렉산드로 플립 화이트 With 링크 리본 디테일',
      price: '29,900원',
      date: '2024-10-05',
      status: '발송',
    },
    {
      id: 204860,
      product: '알렉산드로 플립 화이트 With 링크 리본 디테일',
      price: '29,900원',
      date: '2024-05-05',
      status: '발송',
    },
    {
        id: 204858,
        product: '알렉산드로 플립 화이트 With 링크 리본 디테일',
        price: '29,900원',
        date: '2024-08-05',
        status: '발송',
      },
      {
        id: 204859,
        product: '알렉산드로 플립 화이트 With 링크 리본 디테일',
        price: '29,900원',
        date: '2024-10-05',
        status: '발송',
      },
      {
        id: 204860,
        product: '알렉산드로 플립 화이트 With 링크 리본 디테일',
        price: '29,900원',
        date: '2024-05-05',
        status: '발송',
      },
    
  ];

  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);
  const [startDateOpen, setStartDateOpen] = useState(false);
  const [endDateOpen, setEndDateOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [filteredOrders, setFilteredOrders] = useState(orders);
  const [visibleOrdersCount, setVisibleOrdersCount] = useState(5); // 기본으로 5개만 보여줌

  // 검색어에 맞는 주문을 필터링하는 함수
  const handleSearch = () => {
    const lowerCasedSearchTerm = searchTerm.toLowerCase();

    const filtered = orders.filter((order) => {
      const orderDate = new Date(order.date);

      const isWithinDateRange =
        (!startDate || orderDate >= startDate) &&
        (!endDate || orderDate <= endDate);

      const matchesProduct = order.product.toLowerCase().includes(lowerCasedSearchTerm);

      return isWithinDateRange && matchesProduct;
    });

    setFilteredOrders(filtered);
    setVisibleOrdersCount(5); // 검색 시 다시 처음 5개만 표시
  };

  // 더보기 버튼 클릭 시 더 많은 주문을 보여주는 함수
  const handleShowMore = () => {
    setVisibleOrdersCount((prevCount) => prevCount + 5);
  };

  // 요일에 따라 CSS 클래스를 적용하는 함수
  const dayClassName = (date) => {
    const day = date.getDay();
    if (day === 6) {
      return 'saturday'; // 토요일
    }
    if (day === 0) {
      return 'sunday'; // 일요일
    }
    return '';
  };

  return (
    <div className="myorder-container">
      <h2 className="myorder-title">주문/배송 내역</h2>
      <p className="myorder-subtitle">나의 주문/배송 내역을 확인해보세요.</p>

      <div className="myorder-user-info">
        <div className="myorder-avatar">
          <FontAwesomeIcon icon={faUser} size="2x" />
        </div>
        <p className="myorder-no-orders">
          <strong>[유저 아이디]</strong>님의 주문/배송 내역 입니다.<br />
          <strong>[<Link to="/mypage">여기</Link>]</strong>를 클릭하면 마이페이지로 이동합니다.
        </p>
      </div>

      <div className="myorder-section-header">주문/배송 내역 조회</div>
      <div className="myorder-search-section">
        <div className="input-with-icon">
          <DatePicker
            selected={startDate}
            onChange={(date) => setStartDate(date)}
            onClickOutside={() => setStartDateOpen(false)}
            onSelect={() => setStartDateOpen(false)}
            open={startDateOpen}
            dateFormat="yyyy-MM-dd"
            locale="ko"
            placeholderText="날짜를 입력해주세요. (예: 2000-02-22)"
            className="myorder-input"
            showYearDropdown
            scrollableYearDropdown
            yearDropdownItemNumber={15}
            dayClassName={dayClassName}
          />
          <FontAwesomeIcon
            icon={faCalendar}
            className="calendar-icon"
            onClick={() => setStartDateOpen(!startDateOpen)}
          />
        </div>
        <span className="date-separator">~</span>
        <div className="input-with-icon">
          <DatePicker
            selected={endDate}
            onChange={(date) => setEndDate(date)}
            onClickOutside={() => setEndDateOpen(false)}
            onSelect={() => setEndDateOpen(false)}
            open={endDateOpen}
            dateFormat="yyyy-MM-dd"
            locale="ko"
            placeholderText="날짜를 입력해주세요. (예: 2000-02-22)"
            className="myorder-input"
            showYearDropdown
            scrollableYearDropdown
            yearDropdownItemNumber={15}
            dayClassName={dayClassName}
          />
          <FontAwesomeIcon
            icon={faCalendar}
            className="calendar-icon"
            onClick={() => setEndDateOpen(!endDateOpen)}
          />
        </div>
        <input
          type="text"
          className="myorder-input"
          placeholder="상품명을 입력해주세요."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        <button className="myorder-search-btn" onClick={handleSearch}>조회</button>
      </div>

      <div className="myorder-section-header">주문/배송 내역 목록</div>
      <table className="myorder-table">
        <thead>
          <tr>
            <th>상품번호</th>
            <th>상품정보</th>
            <th>총 가격</th>
            <th>주문일자</th>
            <th>배송상태</th>
          </tr>
        </thead>
        <tbody>
          {filteredOrders.slice(0, visibleOrdersCount).map((order) => (
            <tr key={order.id}>
              <td>{order.id}</td>
              <td>
                <div className="myorder-product-info">
                  <div className="myorder-product-img-placeholder"></div>
                  <div>
                    <p>{order.product}</p>
                    <p>사이즈: 프리 / 컬러: 화이트</p>
                  </div>
                </div>
              </td>
              <td>{order.price}</td>
              <td>{order.date}</td>
              <td>{order.status}</td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* 항상 더보기 버튼이 보이도록 수정 */}
      <div className="myorder-more-btn-container">
        <button className="myorder-more-btn" onClick={handleShowMore}>더보기</button>
      </div>
    </div>
  );
};

export default MyOrder;

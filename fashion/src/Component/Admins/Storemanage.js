import React, { useState, useEffect, forwardRef } from 'react';
import Admheader from '../Admins/Admheader';
import '../../CSS/Storemanage.css';
import { Line } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend } from 'chart.js';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend);

// 커스텀 입력 컴포넌트
const CustomInput = forwardRef(({ value, onClick }, ref) => (
  <input
    className="custom-date-input"
    onClick={onClick}
    ref={ref}
    value={value}
    readOnly
  />
));

const Storemanage = () => {
  const [startDate, setStartDate] = useState(new Date());
  const [endDate, setEndDate] = useState(new Date());
  const [startDay, setStartDay] = useState(1); // 시작 일
  const [endDay, setEndDay] = useState(1);     // 종료 일
  const [selectedStore, setSelectedStore] = useState('');
  const [chartDataList, setChartDataList] = useState([]);
  const [storeList, setStoreList] = useState([]); 
  const [viewMode, setViewMode] = useState('day');
  const [isSalesViewed, setIsSalesViewed] = useState(false);
  const [startMonth, setStartMonth] = useState(1);
  const [endMonth, setEndMonth] = useState(12);
  const [startYear, setStartYear] = useState(new Date().getFullYear());
  const [endYear, setEndYear] = useState(new Date().getFullYear());
  const [activeItem, setActiveItem] = useState(null); 
  const [relatedItems, setRelatedItems] = useState([]); 
  const [productDisplay, setProductDisplay] = useState([]);
  const [isYearChecked, setIsYearChecked] = useState(false);
  const [isMonthChecked, setIsMonthChecked] = useState(false);
  const [isDateChecked, setIsDateChecked] = useState(false);

// 매장 목록 가져오기
useEffect(() => {
  const fetchStores = async () => {
    try {
      // 새로운 API 엔드포인트 URL
      let endpoint = `http://10.125.121.188:8080/api/sales/store-sales?fromYear=${startYear}`;

      const response = await fetch(endpoint);

      if (!response.ok) {
        throw new Error('Failed to fetch store list');
      }

      const salesData = await response.json();
      console.log("Fetched Store Data:", salesData);

      // 매장 목록을 중복 없이 가져오기 위해 Set 사용
      const uniqueStores = [...new Map(salesData.salesData.map(item => [item.storeId, item])).values()];

      setStoreList(uniqueStores); 
    } catch (error) {
      console.error('Error fetching store list:', error);
    }
  };

  fetchStores();
}, [startYear]);

const handleSalesSearch = async () => {
  try {
    // 기본 매출 조회 API URL 설정
    let endpoint = `http://10.125.121.188:8080/api/sales/store-sales`;

    // 매장이 선택되지 않은 경우 경고 메시지를 출력하고 함수 종료
    if (!selectedStore) {
      console.warn('매장을 선택해주세요.');
      return;
    }

    // 선택된 매장의 storeId를 URL에 추가
    endpoint += `?storeId=${selectedStore}`;

    // 월별 조회가 선택된 경우
    if (isMonthChecked) {
      const fromMonth = startMonth; // 시작 월
      const toMonth = endMonth;     // 종료 월

      // 특정 기간 조회도 선택된 경우
      if (isDateChecked) {
        // 입력받은 특정 기간의 날짜, 월, 연도 정보 설정
        const fromYear = startYear;
        const toYear = endYear;
        const fromDay = startDay;
        const toDay = endDay;

        console.log("Start Date:", `${fromYear}-${fromMonth}-${fromDay}`);
        console.log("End Date:", `${toYear}-${toMonth}-${toDay}`);

        // URL에 해당 기간의 연도, 월, 일 정보를 추가
        endpoint += `&fromYear=${fromYear}&fromMonth=${fromMonth}&fromDay=${fromDay}&toYear=${toYear}&toMonth=${toMonth}&toDay=${toDay}`;
      } else {
        // 특정 일자 없이 월 전체 조회인 경우
        endpoint += `&fromYear=${startYear}&toYear=${startYear}&fromMonth=${startMonth}&toMonth=${startMonth}&fromDay=1&toDay=${new Date(startYear, startMonth, 0).getDate()}`;
      }
    } else {
      // 월별 조회가 선택되지 않았을 경우 연도별 조회를 위한 기본 파라미터 추가
      endpoint += `&fromYear=${startYear}&toYear=${startYear}`;
    }

    // 최종적으로 만들어진 매출 조회 API URL을 출력
    console.log("매출 조회 API 요청 URL:", endpoint);

    // 매출 데이터를 조회하기 위한 API 요청
    const response = await fetch(endpoint, { method: 'GET' });

    // 응답이 성공적이지 않을 경우 에러 메시지 출력 및 예외 발생
    if (!response.ok) {
      console.error(`Error: ${response.status} - ${response.statusText}`);
      throw new Error('Failed to fetch sales data');
    }

    // 응답 데이터를 JSON 형태로 변환
    const fetchedData = await response.json();
    console.log("Fetched Sales Data:", fetchedData);

    // 매출 데이터 배열을 가져옴
    const salesDataArray = fetchedData.salesData;
    if (!Array.isArray(salesDataArray) || salesDataArray.length === 0) {
      console.warn('선택한 기간에 대한 매출 데이터가 없습니다.');
      setChartDataList([]);
      return;
    }

    // 선택한 매장의 이름 설정 (storeList 배열에서 해당 매장 정보 찾기)
    let storeName = "매장명 불러오기 실패";
    const storeInfo = storeList.find(store => store.storeId === selectedStore);
    if (storeInfo) {
      storeName = storeInfo.storeName;
    } else if (salesDataArray.length > 0) {
      storeName = salesDataArray[0].storeName || "매장명 없음";
    }

    // 차트 데이터 배열 초기화
    const chartDataList = [];

    // 날짜별 조회 또는 월 전체 조회일 경우
    if (isDateChecked || (isMonthChecked && !isDateChecked)) {
      // 일별 매출 데이터 생성
      const dailySalesData = salesDataArray.map(item => {
        if (item.date && item.totalSales !== undefined) {
          return {
            date: item.date,
            sales: item.totalSales
          };
        }
        return null;
      }).filter(item => item !== null);

      // 일별 매출 데이터가 있는 경우 차트 데이터 생성
      if (dailySalesData.length > 0) {
        const chartData = {
          labels: dailySalesData.map(item => item.date), // X축 라벨 (날짜)
          datasets: [{
            label: `${storeName} 일별 매출`, // 차트 라벨
            data: dailySalesData.map(item => item.sales), // Y축 데이터 (매출)
            fill: false, // 그래프 채우기 비활성화
            borderColor: 'rgba(75,192,192,1)', // 그래프 선 색상
            tension: 0.1, // 그래프 선의 곡선 정도
          }]
        };
        chartDataList.push(chartData); // 차트 데이터 리스트에 추가
      } else {
        console.warn('일별 매출 데이터가 없습니다.');
      }
    } else {
      // 월별 매출 데이터 생성
      const monthlySalesData = salesDataArray.map(item => {
        if (item.month !== undefined && item.totalSales !== undefined) {
          return {
            month: `${item.month}월`,
            sales: item.totalSales
          };
        }
        return null;
      }).filter(item => item !== null);

      // 월별 매출 데이터가 있는 경우 차트 데이터 생성
      if (monthlySalesData.length > 0) {
        const chartData = {
          labels: monthlySalesData.map(item => item.month), // X축 라벨 (월)
          datasets: [{
            label: `${storeName} 월별 매출`, // 차트 라벨
            data: monthlySalesData.map(item => item.sales), // Y축 데이터 (매출)
            fill: false, // 그래프 채우기 비활성화
            borderColor: 'rgba(75,192,192,1)', // 그래프 선 색상
            tension: 0.1, // 그래프 선의 곡선 정도
          }]
        };
        chartDataList.push(chartData); // 차트 데이터 리스트에 추가
      } else {
        console.warn('월별 매출 데이터가 없습니다.');
      }
    }

    // 차트 데이터가 없는 경우 경고 메시지 출력 후 함수 종료
    if (chartDataList.length === 0) {
      console.warn('선택한 기간에 대한 매출 데이터가 없습니다.');
      setChartDataList([]);
      return;
    }

    // 차트 데이터 설정 및 조회 상태를 true로 변경
    setChartDataList(chartDataList);
    setIsSalesViewed(true);
  } catch (error) {
    // 에러가 발생한 경우 콘솔에 에러 메시지 출력
    console.error('Error fetching sales data:', error);
  }
};

const handleProductSearch = async () => {
  try {
    let endpoint = `http://10.125.121.188:8080/api/sales/top-products`;

    if (isYearChecked) {
      endpoint += `?year=${startYear}`;
    }

    if (isMonthChecked) {
      endpoint += `&fromMonth=${startMonth}&toMonth=${endMonth}`;
    }

    if (isDateChecked) {
      endpoint += `&startDate=${startDate.toISOString().split('T')[0]}&endDate=${endDate.toISOString().split('T')[0]}`;
    }

    if (selectedStore) {
      endpoint += `&storeId=${selectedStore}`;
    }

    console.log("상품 조회 API 요청 URL:", endpoint);
    const response = await fetch(endpoint, { method: 'GET' });

    if (!response.ok) {
      throw new Error('Failed to fetch product data');
    }

    const productData = await response.json();
    console.log("Fetched Product Data:", productData);

    const productList = productData.map(product => (
      <div key={product.productId} className="product-item" onClick={() => handleProductClick(product.productId)}>
        <div className="product-image-container">
          {product.pimgPath ? (
            <img 
              src={`http://10.125.121.188:8080${product.pimgPath}`} 
              alt={product.productName} 
              className="product-image" 
            />
          ) : (
            <div className="product-image-placeholder">이미지 없음</div>
          )}
        </div>
        <div className="product-info">
          <p className="product-name">{product.productName}</p>
          <p className="product-quantity">판매량: {product.totalQuantity}</p>
        </div>
      </div>
    ));

    setProductDisplay(productList);
  } catch (error) {
    console.error('Error fetching product data:', error);
  }
};

  const handleProductClick = async (productId) => {
    try {
        setActiveItem(productId);

        const response = await fetch(`http://10.125.121.188:8080/api/admin/recommend-products`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                year: startYear,
                month: startMonth,
                day: startDate.getDate(),
                storeid: selectedStore,
            }),
        });

        if (!response.ok) {
            throw new Error('Failed to fetch recommended products');
        }

        const recommendationData = await response.json();
        console.log("Fetched Recommendation Data:", recommendationData);

        const recommendedItems = Object.values(recommendationData.category).map((item, index) => (
            <div key={index} className="recommended-item">
                <a href={item.seller_url} target="_blank" rel="noopener noreferrer">
                    <img src={item.pimgPath} alt={item.name} className="recommended-item-image" />
                    <p className="recommended-item-name">{item.name}</p>
                    <p className="recommended-item-price">가격: {item.price}원</p>
                    <p className="recommended-item-reviews">리뷰 수: {item.review_count}</p>
                </a>
            </div>
        ));

        setRelatedItems(recommendedItems);
    } catch (error) {
        console.error('Error fetching recommended products:', error);
    }
};

  const handleReset = () => {
    window.location.reload();
  };

  return (
    <div>
      <Admheader />
      <div className="storemanage-container">
        <h1 className="storemanage-title">매장관리</h1>
        <p className="storemanage-welcome-text">이 페이지는 관리자만 이용 가능합니다.</p>

        <div>
          <p className="storemanage-search-title">매장 조회</p>
          <div className="storemanage-search-section">
          <select className="storemanage-dropdown wide" value={selectedStore} onChange={(e) => setSelectedStore(e.target.value)}>
          <option value="">매장 선택</option>

          {storeList.map((store) => (
            <option key={store.storeId} value={store.storeId}>{store.storeName}</option>
          ))}
        </select>

            <label>
              <input
                type="checkbox"
                className="circle-checkbox"
                checked={isYearChecked}
                onChange={() => setIsYearChecked(!isYearChecked)}
              />
              년도
            </label>
            
            <select 
              className="storemanage-dropdown"
              value={startYear}
              onChange={(e) => setStartYear(Number(e.target.value))}
              disabled={!isYearChecked}
            >
              <option value={2023}>2023년</option>
              <option value={2024}>2024년</option>
            </select>
            
            <label>

            <input
              type="checkbox"
              className="circle-checkbox"
              checked={isMonthChecked}
              onChange={() => setIsMonthChecked(!isMonthChecked)}
              disabled={!isYearChecked} // 연도 선택이 체크되지 않은 경우 비활성화
            />
            월
          </label>

          <select
            className="storemanage-dropdown"
            value={startMonth}
            onChange={(e) => setStartMonth(Number(e.target.value))}
            disabled={!isMonthChecked}
          >
            {Array.from({ length: 12 }, (_, i) => (
              <option key={i + 1} value={i + 1}>{i + 1}월</option>
            ))}
          </select>

          <label>
            <input
              type="checkbox"
              className="circle-checkbox"
              checked={isDateChecked}
              onChange={() => setIsDateChecked(!isDateChecked)}
            />
            특정기간
          </label>

          <div className="date-input-container">
  <input
    type="number"
    placeholder="연도"
    value={startYear}
    onChange={(e) => setStartYear(Number(e.target.value))}
    className="storemanage-date-input"
    disabled={!isDateChecked}
  />
  <input
    type="number"
    placeholder="월"
    value={startMonth}
    onChange={(e) => setStartMonth(Number(e.target.value))}
    className="storemanage-date-input"
    min="1"
    max="12"
    disabled={!isDateChecked}
  />
  <input
    type="number"
    placeholder="일"
    value={startDay}
    onChange={(e) => setStartDay(Number(e.target.value))}
    className="storemanage-date-input"
    min="1"
    max="31"
    disabled={!isDateChecked}
  />
</div>

<span> ~ </span>

<div className="date-input-container">
  <input
    type="number"
    placeholder="연도"
    value={endYear}
    onChange={(e) => setEndYear(Number(e.target.value))}
    className="storemanage-date-input"
    disabled={!isDateChecked}
  />
  <input
    type="number"
    placeholder="월"
    value={endMonth}
    onChange={(e) => setEndMonth(Number(e.target.value))}
    className="storemanage-date-input"
    min="1"
    max="12"
    disabled={!isDateChecked}
  />
  <input
    type="number"
    placeholder="일"
    value={endDay}
    onChange={(e) => setEndDay(Number(e.target.value))}
    className="storemanage-date-input"
    min="1"
    max="31"
    disabled={!isDateChecked}
  />
</div>


            <button className="storemanage-search-button" style={{ marginLeft: '10px' }} onClick={handleSalesSearch}>매출 조회</button>
            
            <button className="storemanage-search-button" onClick={handleProductSearch}>상품 조회</button>
            
            <button className="storemanage-search-button" onClick={handleReset}>다시 조회</button>
          </div>
        </div>

        {chartDataList.length > 0 && (
          <div className={`storemanage-sales-charts ${chartDataList.length === 1 ? 'storemanage-single-chart' : ''}`}>
            {chartDataList.map((chartData, index) => (
              <div className="storemanage-sales-chart" key={index}>
                <h3>{chartData.datasets[0].label || '매장 매출 차트'}</h3>
                <Line
                  data={chartData}
                  options={{
                    maintainAspectRatio: false,
                    responsive: true,
                  }}
                />
              </div>
            ))}
          </div>
        )}

        <div className="product-display-section">
          {productDisplay}
        </div>

        {activeItem !== null && (
          <div>
            <h2 className="related-item-title">추천 아이템</h2>
            <div className="related-items-list">
              {relatedItems}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default Storemanage;
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
      let endpoint = `http://10.125.121.188:8080/api/new-sales/store-list?fromYear=${startYear}&toYear=${endYear}`;

      if (isMonthChecked) {
        endpoint += `&fromMonth=${startMonth}&toMonth=${endMonth}`;
      }

      const response = await fetch(endpoint);
      
      if (!response.ok) {
        throw new Error('Failed to fetch store list');
      }

      const stores = await response.json();
      console.log("Fetched Store Data:", stores);

      setStoreList(stores); 
    } catch (error) {
      console.error('Error fetching store list:', error);
    }
  };

  fetchStores();
}, [startYear, endYear, startMonth, endMonth, isMonthChecked]);


  const handleSalesSearch = async () => {
    try {
      let endpoint = `http://10.125.121.188:8080/api/sales/store-sales`;

      // 필수 파라미터인 `fromYear`와 `toYear` 추가
      if (isYearChecked) {
        endpoint += `?fromYear=${startYear}&toYear=${endYear}`;
      } else {
        console.warn('년도를 선택해주세요.');
        return;
      }

      // 매장이 선택된 경우 storeId 추가
      if (selectedStore) {
        endpoint += `&storeId=${selectedStore}`;
      } else {
        console.warn('매장을 선택해주세요.');
        return;
      }

      console.log("매출 조회 API 요청 URL:", endpoint);
      const response = await fetch(endpoint, { method: 'GET' });

      if (!response.ok) {
        console.error(`Error: ${response.status} - ${response.statusText}`);
        throw new Error('Failed to fetch sales data');
      }

      const salesData = await response.json();
      console.log("Fetched Sales Data:", salesData);

      const chartData = {
        labels: [salesData.storeName], // 매장 이름을 x축 레이블로 사용
        datasets: [{
          label: '매출',
          data: [salesData.totalSales], // 매출 데이터를 y축 값으로 사용
          fill: false,
          borderColor: 'rgba(75,192,192,1)',
          tension: 0.1,
        }]
      };

      setChartDataList([chartData]);
      setIsSalesViewed(true);
    } catch (error) {
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
            endpoint += `&month=${startMonth}`;
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
          <option value="">매장 전체 보기</option>
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
              {Array.from({ length: 10 }, (_, i) => (
                <option key={startYear - i} value={startYear - i}>{startYear - i}년</option>
              ))}
            </select>
            
            <span> ~ </span>

            <select 
              className="storemanage-dropdown"
              value={endYear}
              onChange={(e) => setEndYear(Number(e.target.value))}
              disabled={!isYearChecked}
            >
              {Array.from({ length: 10 }, (_, i) => (
                <option key={endYear - i} value={endYear - i}>{endYear - i}년</option>
              ))}
            </select>

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
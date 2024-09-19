import React, { useState, forwardRef } from 'react';
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
  const [viewMode, setViewMode] = useState('day');
  const [isSalesViewed, setIsSalesViewed] = useState(false);
  const [startMonth, setStartMonth] = useState(1);
  const [endMonth, setEndMonth] = useState(12);
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear());

  const storeData = [
    { id: 1, name: '부산구서점(직)', dailySales: [200, 150, 300, 250, 180, 300, 280], monthlySales: [1000, 1200, 1300] },
    { id: 2, name: '부산두실점(직)', dailySales: [180, 220, 250, 300, 200, 270, 290], monthlySales: [1100, 1150, 1400] },
    { id: 3, name: '부산만덕점(직)', dailySales: [170, 190, 210, 260, 240, 250, 300], monthlySales: [1050, 1250, 1350] },
    { id: 4, name: '부산모라점(직)', dailySales: [160, 180, 240, 230, 250, 280, 320], monthlySales: [1080, 1180, 1280] },
    { id: 5, name: '부산연지점(직)', dailySales: [190, 210, 230, 270, 250, 300, 350], monthlySales: [1090, 1190, 1390] },
    { id: 6, name: '부산하단점', dailySales: [200, 230, 260, 290, 300, 320, 340], monthlySales: [1120, 1220, 1320] },
  ];

  const handleSalesSearch = () => {
    const start = new Date(startDate);
    const end = new Date(endDate);

    const timeDiff = end - start;
    const dayDiff = timeDiff / (1000 * 3600 * 24);

    if (dayDiff < 3 && viewMode === 'day') {
      alert("조회 기간은 최소 3일 이상이어야 합니다.");
      return;
    }

    let filteredData = storeData;
    if (selectedStore !== '') {
      filteredData = storeData.filter(store => store.name === selectedStore);
    }

    const datasetsList = filteredData.map(store => {
      let labels = [];
      let data = [];

      if (viewMode === 'day') {
        let currentDate = new Date(start);
        while (currentDate <= end) {
          labels.push(`${currentDate.getMonth() + 1}월 ${currentDate.getDate()}일`);
          const salesDataIndex = Math.floor((currentDate - start) / (1000 * 3600 * 24));
          data.push(store.dailySales[salesDataIndex] || 0);
          currentDate.setDate(currentDate.getDate() + 1);
        }
      } else {
        labels = ['9월', '10월', '11월'];
        data = store.monthlySales;
      }

      return {
        labels: labels,
        datasets: [
          {
            label: store.name,
            data: data,
            fill: false,
            borderColor: 'rgba(0, 0, 0, 0.6)',
            tension: 0.1,
          },
        ],
      };
    });

    setChartDataList(datasetsList);
    setIsSalesViewed(true);
  };

  const handleReset = () => {
    window.location.reload();
  };

  const handleViewModeChange = (mode) => {
    setViewMode(mode);
    handleSalesSearch();
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
              {storeData.map(store => (
                <option key={store.id} value={store.name}>{store.name}</option>
              ))}
            </select>

            <select className="storemanage-dropdown" value={selectedYear} onChange={(e) => setSelectedYear(Number(e.target.value))}>
              {Array.from({ length: 10 }, (_, i) => (
                <option key={i + 2021} value={i + 2021}>{i + 2021}년</option>
              ))}
            </select>

            <select className="storemanage-dropdown month-input" value={startMonth} onChange={(e) => setStartMonth(Number(e.target.value))}>
                {Array.from({ length: 12 }, (_, i) => (
                    <option key={i + 1} value={i + 1}>{i + 1}월</option>
                ))}
            </select>
            <span> ~ </span>
            <select className="storemanage-dropdown month-input" value={endMonth} onChange={(e) => setEndMonth(Number(e.target.value))}>
                {Array.from({ length: 12 }, (_, i) => (
                    <option key={i + 1} value={i + 1}>{i + 1}월</option>
                ))}
            </select>

            <DatePicker
              selected={startDate}
              onChange={(date) => setStartDate(date)}
              dateFormat="yyyy-MM-dd"
              className="storemanage-date-input"
              placeholderText="시작 날짜"
              customInput={<CustomInput />}
            />
            <span> ~ </span>
            <DatePicker
              selected={endDate}
              onChange={(date) => setEndDate(date)}
              dateFormat="yyyy-MM-dd"
              className="storemanage-date-input"
              placeholderText="종료 날짜"
              customInput={<CustomInput />}
            />

            {isSalesViewed && (
              <>
                <button 
                  className={`storemanage-search-buttons ${viewMode === 'day' ? 'active' : ''}`} 
                  onClick={() => handleViewModeChange('day')}
                >
                  일별 조회
                </button>
                <button 
                  className={`storemanage-search-button ${viewMode === 'month' ? 'active' : ''}`} 
                  onClick={() => handleViewModeChange('month')}
                >
                  월별 조회
                </button>
              </>
            )}
            <button className="storemanage-search-button" style={{ marginLeft: '10px' }} onClick={handleSalesSearch}>매출 조회</button>
            <button className="storemanage-search-button" style={{ display: isSalesViewed ? 'none' : 'block' }}>상품 조회</button>
            <button className="storemanage-search-button" onClick={handleReset}>다시 조회</button>
          </div>
        </div>

        {chartDataList.length > 0 && (
          <div className={`storemanage-sales-charts ${chartDataList.length === 1 ? 'storemanage-single-chart' : ''}`}>
            {chartDataList.map((chartData, index) => (
              <div className="storemanage-sales-chart" key={index}>
                <Line 
                  data={chartData} 
                  options={{
                    maintainAspectRatio: false,
                    responsive: true,
                    plugins: {
                      tooltip: {
                        backgroundColor: 'rgba(0, 0, 0, 0.7)',
                        titleColor: '#fff',
                        bodyColor: '#fff',
                        borderColor: '#fff',
                        borderWidth: 1,
                        padding: 10,
                        displayColors: false,
                        callbacks: {
                          title: (tooltipItems) => {
                            return tooltipItems[0].label;
                          },
                          label: (tooltipItem) => {
                            return `매출: ${tooltipItem.raw}원`;
                          },
                        },
                      },
                    },
                  }}
                />
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

export default Storemanage;
import React, { useEffect, useState } from "react";
import { useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faUser, faTimes, faAngleLeft, faAngleRight, faCartShopping, faBox, faTruckMoving, faHouseChimney } from '@fortawesome/free-solid-svg-icons';
import ReactGA from 'react-ga4';

const ProductDetailsPageAIRecommendation = ({ product }) => {
    const navigate = useNavigate();
    // 상태: 현재 보여지는 추천 상품의 시작 인덱스
    const [startIndex, setStartIndex] = useState(0);
    const [isPrevDisabled, setIsPrevDisabled] = useState(true);
    const [isNextDisabled, setIsNextDisabled] = useState(false);
    useEffect(() => { 
        setStartIndex(0)   
        setIsPrevDisabled(true)    
        setIsNextDisabled(false)
    },[product])
    // 한 번에 보여질 추천 상품 수
    const itemsPerPage = 5;

    // 화살표 상태를 위한 변수

    // 다음 페이지로 넘기기
    const handleNextClick = () => {
        const newIndex = startIndex + itemsPerPage;
        if (newIndex < product.similarProducts.length) {
            setStartIndex(newIndex);
            setIsPrevDisabled(false);
            // 끝에 도달했는지 확인
            if (newIndex + itemsPerPage >= product.similarProducts.length) {
                setIsNextDisabled(true);
            }
        }
    };
    const handleProductClick = (product) => {
        navigate(`/product/${product.productId}`);
        ReactGA.event("view_item", {
            items: [{
                item_id: product.productId,
                item_name: product.name,
                item_category: product.category,
                price: product.price,
            }],
        });
    };

    // 이전 페이지로 넘기기
    const handlePrevClick = () => {
        const newIndex = startIndex - itemsPerPage;
        if (newIndex >= 0) {
            setStartIndex(newIndex);
            setIsNextDisabled(false);
            // 시작에 도달했는지 확인
            if (newIndex === 0) {
                setIsPrevDisabled(true);
            }
        }
    };
    return (
        <div className="product-details-page-ai-recommendation">
            <hr />
            <p><strong>AI 추천</strong></p>
            <div className="ai-recommendation-wrapper">
                <div className="ai-recommendation-shapes">
                {/* 왼쪽 버튼 */}
                <FontAwesomeIcon
                    icon={faAngleLeft}
                    onClick={handlePrevClick}
                    className={`pagination-arrow ${isPrevDisabled ? 'disabled' : ''}`}
                    style={{ color: isPrevDisabled ? '#ccc' : '#000', cursor: isPrevDisabled ? 'not-allowed' : 'pointer' }}
                />
                    {product.similarProducts
                        .slice(startIndex, startIndex + itemsPerPage)
                        .map((recommendation) => (
                            <div
                                key={recommendation.productId}
                                className="shape-placeholder-wrapper"
                                onClick={() => handleProductClick(recommendation)}
                            >
                                {/* Display the image */}
                                <img
                                    src={process.env.REACT_APP_URL + recommendation.pimgPath}
                                    alt={recommendation.name}
                                    className="ai-recommendation-image"
                                />
                                {/* Display the product name */}
                                <p className="item-title">{recommendation.name}</p>
                                {/* Display the product price */}
                                <p className="item-price">{recommendation.price.toLocaleString()}원</p>
                            </div>
                        ))}
                {/* 오른쪽 버튼 */}
                <FontAwesomeIcon
                    icon={faAngleRight}
                    onClick={handleNextClick}
                    className={`pagination-arrow ${isNextDisabled ? 'disabled' : ''}`}
                    style={{ color: isNextDisabled ? '#ccc' : '#000', cursor: isNextDisabled ? 'not-allowed' : 'pointer' }}
                />
                </div>
            </div>
        </div>
    );
};

export default ProductDetailsPageAIRecommendation;

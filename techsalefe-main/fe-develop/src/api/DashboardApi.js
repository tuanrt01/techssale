import baseAxios from "@/api/BaseAxios";


export const getTotalByMonth = async (params = {}) => {
    return await baseAxios.get(`/dash-board/total-by-month?year=${params}`);
};

export const getTotalByQuarter = async (params = {}) => {
    return await baseAxios.get(`/dash-board/total-by-quarter?year=${params}`);
};

export const getReason = async (year, month = null) => {
    const url = month 
        ? `/dash-board/total-by-reason?year=${year}&month=${month}` 
        : `/dash-board/total-by-reason?year=${year}`;
    return await baseAxios.get(url);
}

export const getBestUser = async (year, month = null) => {
    const url = month 
        ? `/dash-board/total-by-user?year=${year}&month=${month}` 
        : `/dash-board/total-by-user?year=${year}`;
    return await baseAxios.get(url);
};

export const getHeader = async (params = {}) => {
    return await baseAxios.get(`/dash-board/header?year=${params}`);
};

// Lấy chi tiết chi tiêu theo tháng
export const getMonthDetails = async (year, month) => {
    return await baseAxios.get(`/dash-board/month-detail?year=${year}&month=${month}`);
};

// Lấy chi tiết chi tiêu theo quý
export const getQuarterDetails = async (year, quarter) => {
    return await baseAxios.get(`/dash-board/quarter-detail?year=${year}&quarter=${quarter}`);
};

// Lấy chi tiết chi tiêu theo lý do
export const getReasonDetails = async (year, reason, month = null) => {
    let url = `/dash-board/reason-detail?year=${year}&reason=${encodeURIComponent(reason)}`;
    
    // Nếu có chọn tháng thì thêm vào query
    if (month) {
        url += `&month=${month}`;
    }
    
    return await baseAxios.get(url);
};

// Lấy chi tiết chi tiêu theo user (sử dụng API chuyên dụng mới)
export const getUserDetails = async (year, userId, month = null) => {
    try {
        // Tạo URL cơ bản
        let url = `/dash-board/user-detail?year=${year}&user=${encodeURIComponent(userId)}`;
        
        // Thêm tham số tháng nếu có
        if (month) {
            url += `&month=${month}`;
        }
        
        const response = await baseAxios.get(url);
        
        if (response && response.data) {
            return response;
        }
        
        return { data: [] };
    } catch (error) {
        console.error("Error fetching user details:", error);
        return { data: [] };
    }
};

export default {
    getTotalByMonth,
    getTotalByQuarter,
    getReason,
    getBestUser,
    getHeader,
    getMonthDetails,
    getQuarterDetails,
    getReasonDetails,
    getUserDetails
}
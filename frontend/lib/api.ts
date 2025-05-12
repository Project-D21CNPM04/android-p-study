// API base URL
const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8000";

// Login function
export async function login(email: string, password: string) {
  try {
    const response = await fetch(`${API_URL}/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email, password }),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.detail || "Đăng nhập thất bại");
    }

    return response.json();
  } catch (error) {
    console.error("Login error:", error);
    throw error;
  }
}

// Get dashboard statistics
export async function getDashboardStats() {
  try {
    const token = localStorage.getItem("token");
    
    // Thực hiện request lấy dữ liệu từ backend API
    const response = await fetch(`${API_URL}/stats/dashboard`, {
      headers: {
        "Authorization": token ? `Bearer ${token}` : "",
      },
    });

    if (!response.ok) {
      console.warn("Error from API:", await response.text());
      // Nếu API không thành công, sử dụng dữ liệu mẫu thay thế
      return getMockDashboardStats();
    }

    return response.json();
  } catch (error) {
    console.error("Failed to fetch dashboard data:", error);
    // Nếu có lỗi kết nối, sử dụng dữ liệu mẫu
    return getMockDashboardStats();
  }
}

// Lấy dữ liệu mẫu khi API không hoạt động
function getMockDashboardStats() {
  const contentTypes = ["Link", "Text", "Audio", "Image", "File"];
  
  // Tạo dữ liệu mẫu cho notes by hour
  const notesByHour = Array.from({ length: 24 }, (_, i) => ({
    name: `${i}:00`,
    value: Math.floor(Math.random() * 45) + 5,
  }));
  
  // Tạo dữ liệu mẫu cho notes by day
  const days = ["Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"];
  const notesByDay = days.map((day) => ({
    name: day,
    value: Math.floor(Math.random() * 80) + 20,
  }));
  
  // Tạo dữ liệu mẫu cho notes by type
  const notesByType = contentTypes.map((type) => ({
    name: type,
    value: Math.floor(Math.random() * 120) + 30,
  }));
  
  // Tính tổng số notes
  const totalNotes = notesByType.reduce((sum, item) => sum + item.value, 0);
  
  // Tạo dữ liệu mẫu cho quiz by type
  const quizByType = contentTypes.map((type) => ({
    name: type,
    value: Math.floor(Math.random() * 70) + 10,
  }));
  
  // Tính tổng số quiz
  const totalQuiz = quizByType.reduce((sum, item) => sum + item.value, 0);
  
  // Tạo dữ liệu mẫu cho flashcard by type
  const flashcardByType = contentTypes.map((type) => ({
    name: type,
    value: Math.floor(Math.random() * 75) + 15,
  }));
  
  // Tính tổng số flashcard
  const totalFlashcard = flashcardByType.reduce((sum, item) => sum + item.value, 0);
  
  // Tạo dữ liệu mẫu cho mindmap by type
  const mindmapByType = contentTypes.map((type) => ({
    name: type,
    value: Math.floor(Math.random() * 55) + 5,
  }));
  
  // Tính tổng số mindmap
  const totalMindmap = mindmapByType.reduce((sum, item) => sum + item.value, 0);
  
  // Tạo dữ liệu mẫu cho users
  const userNames = [
    "Nguyễn Văn A",
    "Trần Thị B",
    "Lê Văn C",
    "Phạm Thị D",
    "Hoàng Văn E",
    "Đỗ Thị F",
    "Vũ Văn G",
    "Ngô Thị H",
    "Đặng Văn I",
    "Bùi Thị K",
  ];
  
  const notesPerUser = userNames.map((name) => {
    const notes = Math.floor(Math.random() * 95) + 5;
    return {
      name,
      notes,
    };
  });
  
  // Tính toán thống kê user
  const maxNotes = Math.max(...notesPerUser.map((user) => user.notes));
  const minNotes = Math.min(...notesPerUser.map((user) => user.notes));
  const avgNotes = Math.round(notesPerUser.reduce((sum, user) => sum + user.notes, 0) / notesPerUser.length);
  
  return {
    notes: {
      total: totalNotes,
      byHour: notesByHour,
      byDay: notesByDay,
      byType: notesByType,
    },
    quiz: {
      total: totalQuiz,
      byType: quizByType,
    },
    flashcard: {
      total: totalFlashcard,
      byType: flashcardByType,
    },
    mindmap: {
      total: totalMindmap,
      byType: mindmapByType,
    },
    users: {
      total: userNames.length,
      notesPerUser,
      maxNotes,
      minNotes,
      avgNotes,
    },
    isMockData: true,
  };
} 
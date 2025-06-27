// feedback.js

// Hàm gửi phản hồi
function submitFeedback() {
    const rating = document.querySelector('input[name="rating"]:checked')?.value;
    const comment = document.getElementById('comment').value.trim();
  
    if (!rating) {
      alert("Vui lòng chọn số sao đánh giá.");
      return;
    }
  
    // Gửi dữ liệu phản hồi đến server
    const feedbackData = {
      rating: parseInt(rating),
      comment: comment,
      submittedAt: new Date().toISOString()
    };
  
    console.log("Đang gửi phản hồi:", feedbackData);
  
    // Giả lập gọi API
    fetch('/api/feedback/submit', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(feedbackData)
    })
    .then(response => {
      if (response.ok) {
        alert("Cảm ơn bạn đã gửi phản hồi!");
      } else {
        alert("Có lỗi xảy ra khi gửi phản hồi.");
      }
    })
    .catch(error => {
      console.error("Lỗi:", error);
      alert("Không thể gửi phản hồi.");
    });
  }
  

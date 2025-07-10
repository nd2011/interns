document.addEventListener('DOMContentLoaded', function() {
  const bell = document.getElementById('notificationBell');
  const dropdown = document.getElementById('notificationDropdown');
  if (bell && dropdown) {
      // Chỉ toggle dropdown, không mark all read
      bell.addEventListener('click', function(e) {
          dropdown.classList.toggle('show');
          e.stopPropagation();
      });
      document.addEventListener('click', function() {
          dropdown.classList.remove('show');
      });
      dropdown.addEventListener('click', function(e){
          e.stopPropagation();
      });

      // Lắng nghe tất cả các item chưa đọc (class 'unread')
      dropdown.querySelectorAll('.notification-item.unread').forEach(function(item) {
          item.addEventListener('click', function(e) {
              const notiId = this.getAttribute('data-id');
              fetch(`/notifications/mark-read/${notiId}`, { method: 'POST' })
                  .then(() => {
                      // Giảm số badge đi 1
                      const badge = document.getElementById('notificationBadge');
                      if (badge) {
                          let current = parseInt(badge.innerText, 10);
                          if (current > 1) {
                              badge.innerText = current - 1;
                          } else {
                              badge.style.display = 'none';
                          }
                      }
                      this.classList.remove('unread');
                      this.querySelector('.dot')?.remove();
                  });
              // Nếu muốn click notification tự chuyển trang:
              // window.location.href = this.querySelector('a').getAttribute('href');
              // Nếu không, để mặc định là link <a>
          });
      });
  }
});

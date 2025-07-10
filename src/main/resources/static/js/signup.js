
  document.addEventListener('DOMContentLoaded', function() {
    const bell = document.getElementById('notificationBell');
    const dropdown = document.getElementById('notificationDropdown');
    const badge = document.getElementById('notificationBadge');
    let unreadItems = dropdown ? dropdown.querySelectorAll('.notification-item.unread') : [];
    let unreadCount = unreadItems.length;
  
    function updateBadge() {
      if (badge) {
        badge.textContent = unreadCount > 0 ? unreadCount : '';
        badge.style.display = unreadCount > 0 ? 'inline-block' : 'none';
      }
    }
  
    if (bell && dropdown) {
      bell.addEventListener('click', function(e){
        dropdown.classList.toggle('show');
        e.stopPropagation();
      });
      document.addEventListener('click', function() {
        dropdown.classList.remove('show');
      });
      dropdown.addEventListener('click', function(e){
        e.stopPropagation();
      });
  
      unreadItems.forEach(function(item) {
        item.addEventListener('click', function() {
          const dot = item.querySelector('.dot');
          if(dot) dot.style.display = 'none';
          item.classList.remove('unread');
          unreadCount = Math.max(0, unreadCount - 1);
          updateBadge();
          // Nếu muốn gửi AJAX cập nhật backend, chèn code ở đây
        });
      });
  
      updateBadge();
    }
  });

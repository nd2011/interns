document.addEventListener('DOMContentLoaded', function() {
  const bell = document.getElementById('notificationBell');
  const dropdown = document.getElementById('notificationDropdown');
  if(bell && dropdown) {
      bell.addEventListener('click', function(e){
          dropdown.classList.toggle('show');
          // Gọi backend để mark all as read
          fetch('/notifications/mark-all-read', { method: 'POST' }).then(() => {
              const badge = document.getElementById('notificationBadge');
              if(badge) badge.style.display = 'none';
          });
          e.stopPropagation();
      });
      document.addEventListener('click', function() {
          dropdown.classList.remove('show');
      });
      dropdown.addEventListener('click', function(e){
          e.stopPropagation();
      });
  }
});

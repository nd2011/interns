document.addEventListener('DOMContentLoaded', function() {
  const bell = document.getElementById('notificationBell');
  const dropdown = document.getElementById('notificationDropdown');
  if(bell && dropdown) {
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
  }
});

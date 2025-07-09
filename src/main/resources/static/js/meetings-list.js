document.addEventListener('DOMContentLoaded', function() {
    const input = document.getElementById('searchMeeting');
    const table = document.getElementById('meetingTable');
    if (!input || !table) return;

    input.addEventListener('keyup', function() {
        const filter = input.value.trim().toLowerCase();
        const rows = table.querySelectorAll('tbody tr');
        rows.forEach(row => {
            // Không filter dòng "Chưa có cuộc họp nào!"
            if (row.querySelectorAll('td').length < 5) return;
            let found = false;
            row.querySelectorAll('td').forEach((td, idx) => {
                if ((idx === 0 || idx === 1) && td.textContent.toLowerCase().includes(filter)) {
                    found = true;
                }
            });
            row.style.display = found || filter === "" ? "" : "none";
        });
    });
});

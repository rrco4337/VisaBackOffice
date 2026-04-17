<%@ page contentType="text/html;charset=UTF-8" %>
        </div> <!-- End Page Content Container -->
    </div> <!-- End Main Content -->
</div> <!-- End d-flex wrapper -->

<!-- Bootstrap JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    document.addEventListener("DOMContentLoaded", function() {
        const toggleBtn = document.getElementById('sidebarToggle');
        const sidebar = document.getElementById('sidebar');
        const overlay = document.getElementById('sidebarOverlay');
        
        if(toggleBtn) {
            toggleBtn.addEventListener('click', function() {
                sidebar.classList.toggle('active');
                overlay.classList.toggle('active');
            });
        }
        if(overlay) {
            overlay.addEventListener('click', function() {
                sidebar.classList.remove('active');
                overlay.classList.remove('active');
            });
        }
    });
</script>
</body>
</html>

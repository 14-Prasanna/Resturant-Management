package org.restaurant.service.report;

import org.restaurant.config.CleverCloudDB;
import java.sql.*;

public class SystemReportService {

    public boolean generateDailyOverviewReport() {
        String reportData = "Daily System Overview:\n";

        // Aggregate data
        String qOrders = "SELECT COUNT(*) as cnt, SUM(final_amount) as total FROM customer_orders WHERE DATE(ordered_at) = CURDATE()";
        try (Connection con = CleverCloudDB.getConnection();
             Statement st = con.createStatement()) {
            
            ResultSet rs = st.executeQuery(qOrders);
            if (rs.next()) {
                reportData += "- Total Orders Today: " + rs.getInt("cnt") + "\n";
                reportData += "- Total Revenue Today: $" + String.format("%.2f", rs.getDouble("total")) + "\n";
            }

            // Insert into reports (Assuming admin_id = 1 for 'admin')
            String sql = "INSERT INTO reports (admin_id, report_type, report_data) VALUES (1, 'DAILY_OVERVIEW', ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, reportData);
                ps.executeUpdate();
                System.out.println("✅ Report generated and saved to DB successfully.\n\n" + reportData);
                return true;
            }

        } catch (Exception e) {
            System.out.println("❌ Failed to generate report: " + e.getMessage());
            return false;
        }
    }
}

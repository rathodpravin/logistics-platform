import { useEffect, useState } from "react";
import api from "../api/api";

function ManageDeliveries() {
  const [deliveries, setDeliveries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const fetchDeliveries = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await api.get("/api/deliveries");

      setDeliveries(
        Array.isArray(response.data) ? response.data : []
      );
    } catch (err) {
      console.error("Failed to fetch deliveries:", err);

      setError(
        err.response?.data?.message ||
          "Unable to load deliveries."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDeliveries();
  }, []);

  return (
    <section className="deliveries-page">
      <div className="deliveries-heading">
        <div>
          <h1>Manage Deliveries</h1>
          <p>View and monitor all delivery requests.</p>
        </div>

        <button
          className="primary-button"
          type="button"
          onClick={fetchDeliveries}
        >
          ↻ Refresh
        </button>
      </div>

      {success && (
        <p className="delivery-success" role="status">
          {success}
        </p>
      )}

      {error && (
        <div className="deliveries-error" role="alert">
          <p>{error}</p>

          <button
            className="primary-button"
            type="button"
            onClick={fetchDeliveries}
          >
            Try Again
          </button>
        </div>
      )}

      {loading ? (
        <p className="deliveries-message">
          Loading deliveries...
        </p>
      ) : deliveries.length === 0 ? (
        <div className="empty-deliveries">
          <h2>No deliveries found</h2>
        </div>
      ) : (
        <div className="deliveries-table-wrapper">
          <table className="deliveries-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Customer</th>
                <th>Driver</th>
                <th>Pickup</th>
                <th>Destination</th>
                <th>Package</th>
                <th>Weight</th>
                <th>Status</th>
              </tr>
            </thead>

            <tbody>
              {deliveries.map((delivery) => (
                <tr key={delivery.id}>
                  <td>#{delivery.id}</td>

                  <td>
                    {delivery.customerName || "-"}
                  </td>

                  <td>
                    {delivery.driverName || "Not assigned"}
                  </td>

                  <td>
                    {delivery.pickupAddress || "-"}
                  </td>

                  <td>
                    {delivery.destinationAddress || "-"}
                  </td>

                  <td>
                    {delivery.packageDescription || "-"}
                  </td>

                  <td>
                    {delivery.weight ?? "-"}
                  </td>

                  <td>
                    <span
                      className={`delivery-status ${
                        delivery.status?.toLowerCase() || "unknown"
                      }`}
                    >
                      {delivery.status || "UNKNOWN"}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}

export default ManageDeliveries;
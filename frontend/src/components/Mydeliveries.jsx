import { useCallback, useEffect, useState } from "react";
import api from "../api/api";

function MyDeliveries({ user }) {
  const [deliveries, setDeliveries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [updatingId, setUpdatingId] = useState(null);

  const isDriver = user?.role === "DRIVER";

  // Fetch deliveries for the logged-in customer or driver
  const fetchDeliveries = useCallback(async () => {
    try {
      setLoading(true);
      setError("");

      let endpoint = "/api/deliveries/customer/me";

      if (isDriver) {
        const driverId = user?.driverId;

        if (!driverId) {
          throw new Error(
            "Driver ID is missing. Please check your logged-in user details."
          );
        }

        endpoint = `/api/deliveries/driver/${driverId}`;
      }

      const response = await api.get(endpoint);

      setDeliveries(
        Array.isArray(response.data)
          ? response.data
          : response.data?.content || []
      );
    } catch (err) {
      console.error("Failed to fetch deliveries:", err);

      setError(
        err.response?.data?.message ||
          err.message ||
          "Unable to load deliveries. Please try again."
      );
    } finally {
      setLoading(false);
    }
  }, [isDriver, user?.driverId]);

  useEffect(() => {
    fetchDeliveries();
  }, [fetchDeliveries]);

  // Update delivery status
  const updateDeliveryStatus = async (deliveryId, status) => {
    try {
      setUpdatingId(deliveryId);
      setError("");
      setSuccess("");

      await api.put(
        `/api/deliveries/${deliveryId}/status`,
        null,
        {
          params: { status },
        }
      );

      setSuccess(
        `Delivery #${deliveryId} updated to ${status} successfully.`
      );

      await fetchDeliveries();
    } catch (err) {
      console.error("Failed to update delivery status:", err);

      setError(
        err.response?.data?.message ||
          err.response?.data?.error ||
          "Unable to update delivery status."
      );
    } finally {
      setUpdatingId(null);
    }
  };

  // Loading state
  if (loading) {
    return (
      <div className="deliveries-message">
        Loading deliveries...
      </div>
    );
  }

  // Error state
  if (error && deliveries.length === 0) {
    return (
      <div className="deliveries-error">
        <p>{error}</p>

        <button
          className="primary-button"
          onClick={fetchDeliveries}
          type="button"
        >
          Try Again
        </button>
      </div>
    );
  }

  return (
    <section className="deliveries-page">
      {/* Page heading */}
      <div className="deliveries-heading">
        <div>
          <h1>
            {isDriver ? "Assigned Deliveries" : "My Deliveries"}
          </h1>

          <p>
            {isDriver
              ? "View your assigned deliveries and update their status."
              : "View your delivery requests and their current status."}
          </p>
        </div>

        <button
          className="primary-button"
          onClick={fetchDeliveries}
          type="button"
        >
          ↻ Refresh
        </button>
      </div>

      {/* Success message */}
      {success && (
        <p className="delivery-success" role="status">
          {success}
        </p>
      )}

      {/* Error message */}
      {error && (
        <p className="deliveries-error" role="alert">
          {error}
        </p>
      )}

      {/* Empty state */}
      {deliveries.length === 0 ? (
        <div className="empty-deliveries">
          <div className="empty-deliveries-icon">📦</div>

          <h2>No deliveries found</h2>

          <p>
            {isDriver
              ? "You currently have no assigned deliveries."
              : "Your delivery requests will appear here."}
          </p>
        </div>
      ) : (
        /* Deliveries table */
        <div className="deliveries-table-wrapper">
          <table className="deliveries-table">
            <thead>
              <tr>
                <th>Delivery ID</th>
                <th>Pickup Location</th>
                <th>Destination</th>
                <th>Package</th>
                <th>Status</th>

                {isDriver && <th>Action</th>}
              </tr>
            </thead>

            <tbody>
              {deliveries.map((delivery) => (
                <tr key={delivery.id}>
                  <td>#{delivery.id}</td>

                  <td>
                    {delivery.pickupAddress ||
                      delivery.pickupLocation ||
                      "-"}
                  </td>

                  <td>
                    {delivery.destinationAddress ||
                      delivery.destination ||
                      "-"}
                  </td>

                  <td>
                    {delivery.packageDescription || "-"}
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

                  {/* Driver status actions */}
                  {isDriver && (
                    <td>
                      {delivery.status === "ASSIGNED" && (
                        <button
                          className="primary-button"
                          disabled={updatingId === delivery.id}
                          onClick={() =>
                            updateDeliveryStatus(
                              delivery.id,
                              "PICKED_UP"
                            )
                          }
                          type="button"
                        >
                          {updatingId === delivery.id
                            ? "Updating..."
                            : "Pick Up"}
                        </button>
                      )}

                      {delivery.status === "PICKED_UP" && (
                        <button
                          className="primary-button"
                          disabled={updatingId === delivery.id}
                          onClick={() =>
                            updateDeliveryStatus(
                              delivery.id,
                              "IN_TRANSIT"
                            )
                          }
                          type="button"
                        >
                          {updatingId === delivery.id
                            ? "Updating..."
                            : "Start Transit"}
                        </button>
                      )}

                      {delivery.status === "IN_TRANSIT" && (
                        <button
                          className="primary-button"
                          disabled={updatingId === delivery.id}
                          onClick={() =>
                            updateDeliveryStatus(
                              delivery.id,
                              "DELIVERED"
                            )
                          }
                          type="button"
                        >
                          {updatingId === delivery.id
                            ? "Updating..."
                            : "Mark Delivered"}
                        </button>
                      )}

                      {delivery.status === "DELIVERED" && (
                        <span>Completed ✓</span>
                      )}

                      {delivery.status === "CREATED" && (
                        <span>Waiting for driver assignment</span>
                      )}
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}

export default MyDeliveries;
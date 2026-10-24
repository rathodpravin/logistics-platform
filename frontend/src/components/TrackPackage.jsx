import { useState } from "react";
import api from "../api/api";

function TrackPackage() {
  const [deliveryId, setDeliveryId] = useState("");
  const [trackingHistory, setTrackingHistory] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searched, setSearched] = useState(false);
  const [error, setError] = useState("");

  const handleTrackPackage = async (event) => {
    event.preventDefault();

    const id = deliveryId.trim();

    if (!id || !/^[1-9]\d*$/.test(id)) {
      setError("Please enter a valid positive delivery ID.");
      return;
    }

    setLoading(true);
    setError("");
    setTrackingHistory([]);
    setSearched(false);

    try {
      const response = await api.get(`/api/tracking/${id}`);

      setTrackingHistory(
        Array.isArray(response.data) ? response.data : []
      );

      setSearched(true);
    } catch (err) {
      console.error("Tracking error:", err);

      if (err.response?.status === 401) {
        setError("Your session has expired. Please log in again.");
      } else if (err.response?.status === 403) {
        setError("You do not have permission to track this delivery.");
      } else if (err.response?.status === 404) {
        setError("Delivery not found. Please check the delivery ID.");
      } else {
        setError(
          err.response?.data?.message ||
            "Unable to retrieve tracking information. Please try again."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateValue) => {
    if (!dateValue) {
      return "Time unavailable";
    }

    const date = new Date(dateValue);

    if (Number.isNaN(date.getTime())) {
      return dateValue;
    }

    return date.toLocaleString();
  };

  const getStatusClass = (status) => {
    switch (status) {
      case "DELIVERED":
        return "tracking-status-delivered";
      case "IN_TRANSIT":
        return "tracking-status-transit";
      case "CANCELLED":
        return "tracking-status-cancelled";
      default:
        return "tracking-status-default";
    }
  };

  return (
    <div className="track-package-page">
      <div className="track-package-heading">
        <h2>Track Package</h2>
        <p>Track your delivery and view its status history.</p>
      </div>

      <div className="tracking-search-card">
        <form onSubmit={handleTrackPackage}>
          <label htmlFor="deliveryId">Delivery ID</label>

          <div className="tracking-search-row">
            <input
              id="deliveryId"
              type="number"
              min="1"
              placeholder="Enter your delivery ID"
              value={deliveryId}
              onChange={(event) => setDeliveryId(event.target.value)}
            />

            <button type="submit" disabled={loading}>
              {loading ? "Tracking..." : "Track Package"}
            </button>
          </div>
        </form>

        {error && (
          <div className="tracking-error" role="alert">
            {error}
          </div>
        )}
      </div>

      {searched && (
        <div className="tracking-results">
          <div className="tracking-results-header">
            <h3>Tracking History</h3>
            <span>Delivery ID: {deliveryId}</span>
          </div>

          {trackingHistory.length === 0 ? (
            <div className="tracking-empty">
              <h4>No tracking updates yet</h4>
              <p>
                Tracking updates will appear here when available.
              </p>
            </div>
          ) : (
            <div className="tracking-timeline">
              {trackingHistory.map((record, index) => (
                <div
                  className="tracking-timeline-item"
                  key={`${record.deliveryId}-${record.createdAt}-${index}`}
                >
                  <div className="tracking-timeline-marker">
                    <span />
                  </div>

                  <div className="tracking-event-card">
                    <div className="tracking-event-header">
                      <span
                        className={`tracking-status ${getStatusClass(
                          record.status
                        )}`}
                      >
                        {(record.status || "UNKNOWN").replaceAll(
                          "_",
                          " "
                        )}
                      </span>

                      <span className="tracking-event-time">
                        {formatDate(record.createdAt)}
                      </span>
                    </div>

                    <p className="tracking-event-message">
                      {record.message || "Delivery status updated."}
                    </p>

                    <small>
                      Delivery ID: {record.deliveryId}
                    </small>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default TrackPackage;
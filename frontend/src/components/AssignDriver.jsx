import { useEffect, useState } from "react";
import api from "../api/api";

function AssignDriver() {
  const [deliveries, setDeliveries] = useState([]);
  const [drivers, setDrivers] = useState([]);
  const [selectedDrivers, setSelectedDrivers] = useState({});
  const [loading, setLoading] = useState(true);
  const [assigningId, setAssigningId] = useState(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const fetchData = async () => {
    try {
      setLoading(true);
      setError("");

      const [deliveryResponse, driverResponse] = await Promise.all([
        api.get("/api/deliveries"),
        api.get("/api/drivers"),
      ]);

      setDeliveries(
        Array.isArray(deliveryResponse.data)
          ? deliveryResponse.data
          : []
      );

      setDrivers(
        Array.isArray(driverResponse.data)
          ? driverResponse.data
          : []
      );
    } catch (err) {
      console.error("Failed to load assignment data:", err);

      setError(
        err.response?.data?.message ||
          "Unable to load deliveries or drivers. Check the backend endpoints."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleAssign = async (delivery) => {
    const driverId = selectedDrivers[delivery.id];

    if (!driverId) {
      setError("Please select a driver first.");
      return;
    }

    try {
      setAssigningId(delivery.id);
      setError("");
      setSuccess("");

      await api.put(
        `/api/deliveries/${delivery.id}/assign/${driverId}`
      );

      setSuccess(
        `Driver assigned successfully to delivery #${delivery.id}.`
      );

      await fetchData();
    } catch (err) {
      console.error("Driver assignment failed:", err);

      setError(
        err.response?.data?.message ||
          "Unable to assign driver. Check driver availability and delivery status."
      );
    } finally {
      setAssigningId(null);
    }
  };

  if (loading) {
    return <p className="deliveries-message">Loading assignment data...</p>;
  }

  return (
    <section className="deliveries-page">
      <div className="deliveries-heading">
        <div>
          <h1>Assign Drivers</h1>
          <p>Assign drivers to delivery requests.</p>
        </div>

        <button
          className="primary-button"
          onClick={fetchData}
          type="button"
        >
          ↻ Refresh
        </button>
      </div>

      {error && (
        <p className="deliveries-error" role="alert">
          {error}
        </p>
      )}

      {success && (
        <p className="delivery-success" role="status">
          {success}
        </p>
      )}

      {deliveries.length === 0 ? (
        <div className="empty-deliveries">
          <h2>No deliveries found</h2>
        </div>
      ) : (
        <div className="deliveries-table-wrapper">
          <table className="deliveries-table">
            <thead>
              <tr>
                <th>Delivery ID</th>
                <th>Customer</th>
                <th>Pickup</th>
                <th>Destination</th>
                <th>Status</th>
                <th>Assigned Driver</th>
                <th>Action</th>
              </tr>
            </thead>

            <tbody>
              {deliveries.map((delivery) => (
                <tr key={delivery.id}>
                  <td>#{delivery.id}</td>
                  <td>{delivery.customerName || "-"}</td>
                  <td>{delivery.pickupAddress || "-"}</td>
                  <td>{delivery.destinationAddress || "-"}</td>
                  <td>{delivery.status || "UNKNOWN"}</td>

                  <td>{delivery.driverName || "Not assigned"}</td>

                  <td>
                    {delivery.status === "CREATED" ? (
                      <>
                        <select
                          value={selectedDrivers[delivery.id] || ""}
                          onChange={(event) =>
                            setSelectedDrivers((previous) => ({
                              ...previous,
                              [delivery.id]: event.target.value,
                            }))
                          }
                        >
                          <option value="">Select driver</option>

                          {drivers
                            .filter(
                              (driver) => driver.status === "AVAILABLE"
                            )
                            .map((driver) => (
                              <option
                                key={driver.id}
                                value={driver.id}
                              >
                                {driver.name || driver.fullName || driver.email}
                                {" "} (ID: {driver.id})
                              </option>
                            ))}
                        </select>

                        <button
                          className="primary-button"
                          type="button"
                          disabled={assigningId === delivery.id}
                          onClick={() => handleAssign(delivery)}
                        >
                          {assigningId === delivery.id
                            ? "Assigning..."
                            : "Assign"}
                        </button>
                      </>
                    ) : (
                      <span>
                        {delivery.status === "DELIVERED"
                          ? "Completed"
                          : "Already assigned or in progress"}
                      </span>
                    )}
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

export default AssignDriver;
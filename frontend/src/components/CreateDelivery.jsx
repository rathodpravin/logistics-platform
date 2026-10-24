import { useState } from "react";
import api from "../api/api";

function CreateDelivery({ onDeliveryCreated, onCancel }) {
  const [formData, setFormData] = useState({
    pickupAddress: "",
    destinationAddress: "",
    packageDescription: "",
    weight: "",
  });

  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    setMessage("");
    setError("");

    const weight = Number(formData.weight);

    if (!Number.isFinite(weight) || weight <= 0) {
      setError("Please enter a valid weight greater than zero.");
      return;
    }

    setLoading(true);

    try {
      const requestData = {
        pickupAddress: formData.pickupAddress.trim(),
        destinationAddress: formData.destinationAddress.trim(),
        packageDescription: formData.packageDescription.trim(),
        weight,
      };

      const response = await api.post(
        "/api/deliveries",
        requestData
      );

      setMessage(
        `Delivery #${response.data.id ?? response.data.deliveryId ?? ""} created successfully!`
      );

      setFormData({
        pickupAddress: "",
        destinationAddress: "",
        packageDescription: "",
        weight: "",
      });

      if (onDeliveryCreated) {
        setTimeout(() => {
          onDeliveryCreated();
        }, 1000);
      }
    } catch (err) {
      console.error("Create delivery error:", err);

      if (err.response?.status === 401) {
        setError("Your session has expired. Please log in again.");
      } else if (err.response?.status === 403) {
        setError(
          "Access denied. Only customers can create deliveries. Please log in with a CUSTOMER account."
        );
      } else {
        setError(
          err.response?.data?.message ||
            "Unable to create delivery. Please check your details and try again."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="create-delivery-page">
      <div className="create-delivery-heading">
        <div>
          <span className="page-eyebrow">DELIVERY MANAGEMENT</span>
          <h1>Create Delivery</h1>
          <p>Enter your package and delivery address details.</p>
        </div>

        <div className="create-delivery-icon">📦</div>
      </div>

      <div className="delivery-form-card">
        <div className="form-card-heading">
          <h2>Delivery Information</h2>
          <p>Fields marked with * are required.</p>
        </div>

        {message && (
          <div className="form-success" role="status">
            ✅ {message}
          </div>
        )}

        {error && (
          <div className="form-error" role="alert">
            ⚠️ {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="delivery-form-grid">
            <div className="delivery-form-group">
              <label htmlFor="pickupAddress">
                Pickup Address *
              </label>

              <input
                id="pickupAddress"
                name="pickupAddress"
                type="text"
                placeholder="Enter pickup address"
                value={formData.pickupAddress}
                onChange={handleChange}
                required
                maxLength={255}
              />
            </div>

            <div className="delivery-form-group">
              <label htmlFor="destinationAddress">
                Destination Address *
              </label>

              <input
                id="destinationAddress"
                name="destinationAddress"
                type="text"
                placeholder="Enter destination address"
                value={formData.destinationAddress}
                onChange={handleChange}
                required
                maxLength={255}
              />
            </div>

            <div className="delivery-form-group full-width">
              <label htmlFor="packageDescription">
                Package Description *
              </label>

              <textarea
                id="packageDescription"
                name="packageDescription"
                placeholder="Describe your package"
                value={formData.packageDescription}
                onChange={handleChange}
                required
                rows={4}
                maxLength={500}
              />
            </div>

            <div className="delivery-form-group">
              <label htmlFor="weight">
                Package Weight (kg) *
              </label>

              <input
                id="weight"
                name="weight"
                type="number"
                placeholder="e.g. 2.5"
                value={formData.weight}
                onChange={handleChange}
                min="0.01"
                max="10000"
                step="0.01"
                required
              />

              <small>Enter the weight in kilograms.</small>
            </div>
          </div>

          <div className="delivery-form-actions">
            {onCancel && (
              <button
                className="secondary-button"
                type="button"
                onClick={onCancel}
                disabled={loading}
              >
                Cancel
              </button>
            )}

            <button
              className="primary-button submit-delivery-button"
              type="submit"
              disabled={loading}
            >
              {loading
                ? "Creating Delivery..."
                : "＋ Create Delivery"}
            </button>
          </div>
        </form>
      </div>
    </section>
  );
}

export default CreateDelivery;
import { useState } from "react";

import MyDeliveries from "./MyDeliveries";
import CreateDelivery from "./CreateDelivery";
import TrackPackage from "./TrackPackage";
import ManageDeliveries from "./ManageDeliveries";
import AssignDriver from "./AssignDriver";

function Dashboard({ user, onLogout }) {
  const [activePage, setActivePage] = useState("Dashboard");

  // Dashboard title and welcome message
  let dashboardTitle = "Dashboard";

  let welcomeMessage =
    "Manage your logistics activities from one place.";

  if (user.role === "CUSTOMER") {
    dashboardTitle = "Customer Dashboard";

    welcomeMessage =
      "Create deliveries and keep track of your packages.";
  } else if (user.role === "DRIVER") {
    dashboardTitle = "Driver Dashboard";

    welcomeMessage =
      "View assigned deliveries and manage your delivery work.";
  } else if (user.role === "ADMIN") {
    dashboardTitle = "Admin Dashboard";

    welcomeMessage =
      "Monitor deliveries and manage your logistics operations.";
  }

  // Role-based dashboard actions
  const actions = {
    CUSTOMER: [
      {
        icon: "📦",
        title: "Create Delivery",
        description: "Create a new delivery request.",
        button: "Create Delivery",
      },
      {
        icon: "🚚",
        title: "My Deliveries",
        description: "View your delivery requests and their status.",
        button: "View Deliveries",
      },
      {
        icon: "📍",
        title: "Track Package",
        description: "Check the tracking history of a delivery.",
        button: "Track Package",
      },
    ],

    DRIVER: [
      {
        icon: "🚚",
        title: "Assigned Deliveries",
        description: "View deliveries assigned to you.",
        button: "View Deliveries",
      },
      {
        icon: "📋",
        title: "Delivery Status",
        description: "Manage the status of your active deliveries.",
        button: "Update Status",
      },
      {
        icon: "📍",
        title: "Location Tracking",
        description: "Send your latest location to the platform.",
        button: "Update Location",
      },
    ],

    ADMIN: [
      {
        icon: "👥",
        title: "Manage Drivers",
        description: "Review drivers and their availability.",
        button: "View Drivers",
      },
      {
        icon: "🚚",
        title: "Assign Drivers",
        description: "Assign available drivers to new deliveries.",
        button: "Assign Drivers",
      },
      {
        icon: "📦",
        title: "Manage Deliveries",
        description: "Monitor and view delivery requests.",
        button: "View Deliveries",
      },
      {
        icon: "📊",
        title: "Operations",
        description: "Access logistics operations and delivery information.",
        button: "View Operations",
      },
    ],
  };

  const userActions = actions[user.role] || [];

  // Navigate to the selected page
  const handleNavigation = (pageTitle) => {
    if (
      pageTitle === "My Deliveries" ||
      pageTitle === "Assigned Deliveries"
    ) {
      setActivePage("My Deliveries");
    } else {
      setActivePage(pageTitle);
    }
  };

  // Render the selected page
  const renderPage = () => {
    switch (activePage) {
      case "Create Delivery":
        return (
          <CreateDelivery
            onDeliveryCreated={() => setActivePage("My Deliveries")}
            onCancel={() => setActivePage("Dashboard")}
          />
        );

      case "My Deliveries":
        return <MyDeliveries user={user} />;

      case "Track Package":
        return <TrackPackage />;

      case "Manage Deliveries":
        return <ManageDeliveries />;

      case "Assign Drivers":
        return <AssignDriver />;

      default:
        return (
          <>
            <section className="welcome-card">
              <h2>Hello, {user.name || user.email}! 👋</h2>
              <p>{welcomeMessage}</p>
            </section>

            <h2 className="dashboard-section-title">
              Quick Actions
            </h2>

            <section className="dashboard-grid">
              {userActions.map((action) => (
                <article
                  className="dashboard-card"
                  key={action.title}
                >
                  <div className="card-icon">
                    {action.icon}
                  </div>

                  <h3>{action.title}</h3>

                  <p>{action.description}</p>

                  <button
                    className="primary-button"
                    onClick={() => handleNavigation(action.title)}
                    type="button"
                  >
                    {action.button} →
                  </button>
                </article>
              ))}
            </section>
          </>
        );
    }
  };

  return (
    <div className="dashboard-layout">
      {/* Sidebar */}
      <aside className="sidebar">
        <div>
          <h2>📦 LogisticsPro</h2>

          <p className="brand-subtitle">
            Delivery Management System
          </p>
        </div>

        <nav className="sidebar-menu">
          <button
            className={activePage === "Dashboard" ? "active" : ""}
            onClick={() => setActivePage("Dashboard")}
            type="button"
          >
            ▦ Dashboard
          </button>

          {userActions.map((action) => (
            <button
              key={action.title}
              className={
                activePage === action.title ||
                (activePage === "My Deliveries" &&
                  action.title === "Assigned Deliveries")
                  ? "active"
                  : ""
              }
              onClick={() => handleNavigation(action.title)}
              type="button"
            >
              {action.icon} {action.title}
            </button>
          ))}
        </nav>

        {/* Sidebar user information */}
        <div className="sidebar-user">
          <div className="user-avatar">
            {(user.name || user.email || "U")
              .charAt(0)
              .toUpperCase()}
          </div>

          <div className="user-info">
            <span className="user-name">
              {user.name || user.email}
            </span>

            <span className="user-role">
              {user.role}
            </span>
          </div>
        </div>
      </aside>

      {/* Main dashboard */}
      <main className="dashboard-main">
        {/* Header */}
        <header className="dashboard-header">
          <div className="header-title">
            <h1>
              {activePage === "Dashboard"
                ? dashboardTitle
                : activePage}
            </h1>

            <p>
              {activePage === "Dashboard"
                ? welcomeMessage
                : `Manage your ${activePage.toLowerCase()} here.`}
            </p>
          </div>

          <div className="header-actions">
            <div className="user-profile">
              <div className="user-avatar">
                {(user.name || user.email || "U")
                  .charAt(0)
                  .toUpperCase()}
              </div>

              <div className="user-info">
                <span className="user-name">
                  {user.name || user.email}
                </span>

                <span className="user-role">
                  {user.role}
                </span>
              </div>
            </div>

            <button
              className="logout-button"
              onClick={onLogout}
              type="button"
            >
              <span>↪</span> Logout
            </button>
          </div>
        </header>

        {/* Selected page content */}
        {renderPage()}
      </main>
    </div>
  );
}

export default Dashboard;
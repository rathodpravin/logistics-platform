import { useState } from "react";
import Login from "./components/Login";
import Dashboard from "./components/Dashboard";
import LiveLocationTest from "./LiveLocationTest.jsx";
import "./App.css";

function App() {
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem("user");
    return savedUser ? JSON.parse(savedUser) : null;
  });

  const [showLocationTest, setShowLocationTest] = useState(false);

  const handleLoginSuccess = (data) => {
    setUser(data);
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setUser(null);
    setShowLocationTest(false);
  };

  if (!user) {
    return <Login onLoginSuccess={handleLoginSuccess} />;
  }

  return (
    <div>
      <Dashboard user={user} onLogout={handleLogout} />

      <hr />

      <div style={{ padding: "20px" }}>
        <button onClick={() => setShowLocationTest(!showLocationTest)}>
          {showLocationTest
            ? "Hide Live Location Test"
            : "Test Live Driver Location"}
        </button>
      </div>

      {showLocationTest && <LiveLocationTest />}
    </div>
  );
}

export default App;

import { useEffect, useState } from "react";
import { Client } from "@stomp/stompjs";

function LiveLocationTest() {
    const [location, setLocation] = useState(null);
    const [connectionStatus, setConnectionStatus] = useState("Connecting...");
    const driverId = 2;

    useEffect(() => {
        const client = new Client({
            brokerURL: "ws://localhost:8080/ws-location",

            reconnectDelay: 5000,

            onConnect: () => {
                setConnectionStatus("Connected");

                client.subscribe(
                    `/topic/driver/${driverId}/location`,
                    (message) => {
                        const receivedLocation = JSON.parse(message.body);
                        setLocation(receivedLocation);
                    }
                );
            },

            onWebSocketError: (error) => {
                console.error("WebSocket error:", error);
                setConnectionStatus("Connection error");
            },

            onStompError: (frame) => {
                console.error("STOMP error:", frame.headers.message);
                setConnectionStatus("STOMP error");
            },

            onDisconnect: () => {
                setConnectionStatus("Disconnected");
            },
        });

        client.activate();

        return () => {
            client.deactivate();
        };
    }, []);

    return (

        <div style={{ padding: "20px" }}>
            <h2>Live Driver Location Test</h2>

            <p style={{ color: "green", fontWeight: "bold" }}>
                React component is rendering successfully!
            </p>

            <p>
                <strong>Connection Status:</strong> {connectionStatus}
            </p>

            {location ? (
                <div>
                    <p>Latitude: {location.latitude}</p>
                    <p>Longitude: {location.longitude}</p>
                    <p>Recorded At: {location.recordedAt}</p>
                </div>
            ) : (
                <p>Waiting for a new location update...</p>
            )}
        </div>
    );

}

export default LiveLocationTest;
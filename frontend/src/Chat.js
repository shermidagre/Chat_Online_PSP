import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import './App.css'; // Import App.css for styling

const Chat = () => {
  const [messages, setMessages] = useState([]);
  const [messageInput, setMessageInput] = useState('');
  const [isConnected, setIsConnected] = useState(false);
  const [error, setError] = useState('');
  const ws = useRef(null);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('jwt_token');
    if (!token) {
      navigate('/login');
      return;
    }

    ws.current = new WebSocket('ws://localhost:8081/chat'); // Connect to servicio-chat

    ws.current.onopen = () => {
      console.log('WebSocket Connected');
      setIsConnected(true);
      ws.current.send(token); // Send JWT token for authentication
    };

    ws.current.onmessage = (event) => {
      setMessages((prevMessages) => [...prevMessages, event.data]);
    };

    ws.current.onclose = () => {
      console.log('WebSocket Disconnected');
      setIsConnected(false);
      // Optionally, handle re-connection or show an error
    };

    ws.current.onerror = (err) => {
      console.error('WebSocket Error:', err);
      setError('WebSocket connection error. Please try again.');
    };

    return () => {
      if (ws.current) {
        ws.current.close();
      }
    };
  }, [navigate]);

  const sendMessage = (e) => {
    e.preventDefault();
    if (ws.current && ws.current.readyState === WebSocket.OPEN && messageInput.trim()) {
      ws.current.send(messageInput);
      setMessageInput('');
    }
  };

  const logout = () => {
    localStorage.removeItem('jwt_token');
    navigate('/login');
  };

  return (
    <div className="chat-container">
      <h2>Chat Page</h2>
      {error && <p className="error-message">{error}</p>}
      <div className="chat-messages">
        {messages.map((msg, index) => (
          <p key={index}>{msg}</p>
        ))}
      </div>
      <form onSubmit={sendMessage} className="chat-input-form">
        <input
          type="text"
          value={messageInput}
          onChange={(e) => setMessageInput(e.target.value)}
          placeholder="Type your message..."
          disabled={!isConnected}
        />
        <button type="submit" disabled={!isConnected}>Send</button>
      </form>
      <button onClick={logout} className="logout-button">Logout</button>
    </div>
  );
};

export default Chat;
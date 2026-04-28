import { useEffect, useRef, useCallback, useState } from 'react';

interface UseWebSocketOptions {
  onOpen?: (event: Event) => void;
  onClose?: (event: CloseEvent) => void;
  onError?: (event: Event) => void;
  onMessage?: (event: MessageEvent) => void;
  reconnect?: boolean;
  reconnectInterval?: number;
  reconnectAttempts?: number;
}

interface UseWebSocketResult {
  readyState: number;
  send: (data: string | ArrayBuffer | Blob) => void;
  close: () => void;
}

export function useWebSocket(url: string, options: UseWebSocketOptions = {}): UseWebSocketResult {
  const {
    onOpen,
    onClose,
    onError,
    onMessage,
    reconnect = true,
    reconnectInterval = 3000,
    reconnectAttempts = 5,
  } = options;

  const wsRef = useRef<WebSocket | null>(null);
  const attemptsRef = useRef(0);
  const [readyState, setReadyState] = useState(WebSocket.CONNECTING);

  const connect = useCallback(() => {
    const ws = new WebSocket(url);
    wsRef.current = ws;

    ws.onopen = (event) => {
      setReadyState(WebSocket.OPEN);
      attemptsRef.current = 0;
      onOpen?.(event);
    };

    ws.onclose = (event) => {
      setReadyState(WebSocket.CLOSED);
      onClose?.(event);

      if (reconnect && attemptsRef.current < reconnectAttempts) {
        attemptsRef.current++;
        setTimeout(connect, reconnectInterval);
      }
    };

    ws.onerror = (event) => {
      onError?.(event);
    };

    ws.onmessage = (event) => {
      onMessage?.(event);
    };
  }, [url, onOpen, onClose, onError, onMessage, reconnect, reconnectInterval, reconnectAttempts]);

  useEffect(() => {
    connect();
    return () => {
      wsRef.current?.close();
    };
  }, [connect]);

  const send = useCallback((data: string | ArrayBuffer | Blob) => {
    if (wsRef.current?.readyState === WebSocket.OPEN) {
      wsRef.current.send(data);
    }
  }, []);

  const close = useCallback(() => {
    wsRef.current?.close();
  }, []);

  return { readyState, send, close };
}

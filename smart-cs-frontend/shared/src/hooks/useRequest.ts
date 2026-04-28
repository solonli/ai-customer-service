import { useState, useCallback } from 'react';
import axios, { AxiosRequestConfig, AxiosError } from 'axios';

interface UseRequestResult<T> {
  data: T | null;
  loading: boolean;
  error: AxiosError | null;
  execute: (config?: AxiosRequestConfig) => Promise<T | null>;
}

export function useRequest<T>(defaultConfig?: AxiosRequestConfig): UseRequestResult<T> {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<AxiosError | null>(null);

  const execute = useCallback(async (config?: AxiosRequestConfig): Promise<T | null> => {
    setLoading(true);
    setError(null);
    try {
      const response = await axios({ ...defaultConfig, ...config });
      const result = response.data as T;
      setData(result);
      return result;
    } catch (err) {
      setError(err as AxiosError);
      return null;
    } finally {
      setLoading(false);
    }
  }, [defaultConfig]);

  return { data, loading, error, execute };
}

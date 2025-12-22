import axios from "axios";
import type { LogEntry, MessagePayload, PageResponse } from "../types";

export const API_BASE_URL = "http://localhost:8080/api/v1";

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000, // Increased from 5s to 15s to handle first JMS connection initialization
});

export const fetchLogs = async (
  page: number,
  size: number,
  search: string
): Promise<PageResponse<LogEntry>> => {
  const { data } = await api.get<PageResponse<LogEntry>>("/notifications", {
    params: {
      page,
      size,
      search,
    },
  });
  return data;
};

export const sendMessage = async (payload: MessagePayload) => {
  const { data } = await api.post("/notifications", payload, {
    timeout: 20000, // Give POST request more time for first connection to JMS
  });
  return data;
};

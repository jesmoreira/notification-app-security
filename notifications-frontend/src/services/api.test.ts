const mockGet = jest.fn();
const mockPost = jest.fn();

jest.mock("axios", () => ({
  create: jest.fn(() => ({
    get: mockGet,
    post: mockPost,
  })),
}));

import { fetchLogs, sendMessage, API_BASE_URL } from "./api";
import type { MessagePayload } from "../types";

describe("API Service", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe("Constants", () => {
    it("should have the correct API base URL", () => {
      expect(API_BASE_URL).toBe("http://localhost:8080/api/v1");
    });
  });

  describe("fetchLogs", () => {
    it("should call api.get with correct parameters and return data", async () => {
      const page = 0;
      const size = 10;
      const search = "error";
      const mockResponse = {
        data: {
          content: [],
          totalElements: 0,
        },
      };

      mockGet.mockResolvedValue(mockResponse);

      const result = await fetchLogs(page, size, search);

      expect(mockGet).toHaveBeenCalledTimes(1);
      expect(mockGet).toHaveBeenCalledWith("/notifications", {
        params: {
          page,
          size,
          search,
        },
      });
      expect(result).toEqual(mockResponse.data);
    });

    it("should propagate errors from axios", async () => {
      const error = new Error("Network Error");
      mockGet.mockRejectedValue(error);

      await expect(fetchLogs(0, 10, "")).rejects.toThrow("Network Error");
    });
  });

  describe("sendMessage", () => {
    it("should call api.post with correct payload and return response", async () => {
      const payload: MessagePayload = {
        category: "FINANCE",
        message: "Test message",
      };
      const mockResponse = { data: { id: "uuid-123" } };

      mockPost.mockResolvedValue(mockResponse);

      const result = await sendMessage(payload);

      expect(mockPost).toHaveBeenCalledTimes(1);
      expect(mockPost).toHaveBeenCalledWith("/notifications", payload, {
        timeout: 20000,
      });
      expect(result).toEqual(mockResponse.data);
    });

    it("should propagate errors when sending fails", async () => {
      const payload: MessagePayload = {
        category: "SYSTEM",
        message: "Alert",
      };
      const error = new Error("Bad Request");
      mockPost.mockRejectedValue(error);

      await expect(sendMessage(payload)).rejects.toThrow("Bad Request");
    });
  });
});

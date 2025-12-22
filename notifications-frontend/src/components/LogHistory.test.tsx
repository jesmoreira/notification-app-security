import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { LogHistory } from "./LogHistory";
import * as apiModule from "../services/api";

jest.mock("../services/api", () => ({
  fetchLogs: jest.fn(),
}));

const createTestWrapper = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
    },
  });
  return {
    queryClient,
    wrapper: ({ children }: { children: React.ReactNode }) => (
      <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
    ),
  };
};

const mockLogsPage0 = {
  content: [
    {
      id: "1",
      timestamp: "2023-10-01T10:00:00Z",
      userName: "John Doe",
      category: "FINANCE",
      channel: "WEB",
      message: "Transaction approved",
    },
    {
      id: "2",
      timestamp: "2023-10-01T10:05:00Z",
      userName: "Jane Smith",
      category: "SYSTEM",
      channel: "MOBILE",
      message: "User login",
    },
  ],
  totalElements: 20,
  totalPages: 2,
  first: true,
  last: false,
  pageable: { pageNumber: 0 },
};

const mockLogsPage1 = {
  ...mockLogsPage0,
  content: [
    {
      id: "3",
      timestamp: "2023-10-01T11:00:00Z",
      userName: "Alice",
      category: "FINANCE",
      channel: "WEB",
      message: "Refund initiated",
    },
  ],
  first: false,
  last: true,
  pageable: { pageNumber: 1 },
};

describe("LogHistory Component", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("renders loading skeletons initially", () => {
    (apiModule.fetchLogs as jest.Mock).mockImplementation(
      () => new Promise(() => {})
    );
    const { wrapper } = createTestWrapper();

    render(<LogHistory />, { wrapper });

    const skeletons = screen.getAllByTestId("loading-skeleton");
    expect(skeletons.length).toBeGreaterThan(0);
  });

  it("renders log data successfully", async () => {
    (apiModule.fetchLogs as jest.Mock).mockResolvedValue(mockLogsPage0);
    const { wrapper } = createTestWrapper();

    render(<LogHistory />, { wrapper });

    await waitFor(() =>
      expect(screen.getByText("Transaction approved")).toBeInTheDocument()
    );
    expect(screen.getByText("John Doe")).toBeInTheDocument();
    expect(screen.getByText("FINANCE")).toBeInTheDocument();
    expect(
      screen.getByText((content, element) => {
        return element?.textContent === "Showing 2 of 20 records";
      })
    ).toBeInTheDocument();
  });

  it("handles pagination correctly", async () => {
    (apiModule.fetchLogs as jest.Mock)
      .mockResolvedValueOnce(mockLogsPage0)
      .mockResolvedValueOnce(mockLogsPage1);

    const { wrapper } = createTestWrapper();
    render(<LogHistory />, { wrapper });

    await waitFor(() =>
      expect(screen.getByText("Transaction approved")).toBeInTheDocument()
    );

    const nextButton = screen.getByRole("button", { name: /next/i });
    fireEvent.click(nextButton);

    await waitFor(() =>
      expect(apiModule.fetchLogs).toHaveBeenCalledWith(1, 10, "")
    );

    await waitFor(() =>
      expect(screen.getByText("Refund initiated")).toBeInTheDocument()
    );
  });

  it("handles search with debounce", async () => {
    (apiModule.fetchLogs as jest.Mock).mockResolvedValue(mockLogsPage0);
    const { wrapper } = createTestWrapper();

    render(<LogHistory />, { wrapper });

    const searchInput = screen.getByPlaceholderText("Search logs...");

    fireEvent.change(searchInput, { target: { value: "test" } });

    await waitFor(
      () => {
        expect(apiModule.fetchLogs).toHaveBeenCalledWith(0, 10, "test");
      },
      { timeout: 1000 }
    );
  });

  it("displays error message on API failure", async () => {
    (apiModule.fetchLogs as jest.Mock).mockRejectedValue(
      new Error("API Error")
    );
    const { wrapper } = createTestWrapper();

    render(<LogHistory />, { wrapper });

    await waitFor(() => {
      expect(screen.getByText(/failed to load data/i)).toBeInTheDocument();
    });
  });

  it("handles page size change correctly", async () => {
    (apiModule.fetchLogs as jest.Mock)
      .mockResolvedValueOnce(mockLogsPage0)
      .mockResolvedValueOnce(mockLogsPage1);

    const { wrapper } = createTestWrapper();
    render(<LogHistory />, { wrapper });

    await waitFor(() =>
      expect(screen.getByText("Transaction approved")).toBeInTheDocument()
    );

    const pageSizeSelect = screen.getByLabelText("Rows per page");
    fireEvent.change(pageSizeSelect, { target: { value: "50" } });

    await waitFor(() => {
      expect(apiModule.fetchLogs).toHaveBeenCalledWith(0, 50, "");
    });
  });
});

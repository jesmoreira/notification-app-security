jest.mock("@tanstack/react-query", () => {
  const actual = jest.requireActual("@tanstack/react-query");
  return {
    ...actual,
    useMutation: jest.fn((options) => {
      return {
        mutate: jest.fn((data) => {
          if (options?.onSuccess) {
            options.onSuccess();
          }
        }),
        isPending: false,
        isError: false,
        isSuccess: false,
        error: null,
        data: null,
        status: "idle",
      };
    }),
    useQueryClient: jest.fn(() => ({
      invalidateQueries: jest.fn(),
    })),
  };
});

jest.mock("../services/api", () => ({
  sendMessage: jest.fn(),
}));

import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { SubmissionForm } from "./SubmissionForm";

const createTestWrapper = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      mutations: {
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

describe("SubmissionForm Component", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    global.alert = jest.fn();
    (useMutation as jest.Mock).mockImplementation((options) => {
      return {
        mutate: jest.fn((data) => {
          if (options?.onSuccess) {
            options.onSuccess();
          }
        }),
        isPending: false,
        isError: false,
        isSuccess: false,
        error: null,
        data: null,
        status: "idle",
      };
    });
  });

  it("renders form with all fields", () => {
    const { wrapper } = createTestWrapper();
    render(<SubmissionForm />, { wrapper });

    expect(screen.getByText("Send Notification")).toBeInTheDocument();
    expect(screen.getByLabelText("Category")).toBeInTheDocument();
    expect(screen.getByLabelText("Message")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /send/i })).toBeInTheDocument();
  });

  it("renders all category options", () => {
    const { wrapper } = createTestWrapper();
    render(<SubmissionForm />, { wrapper });

    const categorySelect = screen.getByLabelText(
      "Category"
    ) as HTMLSelectElement;
    const options = Array.from(categorySelect.options).map((opt) => opt.value);

    expect(options).toContain("Sports");
    expect(options).toContain("Finance");
    expect(options).toContain("Movies");
  });

  it("displays error message when message is empty on submit", async () => {
    const user = userEvent.setup();
    const { wrapper } = createTestWrapper();
    render(<SubmissionForm />, { wrapper });

    const submitButton = screen.getByRole("button", { name: /send/i });
    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText("Message cannot be empty.")).toBeInTheDocument();
    });
  });

  it("submits form with valid data", async () => {
    const user = userEvent.setup();
    const mutate = jest.fn();
    (useMutation as jest.Mock).mockReturnValue({
      mutate,
      isPending: false,
      isError: false,
      isSuccess: false,
      error: null,
      data: null,
      status: "idle",
    });

    const { wrapper } = createTestWrapper();
    render(<SubmissionForm />, { wrapper });

    const categorySelect = screen.getByLabelText("Category");
    const messageTextarea = screen.getByLabelText("Message");
    const submitButton = screen.getByRole("button", { name: /send/i });

    await user.selectOptions(categorySelect, "Finance");
    await user.type(messageTextarea, "Test message");
    await user.click(submitButton);

    await waitFor(() => {
      expect(mutate).toHaveBeenCalledWith({
        category: "Finance",
        message: "Test message",
      });
    });
  });

  it("displays success message on successful submission", async () => {
    const user = userEvent.setup();
    const { wrapper } = createTestWrapper();
    render(<SubmissionForm />, { wrapper });

    const messageTextarea = screen.getByLabelText("Message");
    const submitButton = screen.getByRole("button", { name: /send/i });

    await user.type(messageTextarea, "Test message");
    await user.click(submitButton);

    await waitFor(() => {
      expect(global.alert).toHaveBeenCalledWith("Message sent successfully!");
    });
  });

  it("displays error message on failed submission", async () => {
    const user = userEvent.setup();
    (useMutation as jest.Mock).mockImplementation((options) => {
      return {
        mutate: jest.fn((data) => {
          if (options.onError) {
            options.onError();
          }
        }),
        isPending: false,
        isError: false,
        isSuccess: false,
        error: null,
        data: null,
        status: "idle",
      };
    });

    const { wrapper } = createTestWrapper();
    render(<SubmissionForm />, { wrapper });

    const messageTextarea = screen.getByLabelText("Message");
    const submitButton = screen.getByRole("button", { name: /send/i });

    await user.type(messageTextarea, "Test message");
    await user.click(submitButton);

    await waitFor(() => {
      expect(global.alert).toHaveBeenCalledWith(
        "Failed to send. Please check the backend connection."
      );
    });
  });

  it("resets form after successful submission", async () => {
    const user = userEvent.setup();
    const { wrapper } = createTestWrapper();
    render(<SubmissionForm />, { wrapper });

    const messageTextarea = screen.getByLabelText(
      "Message"
    ) as HTMLTextAreaElement;
    const submitButton = screen.getByRole("button", { name: /send/i });

    await user.type(messageTextarea, "Test message");
    await user.click(submitButton);

    await waitFor(() => {
      expect(messageTextarea.value).toBe("");
    });
  });

  it("disables submit button while submitting", async () => {
    const user = userEvent.setup();
    (useMutation as jest.Mock).mockReturnValue({
      mutate: jest.fn(),
      isPending: true,
      isError: false,
      isSuccess: false,
      error: null,
      data: null,
      status: "pending",
    });

    const { wrapper } = createTestWrapper();
    render(<SubmissionForm />, { wrapper });

    const submitButton = screen.getByRole("button", {
      name: /sending/i,
    }) as HTMLButtonElement;

    expect(submitButton).toBeDisabled();
    expect(screen.getByText("Sending...")).toBeInTheDocument();
  });

  it("invalidates logs query on successful submission", async () => {
    const user = userEvent.setup();
    const invalidateQueries = jest.fn();
    (useQueryClient as jest.Mock).mockReturnValue({
      invalidateQueries,
    });

    const { wrapper } = createTestWrapper();
    render(<SubmissionForm />, { wrapper });

    const messageTextarea = screen.getByLabelText("Message");
    const submitButton = screen.getByRole("button", { name: /send/i });

    await user.type(messageTextarea, "Test message");
    await user.click(submitButton);

    await waitFor(() => {
      expect(invalidateQueries).toHaveBeenCalledWith({ queryKey: ["logs"] });
    });
  });
});

import { useForm } from "react-hook-form";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { sendMessage } from "../services/api";
import type { MessagePayload } from "../types";

const CATEGORIES = ["Sports", "Finance", "Movies"];

export const SubmissionForm = () => {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<MessagePayload>();
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: sendMessage,
    onSuccess: () => {
      reset();
      queryClient.invalidateQueries({ queryKey: ["logs"] });
      alert("Message sent successfully!");
    },
    onError: () =>
      alert("Failed to send. Please check the backend connection."),
  });

  const onSubmit = (data: MessagePayload) => mutation.mutate(data);

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className="p-6 bg-white rounded shadow-md mb-6"
    >
      <h2 className="text-xl font-bold mb-4 text-gray-800">
        Send Notification
      </h2>

      <div className="mb-4">
        <label
          htmlFor="category"
          className="block text-gray-700 font-medium mb-2"
        >
          Category
        </label>
        <select
          id="category"
          {...register("category")}
          className="w-full p-2 border rounded bg-gray-50"
        >
          {CATEGORIES.map((cat) => (
            <option key={cat} value={cat}>
              {cat}
            </option>
          ))}
        </select>
      </div>

      <div className="mb-4">
        <label
          htmlFor="message"
          className="block text-gray-700 font-medium mb-2"
        >
          Message
        </label>
        <textarea
          id="message"
          {...register("message", {
            required: "Message cannot be empty.",
          })}
          className="w-full p-2 border rounded focus:ring-2 focus:ring-blue-500"
          rows={3}
        />
        {errors.message && (
          <span className="text-red-500 text-sm">{errors.message.message}</span>
        )}
      </div>

      <button
        type="submit"
        disabled={mutation.isPending}
        className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition-colors disabled:opacity-50"
      >
        {mutation.isPending ? "Sending..." : "Send"}
      </button>
    </form>
  );
};

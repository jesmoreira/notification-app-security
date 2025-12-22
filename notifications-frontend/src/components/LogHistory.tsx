import { useState, useEffect, type ChangeEvent } from "react";
import { useQuery } from "@tanstack/react-query";
import { fetchLogs } from "../services/api";

interface LogEntry {
  id: string;
  timestamp: string;
  userName: string;
  category: string;
  channel: string;
  message: string;
}

interface LogResponse {
  content: LogEntry[];
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  pageable: {
    pageNumber: number;
  };
}

function useDebounce<T>(value: T, delay: number): T {
  const [debouncedValue, setDebouncedValue] = useState(value);

  useEffect(() => {
    const handler = setTimeout(() => setDebouncedValue(value), delay);
    return () => clearTimeout(handler);
  }, [value, delay]);

  return debouncedValue;
}

const LogBadge = ({ category }: { category: string }) => {
  const isFinance = category === "FINANCE";
  const baseClasses = "px-2 py-0.5 rounded text-[10px] font-bold uppercase";
  const colorClasses = isFinance
    ? "bg-green-100 text-green-800"
    : "bg-gray-100 text-gray-800";

  return <span className={`${baseClasses} ${colorClasses}`}>{category}</span>;
};

export const LogHistory = () => {
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [searchInput, setSearchInput] = useState("");

  const debouncedSearchTerm = useDebounce(searchInput, 500);

  const { data, isLoading, isError, isFetching } = useQuery<LogResponse>({
    queryKey: ["logs", page, pageSize, debouncedSearchTerm],
    queryFn: () => fetchLogs(page, pageSize, debouncedSearchTerm),
    placeholderData: (prev) => prev,
  });

  const handleSearchChange = (e: ChangeEvent<HTMLInputElement>) => {
    setSearchInput(e.target.value);
    setPage(0);
  };

  const handlePageSizeChange = (e: ChangeEvent<HTMLSelectElement>) => {
    setPageSize(Number(e.target.value));
    setPage(0);
  };

  const handlePreviousPage = () => setPage((old) => Math.max(old - 1, 0));
  const handleNextPage = () => setPage((old) => (data?.last ? old : old + 1));

  if (isError) {
    return (
      <div role="alert" className="p-4 text-red-500 bg-red-50 rounded">
        Failed to load data. Please check the backend connection.
      </div>
    );
  }

  return (
    <div className="p-6 bg-white rounded shadow-md mt-6 flex flex-col min-h-[600px]">
      <div className="flex flex-col md:flex-row justify-between items-center mb-6 gap-4">
        <div className="flex items-center gap-2">
          <h2 className="text-xl font-bold text-gray-800">Log History</h2>
          {isFetching && (
            <span role="status" className="text-xs text-blue-500 animate-pulse">
              Updating...
            </span>
          )}
        </div>

        <div className="flex gap-3 w-full md:w-auto">
          <select
            value={pageSize}
            onChange={handlePageSizeChange}
            aria-label="Rows per page"
            className="border rounded p-2 text-sm bg-gray-50 outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value={10}>10 per page</option>
            <option value={50}>50 per page</option>
            <option value={100}>100 per page</option>
            <option value={1000}>1000 per page</option>
          </select>

          <input
            type="text"
            placeholder="Search logs..."
            className="border rounded p-2 text-sm w-full md:w-64 outline-none focus:ring-2 focus:ring-blue-500"
            value={searchInput}
            onChange={handleSearchChange}
            aria-label="Search logs"
          />
        </div>
      </div>

      <div className="flex-grow overflow-auto border rounded-lg">
        <table className="w-full text-left text-sm text-gray-600">
          <thead className="bg-gray-100 uppercase text-xs font-semibold text-gray-700 sticky top-0">
            <tr>
              <th className="p-3">Timestamp</th>
              <th className="p-3">User</th>
              <th className="p-3">Category</th>
              <th className="p-3">Channel</th>
              <th className="p-3 w-1/3">Message</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {isLoading ? (
              Array.from({ length: 5 }).map((_, i) => (
                <tr
                  key={i}
                  className="animate-pulse"
                  data-testid="loading-skeleton"
                >
                  <td colSpan={5} className="p-4 bg-gray-50 h-10 border-b"></td>
                </tr>
              ))
            ) : data?.content.length === 0 ? (
              <tr>
                <td colSpan={5} className="p-8 text-center text-gray-400">
                  No records found.
                </td>
              </tr>
            ) : (
              data?.content.map((log) => (
                <tr key={log.id} className="hover:bg-blue-50 transition-colors">
                  <td className="p-3 whitespace-nowrap font-mono text-xs">
                    {new Date(log.timestamp).toLocaleString()}
                  </td>
                  <td className="p-3 font-medium text-gray-900">
                    {log.userName}
                  </td>
                  <td className="p-3">
                    <LogBadge category={log.category} />
                  </td>
                  <td className="p-3">{log.channel}</td>
                  <td className="p-3 truncate max-w-xs" title={log.message}>
                    {log.message}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {data && (
        <div className="flex justify-between items-center mt-4 pt-4 border-t">
          <span className="text-sm text-gray-500">
            Showing {data.content.length} of{" "}
            <strong>{data.totalElements}</strong> records
          </span>

          <div className="flex items-center gap-2">
            <button
              onClick={handlePreviousPage}
              disabled={data.first}
              className="px-3 py-1 border rounded disabled:opacity-50 hover:bg-gray-100 disabled:hover:bg-white transition"
            >
              Previous
            </button>

            <span className="text-sm font-medium px-2">
              Page {data.pageable.pageNumber + 1} of {data.totalPages}
            </span>

            <button
              onClick={handleNextPage}
              disabled={data.last}
              className="px-3 py-1 border rounded disabled:opacity-50 hover:bg-gray-100 disabled:hover:bg-white transition"
            >
              Next
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

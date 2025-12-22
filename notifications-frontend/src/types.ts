export interface LogEntry {
  id: string;
  timestamp: string;
  userName: string;
  category: string;
  channel: string;
  message: string;
}

export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
}

export interface MessagePayload {
  category: string;
  message: string;
}

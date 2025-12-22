import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { SubmissionForm } from "./components/SubmissionForm.tsx";
import { LogHistory } from "./components/LogHistory.tsx";
const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            retry: 3,
            staleTime: 5000,
            refetchOnWindowFocus: false,
        },
    },
});
ReactDOM.createRoot(document.getElementById('root')).render(_jsx(React.StrictMode, { children: _jsxs(QueryClientProvider, { client: queryClient, children: [_jsx(SubmissionForm, {}), _jsx(LogHistory, {})] }) }));

import React from 'react'
import ReactDOM from 'react-dom/client'
import './index.css'

import {QueryClient, QueryClientProvider} from '@tanstack/react-query'
import {SubmissionForm} from "./components/SubmissionForm.tsx";
import {LogHistory} from "./components/LogHistory.tsx";


const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            retry: 3,
            staleTime: 5000,
            refetchOnWindowFocus: false,
        },
    },
})

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <QueryClientProvider client={queryClient}>
            <SubmissionForm/>
            <LogHistory/>
        </QueryClientProvider>
    </React.StrictMode>,
)
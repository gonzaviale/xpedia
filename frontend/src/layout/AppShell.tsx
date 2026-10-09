import { Outlet } from '@tanstack/react-router';
import { Header } from './Header';
import { Sidebar } from './Sidebar';

export function AppShell() {
  return (
    <>
      <Header />
      <div className="flex min-h-[calc(100vh-3.25rem)] flex-col md:flex-row">
        <Sidebar />
        <main className="min-w-0 flex-1 px-4 pt-5 pb-18 md:px-8 md:pt-7">
          <Outlet />
        </main>
      </div>
    </>
  );
}

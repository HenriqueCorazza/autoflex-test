import { useState } from 'react';
import { Provider } from 'react-redux';
import store from './store';
import ProductsPage from './pages/ProductsPage';
import RawMaterialsPage from './pages/RawMaterialsPage';
import SuggestionsPage from './pages/SuggestionsPage';
import { ToastContainer } from './components/Toast';
import './index.css';

const NAV_ITEMS = [
  { id: 'products', label: 'Products', icon: '📦' },
  { id: 'rawMaterials', label: 'Raw Materials', icon: '🧱' },
  { id: 'suggestions', label: 'Production Suggestion', icon: '⚡' },
];

function AppContent() {
  const [activePage, setActivePage] = useState('products');

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="sidebar-brand">
          <span className="brand-icon">⚙</span>
          <div>
            <div className="brand-title">Production</div>
            <div className="brand-sub">Management</div>
          </div>
        </div>
        <nav className="sidebar-nav">
          {NAV_ITEMS.map(item => (
            <button
              key={item.id}
              className={`nav-item ${activePage === item.id ? 'active' : ''}`}
              onClick={() => setActivePage(item.id)}
            >
              <span className="nav-icon">{item.icon}</span>
              <span>{item.label}</span>
            </button>
          ))}
        </nav>
        <div className="sidebar-footer">
          <span className="footer-badge">Autoflex Challenge</span>
        </div>
      </aside>

      <main className="main-content">
        <div className="page-container">
          {activePage === 'products' && <ProductsPage />}
          {activePage === 'rawMaterials' && <RawMaterialsPage />}
          {activePage === 'suggestions' && <SuggestionsPage />}
        </div>
      </main>
    </div>
  );
}

export default function App() {
  return (
    <Provider store={store}>
      <AppContent />
      <ToastContainer />
    </Provider>
  );
}

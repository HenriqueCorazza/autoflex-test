import { BrowserRouter, Routes, Route, NavLink } from 'react-router-dom';
import { Provider } from 'react-redux';
import store from './store';
import ProductsPage from './pages/ProductsPage';
import RawMaterialsPage from './pages/RawMaterialsPage';
import SuggestionsPage from './pages/SuggestionsPage';
import { ToastContainer } from './components/Toast';
import './index.css';

const NAV_ITEMS = [
    { path: '/',             label: 'Products',           icon: '📦' },
    { path: '/raw-materials', label: 'Raw Materials',      icon: '🧱' },
    { path: '/suggestions',  label: 'Production Suggestion', icon: '⚡' },
];

function AppContent() {
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
                        <NavLink
                            key={item.path}
                            to={item.path}
                            end={item.path === '/'}
                            className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
                        >
                            <span className="nav-icon">{item.icon}</span>
                            <span>{item.label}</span>
                        </NavLink>
                    ))}
                </nav>
                <div className="sidebar-footer">
                    <span className="footer-badge">Autoflex Challenge</span>
                </div>
            </aside>

            <main className="main-content">
                <div className="page-container">
                    <Routes>
                        <Route path="/" element={<ProductsPage />} />
                        <Route path="/raw-materials" element={<RawMaterialsPage />} />
                        <Route path="/suggestions" element={<SuggestionsPage />} />
                    </Routes>
                </div>
            </main>
        </div>
    );
}

export default function App() {
    return (
        <Provider store={store}>
            <BrowserRouter>
                <AppContent />
                <ToastContainer />
            </BrowserRouter>
        </Provider>
    );
}
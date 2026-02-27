import { useEffect, useState } from 'react';

// ── Toast Component ──────────────────────────────────────────────────────────
export function Toast({ message, type = 'error', onClose }) {
  useEffect(() => {
    const t = setTimeout(onClose, 4000);
    return () => clearTimeout(t);
  }, [onClose]);

  const icons = { error: '✕', success: '✓', warning: '⚠' };
  const colors = {
    error: { bg: '#2a1010', border: '#e05252', icon: '#e05252', text: '#f5a0a0' },
    success: { bg: '#0d2a1a', border: '#34c78a', icon: '#34c78a', text: '#7de8bb' },
    warning: { bg: '#2a1f08', border: '#f0b429', icon: '#f0b429', text: '#f5d07a' },
  };
  const c = colors[type];

  return (
    <div style={{
      display: 'flex', alignItems: 'flex-start', gap: 12,
      background: c.bg, border: `1px solid ${c.border}`,
      borderRadius: 8, padding: '14px 16px', minWidth: 300, maxWidth: 420,
      boxShadow: `0 8px 32px rgba(0,0,0,0.5), 0 0 0 1px ${c.border}22`,
      animation: 'toastIn 0.25s cubic-bezier(0.34, 1.56, 0.64, 1)',
    }}>
      <span style={{
        width: 22, height: 22, borderRadius: '50%',
        background: `${c.icon}22`, color: c.icon,
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        fontSize: 12, fontWeight: 700, flexShrink: 0, marginTop: 1,
      }}>{icons[type]}</span>
      <div style={{ flex: 1 }}>
        <div style={{ fontSize: 12, fontWeight: 700, color: c.icon, textTransform: 'uppercase', letterSpacing: '0.08em', marginBottom: 3 }}>
          {type === 'error' ? 'Error' : type === 'success' ? 'Success' : 'Warning'}
        </div>
        <div style={{ fontSize: 13, color: c.text, lineHeight: 1.5 }}>{message}</div>
      </div>
      <button onClick={onClose} style={{
        background: 'none', border: 'none', color: c.text,
        cursor: 'pointer', fontSize: 16, padding: '0 2px', opacity: 0.6, flexShrink: 0,
      }}>×</button>
    </div>
  );
}

// ── Toast Container ──────────────────────────────────────────────────────────
let _addToast = null;

export function ToastContainer() {
  const [toasts, setToasts] = useState([]);

  useEffect(() => {
    _addToast = (msg, type) => {
      const id = Date.now();
      setToasts(t => [...t, { id, msg, type }]);
    };
    return () => { _addToast = null; };
  }, []);

  const remove = (id) => setToasts(t => t.filter(x => x.id !== id));

  return (
    <div style={{
      position: 'fixed', bottom: 24, right: 24,
      display: 'flex', flexDirection: 'column', gap: 10,
      zIndex: 9999, pointerEvents: 'none',
    }}>
      {toasts.map(t => (
        <div key={t.id} style={{ pointerEvents: 'all' }}>
          <Toast message={t.msg} type={t.type} onClose={() => remove(t.id)} />
        </div>
      ))}
    </div>
  );
}

// ── Helper to trigger toasts from anywhere ───────────────────────────────────
export const toast = {
  error: (msg) => _addToast?.(msg, 'error'),
  success: (msg) => _addToast?.(msg, 'success'),
  warning: (msg) => _addToast?.(msg, 'warning'),
};

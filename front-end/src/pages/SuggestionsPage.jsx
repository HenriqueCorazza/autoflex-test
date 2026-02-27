import { useDispatch, useSelector } from 'react-redux';
import { fetchSuggestions } from '../store';
import { toast } from '../components/Toast';

export default function SuggestionsPage() {
  const dispatch = useDispatch();
  const { data, loading, error } = useSelector(s => s.suggestions);

  const handleFetch = async () => {
    const action = await dispatch(fetchSuggestions());
    if (action.error) {
      toast.error(action.payload || 'Failed to calculate suggestions.');
    }
  };

  const totalUnits = data?.suggestions?.reduce((sum, s) => sum + s.quantityProduced, 0) ?? 0;

  return (
      <>
        <div className="page-header">
          <h1 className="page-title">⚡ <span>Production Suggestion</span></h1>
          <button className="btn btn-primary" onClick={handleFetch} disabled={loading}>
            {loading ? 'Calculating...' : '⟳ Calculate'}
          </button>
        </div>

        {!data && !loading && !error && (
            <div className="empty-state" style={{ paddingTop: 80 }}>
              <div className="empty-state-icon">⚡</div>
              <p style={{ marginBottom: 20 }}>Click <strong>Calculate</strong> to see which products can be produced with current stock.</p>
              <p style={{ fontSize: 12, color: 'var(--text-muted)' }}>Products are prioritized by highest value first.</p>
            </div>
        )}

        {loading && <div className="loading"><div className="spinner" /> Calculating optimal production...</div>}

        {data && !loading && (
            <>
              <div className="suggestion-summary">
                <div className="summary-card">
                  <div className="summary-label">Total Units to Produce</div>
                  <div className="summary-value">{totalUnits}</div>
                </div>
                <div className="summary-card">
                  <div className="summary-label">Total Value</div>
                  <div className="summary-value">R$ {data.totalValue?.toFixed(2) ?? '0.00'}</div>
                </div>
              </div>

              {data.suggestions?.length === 0 ? (
                  <div className="empty-state">
                    <div className="empty-state-icon">📭</div>
                    <p>No products can be produced with current stock levels.</p>
                  </div>
              ) : (
                  data.suggestions.map((s, i) => (
                      <div key={i} className="suggestion-card">
                        <div>
                          <div className="suggestion-rank">#{i + 1} — Priority by value</div>
                          <div className="suggestion-name">{s.productName}</div>
                          <div className="suggestion-qty">{s.quantityProduced} unit{s.quantityProduced !== 1 ? 's' : ''} to produce</div>
                        </div>
                        <div className="suggestion-subtotal">R$ {s.subtotal?.toFixed(2)}</div>
                      </div>
                  ))
              )}
            </>
        )}
      </>
  );
}
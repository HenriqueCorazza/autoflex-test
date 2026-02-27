import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  fetchRawMaterials, createRawMaterial, updateRawMaterial, deleteRawMaterial, clearRawMaterialsError,
} from '../store';
import { toast } from '../components/Toast';
import ConfirmModal from '../components/ConfirmModal';

const EMPTY_FORM = { materialName: '', skuCode: '', stock: '' };

export default function RawMaterialsPage() {
  const dispatch = useDispatch();
  const { items: materials, loading } = useSelector(s => s.rawMaterials);

  const [showModal, setShowModal] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(EMPTY_FORM);
  const [saving, setSaving] = useState(false);
  const [confirmId, setConfirmId] = useState(null);

  useEffect(() => { dispatch(fetchRawMaterials()); }, [dispatch]);

  const openCreate = () => {
    setEditing(null); setForm(EMPTY_FORM);
    dispatch(clearRawMaterialsError()); setShowModal(true);
  };

  const openEdit = (m) => {
    setEditing(m);
    setForm({ materialName: m.materialName, skuCode: m.skuCode, stock: m.stock });
    dispatch(clearRawMaterialsError()); setShowModal(true);
  };

  const closeModal = () => { setShowModal(false); setEditing(null); };
  const setField = (k, v) => setForm(f => ({ ...f, [k]: v }));

  const handleSubmit = async () => {
    if (!form.materialName || !form.skuCode || form.stock === '') {
      toast.warning('Please fill all required fields.');
      return;
    }
    setSaving(true);
    const payload = { materialName: form.materialName, skuCode: form.skuCode, stock: parseInt(form.stock) };
    const action = editing
        ? await dispatch(updateRawMaterial({ id: editing.id, data: payload }))
        : await dispatch(createRawMaterial(payload));

    if (!action.error) {
      toast.success(editing ? 'Material updated successfully!' : 'Material created successfully!');
      closeModal();
    } else {
      toast.error(action.payload || 'Failed to save material.');
    }
    setSaving(false);
  };

  const handleDelete = async (id) => {
    const action = await dispatch(deleteRawMaterial(id));
    if (!action.error) {
      toast.success('Material deleted successfully!');
    } else {
      toast.error(action.payload || 'Cannot delete: this material may be linked to a product.');
    }
    setConfirmId(null);
  };

  const stockClass = (s) => s === 0 ? 'stock-zero' : s < 10 ? 'stock-low' : 'stock-ok';

  return (
      <>
        <div className="page-header">
          <h1 className="page-title">🧱 <span>Raw Materials</span></h1>
          <button className="btn btn-primary" onClick={openCreate}>+ New Material</button>
        </div>

        {loading ? (
            <div className="loading"><div className="spinner" /> Loading...</div>
        ) : (
            <div className="table-wrapper">
              <table>
                <thead>
                <tr>
                  <th>SKU</th>
                  <th>Name</th>
                  <th>Stock</th>
                  <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {materials.length === 0 ? (
                    <tr><td colSpan={4}>
                      <div className="empty-state">
                        <div className="empty-state-icon">🧱</div>
                        <p>No raw materials yet. Add your first one!</p>
                      </div>
                    </td></tr>
                ) : materials.map(m => (
                    <tr key={m.id}>
                      <td><span className="sku-badge">{m.skuCode}</span></td>
                      <td>{m.materialName}</td>
                      <td><span className={`stock-badge ${stockClass(m.stock)}`}>{m.stock} units</span></td>
                      <td>
                        <div className="actions-cell">
                          <button className="btn btn-ghost btn-sm" onClick={() => openEdit(m)}>✏ Edit</button>
                          <button className="btn btn-danger btn-sm" onClick={() => setConfirmId(m.id)}>✕ Delete</button>
                        </div>
                      </td>
                    </tr>
                ))}
                </tbody>
              </table>
            </div>
        )}

        {showModal && (
            <div className="modal-overlay" onClick={(e) => e.target === e.currentTarget && closeModal()}>
              <div className="modal">
                <div className="modal-header">
                  <h2 className="modal-title">{editing ? 'Edit Raw Material' : 'New Raw Material'}</h2>
                  <button className="modal-close" onClick={closeModal}>×</button>
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label className="form-label">Material Name *</label>
                    <input className="form-input" value={form.materialName}
                           onChange={e => setField('materialName', e.target.value)} placeholder="e.g. Steel" />
                  </div>
                  <div className="form-group">
                    <label className="form-label">SKU Code *</label>
                    <input className="form-input" value={form.skuCode}
                           onChange={e => setField('skuCode', e.target.value)} placeholder="e.g. STL-001" />
                  </div>
                </div>

                <div className="form-group">
                  <label className="form-label">Stock Quantity *</label>
                  <input className="form-input" type="number" min="0" value={form.stock}
                         onChange={e => setField('stock', e.target.value)} placeholder="0" />
                </div>

                <div className="modal-footer">
                  <button className="btn btn-ghost" onClick={closeModal}>Cancel</button>
                  <button className="btn btn-primary" onClick={handleSubmit} disabled={saving}>
                    {saving ? 'Saving...' : editing ? 'Save Changes' : 'Create Material'}
                  </button>
                </div>
              </div>
            </div>
        )}
        {confirmId && (
            <ConfirmModal
                message="Are you sure you want to delete this material? This action cannot be undone."
                onConfirm={() => handleDelete(confirmId)}
                onCancel={() => setConfirmId(null)}
            />
        )}
      </>
  );
}
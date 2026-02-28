import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  fetchProducts, createProduct, updateProduct, deleteProduct, clearProductsError,
} from '../store';
import { fetchRawMaterials } from '../store';
import { toast } from '../components/Toast';
import ConfirmModal from '../components/ConfirmModal';

const EMPTY_FORM = { productName: '', skuCode: '', productValue: '', materialsRequired: [] };

export default function ProductsPage() {
  const dispatch = useDispatch();
  const { items: products, loading } = useSelector(s => s.products);
  const { items: rawMaterials } = useSelector(s => s.rawMaterials);

  const [showModal, setShowModal] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(EMPTY_FORM);
  const [saving, setSaving] = useState(false);
  const [confirmId, setConfirmId] = useState(null);

  useEffect(() => {
    dispatch(fetchProducts());
    dispatch(fetchRawMaterials());
  }, [dispatch]);

  const openCreate = () => {
    setEditing(null);
    setForm(EMPTY_FORM);
    dispatch(clearProductsError());
    setShowModal(true);
  };

  const openEdit = (product) => {
    setEditing(product);
    const materialsRequired = product.materialsRequired.map(m => {
      const found = rawMaterials.find(rm => rm.materialName === m.materialName);
      return { rawMaterialId: found?.id || '', requiredQuantity: m.requiredQuantity };
    });
    setForm({
      productName: product.productName,
      skuCode: product.skuCode,
      productValue: product.productValue,
      materialsRequired,
    });
    dispatch(clearProductsError());
    setShowModal(true);
  };

  const closeModal = () => { setShowModal(false); setEditing(null); };
  const setField = (key, val) => setForm(f => ({ ...f, [key]: val }));

  const addMaterial = () =>
      setForm(f => ({ ...f, materialsRequired: [...f.materialsRequired, { rawMaterialId: '', requiredQuantity: 1 }] }));

  const removeMaterial = (i) =>
      setForm(f => ({ ...f, materialsRequired: f.materialsRequired.filter((_, idx) => idx !== i) }));

  const updateMaterial = (i, key, val) =>
      setForm(f => ({
        ...f,
        materialsRequired: f.materialsRequired.map((m, idx) => idx === i ? { ...m, [key]: val } : m),
      }));

  const handleSubmit = async () => {
    if (!form.productName || !form.productValue || form.materialsRequired.length === 0) {
      toast.warning('Please fill all required fields and add at least one material.');
      return;
    }
    setSaving(true);
    const payload = {
      productName: form.productName,
      skuCode: form.skuCode,
      productValue: parseFloat(form.productValue),
      materialsRequired: form.materialsRequired.map(m => ({
        rawMaterialId: parseInt(m.rawMaterialId),
        requiredQuantity: parseInt(m.requiredQuantity),
      })),
    };
    const action = editing
        ? await dispatch(updateProduct({ id: editing.productId, data: payload }))
        : await dispatch(createProduct(payload));

    if (!action.error) {
      toast.success(editing ? 'Product updated successfully!' : 'Product created successfully!');
      closeModal();
    } else {
      toast.error(action.payload || 'Failed to save product.');
    }
    setSaving(false);
  };

  const handleDelete = async (id) => {
    const action = await dispatch(deleteProduct(id));
    if (!action.error) {
      toast.success('Product deleted successfully!');
    } else {
      toast.error(action.payload || 'Failed to delete product.');
    }
    setConfirmId(null);
  };

  return (
      <>
        <div className="page-header">
          <h1 className="page-title">📦 <span>Products</span></h1>
          <button className="btn btn-primary" onClick={openCreate}>+ New Product</button>
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
                  <th>Value</th>
                  <th>Materials</th>
                  <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {products.length === 0 ? (
                    <tr><td colSpan={5}>
                      <div className="empty-state">
                        <div className="empty-state-icon">📦</div>
                        <p>No products yet. Create your first one!</p>
                      </div>
                    </td></tr>
                ) : products.map(p => (
                    <tr key={p.productId}>
                      <td><span className="sku-badge">{p.skuCode}</span></td>
                      <td>{p.productName}</td>
                      <td><span className="value-text">R$ {p.productValue.toFixed(2)}</span></td>
                      <td>
                        {p.materialsRequired.map((m, i) => (
                            <span key={i} className="material-tag">{m.materialName} × {m.requiredQuantity}</span>
                        ))}
                      </td>
                      <td>
                        <div className="actions-cell">
                          <button className="btn btn-ghost btn-sm" onClick={() => openEdit(p)}>✏ Edit</button>
                          <button className="btn btn-danger btn-sm" onClick={() => setConfirmId(p.productId)}>✕ Delete</button>
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
                  <h2 className="modal-title">{editing ? 'Edit Product' : 'New Product'}</h2>
                  <button className="modal-close" onClick={closeModal}>×</button>
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label className="form-label">Product Name *</label>
                    <input className="form-input" value={form.productName} data-cy="input-product-name"
                           onChange={e => setField('productName', e.target.value)} placeholder="e.g. Widget A" />
                  </div>
                  <div className="form-group">
                    <label className="form-label">SKU Code *</label>
                    <input className="form-input" value={form.skuCode} data-cy="input-sku"
                           onChange={e => setField('skuCode', e.target.value)} placeholder="e.g. WGT-001" />
                  </div>
                </div>

                <div className="form-group">
                  <label className="form-label">Value (R$) *</label>
                  <input className="form-input" type="number" min="0" step="0.01" data-cy="input-value"
                         value={form.productValue} onChange={e => setField('productValue', e.target.value)}
                         placeholder="0.00" />
                </div>

                <div className="materials-section">
                  <div className="materials-header">
                    <span className="materials-title">Raw Materials Required *</span>
                    <button className="btn btn-ghost btn-sm" onClick={addMaterial}>+ Add</button>
                  </div>
                  {form.materialsRequired.length === 0 && (
                      <p style={{ color: 'var(--text-muted)', fontSize: 13 }}>No materials added yet.</p>
                  )}
                  {form.materialsRequired.map((m, i) => (
                      <div key={i} className="material-row">
                        <select className="form-select" value={m.rawMaterialId}
                                onChange={e => updateMaterial(i, 'rawMaterialId', e.target.value)}>
                          <option value="">Select material...</option>
                          {rawMaterials.map(rm => (
                              <option key={rm.id} value={rm.id}>{rm.materialName} (stock: {rm.stock})</option>
                          ))}
                        </select>
                        <input className="form-input" type="number" min="1" value={m.requiredQuantity}
                               onChange={e => updateMaterial(i, 'requiredQuantity', e.target.value)} placeholder="Qty" />
                        <button className="btn-remove" onClick={() => removeMaterial(i)}>×</button>
                      </div>
                  ))}
                </div>

                <div className="modal-footer">
                  <button className="btn btn-ghost" onClick={closeModal}>Cancel</button>
                  <button className="btn btn-primary" onClick={handleSubmit} disabled={saving}>
                    {saving ? 'Saving...' : editing ? 'Save Changes' : 'Create Product'}
                  </button>
                </div>
              </div>
            </div>
        )}

        {confirmId && (
            <ConfirmModal
                message="Are you sure you want to delete this product? This action cannot be undone."
                onConfirm={() => handleDelete(confirmId)}
                onCancel={() => setConfirmId(null)}
            />
        )}
      </>
  );
}
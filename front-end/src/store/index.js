import { configureStore, createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import * as api from '../services/api';

// Helper to extract backend error message
const extractError = async (e) => {
  try {
    const data = JSON.parse(e.message);
    return data.message || data.error || e.message;
  } catch {
    return e.message || 'Unexpected error';
  }
};

// ─── Products ───────────────────────────────────────────────────────────────
export const fetchProducts = createAsyncThunk('products/fetchAll', async () => {
  try { return await api.getProducts(); } catch { return []; }
});
export const createProduct = createAsyncThunk('products/create', async (data, { rejectWithValue }) => {
  try { return await api.createProduct(data); }
  catch (e) { return rejectWithValue(await extractError(e)); }
});
export const updateProduct = createAsyncThunk('products/update', async ({ id, data }, { rejectWithValue }) => {
  try { return await api.updateProduct(id, data); }
  catch (e) { return rejectWithValue(await extractError(e)); }
});
export const deleteProduct = createAsyncThunk('products/delete', async (id, { rejectWithValue }) => {
  try { await api.deleteProduct(id); return id; }
  catch (e) { return rejectWithValue(await extractError(e)); }
});

const productsSlice = createSlice({
  name: 'products',
  initialState: { items: [], loading: false, error: null },
  reducers: { clearError: (state) => { state.error = null; } },
  extraReducers: (builder) => {
    builder
      .addCase(fetchProducts.pending, (s) => { s.loading = true; s.error = null; })
      .addCase(fetchProducts.fulfilled, (s, a) => { s.loading = false; s.items = a.payload || []; })
      .addCase(fetchProducts.rejected, (s) => { s.loading = false; })
      .addCase(createProduct.fulfilled, (s, a) => { s.items.push(a.payload); })
      .addCase(createProduct.rejected, (s, a) => { s.error = a.payload; })
      .addCase(updateProduct.fulfilled, (s, a) => {
        const idx = s.items.findIndex(p => p.productId === a.payload.productId);
        if (idx !== -1) s.items[idx] = a.payload;
      })
      .addCase(updateProduct.rejected, (s, a) => { s.error = a.payload; })
      .addCase(deleteProduct.fulfilled, (s, a) => {
        s.items = s.items.filter(p => p.productId !== a.payload);
      })
      .addCase(deleteProduct.rejected, (s, a) => { s.error = a.payload; });
  },
});

// ─── Raw Materials ───────────────────────────────────────────────────────────
export const fetchRawMaterials = createAsyncThunk('rawMaterials/fetchAll', async () => {
  try { return await api.getRawMaterials(); } catch { return []; }
});
export const createRawMaterial = createAsyncThunk('rawMaterials/create', async (data, { rejectWithValue }) => {
  try { return await api.createRawMaterial(data); }
  catch (e) { return rejectWithValue(await extractError(e)); }
});
export const updateRawMaterial = createAsyncThunk('rawMaterials/update', async ({ id, data }, { rejectWithValue }) => {
  try { return await api.updateRawMaterial(id, data); }
  catch (e) { return rejectWithValue(await extractError(e)); }
});
export const deleteRawMaterial = createAsyncThunk('rawMaterials/delete', async (id, { rejectWithValue }) => {
  try { await api.deleteRawMaterial(id); return id; }
  catch (e) { return rejectWithValue(await extractError(e)); }
});

const rawMaterialsSlice = createSlice({
  name: 'rawMaterials',
  initialState: { items: [], loading: false, error: null },
  reducers: { clearError: (state) => { state.error = null; } },
  extraReducers: (builder) => {
    builder
      .addCase(fetchRawMaterials.pending, (s) => { s.loading = true; s.error = null; })
      .addCase(fetchRawMaterials.fulfilled, (s, a) => { s.loading = false; s.items = a.payload || []; })
      .addCase(fetchRawMaterials.rejected, (s) => { s.loading = false; })
      .addCase(createRawMaterial.fulfilled, (s, a) => { s.items.push(a.payload); })
      .addCase(createRawMaterial.rejected, (s, a) => { s.error = a.payload; })
      .addCase(updateRawMaterial.fulfilled, (s, a) => {
        const idx = s.items.findIndex(m => m.id === a.payload.id);
        if (idx !== -1) s.items[idx] = a.payload;
      })
      .addCase(updateRawMaterial.rejected, (s, a) => { s.error = a.payload; })
      .addCase(deleteRawMaterial.fulfilled, (s, a) => {
        s.items = s.items.filter(m => m.id !== a.payload);
      })
      .addCase(deleteRawMaterial.rejected, (s, a) => { s.error = a.payload; });
  },
});

// ─── Suggestions ─────────────────────────────────────────────────────────────
export const fetchSuggestions = createAsyncThunk('suggestions/fetch', async (_, { rejectWithValue }) => {
  try { return await api.getProductSuggestions(); }
  catch (e) { return rejectWithValue(await extractError(e)); }
});

const suggestionsSlice = createSlice({
  name: 'suggestions',
  initialState: { data: null, loading: false, error: null },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchSuggestions.pending, (s) => { s.loading = true; s.error = null; s.data = null; })
      .addCase(fetchSuggestions.fulfilled, (s, a) => { s.loading = false; s.data = a.payload; })
      .addCase(fetchSuggestions.rejected, (s, a) => { s.loading = false; s.error = a.payload; });
  },
});

export const { clearError: clearProductsError } = productsSlice.actions;
export const { clearError: clearRawMaterialsError } = rawMaterialsSlice.actions;

export default configureStore({
  reducer: {
    products: productsSlice.reducer,
    rawMaterials: rawMaterialsSlice.reducer,
    suggestions: suggestionsSlice.reducer,
  },
});

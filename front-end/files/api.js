const BASE_URL = 'http://localhost:8080/api';

const handleResponse = async (res) => {
  if (!res.ok) {
    // Try JSON first, fall back to plain text (GlobalExceptionHandler returns String)
    const text = await res.text().catch(() => 'Unexpected error');
    let msg;
    try {
      const json = JSON.parse(text);
      // Handle array of validation errors (MethodArgumentNotValidException)
      if (Array.isArray(json)) msg = json.join(', ');
      else msg = json.message || json.error || text;
    } catch {
      msg = text; // plain string from EntityNotFoundException / RuntimeException
    }
    throw new Error(JSON.stringify({ message: msg, status: res.status }));
  }
  if (res.status === 204) return null;
  return res.json();
};

// Products
export const getProducts = () =>
  fetch(`${BASE_URL}/products`).then(handleResponse);

export const getProductById = (id) =>
  fetch(`${BASE_URL}/products/${id}`).then(handleResponse);

export const createProduct = (data) =>
  fetch(`${BASE_URL}/products`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  }).then(handleResponse);

export const updateProduct = (id, data) =>
  fetch(`${BASE_URL}/products/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  }).then(handleResponse);

export const deleteProduct = (id) =>
  fetch(`${BASE_URL}/products/${id}`, { method: 'DELETE' }).then(handleResponse);

export const getProductSuggestions = () =>
  fetch(`${BASE_URL}/products/suggestions`).then(handleResponse);

// Raw Materials
export const getRawMaterials = () =>
  fetch(`${BASE_URL}/raw-materials`).then(handleResponse);

export const getRawMaterialById = (id) =>
  fetch(`${BASE_URL}/raw-materials/${id}`).then(handleResponse);

export const createRawMaterial = (data) =>
  fetch(`${BASE_URL}/raw-materials`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  }).then(handleResponse);

export const updateRawMaterial = (id, data) =>
  fetch(`${BASE_URL}/raw-materials/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  }).then(handleResponse);

export const deleteRawMaterial = (id) =>
  fetch(`${BASE_URL}/raw-materials/${id}`, { method: 'DELETE' }).then(handleResponse);

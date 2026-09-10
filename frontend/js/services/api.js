/**
 * Cliente de API REST para comunicação com o backend Spring Boot.
 */
const API_BASE_URL = '/api';

export const ApiService = {
  /**
   * Executa requisições HTTP padronizadas com tratamento de erros.
   */
  async request(endpoint, options = {}) {
    const defaultHeaders = {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    };

    // Adiciona token JWT se presente
    const token = localStorage.getItem('vetcare_auth_token');
    if (token) {
      defaultHeaders['Authorization'] = `Bearer ${token}`;
    }

    const config = {
      ...options,
      headers: {
        ...defaultHeaders,
        ...options.headers
      }
    };

    try {
      const response = await fetch(`${API_BASE_URL}${endpoint}`, config);

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `Erro ${response.status}: ${response.statusText}`);
      }

      // Se status 204 No Content
      if (response.status === 204) {
        return null;
      }

      return await response.json();
    } catch (error) {
      console.error(`Falha na requisição para ${endpoint}:`, error);
      throw error;
    }
  },

  // Endpoints do Domínio Clínico & Silvestre
  auth: {
    login: (credentials) => ApiService.request('/auth/login', { method: 'POST', body: JSON.stringify(credentials) })
  },
  funcionarios: {
    listar: () => ApiService.request('/funcionarios'),
    obterPorId: (id) => ApiService.request(`/funcionarios/${id}`),
    salvar: (dados) => ApiService.request('/funcionarios', { method: 'POST', body: JSON.stringify(dados) })
  },
  tutores: {
    listar: () => ApiService.request('/tutores'),
    salvar: (dados) => ApiService.request('/tutores', { method: 'POST', body: JSON.stringify(dados) })
  },
  pets: {
    listar: () => ApiService.request('/pets'),
    salvar: (dados) => ApiService.request('/pets', { method: 'POST', body: JSON.stringify(dados) })
  },
  atendimentos: {
    listar: () => ApiService.request('/atendimentos'),
    salvar: (dados) => ApiService.request('/atendimentos', { method: 'POST', body: JSON.stringify(dados) })
  }
};

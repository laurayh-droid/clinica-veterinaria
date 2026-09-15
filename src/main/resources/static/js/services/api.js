/**
 * Cliente de API REST para comunicação com o backend Spring Boot Monolítico.
 * Context-path: /api
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

  // 1. Corpo Clínico & Funcionários
  funcionarios: {
    listar: (busca = '') => ApiService.request(`/funcionarios${busca ? `?busca=${encodeURIComponent(busca)}` : ''}`),
    listarAtivos: () => ApiService.request('/funcionarios/ativos'),
    obterPorId: (id) => ApiService.request(`/funcionarios/${id}`),
    salvar: (dados) => ApiService.request('/funcionarios', { method: 'POST', body: JSON.stringify(dados) }),
    atualizar: (id, dados) => ApiService.request(`/funcionarios/${id}`, { method: 'PUT', body: JSON.stringify(dados) }),
    alternarStatus: (id) => ApiService.request(`/funcionarios/${id}/status`, { method: 'PATCH' }),
    remover: (id) => ApiService.request(`/funcionarios/${id}`, { method: 'DELETE' })
  },

  // 2. Tutores
  tutores: {
    listar: (busca = '') => ApiService.request(`/tutores${busca ? `?busca=${encodeURIComponent(busca)}` : ''}`),
    obterPorId: (id) => ApiService.request(`/tutores/${id}`),
    salvar: (dados) => ApiService.request('/tutores', { method: 'POST', body: JSON.stringify(dados) }),
    atualizar: (id, dados) => ApiService.request(`/tutores/${id}`, { method: 'PUT', body: JSON.stringify(dados) }),
    remover: (id) => ApiService.request(`/tutores/${id}`, { method: 'DELETE' })
  },

  // 3. Pets & Pacientes (Domésticos e Silvestres)
  pets: {
    listar: (filtros = {}) => {
      const params = new URLSearchParams();
      if (filtros.busca) params.append('busca', filtros.busca);
      if (filtros.categoria) params.append('categoria', filtros.categoria);
      if (filtros.tutorId) params.append('tutorId', filtros.tutorId);
      const queryString = params.toString();
      return ApiService.request(`/pets${queryString ? `?${queryString}` : ''}`);
    },
    obterPorId: (id) => ApiService.request(`/pets/${id}`),
    salvar: (dados) => ApiService.request('/pets', { method: 'POST', body: JSON.stringify(dados) }),
    atualizar: (id, dados) => ApiService.request(`/pets/${id}`, { method: 'PUT', body: JSON.stringify(dados) }),
    remover: (id) => ApiService.request(`/pets/${id}`, { method: 'DELETE' })
  },

  // 4. Agenda & Fila do Dia
  atendimentos: {
    listar: (data = '') => ApiService.request(`/atendimentos${data ? `?data=${data}` : ''}`),
    listarFilaHoje: () => ApiService.request('/atendimentos/fila-hoje'),
    listarPorPet: (petId) => ApiService.request(`/atendimentos/pet/${petId}`),
    obterPorId: (id) => ApiService.request(`/atendimentos/${id}`),
    salvar: (dados) => ApiService.request('/atendimentos', { method: 'POST', body: JSON.stringify(dados) }),
    atualizarStatus: (id, status) => ApiService.request(`/atendimentos/${id}/status`, { method: 'PATCH', body: JSON.stringify({ status }) }),
    remover: (id) => ApiService.request(`/atendimentos/${id}`, { method: 'DELETE' })
  },

  // 5. Prontuários Médicos & Receituário (Médico Veterinário)
  prontuarios: {
    listarPorPet: (petId) => ApiService.request(`/prontuarios/pet/${petId}`),
    obterPorId: (id) => ApiService.request(`/prontuarios/${id}`),
    registrar: (dados) => ApiService.request('/prontuarios', { method: 'POST', body: JSON.stringify(dados) }),
    emitirReceita: (id) => ApiService.request(`/prontuarios/${id}/receita`)
  },

  // 6. Guia de Manejo Silvestre & Adestramento (Especialista / Adestrador)
  guiasManejo: {
    listarPorPet: (petId) => ApiService.request(`/guias-manejo/pet/${petId}`),
    obterPorId: (id) => ApiService.request(`/guias-manejo/${id}`),
    registrar: (dados) => ApiService.request('/guias-manejo', { method: 'POST', body: JSON.stringify(dados) })
  },

  // 7. Vacinação & Imunizações
  vacinas: {
    listarPorPet: (petId) => ApiService.request(`/vacinas/pet/${petId}`),
    obterPorId: (id) => ApiService.request(`/vacinas/${id}`),
    cadastrar: (dados) => ApiService.request('/vacinas', { method: 'POST', body: JSON.stringify(dados) }),
    remover: (id) => ApiService.request(`/vacinas/${id}`, { method: 'DELETE' })
  },

  // 8. Indicadores & Métricas do Dashboard
  dashboard: {
    obterMetricas: () => ApiService.request('/dashboard/metricas')
  }
};

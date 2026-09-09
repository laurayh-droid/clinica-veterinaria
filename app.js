/* ==========================================================================
   VetCare - Sistema de Gestão para Clínica Veterinária
   Lógica JavaScript Interativa & Controle de Estado
   ========================================================================== */

// Data de simulação do sistema (08/09/2026)
const CURRENT_DATE = new Date().toISOString().split('T')[0];

// Perfis de Usuário Pré-configurados (RF03)
const USERS = {
  vet: { name: 'Dr. Carlos Eduardo', role: 'Médico Veterinário', crmv: 'CRMV/SP 12345' },
  recepcao: { name: 'Ana Silva', role: 'Recepcionista / Atendente', crmv: '' },
  admin: { name: 'Mariana Oliveira', role: 'Administradora', crmv: '' }
};

let currentUserRole = 'vet'; // Perfil padrão inicial

// Estado Inicial de Dados (Pré-carregado para demonstração imediata)
let state = {
  tutores: [
    {
      id: 1,
      nome: 'Mariana Oliveira Santos',
      cpf: '123.456.789-00',
      telefone: '(11) 98765-4321',
      endereco: 'Rua das Flores, 120 - SP',
      pets: [
        { id: 101, nome: 'Thor', especie: 'Cão', raca: 'Golden Retriever', sexo: 'Macho', idade: '3 anos', peso: 28.5 }
      ]
    },
    {
      id: 2,
      nome: 'Roberto Almeida',
      cpf: '987.654.321-11',
      telefone: '(11) 91234-5678',
      endereco: 'Av. Paulista, 900 - SP',
      pets: [
        { id: 102, nome: 'Luna', especie: 'Gato', raca: 'Siamês', sexo: 'Fêmea', idade: '2 anos', peso: 4.2 }
      ]
    },
    {
      id: 3,
      nome: 'Fernanda Costa',
      cpf: '456.789.123-22',
      telefone: '(11) 99887-7665',
      endereco: 'Rua Augusta, 45 - SP',
      pets: [
        { id: 103, nome: 'Pipoca', especie: 'Cão', raca: 'Poodle', sexo: 'Fêmea', idade: '5 anos', peso: 6.0 }
      ]
    }
  ],
  agendamentos: [
    { id: 1, data: CURRENT_DATE, hora: '09:00', petId: 101, petNome: 'Thor', tutorNome: 'Mariana Oliveira', vet: 'Dr. Carlos (CRMV 12345)', status: 'Finalizado' },
    { id: 2, data: CURRENT_DATE, hora: '10:30', petId: 102, petNome: 'Luna', tutorNome: 'Roberto Almeida', vet: 'Dr. Carlos (CRMV 12345)', status: 'Em Atendimento' },
    { id: 3, data: CURRENT_DATE, hora: '14:00', petId: 103, petNome: 'Pipoca', tutorNome: 'Fernanda Costa', vet: 'Dr. Carlos (CRMV 12345)', status: 'Aguardando' }
  ],
  prontuarios: {
    101: [
      {
        data: '15/08/2026',
        vet: 'Dr. Carlos (CRMV 12345)',
        queixa: 'Consulta de rotina e vacinação anual.',
        sintomas: 'Animal ativo, apetite normal, afebril.',
        diagnostico: 'Exame físico sem alterações.',
        orientacoes: 'Manter vacinação em dia e vermifugação a cada 4 meses.'
      }
    ],
    102: [
      {
        data: CURRENT_DATE,
        vet: 'Dr. Carlos (CRMV 12345)',
        queixa: 'Tutor relata espirros ocasionais e falta de apetite há 1 dia.',
        sintomas: 'Mucosas coradas, leves secreções nasais claras, T: 38.8°C.',
        diagnostico: 'Suspeita de traqueobronquite fofinha / resfriado felino leve.',
        orientacoes: 'Aumentar hidratação, manter em local aquecido.'
      }
    ]
  },
  vacinas: {
    101: [
      { nome: 'Vacina V10 Polivalente', dataAplicacao: '15/08/2026', proximaDose: '15/08/2027' },
      { nome: 'Antirrábica', dataAplicacao: '15/08/2026', proximaDose: '15/08/2027' }
    ],
    102: [
      { nome: 'Vacina Quádrupla Felina (V4)', dataAplicacao: '10/01/2026', proximaDose: '10/01/2027' }
    ]
  },
  estoque: [
    { id: 1, nome: 'Vacina Polivalente V10', preco: 95.00, qtd: 15 },
    { id: 2, nome: 'Vermífugo Canine Tab (Cx 4 comp)', preco: 42.50, qtd: 24 },
    { id: 3, nome: 'Anti-inflamatório Meloxivet 1mg', preco: 38.00, qtd: 8 },
    { id: 4, nome: 'Shampoo Hipoalergênico Pet 500ml', preco: 55.00, qtd: 0 }, // Qtd 0 para testar RN04!
    { id: 5, nome: 'Soro Fisiológico 500ml', preco: 12.00, qtd: 5 }
  ],
  transacoesCaixa: [
    { id: 1, hora: '09:45', descricao: 'Consulta Clínica - Thor', tutor: 'Mariana Oliveira', metodo: 'Pix', valor: 150.00 },
    { id: 2, hora: '10:00', descricao: 'Vermífugo Canine Tab', tutor: 'Cliente Avulso', metodo: 'Cartão de Débito', valor: 42.50 }
  ]
};

// Carregar LocalStorage se disponível
function loadState() {
  const saved = localStorage.getItem('vetcare_state');
  if (saved) {
    try {
      state = JSON.parse(saved);
    } catch (e) {
      console.warn('Erro ao carregar localStorage, usando dados padrão.', e);
    }
  }
}

function saveState() {
  localStorage.setItem('vetcare_state', JSON.stringify(state));
}

// Inicialização da Aplicação
document.addEventListener('DOMContentLoaded', () => {
  loadState();
  initNavigation();
  initDateDisplay();
  renderAll();
});

function initDateDisplay() {
  const today = new Date();
  const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
  document.getElementById('current-date-display').innerText = today.toLocaleDateString('pt-BR', options);
  
  // Set default values in forms
  const filterDateInput = document.getElementById('agenda-date-filter');
  if (filterDateInput) filterDateInput.value = CURRENT_DATE;

  const agendamentoData = document.getElementById('agendamento-data');
  if (agendamentoData) agendamentoData.value = CURRENT_DATE;
}

/* ==========================================================================
   Navegação por Abas & Controle de Permissão (RF03, RN02)
   ========================================================================== */

function initNavigation() {
  const navItems = document.querySelectorAll('.nav-item');
  navItems.forEach(item => {
    item.addEventListener('click', (e) => {
      e.preventDefault();
      const tabId = item.getAttribute('data-tab');
      switchTab(tabId);
    });
  });
}

function switchTab(tabId) {
  document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));
  document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));

  const activeNav = document.querySelector(`.nav-item[data-tab="${tabId}"]`);
  const activeTab = document.getElementById(tabId);

  if (activeNav) activeNav.classList.add('active');
  if (activeTab) activeTab.classList.add('active');

  // Atualizar títulos do Header
  const titles = {
    'dashboard': { title: 'Dashboard & Fila do Dia', subtitle: 'Acompanhamento em tempo real da recepção' },
    'tutores-pets': { title: 'Cadastros de Tutores e Pacientes', subtitle: 'Gerenciamento de clientes e seus animais' },
    'agenda': { title: 'Agenda de Consultas', subtitle: 'Marcação de horários e retornos' },
    'atendimento': { title: 'Atendimento Clínico & Prontuário', subtitle: 'Registro médico, exames, vacinas e receituário' },
    'estoque': { title: 'Controle de Estoque', subtitle: 'Gestão de medicamentos, produtos e insumos' },
    'caixa': { title: 'Caixa & Fechamento Diário', subtitle: 'Lançamento de pagamentos e balanço do dia' }
  };

  if (titles[tabId]) {
    document.getElementById('page-title').innerText = titles[tabId].title;
    document.getElementById('page-subtitle').innerText = titles[tabId].subtitle;
  }
}

// Troca de Perfil de Usuário em Tempo Real (RF03, RN02)
function switchUserRole(roleKey) {
  currentUserRole = roleKey;
  const user = USERS[roleKey];

  const badgeText = document.getElementById('role-badge-text');
  const badgeIcon = document.querySelector('#role-badge i');

  if (user) {
    badgeText.innerText = `${user.name} (${user.role})`;
    
    if (roleKey === 'vet') {
      badgeIcon.className = 'fa-solid fa-user-doctor';
    } else if (roleKey === 'recepcao') {
      badgeIcon.className = 'fa-solid fa-headset';
    } else {
      badgeIcon.className = 'fa-solid fa-user-gear';
    }
  }

  // Trava de Permissão de Atendimento Clínico (RN02)
  const vetLock = document.getElementById('vet-permission-lock');
  const formProntuario = document.getElementById('form-prontuario');
  const btnEmitirReceita = document.getElementById('btn-emitir-receita');
  const btnSalvarProntuario = document.getElementById('btn-salvar-prontuario');

  if (roleKey !== 'vet') {
    if (vetLock) vetLock.style.display = 'flex';
    if (btnEmitirReceita) btnEmitirReceita.disabled = true;
    if (btnSalvarProntuario) btnSalvarProntuario.disabled = true;
    
    // Desabilitar campos do formulário de prontuário
    if (formProntuario) {
      Array.from(formProntuario.elements).forEach(el => el.disabled = true);
    }
  } else {
    if (vetLock) vetLock.style.display = 'none';
    if (btnEmitirReceita) btnEmitirReceita.disabled = false;
    if (btnSalvarProntuario) btnSalvarProntuario.disabled = false;
    
    if (formProntuario) {
      Array.from(formProntuario.elements).forEach(el => el.disabled = false);
    }
  }
}

/* ==========================================================================
   Renderização dos Módulos & Atualizações da UI
   ========================================================================== */

function renderAll() {
  renderMetrics();
  renderFilaDia();
  renderTutores();
  renderAgendaTable(CURRENT_DATE);
  renderEstoque();
  renderCaixa();
  populatePetSelects();
  populateTutorSelects();
  checkStockAlerts();
}

// 1. Métricas da Dashboard
function renderMetrics() {
  const hojeAgendamentos = state.agendamentos.filter(a => a.data === CURRENT_DATE);
  const aguardando = hojeAgendamentos.filter(a => a.status === 'Aguardando');
  const totalFaturamento = state.transacoesCaixa.reduce((acc, curr) => acc + curr.valor, 0);
  const estoqueZeroCount = state.estoque.filter(i => i.qtd === 0).length;

  document.getElementById('metric-consultas-hoje').innerText = hojeAgendamentos.length;
  document.getElementById('metric-aguardando').innerText = aguardando.length;
  document.getElementById('badge-fila-count').innerText = aguardando.length;
  document.getElementById('metric-faturamento').innerText = `R$ ${totalFaturamento.toFixed(2).replace('.', ',')}`;
  document.getElementById('metric-estoque-zero').innerText = estoqueZeroCount;

  const estoqueBadge = document.getElementById('badge-estoque-alert');
  if (estoqueZeroCount > 0) {
    estoqueBadge.style.display = 'inline-block';
  } else {
    estoqueBadge.style.display = 'none';
  }
}

// Check Alertas de Estoque Zero (RN04)
function checkStockAlerts() {
  const zeroStockItems = state.estoque.filter(i => i.qtd === 0);
  const alertBanner = document.getElementById('dashboard-stock-alert');
  const alertMessage = document.getElementById('stock-alert-message');

  if (zeroStockItems.length > 0) {
    alertBanner.style.display = 'flex';
    alertMessage.innerHTML = `<strong>Alerta de Estoque (RN04):</strong> Existem ${zeroStockItems.length} produto(s) zerado(s) em estoque: <em>${zeroStockItems.map(i => i.nome).join(', ')}</em>.`;
  } else {
    alertBanner.style.display = 'none';
  }
}

// 2. Fila do Dia (RF05)
function renderFilaDia() {
  const tbody = document.getElementById('fila-dia-tbody');
  tbody.innerHTML = '';

  const hojeAgendamentos = state.agendamentos.filter(a => a.data === CURRENT_DATE);

  if (hojeAgendamentos.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" style="text-align:center; color:var(--text-muted);">Nenhum atendimento agendado para hoje.</td></tr>`;
    return;
  }

  hojeAgendamentos.forEach(ag => {
    const tr = document.createElement('tr');
    
    let statusClass = 'status-agendado';
    if (ag.status === 'Aguardando') statusClass = 'status-aguardando';
    if (ag.status === 'Em Atendimento') statusClass = 'status-em-atendimento';
    if (ag.status === 'Finalizado') statusClass = 'status-finalizado';
    if (ag.status === 'Cancelado') statusClass = 'status-cancelado';

    tr.innerHTML = `
      <td><strong>${ag.hora}</strong></td>
      <td>
        <div class="entity-info">
          <div class="pet-avatar">🐾</div>
          <div class="entity-details">
            <div class="title">${ag.petNome}</div>
            <div class="subtitle">Tutor: ${ag.tutorNome}</div>
          </div>
        </div>
      </td>
      <td>${ag.vet}</td>
      <td><span class="status-badge ${statusClass}">${ag.status}</span></td>
      <td class="text-right">
        <select class="form-control" style="width:auto; display:inline-block; font-size:0.8rem; padding:0.3rem 0.5rem;" onchange="alterarStatusAgendamento(${ag.id}, this.value)">
          <option value="Agendado" ${ag.status === 'Agendado' ? 'selected' : ''}>Agendado</option>
          <option value="Aguardando" ${ag.status === 'Aguardando' ? 'selected' : ''}>Aguardando</option>
          <option value="Em Atendimento" ${ag.status === 'Em Atendimento' ? 'selected' : ''}>Em Atendimento</option>
          <option value="Finalizado" ${ag.status === 'Finalizado' ? 'selected' : ''}>Finalizado</option>
          <option value="Cancelado" ${ag.status === 'Cancelado' ? 'selected' : ''}>Cancelado</option>
        </select>
        ${ag.status === 'Em Atendimento' ? `
          <button class="btn btn-primary btn-sm ml-1" onclick="iniciarAtendimentoDireto(${ag.petId})">
            <i class="fa-solid fa-stethoscope"></i> Atender
          </button>
        ` : ''}
      </td>
    `;
    tbody.appendChild(tr);
  });
}

function alterarStatusAgendamento(id, novoStatus) {
  const ag = state.agendamentos.find(a => a.id === id);
  if (ag) {
    ag.status = novoStatus;
    saveState();
    renderAll();
  }
}

function iniciarAtendimentoDireto(petId) {
  switchTab('atendimento');
  const petSelect = document.getElementById('atendimento-pet-select');
  if (petSelect) {
    petSelect.value = petId;
    onSelectPetAtendimento(petId);
  }
}

// 3. Cadastros de Tutores & Pets (RF01, RF02, RN01, RNF03 Busca Rápida)
function renderTutores(filter = '') {
  const tbody = document.getElementById('tutores-tbody');
  tbody.innerHTML = '';

  const query = filter.toLowerCase().trim();

  const filtered = state.tutores.filter(t => {
    const matchTutor = t.nome.toLowerCase().includes(query) || t.cpf.includes(query) || t.telefone.includes(query);
    const matchPet = t.pets.some(p => p.nome.toLowerCase().includes(query) || p.raca.toLowerCase().includes(query));
    return matchTutor || matchPet;
  });

  if (filtered.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" style="text-align:center; color:var(--text-muted);">Nenhum tutor ou pet encontrado.</td></tr>`;
    return;
  }

  filtered.forEach(tutor => {
    const petsHtml = tutor.pets.map(p => `
      <span class="status-badge status-atendimento" style="margin-right:4px; margin-bottom:4px;">
        🐾 ${p.nome} (${p.especie} - ${p.raca || 'SRD'}, ${p.peso}kg)
      </span>
    `).join('');

    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td><strong>${tutor.nome}</strong><br><small style="color:var(--text-muted);">${tutor.endereco || 'Sem endereço'}</small></td>
      <td>${tutor.cpf}</td>
      <td><i class="fa-brands fa-whatsapp" style="color:#25D366;"></i> ${tutor.telefone}</td>
      <td>${petsHtml}</td>
      <td class="text-right">
        <button class="btn btn-secondary btn-sm" onclick="agendarParaTutor(${tutor.id})">
          <i class="fa-solid fa-calendar-plus"></i> Agendar
        </button>
      </td>
    `;
    tbody.appendChild(tr);
  });
}

function filterTutores(value) {
  renderTutores(value);
}

// 4. Agenda (RF04)
function renderAgendaTable(dateVal) {
  const tbody = document.getElementById('agenda-tbody');
  tbody.innerHTML = '';

  const agendamentos = state.agendamentos.filter(a => a.data === dateVal);

  if (agendamentos.length === 0) {
    tbody.innerHTML = `<tr><td colspan="6" style="text-align:center; color:var(--text-muted);">Nenhum agendamento para esta data.</td></tr>`;
    return;
  }

  agendamentos.forEach(ag => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td><strong>${ag.data} às ${ag.hora}</strong></td>
      <td>🐾 ${ag.petNome}</td>
      <td>${ag.tutorNome}</td>
      <td>${ag.vet}</td>
      <td><span class="status-badge status-${ag.status.toLowerCase().replace(' ', '-')}">${ag.status}</span></td>
      <td class="text-right">
        <button class="btn btn-secondary btn-sm" onclick="alterarStatusAgendamento(${ag.id}, 'Cancelado')">Cancelar</button>
      </td>
    `;
    tbody.appendChild(tr);
  });
}

function loadAgendaTable(dateVal) {
  renderAgendaTable(dateVal);
}

// 5. Atendimento Clínico & Prontuário (RF06, RF07, RF08, RF09)
function populatePetSelects() {
  const selectFila = document.getElementById('atendimento-pet-select');
  const selectAgendamento = document.getElementById('agendamento-pet-select');

  if (selectFila) {
    selectFila.innerHTML = '<option value="">-- Selecione um paciente --</option>';
    state.tutores.forEach(t => {
      t.pets.forEach(p => {
        selectFila.innerHTML += `<option value="${p.id}">${p.nome} (Tutor: ${t.nome})</option>`;
      });
    });
  }

  if (selectAgendamento) {
    selectAgendamento.innerHTML = '<option value="">-- Selecione o Pet --</option>';
    state.tutores.forEach(t => {
      t.pets.forEach(p => {
        selectAgendamento.innerHTML += `<option value="${p.id}">${p.nome} - ${t.nome} (CPF: ${t.cpf})</option>`;
      });
    });
  }
}

function populateTutorSelects() {
  const selectTutor = document.getElementById('pagamento-tutor-select');
  if (selectTutor) {
    selectTutor.innerHTML = '<option value="">-- Cliente Avulso --</option>';
    state.tutores.forEach(t => {
      selectTutor.innerHTML += `<option value="${t.nome}">${t.nome}</option>`;
    });
  }
}

let activeSelectedPet = null;

function onSelectPetAtendimento(petId) {
  petId = parseInt(petId);
  if (!petId) {
    document.getElementById('selected-pet-card').style.display = 'none';
    document.getElementById('patient-timeline-container').innerHTML = '<p style="font-size:0.85rem; color:var(--text-muted);">Nenhum histórico selecionado.</p>';
    document.getElementById('pet-vaccines-list').innerText = 'Selecione um paciente para ver as vacinas.';
    activeSelectedPet = null;
    return;
  }

  // Encontrar pet e tutor
  let foundPet = null;
  let foundTutor = null;

  for (const t of state.tutores) {
    const p = t.pets.find(pet => pet.id === petId);
    if (p) {
      foundPet = p;
      foundTutor = t;
      break;
    }
  }

  if (foundPet) {
    activeSelectedPet = { pet: foundPet, tutor: foundTutor };
    
    document.getElementById('selected-pet-card').style.display = 'block';
    document.getElementById('pet-name-display').innerText = foundPet.nome;
    document.getElementById('pet-species-breed').innerText = `${foundPet.especie} - ${foundPet.raca || 'SRD'} (${foundPet.sexo})`;
    document.getElementById('pet-tutor-display').innerText = foundTutor.nome;
    document.getElementById('pet-age-weight').innerText = `${foundPet.idade || 'N/I'} | ${foundPet.peso || '--'} kg`;

    renderProntuarioTimeline(petId);
    renderPetVaccines(petId);
  }
}

function renderProntuarioTimeline(petId) {
  const container = document.getElementById('patient-timeline-container');
  container.innerHTML = '';

  const historico = state.prontuarios[petId] || [];

  if (historico.length === 0) {
    container.innerHTML = '<p style="font-size:0.85rem; color:var(--text-muted); padding-top:0.5rem;">Primeira consulta deste paciente registrada no sistema.</p>';
    return;
  }

  historico.forEach(item => {
    const div = document.createElement('div');
    div.className = 'timeline-item';
    div.innerHTML = `
      <div class="timeline-dot"></div>
      <div class="timeline-card">
        <div class="timeline-date">${item.data} - ${item.vet}</div>
        <div class="timeline-title">Queixa: ${item.queixa}</div>
        <div class="timeline-body">
          <strong>Diagnóstico:</strong> ${item.diagnostico}<br>
          <strong>Orientações:</strong> ${item.orientacoes}
        </div>
      </div>
    `;
    container.appendChild(div);
  });
}

function renderPetVaccines(petId) {
  const container = document.getElementById('pet-vaccines-list');
  const vacinas = state.vacinas[petId] || [];

  if (vacinas.length === 0) {
    container.innerHTML = '<em>Nenhuma vacina registrada para este pet.</em>';
    return;
  }

  container.innerHTML = vacinas.map(v => `
    <div style="padding:0.4rem 0; border-bottom:1px solid var(--border); display:flex; justify-content:space-between;">
      <div><strong>${v.nome}</strong><br><small>Aplicada em: ${v.dataAplicacao}</small></div>
      <div class="text-right" style="color:var(--primary-hover);"><strong>Próxima:</strong><br><small>${v.proximaDose}</small></div>
    </div>
  `).join('');
}

function salvarProntuario(e) {
  e.preventDefault();

  if (currentUserRole !== 'vet') {
    alert('Atenção (RN02): Apenas o perfil de Médico Veterinário pode salvar o prontuário!');
    return;
  }

  if (!activeSelectedPet) {
    alert('Por favor, selecione um paciente da fila primeiro!');
    return;
  }

  const queixa = document.getElementById('prontuario-queixa').value;
  const sintomas = document.getElementById('prontuario-sintomas').value;
  const diagnostico = document.getElementById('prontuario-diagnostico').value;
  const orientacoes = document.getElementById('prontuario-orientacoes').value;

  if (!queixa || !diagnostico) {
    alert('Por favor, preencha pelo menos a queixa e o diagnóstico!');
    return;
  }

  const petId = activeSelectedPet.pet.id;
  if (!state.prontuarios[petId]) state.prontuarios[petId] = [];

  const novoRegistro = {
    data: new Date().toLocaleDateString('pt-BR'),
    vet: USERS[currentUserRole].name + ' (' + USERS[currentUserRole].crmv + ')',
    queixa,
    sintomas,
    diagnostico,
    orientacoes
  };

  state.prontuarios[petId].unshift(novoRegistro);

  // Atualizar status do agendamento de hoje para Finalizado
  const ag = state.agendamentos.find(a => a.petId === petId && a.data === CURRENT_DATE);
  if (ag) ag.status = 'Finalizado';

  saveState();
  renderAll();

  // Limpar form
  document.getElementById('form-prontuario').reset();
  renderProntuarioTimeline(petId);
  alert('Prontuário salvo e consulta finalizada com sucesso!');
}

// 6. Emissão de Receita Médica (RF08, RN03 CRMV Automático)
function gerarVisualizacaoReceita(e) {
  e.preventDefault();
  
  if (!activeSelectedPet) {
    alert('Selecione um paciente antes de emitir a receita médica!');
    return;
  }

  const texto = document.getElementById('receita-texto').value;
  const vetInfo = USERS[currentUserRole];

  document.getElementById('recipe-preview-container').style.display = 'block';
  document.getElementById('recipe-vet-info').innerText = `${vetInfo.name} - ${vetInfo.crmv || 'CRMV/SP 12345'}`;
  document.getElementById('recipe-patient-name').innerText = `${activeSelectedPet.pet.nome} (${activeSelectedPet.pet.especie})`;
  document.getElementById('recipe-tutor-name').innerText = activeSelectedPet.tutor.nome;
  document.getElementById('recipe-date-display').innerText = new Date().toLocaleDateString('pt-BR');
  document.getElementById('recipe-meds-list').innerText = texto;
  
  document.getElementById('recipe-vet-signature').innerText = vetInfo.name;
  document.getElementById('recipe-crmv-signature').innerText = `${vetInfo.crmv || 'CRMV/SP 12345'} (RN03 - CRMV Obrigatório)`;
}

function imprimirReceita() {
  const content = document.getElementById('recipe-preview-container');
  if (!content || content.style.display === 'none') {
    alert('Por favor, gere a pré-visualização da receita antes de imprimir!');
    return;
  }

  const printArea = document.getElementById('print-recipe-area');
  printArea.innerHTML = content.outerHTML;
  window.print();
}

// 7. Estoque (RF10, RF11, RN04)
function renderEstoque(filter = '') {
  const tbody = document.getElementById('estoque-tbody');
  tbody.innerHTML = '';

  const query = filter.toLowerCase().trim();
  const items = state.estoque.filter(i => i.nome.toLowerCase().includes(query));

  if (items.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" style="text-align:center; color:var(--text-muted);">Nenhum produto cadastrado.</td></tr>`;
    return;
  }

  items.forEach(item => {
    const isZero = item.qtd === 0;
    const isLow = item.qtd > 0 && item.qtd <= 5;

    let badgeStatus = `<span class="status-badge status-finalizado">Em Estoque</span>`;
    if (isZero) badgeStatus = `<span class="status-badge status-cancelado"><i class="fa-solid fa-triangle-exclamation"></i> ZERADO (RN04)</span>`;
    else if (isLow) badgeStatus = `<span class="status-badge status-aguardando">Baixo Estoque</span>`;

    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td><strong>${item.nome}</strong></td>
      <td>R$ ${item.preco.toFixed(2).replace('.', ',')}</td>
      <td><strong style="${isZero ? 'color:var(--danger-text); font-size:1.1rem;' : ''}">${item.qtd} un</strong></td>
      <td>${badgeStatus}</td>
      <td class="text-right">
        <button class="btn btn-secondary btn-sm" onclick="ajustarEstoque(${item.id}, 1)">+1</button>
        <button class="btn btn-secondary btn-sm" onclick="ajustarEstoque(${item.id}, -1)" ${item.qtd === 0 ? 'disabled' : ''}>-1 Baixa</button>
      </td>
    `;
    tbody.appendChild(tr);
  });
}

function filterEstoque(val) {
  renderEstoque(val);
}

function ajustarEstoque(id, delta) {
  const item = state.estoque.find(i => i.id === id);
  if (item) {
    item.qtd = Math.max(0, item.qtd + delta);
    saveState();
    renderAll();
  }
}

function salvarProduto(e) {
  e.preventDefault();
  const nome = document.getElementById('produto-nome').value;
  const preco = parseFloat(document.getElementById('produto-preco').value);
  const qtd = parseInt(document.getElementById('produto-qtd').value);

  const novo = {
    id: Date.now(),
    nome,
    preco,
    qtd
  };

  state.estoque.push(novo);
  saveState();
  renderAll();
  closeModal('modal-novo-produto');
  document.getElementById('form-novo-produto').reset();
}

// 8. Caixa & Financeiro (RF12, RF13)
function renderCaixa() {
  const tbody = document.getElementById('caixa-transacoes-tbody');
  tbody.innerHTML = '';

  const transacoes = state.transacoesCaixa;
  let total = 0;
  const porMetodo = { 'Pix': 0, 'Cartão de Crédito': 0, 'Cartão de Débito': 0, 'Dinheiro': 0 };

  transacoes.forEach(t => {
    total += t.valor;
    if (porMetodo[t.metodo] !== undefined) {
      porMetodo[t.metodo] += t.valor;
    }

    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${t.hora}</td>
      <td><strong>${t.descricao}</strong></td>
      <td>${t.tutor}</td>
      <td><span class="status-badge status-agendado">${t.metodo}</span></td>
      <td class="text-right"><strong>R$ ${t.valor.toFixed(2).replace('.', ',')}</strong></td>
    `;
    tbody.appendChild(tr);
  });

  document.getElementById('caixa-total-display').innerText = `R$ ${total.toFixed(2).replace('.', ',')}`;

  const breakdownContainer = document.getElementById('caixa-breakdown-list');
  breakdownContainer.innerHTML = Object.keys(porMetodo).map(m => `
    <div class="flex-between" style="font-size:0.875rem; background:white; padding:0.6rem 0.85rem; border-radius:var(--radius-sm); border:1px solid var(--border);">
      <span>${m}</span>
      <strong>R$ ${porMetodo[m].toFixed(2).replace('.', ',')}</strong>
    </div>
  `).join('');
}

function registrarPagamento(e) {
  e.preventDefault();
  const descricao = document.getElementById('pagamento-descricao').value;
  const tutor = document.getElementById('pagamento-tutor-select').value || 'Cliente Avulso';
  const valor = parseFloat(document.getElementById('pagamento-valor').value);
  const metodo = document.getElementById('pagamento-metodo').value;

  const now = new Date();
  const hora = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`;

  const novaTransacao = {
    id: Date.now(),
    hora,
    descricao,
    tutor,
    metodo,
    valor
  };

  state.transacoesCaixa.unshift(novaTransacao);
  saveState();
  renderAll();
  document.getElementById('form-pagamento').reset();
  alert('Pagamento registrado com sucesso no Caixa!');
}

function imprimirRelatorioCaixa() {
  window.print();
}

// 9. Cadastro de Tutor e Pet
function salvarTutorEPet(e) {
  e.preventDefault();
  const nomeTutor = document.getElementById('tutor-nome').value;
  const cpf = document.getElementById('tutor-cpf').value;
  const telefone = document.getElementById('tutor-telefone').value;
  const endereco = document.getElementById('tutor-endereco').value;

  const nomePet = document.getElementById('pet-nome').value;
  const especie = document.getElementById('pet-especie').value;
  const raca = document.getElementById('pet-raca').value;
  const sexo = document.getElementById('pet-sexo').value;
  const idade = document.getElementById('pet-idade').value;
  const peso = parseFloat(document.getElementById('pet-peso').value) || 0;

  const newPetId = Date.now();
  const newTutorId = Date.now() + 1;

  const novoTutor = {
    id: newTutorId,
    nome: nomeTutor,
    cpf,
    telefone,
    endereco,
    pets: [
      { id: newPetId, nome: nomePet, especie, raca, sexo, idade, peso }
    ]
  };

  state.tutores.unshift(novoTutor);
  saveState();
  renderAll();
  closeModal('modal-novo-tutor');
  document.getElementById('form-novo-tutor').reset();
  alert('Tutor e Pet cadastrados com sucesso!');
}

// 10. Salvar Agendamento (RF04)
function salvarAgendamento(e) {
  e.preventDefault();
  const petId = parseInt(document.getElementById('agendamento-pet-select').value);
  const data = document.getElementById('agendamento-data').value;
  const hora = document.getElementById('agendamento-hora').value;
  const vet = document.getElementById('agendamento-vet-select').value;

  let petNome = '';
  let tutorNome = '';

  for (const t of state.tutores) {
    const p = t.pets.find(pet => pet.id === petId);
    if (p) {
      petNome = p.nome;
      tutorNome = t.nome;
      break;
    }
  }

  const novo = {
    id: Date.now(),
    data,
    hora,
    petId,
    petNome,
    tutorNome,
    vet,
    status: 'Agendado'
  };

  state.agendamentos.push(novo);
  saveState();
  renderAll();
  closeModal('modal-novo-agendamento');
  document.getElementById('form-novo-agendamento').reset();
  alert('Consulta agendada com sucesso!');
}

// 11. Salvar Vacina (RF09)
function salvarVacina(e) {
  e.preventDefault();
  if (!activeSelectedPet) {
    alert('Selecione um paciente em atendimento primeiro!');
    return;
  }

  const petId = activeSelectedPet.pet.id;
  const nome = document.getElementById('vacina-nome').value;
  const dataAplicacao = document.getElementById('vacina-data-aplicacao').value;
  const proximaDose = document.getElementById('vacina-data-proxima').value;

  if (!state.vacinas[petId]) state.vacinas[petId] = [];

  state.vacinas[petId].unshift({ nome, dataAplicacao, proximaDose });
  saveState();
  renderPetVaccines(petId);
  closeModal('modal-nova-vacina');
  document.getElementById('form-nova-vacina').reset();
}

// Helpers de Modal
function openModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) modal.classList.add('active');
}

function closeModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) modal.classList.remove('active');
}

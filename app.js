/* ==========================================================================
   VetCare Silvestres - Sistema Clínico e Consultoria Comportamental
   Lógica JavaScript Interativa
   ========================================================================== */

const CURRENT_DATE = new Date().toISOString().split('T')[0];

// Perfis e Usuários Simulados no Sistema
const USERS = {
  vet: { name: 'Dr. Carlos Eduardo', role: 'Médico Veterinário', crmv: 'CRMV/SP 12345', cargoKey: 'Veterinário' },
  adestrador: { name: 'Lucas Mendes', role: 'Adestrador / Esp. Silvestres', crmv: '', cargoKey: 'Adestrador / Esp. Silvestres' },
  recepcao: { name: 'Ana Silva', role: 'Atendente / Recepcionista', crmv: '', cargoKey: 'Atendente' },
  admin: { name: 'Mariana Oliveira', role: 'Administradora', crmv: '', cargoKey: 'Administrador' }
};

let currentUserRole = 'vet'; // Perfil ativo por padrão

// Estado Inicial Pré-carregado
let state = {
  funcionarios: [
    { id: 1, nome: 'Dr. Carlos Eduardo', cpf: '111.222.333-44', email: 'carlos.vet@vetcare.com', telefone: '(11) 98888-1111', cargo: 'Veterinário', crmv: 'CRMV/SP 12345' },
    { id: 2, nome: 'Lucas Mendes', cpf: '555.666.777-88', email: 'lucas.adestrador@vetcare.com', telefone: '(11) 97777-2222', cargo: 'Adestrador / Esp. Silvestres', crmv: '' },
    { id: 3, nome: 'Ana Silva', cpf: '999.888.777-66', email: 'ana.recepcao@vetcare.com', telefone: '(11) 96666-3333', cargo: 'Atendente', crmv: '' }
  ],
  tutores: [
    {
      id: 1,
      nome: 'Mariana Oliveira Santos',
      cpf: '123.456.789-00',
      telefone: '(11) 98765-4321',
      endereco: 'Rua das Flores, 120 - SP',
      pets: [
        { id: 101, nome: 'Kiko', categoria: 'Silvestre / Exótico', especieRaca: 'Papagaio Verdadeiro', sexo: 'Macho', idade: '4 anos', peso: '420g', habitat: 'Viveiro interno com luz natural, necessita de enriquecimento para forrageamento.' }
      ]
    },
    {
      id: 2,
      nome: 'Roberto Almeida',
      cpf: '987.654.321-11',
      telefone: '(11) 91234-5678',
      endereco: 'Av. Paulista, 900 - SP',
      pets: [
        { id: 102, nome: 'Tufão', categoria: 'Silvestre / Exótico', especieRaca: 'Jabuti Piranga', sexo: 'Macho', idade: '6 anos', peso: '2.4kg', habitat: 'Recinto externo de 4m² com lâmpada UVB e aquecimento focal.' }
      ]
    },
    {
      id: 3,
      nome: 'Fernanda Costa',
      cpf: '456.789.123-22',
      telefone: '(11) 99887-7665',
      endereco: 'Rua Augusta, 45 - SP',
      pets: [
        { id: 103, nome: 'Thor', categoria: 'Doméstico', especieRaca: 'Cão - Golden Retriever', sexo: 'Macho', idade: '3 anos', peso: '28kg', habitat: 'Apartamento com 2 passeios diários.' }
      ]
    }
  ],
  agendamentos: [
    { id: 1, data: CURRENT_DATE, hora: '09:00', petId: 101, petNome: 'Kiko (Papagaio)', tutorNome: 'Mariana Oliveira', tipoServico: 'Consultoria de Adestramento & Manejo Silvestre', profissional: 'Lucas Mendes (Adestrador / Esp. Silvestres)', status: 'Em Atendimento' },
    { id: 2, data: CURRENT_DATE, hora: '10:30', petId: 102, petNome: 'Tufão (Jabuti)', tutorNome: 'Roberto Almeida', tipoServico: 'Consulta Clínica Veterinária', profissional: 'Dr. Carlos Eduardo (Veterinário)', status: 'Aguardando' },
    { id: 3, data: CURRENT_DATE, hora: '14:00', petId: 103, petNome: 'Thor (Golden)', tutorNome: 'Fernanda Costa', tipoServico: 'Consulta Clínica Veterinária', profissional: 'Dr. Carlos Eduardo (Veterinário)', status: 'Agendado' }
  ],
  prontuarios: {
    101: [
      {
        data: '20/08/2026',
        profissional: 'Lucas Mendes (Adestrador / Esp. Silvestres)',
        queixa: 'Tutor relata vocalização excessiva e arrancamento de penas (arrancamento de penugem no peito).',
        sintomas: 'Comportamento ansioso quando o tutor se ausenta.',
        diagnostico: 'Estresse por tédio e falta de estimulação cognitiva.',
        orientacoes: 'Instalação de brinquedos de destruição (madeira atóxica, papel pardo) e treinamento de alvos para entrada voluntária na gaiola.'
      }
    ],
    102: [
      {
        data: '10/07/2026',
        profissional: 'Dr. Carlos Eduardo (Veterinário)',
        queixa: 'Check-up nutricional e avaliação de carapaça.',
        sintomas: 'Leve descalcificação sem piramidismo acentuado.',
        diagnostico: 'Deficiência leve de Vitamina D3 e Cálcio.',
        orientacoes: 'Suplementação oral de cálcio 2x por semana e banho de sol diário de 45 minutos.'
      }
    ]
  },
  vacinas: {
    103: [
      { nome: 'Vacina V10 Polivalente', dataAplicacao: '15/08/2026', proximaDose: '15/08/2027' },
      { nome: 'Antirrábica', dataAplicacao: '15/08/2026', proximaDose: '15/08/2027' }
    ]
  }
};

function loadState() {
  const saved = localStorage.getItem('vetcare_silvestres_state');
  if (saved) {
    try {
      state = JSON.parse(saved);
    } catch (e) {
      console.warn('Erro ao carregar localStorage, mantendo padrão.', e);
    }
  }
}

function saveState() {
  localStorage.setItem('vetcare_silvestres_state', JSON.stringify(state));
}

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
  
  const filterDateInput = document.getElementById('agenda-date-filter');
  if (filterDateInput) filterDateInput.value = CURRENT_DATE;

  const agendamentoData = document.getElementById('agendamento-data');
  if (agendamentoData) agendamentoData.value = CURRENT_DATE;
}

/* ==========================================================================
   Navegação por Abas & Perfis
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

  const titles = {
    'dashboard': { title: 'Dashboard & Fila do Dia', subtitle: 'Acompanhamento dos atendimentos em tempo real' },
    'funcionarios': { title: 'Gestão de Funcionários', subtitle: 'Cadastro de veterinários, adestradores e atendentes' },
    'tutores-pets': { title: 'Cadastros de Tutores e Pacientes', subtitle: 'Registros de pets domésticos e silvestres' },
    'agenda': { title: 'Agenda de Atendimentos', subtitle: 'Marcação de consultas e consultorias comportamentais' },
    'atendimento': { title: 'Atendimento & Prontuário', subtitle: 'Registro clínico e orientações de manejo/adestramento' }
  };

  if (titles[tabId]) {
    document.getElementById('page-title').innerText = titles[tabId].title;
    document.getElementById('page-subtitle').innerText = titles[tabId].subtitle;
  }
}

function switchUserRole(roleKey) {
  currentUserRole = roleKey;
  const user = USERS[roleKey];

  const badgeText = document.getElementById('role-badge-text');
  const badgeIcon = document.querySelector('#role-badge i');

  if (user) {
    badgeText.innerText = `${user.name} (${user.role})`;
    
    if (roleKey === 'vet') {
      badgeIcon.className = 'fa-solid fa-user-doctor';
    } else if (roleKey === 'adestrador') {
      badgeIcon.className = 'fa-solid fa-feather';
    } else if (roleKey === 'recepcao') {
      badgeIcon.className = 'fa-solid fa-headset';
    } else {
      badgeIcon.className = 'fa-solid fa-user-gear';
    }
  }

  updateServiceBanner();
}

function updateServiceBanner() {
  const banner = document.getElementById('service-type-banner');
  const text = document.getElementById('service-banner-text');
  const user = USERS[currentUserRole];

  if (currentUserRole === 'vet') {
    banner.style.backgroundColor = 'var(--badge-vet-bg)';
    banner.style.color = 'var(--badge-vet-text)';
    text.innerHTML = `<strong>Perfil Clínico (${user.name}):</strong> Preenchimento de Prontuário Médico e Emissão de Receita com CRMV (${user.crmv}).`;
  } else if (currentUserRole === 'adestrador') {
    banner.style.backgroundColor = 'var(--badge-adestrador-bg)';
    banner.style.color = 'var(--badge-adestrador-text)';
    text.innerHTML = `<strong>Perfil Adestrador / Manejo (${user.name}):</strong> Registro de Orientação Comportamental, Adequação de Recinto e Guia de Adestramento Silvestre.`;
  } else {
    banner.style.backgroundColor = 'var(--badge-atendente-bg)';
    banner.style.color = 'var(--badge-atendente-text)';
    text.innerHTML = `<strong>Perfil Atendimento / Adm (${user.name}):</strong> Modo de visualização. Selecione um perfil técnico (Vet ou Adestrador) para registrar diagnósticos ou orientações.`;
  }
}

/* ==========================================================================
   Renderização e Atualização da UI
   ========================================================================== */

function renderAll() {
  renderMetrics();
  renderFilaDia();
  renderFuncionarios();
  renderTutores();
  renderAgendaTable(CURRENT_DATE);
  populatePetSelects();
  populateProfissionalSelects();
  updateServiceBanner();
}

function renderMetrics() {
  const hojeAgendamentos = state.agendamentos.filter(a => a.data === CURRENT_DATE);
  const aguardando = hojeAgendamentos.filter(a => a.status === 'Aguardando');
  
  let silvestresCount = 0;
  state.tutores.forEach(t => {
    silvestresCount += t.pets.filter(p => p.categoria.includes('Silvestre')).length;
  });

  document.getElementById('metric-consultas-hoje').innerText = hojeAgendamentos.length;
  document.getElementById('metric-aguardando').innerText = aguardando.length;
  document.getElementById('badge-fila-count').innerText = aguardando.length;
  document.getElementById('metric-silvestres').innerText = silvestresCount;
}

// 1. Fila do Dia
function renderFilaDia() {
  const tbody = document.getElementById('fila-dia-tbody');
  tbody.innerHTML = '';

  const hojeAgendamentos = state.agendamentos.filter(a => a.data === CURRENT_DATE);

  if (hojeAgendamentos.length === 0) {
    tbody.innerHTML = `<tr><td colspan="6" style="text-align:center; color:var(--text-muted);">Nenhum atendimento agendado para hoje.</td></tr>`;
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
      <td><span class="status-badge badge-adestrador">${ag.tipoServico}</span></td>
      <td>${ag.profissional}</td>
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

// 2. Cadastro de Funcionários (RF01)
function renderFuncionarios(filter = '') {
  const tbody = document.getElementById('funcionarios-tbody');
  tbody.innerHTML = '';

  const query = filter.toLowerCase().trim();
  const items = state.funcionarios.filter(f => f.nome.toLowerCase().includes(query) || f.cargo.toLowerCase().includes(query) || f.cpf.includes(query));

  if (items.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" style="text-align:center; color:var(--text-muted);">Nenhum funcionário encontrado.</td></tr>`;
    return;
  }

  items.forEach(f => {
    let badgeClass = 'badge-atendente';
    if (f.cargo.includes('Veterinário')) badgeClass = 'badge-vet';
    if (f.cargo.includes('Adestrador')) badgeClass = 'badge-adestrador';

    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td><strong>${f.nome}</strong><br><small style="color:var(--text-muted);">${f.email}</small></td>
      <td>${f.cpf}<br><small>${f.telefone}</small></td>
      <td><span class="status-badge ${badgeClass}">${f.cargo}</span></td>
      <td>${f.crmv ? `<strong>${f.crmv}</strong>` : 'Suporte a Tutores & Silvestres'}</td>
      <td class="text-right">
        <button class="btn btn-secondary btn-sm" onclick="removerFuncionario(${f.id})">Remover</button>
      </td>
    `;
    tbody.appendChild(tr);
  });
}

function filterFuncionarios(val) {
  renderFuncionarios(val);
}

function salvarFuncionario(e) {
  e.preventDefault();
  const nome = document.getElementById('func-nome').value;
  const cpf = document.getElementById('func-cpf').value;
  const telefone = document.getElementById('func-telefone').value;
  const email = document.getElementById('func-email').value;
  const cargo = document.getElementById('func-cargo').value;
  const crmv = document.getElementById('func-crmv').value;

  const novo = {
    id: Date.now(),
    nome,
    cpf,
    telefone,
    email,
    cargo,
    crmv: cargo === 'Veterinário' ? (crmv || 'CRMV/SP 00000') : ''
  };

  state.funcionarios.push(novo);
  saveState();
  renderAll();
  closeModal('modal-novo-funcionario');
  document.getElementById('form-novo-funcionario').reset();
  alert('Funcionário cadastrado com sucesso!');
}

function removerFuncionario(id) {
  if (confirm('Deseja realmente remover este funcionário?')) {
    state.funcionarios = state.funcionarios.filter(f => f.id !== id);
    saveState();
    renderAll();
  }
}

function toggleCRMVField(cargo) {
  const crmvGroup = document.getElementById('crmv-field-group');
  if (cargo === 'Veterinário') {
    crmvGroup.style.display = 'flex';
  } else {
    crmvGroup.style.display = 'none';
  }
}

// 3. Cadastros de Tutores & Pets
function renderTutores(filter = '') {
  const tbody = document.getElementById('tutores-tbody');
  tbody.innerHTML = '';

  const query = filter.toLowerCase().trim();

  const filtered = state.tutores.filter(t => {
    const matchTutor = t.nome.toLowerCase().includes(query) || t.cpf.includes(query);
    const matchPet = t.pets.some(p => p.nome.toLowerCase().includes(query) || p.especieRaca.toLowerCase().includes(query));
    return matchTutor || matchPet;
  });

  if (filtered.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" style="text-align:center; color:var(--text-muted);">Nenhum tutor ou pet encontrado.</td></tr>`;
    return;
  }

  filtered.forEach(tutor => {
    const petsHtml = tutor.pets.map(p => {
      const isSilvestre = p.categoria.includes('Silvestre');
      const badgeClass = isSilvestre ? 'badge-silvestre' : 'status-atendimento';
      const icon = isSilvestre ? '🦜' : '🐾';
      return `
        <span class="status-badge ${badgeClass}" style="margin-right:4px; margin-bottom:4px;">
          ${icon} ${p.nome} (${p.especieRaca})
        </span>
      `;
    }).join('');

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

function filterTutores(val) {
  renderTutores(val);
}

// 4. Agenda
function renderAgendaTable(dateVal) {
  const tbody = document.getElementById('agenda-tbody');
  tbody.innerHTML = '';

  const agendamentos = state.agendamentos.filter(a => a.data === dateVal);

  if (agendamentos.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; color:var(--text-muted);">Nenhum agendamento para esta data.</td></tr>`;
    return;
  }

  agendamentos.forEach(ag => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td><strong>${ag.data} às ${ag.hora}</strong></td>
      <td>🐾 ${ag.petNome}</td>
      <td>${ag.tutorNome}</td>
      <td><span class="status-badge badge-adestrador">${ag.tipoServico}</span></td>
      <td>${ag.profissional}</td>
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

// 5. Atendimento & Prontuário / Manejo
function populatePetSelects() {
  const selectFila = document.getElementById('atendimento-pet-select');
  const selectAgendamento = document.getElementById('agendamento-pet-select');

  if (selectFila) {
    selectFila.innerHTML = '<option value="">-- Selecione um paciente --</option>';
    state.tutores.forEach(t => {
      t.pets.forEach(p => {
        selectFila.innerHTML += `<option value="${p.id}">${p.nome} (${p.especieRaca}) - Tutor: ${t.nome}</option>`;
      });
    });
  }

  if (selectAgendamento) {
    selectAgendamento.innerHTML = '<option value="">-- Selecione o Pet --</option>';
    state.tutores.forEach(t => {
      t.pets.forEach(p => {
        selectAgendamento.innerHTML += `<option value="${p.id}">${p.nome} [${p.categoria}] - ${t.nome}</option>`;
      });
    });
  }
}

function populateProfissionalSelects() {
  const selectAgendamento = document.getElementById('agendamento-profissional-select');
  if (selectAgendamento) {
    selectAgendamento.innerHTML = '';
    state.funcionarios.forEach(f => {
      if (f.cargo !== 'Atendente') {
        selectAgendamento.innerHTML += `<option value="${f.nome} (${f.cargo})">${f.nome} (${f.cargo})</option>`;
      }
    });
  }
}

let activeSelectedPet = null;

function onSelectPetAtendimento(petId) {
  petId = parseInt(petId);
  if (!petId) {
    document.getElementById('selected-pet-card').style.display = 'none';
    document.getElementById('patient-timeline-container').innerHTML = '<p style="font-size:0.85rem; color:var(--text-muted);">Nenhum histórico selecionado.</p>';
    document.getElementById('pet-vaccines-list').innerText = 'Selecione um paciente para ver o histórico.';
    activeSelectedPet = null;
    return;
  }

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
    document.getElementById('pet-species-breed').innerText = `${foundPet.categoria} - ${foundPet.especieRaca} (${foundPet.sexo})`;
    document.getElementById('pet-tutor-display').innerText = foundTutor.nome;
    document.getElementById('pet-age-weight').innerText = `${foundPet.idade || 'N/I'} | ${foundPet.peso || '--'}`;
    document.getElementById('pet-habitat-notes').innerText = foundPet.habitat || 'Sem observações registradas.';

    renderProntuarioTimeline(petId);
    renderPetVaccines(petId);
  }
}

function renderProntuarioTimeline(petId) {
  const container = document.getElementById('patient-timeline-container');
  container.innerHTML = '';

  const historico = state.prontuarios[petId] || [];

  if (historico.length === 0) {
    container.innerHTML = '<p style="font-size:0.85rem; color:var(--text-muted); padding-top:0.5rem;">Primeiro atendimento deste paciente registrado no sistema.</p>';
    return;
  }

  historico.forEach(item => {
    const div = document.createElement('div');
    div.className = 'timeline-item';
    div.innerHTML = `
      <div class="timeline-dot"></div>
      <div class="timeline-card">
        <div class="timeline-date">${item.data} - ${item.profissional}</div>
        <div class="timeline-title">Queixa/Dificuldade: ${item.queixa}</div>
        <div class="timeline-body">
          <strong>Avaliação/Diagnóstico:</strong> ${item.diagnostico}<br>
          <strong>Orientações/Manejo:</strong> ${item.orientacoes}
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
    container.innerHTML = '<em>Nenhuma vacina ou tratamento registrado.</em>';
    return;
  }

  container.innerHTML = vacinas.map(v => `
    <div style="padding:0.4rem 0; border-bottom:1px solid var(--border); display:flex; justify-content:space-between;">
      <div><strong>${v.nome}</strong><br><small>Aplicado: ${v.dataAplicacao}</small></div>
      <div class="text-right" style="color:var(--primary-hover);"><strong>Próximo:</strong><br><small>${v.proximaDose}</small></div>
    </div>
  `).join('');
}

function salvarProntuario(e) {
  e.preventDefault();

  if (!activeSelectedPet) {
    alert('Por favor, selecione um paciente da fila primeiro!');
    return;
  }

  const queixa = document.getElementById('prontuario-queixa').value;
  const sintomas = document.getElementById('prontuario-sintomas').value;
  const diagnostico = document.getElementById('prontuario-diagnostico').value;
  const orientacoes = document.getElementById('prontuario-orientacoes').value;

  if (!queixa || !diagnostico) {
    alert('Por favor, preencha os campos de queixa e diagnóstico/avaliação!');
    return;
  }

  const petId = activeSelectedPet.pet.id;
  if (!state.prontuarios[petId]) state.prontuarios[petId] = [];

  const currentUser = USERS[currentUserRole];

  const novoRegistro = {
    data: new Date().toLocaleDateString('pt-BR'),
    profissional: `${currentUser.name} (${currentUser.role})`,
    queixa,
    sintomas,
    diagnostico,
    orientacoes
  };

  state.prontuarios[petId].unshift(novoRegistro);

  const ag = state.agendamentos.find(a => a.petId === petId && a.data === CURRENT_DATE);
  if (ag) ag.status = 'Finalizado';

  saveState();
  renderAll();

  document.getElementById('form-prontuario').reset();
  renderProntuarioTimeline(petId);
  alert('Atendimento registrado e consulta finalizada!');
}

function gerarVisualizacaoReceita(e) {
  e.preventDefault();
  
  if (!activeSelectedPet) {
    alert('Selecione um paciente antes de emitir o documento!');
    return;
  }

  const texto = document.getElementById('receita-texto').value;
  const user = USERS[currentUserRole];
  const isVet = currentUserRole === 'vet';

  document.getElementById('recipe-preview-container').style.display = 'block';
  document.getElementById('recipe-vet-info').innerText = `${user.name} - ${isVet ? user.crmv : 'Consultor de Manejo & Adestramento'}`;
  document.getElementById('recipe-patient-name').innerText = `${activeSelectedPet.pet.nome} (${activeSelectedPet.pet.especieRaca})`;
  document.getElementById('recipe-doc-type').innerText = isVet ? 'Receita Médica' : 'Guia de Manejo Silvestre';
  document.getElementById('recipe-tutor-name').innerText = activeSelectedPet.tutor.nome;
  document.getElementById('recipe-date-display').innerText = new Date().toLocaleDateString('pt-BR');
  document.getElementById('recipe-doc-header').innerText = isVet ? 'Prescrição Médica:' : 'Plano de Manejo e Treinamento Comportamental:';
  document.getElementById('recipe-meds-list').innerText = texto;
  
  document.getElementById('recipe-vet-signature').innerText = user.name;
  document.getElementById('recipe-crmv-signature').innerText = isVet ? user.crmv : 'Especialista em Manejo & Adestramento Silvestre';
}

function imprimirReceita() {
  const content = document.getElementById('recipe-preview-container');
  if (!content || content.style.display === 'none') {
    alert('Por favor, gere a visualização do documento antes de imprimir!');
    return;
  }

  const printArea = document.getElementById('print-recipe-area');
  printArea.innerHTML = content.outerHTML;
  window.print();
}

// 6. Cadastros Gerais
function salvarTutorEPet(e) {
  e.preventDefault();
  const nomeTutor = document.getElementById('tutor-nome').value;
  const cpf = document.getElementById('tutor-cpf').value;
  const telefone = document.getElementById('tutor-telefone').value;
  const endereco = document.getElementById('tutor-endereco').value;

  const nomePet = document.getElementById('pet-nome').value;
  const categoria = document.getElementById('pet-categoria').value;
  const especieRaca = document.getElementById('pet-especie-raca').value;
  const sexo = document.getElementById('pet-sexo').value;
  const idade = document.getElementById('pet-idade').value;
  const peso = document.getElementById('pet-peso').value;
  const habitat = document.getElementById('pet-habitat').value;

  const newPetId = Date.now();
  const newTutorId = Date.now() + 1;

  const novoTutor = {
    id: newTutorId,
    nome: nomeTutor,
    cpf,
    telefone,
    endereco,
    pets: [
      { id: newPetId, nome: nomePet, categoria, especieRaca, sexo, idade, peso, habitat }
    ]
  };

  state.tutores.unshift(novoTutor);
  saveState();
  renderAll();
  closeModal('modal-novo-tutor');
  document.getElementById('form-novo-tutor').reset();
  alert('Tutor e Pet cadastrados com sucesso!');
}

function salvarAgendamento(e) {
  e.preventDefault();
  const petId = parseInt(document.getElementById('agendamento-pet-select').value);
  const tipoServico = document.getElementById('agendamento-tipo-servico').value;
  const data = document.getElementById('agendamento-data').value;
  const hora = document.getElementById('agendamento-hora').value;
  const profissional = document.getElementById('agendamento-profissional-select').value;

  let petNome = '';
  let tutorNome = '';

  for (const t of state.tutores) {
    const p = t.pets.find(pet => pet.id === petId);
    if (p) {
      petNome = `${p.nome} (${p.especieRaca})`;
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
    tipoServico,
    profissional,
    status: 'Agendado'
  };

  state.agendamentos.push(novo);
  saveState();
  renderAll();
  closeModal('modal-novo-agendamento');
  document.getElementById('form-novo-agendamento').reset();
  alert('Atendimento agendado!');
}

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

function openModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) modal.classList.add('active');
}

function closeModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) modal.classList.remove('active');
}

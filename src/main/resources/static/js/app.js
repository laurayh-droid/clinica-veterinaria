/* ==========================================================================
   VetCare Fauna & Domésticos - Sistema Clínico e Consultoria Silvestre
   Lógica JavaScript: Autenticação, Prontuário Médico & Guia de Manejo
   ========================================================================== */

const CURRENT_DATE = new Date().toISOString().split('T')[0];

// Perfis e Usuários Simulados no Sistema
const USERS = {
  vet: { name: 'Dr. Carlos Eduardo', role: 'Médico Veterinário', crmv: 'CRMV/SP 12345', cargoKey: 'Veterinário', email: 'carlos.vet@vetcare.com' },
  adestrador: { name: 'Lucas Mendes', role: 'Adestrador / Esp. Silvestres', crmv: '', cargoKey: 'Adestrador / Esp. Silvestres', email: 'lucas.adestrador@vetcare.com' },
  recepcao: { name: 'Ana Silva', role: 'Atendente / Recepcionista', crmv: '', cargoKey: 'Atendente', email: 'ana.recepcao@vetcare.com' },
  admin: { name: 'Mariana Oliveira', role: 'Administradora Geral', crmv: '', cargoKey: 'Administrador', email: 'mariana.admin@vetcare.com' }
};

let currentUserRole = 'vet'; // Perfil ativo por padrão

// Helper: Detecção Inteligente de Espécie e Tema Visual
function getPetTheme(pet) {
  const name = (pet.nome || '').toLowerCase();
  const breed = (pet.especieRaca || '').toLowerCase();
  const cat = (pet.categoria || '').toLowerCase();

  // Aves
  if (breed.includes('papagaio') || breed.includes('arara') || breed.includes('calopsita') || breed.includes('ave') || breed.includes('periquito') || breed.includes('tucano') || breed.includes('cacatua')) {
    return { icon: '🦜', badgeClass: 'badge-ave', avatarClass: 'pet-avatar-ave', typeName: 'Ave Silvestre' };
  }
  // Répteis
  if (breed.includes('jabuti') || breed.includes('tartaruga') || breed.includes('cágado')) {
    return { icon: '🐢', badgeClass: 'badge-reptil', avatarClass: 'pet-avatar-reptil', typeName: 'Réptil (Quelônio)' };
  }
  if (breed.includes('iguana') || breed.includes('teiú') || breed.includes('lagarto') || breed.includes('serpente') || breed.includes('jiboia') || breed.includes('corn snake') || breed.includes('cobra') || breed.includes('dragão')) {
    return { icon: '🦎', badgeClass: 'badge-reptil', avatarClass: 'pet-avatar-reptil', typeName: 'Réptil (Escamado)' };
  }
  // Mamíferos exóticos / silvestres
  if (breed.includes('ferret') || breed.includes('furão') || breed.includes('chinchila') || breed.includes('porquinho') || breed.includes('hamster') || breed.includes('coelho') || breed.includes('mini pig')) {
    return { icon: '🦔', badgeClass: 'badge-silvestre', avatarClass: 'pet-avatar-silvestre', typeName: 'Mamífero Exótico' };
  }
  // Domésticos - Caninos
  if (breed.includes('cão') || breed.includes('cachorro') || breed.includes('golden') || breed.includes('poodle') || breed.includes('pastor') || breed.includes('buldogue') || breed.includes('shih') || breed.includes('labrador') || breed.includes('srd')) {
    return { icon: '🐶', badgeClass: 'badge-domestico', avatarClass: 'pet-avatar-domestico', typeName: 'Canino Doméstico' };
  }
  // Domésticos - Felinos
  if (breed.includes('gato') || breed.includes('felino') || breed.includes('siamês') || breed.includes('persa') || breed.includes('angorá')) {
    return { icon: '🐱', badgeClass: 'badge-domestico', avatarClass: 'pet-avatar-domestico', typeName: 'Felino Doméstico' };
  }

  // Fallback baseado na categoria selecionada
  if (cat.includes('silvestre') || cat.includes('exótico')) {
    return { icon: '🌿', badgeClass: 'badge-silvestre', avatarClass: 'pet-avatar-silvestre', typeName: 'Fauna Silvestre' };
  }
  return { icon: '🐾', badgeClass: 'badge-domestico', avatarClass: 'pet-avatar-domestico', typeName: 'Pet Doméstico' };
}

// Estado Inicial Pré-carregado com Prontuários e Guias de Manejo Separados
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
      endereco: 'Rua das Flores, 120 - Jardins, SP',
      pets: [
        { id: 101, nome: 'Kiko', categoria: 'Silvestre / Exótico', especieRaca: 'Papagaio Verdadeiro', sexo: 'Macho', idade: '4 anos', peso: '420g', habitat: 'Viveiro interno com luz natural e enriquecimento para forrageamento.' },
        { id: 104, nome: 'Nina', categoria: 'Doméstico', especieRaca: 'Gato Siamês', sexo: 'Fêmea', idade: '2 anos', peso: '3.8kg', habitat: 'Apartamento com enriquecimento vertical.' }
      ]
    },
    {
      id: 2,
      nome: 'Roberto Almeida',
      cpf: '987.654.321-11',
      telefone: '(11) 91234-5678',
      endereco: 'Av. Paulista, 900 - Bela Vista, SP',
      pets: [
        { id: 102, nome: 'Tufão', categoria: 'Silvestre / Exótico', especieRaca: 'Jabuti Piranga', sexo: 'Macho', idade: '6 anos', peso: '2.4kg', habitat: 'Recinto externo de 4m² com lâmpada UVB 5.0 e aquecimento focal.' }
      ]
    },
    {
      id: 3,
      nome: 'Fernanda Costa',
      cpf: '456.789.123-22',
      telefone: '(11) 99887-7665',
      endereco: 'Rua Augusta, 45 - Consolação, SP',
      pets: [
        { id: 103, nome: 'Thor', categoria: 'Doméstico', especieRaca: 'Cão - Golden Retriever', sexo: 'Macho', idade: '3 anos', peso: '28kg', habitat: 'Casa com quintal e passeios diários.' }
      ]
    },
    {
      id: 4,
      nome: 'Bruno Albuquerque',
      cpf: '321.654.987-33',
      telefone: '(11) 97123-4567',
      endereco: 'Rua Pamplona, 500 - SP',
      pets: [
        { id: 105, nome: 'Pipoca', categoria: 'Silvestre / Exótico', especieRaca: 'Ferret / Furão', sexo: 'Fêmea', idade: '1 ano', peso: '850g', habitat: 'Gaiola de múltiplos andares com redes e tubos de estimulação.' }
      ]
    }
  ],
  agendamentos: [
    { id: 1, data: CURRENT_DATE, hora: '09:00', petId: 101, petNome: 'Kiko (Papagaio)', tutorNome: 'Mariana Oliveira', tipoServico: 'Consultoria de Manejo & Adestramento Silvestre', profissional: 'Lucas Mendes (Adestrador / Esp. Silvestres)', status: 'Em Atendimento' },
    { id: 2, data: CURRENT_DATE, hora: '10:30', petId: 102, petNome: 'Tufão (Jabuti)', tutorNome: 'Roberto Almeida', tipoServico: 'Consulta Clínica Veterinária', profissional: 'Dr. Carlos Eduardo (Veterinário)', status: 'Aguardando' },
    { id: 3, data: CURRENT_DATE, hora: '14:00', petId: 103, petNome: 'Thor (Golden)', tutorNome: 'Fernanda Costa', tipoServico: 'Consulta Clínica Veterinária', profissional: 'Dr. Carlos Eduardo (Veterinário)', status: 'Agendado' },
    { id: 4, data: CURRENT_DATE, hora: '15:30', petId: 105, petNome: 'Pipoca (Ferret)', tutorNome: 'Bruno Albuquerque', tipoServico: 'Check-up Nutricional e Recinto', profissional: 'Lucas Mendes (Adestrador / Esp. Silvestres)', status: 'Agendado' }
  ],
  // 1. Histórico Clínico Médico (Médico Veterinário)
  prontuariosClinicos: {
    102: [
      {
        data: '10/07/2026',
        profissional: 'Dr. Carlos Eduardo (CRMV/SP 12345)',
        queixa: 'Check-up clínico de rotina e avaliação da integridade de carapaça.',
        sinaisVitais: 'FC: 45 bpm | T: 28°C (Temperatura corporal de quelônio)',
        mucosas: 'Mucosa oral rósea, hidratado, carapaça firme.',
        exameFisico: 'Ausculta sem ruídos respiratórios. Sem sinais de piramidismo ou osteodistrofia.',
        diagnostico: 'Animal hígido, discreta deficiência de radiação UVB.',
        conduta: 'Prescrito Carbonato de Cálcio com Vitamina D3 oral (1 pitada 2x/semana) e banho de sol direto.'
      }
    ],
    103: [
      {
        data: '15/08/2026',
        profissional: 'Dr. Carlos Eduardo (CRMV/SP 12345)',
        queixa: 'Avaliação clínica geral pré-vacinal e controle parasitário.',
        sinaisVitais: 'T: 38.6°C | FC: 110 bpm | FR: 24 mpm',
        mucosas: 'Normocoradas, TPC 1.5s',
        exameFisico: 'Linfonodos normais, ausculta cardiopulmonar límpida.',
        diagnostico: 'Animal saudável e apto para protocolo vacinal anual.',
        conduta: 'Aplicação de V10 e Antirrábica. Prescrito vermífugo palatável.'
      }
    ]
  },
  // 2. Histórico de Sessões de Manejo & Bem-Estar (Adestrador / Especialista em Silvestres)
  guiasManejo: {
    101: [
      {
        data: '20/08/2026',
        profissional: 'Lucas Mendes (Adestrador / Esp. Silvestres)',
        queixa: 'Vocalização excessiva ao amanhecer e arranque de penugem peitoral.',
        dimensoes: 'Viveiro recomendado de 2.0m x 1.5m x 2.0m',
        iluminacao: 'Lâmpada UVB 5.0 (10h diárias) e 12h de repouso no escuro',
        temperatura: 'Temperatura ambiente 24°C - 28°C sem correntes de vento',
        substrato: 'Galhos naturais de goiabeira e casca de pinus atóxica',
        enriquecimento: 'Forrageamento diário: quebra-cabeças com pinhas e rolos de papel contendo castanhas.',
        treinamento: 'Treinamento de alvo (target stick) e reforço positivo para pousar no braço voluntariamente.'
      }
    ]
  },
  vacinas: {
    103: [
      { nome: 'Vacina V10 Polivalente Canina', dataAplicacao: '15/08/2026', proximaDose: '15/08/2027' },
      { nome: 'Vacina Antirrábica', dataAplicacao: '15/08/2026', proximaDose: '15/08/2027' }
    ],
    104: [
      { nome: 'Vacina Quádrupla Felina (V4)', dataAplicacao: '01/06/2026', proximaDose: '01/06/2027' }
    ]
  }
};

function loadState() {
  const saved = localStorage.getItem('vetcare_fauna_state_v4');
  if (saved) {
    try {
      state = JSON.parse(saved);
      if (!state.prontuariosClinicos) state.prontuariosClinicos = {};
      if (!state.guiasManejo) state.guiasManejo = {};
    } catch (e) {
      console.warn('Erro ao carregar localStorage, mantendo padrão.', e);
    }
  }
}

function saveState() {
  localStorage.setItem('vetcare_fauna_state_v4', JSON.stringify(state));
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

// ==========================================================================
// AUTENTICAÇÃO, LOGIN & CADASTRO
// ==========================================================================

function switchAuthTab(tab) {
  const btnLogin = document.getElementById('btn-tab-login');
  const btnCadastro = document.getElementById('btn-tab-cadastro');
  const viewLogin = document.getElementById('auth-login-view');
  const viewCadastro = document.getElementById('auth-cadastro-view');

  if (tab === 'login') {
    btnLogin.classList.add('active');
    btnCadastro.classList.remove('active');
    viewLogin.classList.add('active');
    viewCadastro.classList.remove('active');
  } else {
    btnCadastro.classList.add('active');
    btnLogin.classList.remove('active');
    viewCadastro.classList.add('active');
    viewLogin.classList.remove('active');
  }
}

function togglePasswordVisibility(inputId, iconId) {
  const input = document.getElementById(inputId);
  const icon = document.getElementById(iconId);
  if (input.type === 'password') {
    input.type = 'text';
    icon.className = 'fa-solid fa-eye-slash';
  } else {
    input.type = 'password';
    icon.className = 'fa-solid fa-eye';
  }
}

function toggleAuthRegCRMV(cargo) {
  const crmvGroup = document.getElementById('auth-reg-crmv-group');
  if (cargo === 'Veterinário') {
    crmvGroup.style.display = 'block';
  } else {
    crmvGroup.style.display = 'none';
  }
}

function loginQuick(roleKey) {
  currentUserRole = roleKey;
  const user = USERS[roleKey];
  
  // Atualiza os seletores e badges
  const roleSelect = document.getElementById('user-role-select');
  if (roleSelect) roleSelect.value = roleKey;
  
  switchUserRole(roleKey);
  
  // Oculta tela de login e exibe o sistema
  document.getElementById('login-screen').classList.add('hidden');
  document.getElementById('app-container').style.display = 'flex';
  
  // Se for veterinário vai para dashboard ou prontuário; se for adestrador vai para guia de manejo
  if (roleKey === 'vet') {
    switchTab('dashboard');
  } else if (roleKey === 'adestrador') {
    switchTab('guia-manejo');
  } else {
    switchTab('dashboard');
  }
}

function fazerLogin(e) {
  e.preventDefault();
  const email = document.getElementById('login-email').value.trim().toLowerCase();
  
  // Verifica se corresponde a algum perfil existente
  let matchedRole = 'vet';
  for (const [key, u] of Object.entries(USERS)) {
    if (u.email.toLowerCase() === email) {
      matchedRole = key;
      break;
    }
  }

  loginQuick(matchedRole);
}

function fazerCadastroAuth(e) {
  e.preventDefault();
  const nome = document.getElementById('auth-reg-nome').value;
  const email = document.getElementById('auth-reg-email').value;
  const cargo = document.getElementById('auth-reg-cargo').value;
  const crmv = document.getElementById('auth-reg-crmv').value;

  const novoFuncionario = {
    id: Date.now(),
    nome,
    cpf: '000.000.000-00',
    telefone: '(11) 99999-0000',
    email,
    cargo,
    crmv: cargo === 'Veterinário' ? (crmv || 'CRMV/SP 00000') : ''
  };

  state.funcionarios.push(novoFuncionario);
  saveState();
  renderFuncionarios();

  let roleKey = 'vet';
  if (cargo.includes('Adestrador') || cargo.includes('Silvestres')) roleKey = 'adestrador';
  if (cargo.includes('Atendente')) roleKey = 'recepcao';

  USERS[roleKey] = {
    name: nome,
    role: cargo,
    crmv: novoFuncionario.crmv,
    cargoKey: cargo,
    email: email
  };

  alert(`Cadastro realizado com sucesso! Bem-vindo(a), ${nome}.`);
  loginQuick(roleKey);
}

function fazerLogout() {
  if (confirm('Deseja encerrar a sessão atual e retornar à tela de login?')) {
    document.getElementById('app-container').style.display = 'none';
    document.getElementById('login-screen').classList.remove('hidden');
  }
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
    'funcionarios': { title: 'Corpo Clínico & Equipe', subtitle: 'Cadastro de veterinários, especialistas em silvestres e recepcionistas' },
    'tutores-pets': { title: 'Tutores & Pacientes', subtitle: 'Cadastros e fichas de pets domésticos e fauna silvestre/exótica' },
    'agenda': { title: 'Agenda & Atendimentos', subtitle: 'Marcação de consultas clínicas e consultorias comportamentais' },
    'prontuario-clinico': { title: 'Prontuário Médico Veterinário', subtitle: 'Exame físico, anamnese clínica, vacinas e prescrição com CRMV' },
    'guia-manejo': { title: 'Guia de Manejo Silvestre & Bem-Estar', subtitle: 'Consultoria comportamental, recinto, enriquecimento e adestramento' }
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

  updateRoleBanners();
}

function updateRoleBanners() {
  const vetBanner = document.getElementById('vet-banner-text');
  const user = USERS[currentUserRole];

  if (vetBanner) {
    if (currentUserRole === 'vet') {
      vetBanner.innerHTML = `<strong>Módulo Clínico Veterinário (${user.name}):</strong> Preenchimento de exame físico, diagnóstico clínico e emissão de receita com CRMV (${user.crmv}).`;
    } else {
      vetBanner.innerHTML = `<strong>Aviso (${user.name}):</strong> Modo de visualização de prontuário. Apenas Médicos Veterinários com CRMV podem emitir receitas médicas oficiais.`;
    }
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
  updateRoleBanners();
}

function renderMetrics() {
  const hojeAgendamentos = state.agendamentos.filter(a => a.data === CURRENT_DATE);
  const aguardando = hojeAgendamentos.filter(a => a.status === 'Aguardando');
  
  let silvestresCount = 0;
  let domesticosCount = 0;
  
  state.tutores.forEach(t => {
    t.pets.forEach(p => {
      if (p.categoria.includes('Silvestre') || p.categoria.includes('Exótico')) {
        silvestresCount++;
      } else {
        domesticosCount++;
      }
    });
  });

  document.getElementById('metric-consultas-hoje').innerText = hojeAgendamentos.length;
  document.getElementById('metric-aguardando').innerText = aguardando.length;
  document.getElementById('badge-fila-count').innerText = aguardando.length;
  
  const metricSilvestres = document.getElementById('metric-silvestres');
  if (metricSilvestres) metricSilvestres.innerText = silvestresCount;

  const metricDomesticos = document.getElementById('metric-domesticos');
  if (metricDomesticos) metricDomesticos.innerText = domesticosCount;
}

// 1. Fila do Dia
function renderFilaDia() {
  const tbody = document.getElementById('fila-dia-tbody');
  tbody.innerHTML = '';

  const hojeAgendamentos = state.agendamentos.filter(a => a.data === CURRENT_DATE);

  if (hojeAgendamentos.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; color:var(--text-muted); padding: 2rem;">Nenhum atendimento agendado para o dia de hoje.</td></tr>`;
    return;
  }

  hojeAgendamentos.forEach(ag => {
    let petObj = null;
    for (const t of state.tutores) {
      const p = t.pets.find(pet => pet.id === ag.petId);
      if (p) { petObj = p; break; }
    }

    const theme = petObj ? getPetTheme(petObj) : { icon: '🐾', badgeClass: 'badge-domestico', avatarClass: 'pet-avatar-domestico', typeName: 'Pet' };
    const isManejo = ag.tipoServico.includes('Manejo') || ag.tipoServico.includes('Adestramento') || ag.tipoServico.includes('Recinto');

    let statusClass = 'status-agendado';
    if (ag.status === 'Aguardando') statusClass = 'status-aguardando';
    if (ag.status === 'Em Atendimento') statusClass = 'status-em-atendimento';
    if (ag.status === 'Finalizado') statusClass = 'status-finalizado';
    if (ag.status === 'Cancelado') statusClass = 'status-cancelado';

    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td><strong>${ag.hora}</strong></td>
      <td>
        <div class="entity-info">
          <div class="pet-avatar ${theme.avatarClass}">${theme.icon}</div>
          <div class="entity-details">
            <div class="title">${ag.petNome}</div>
            <div class="subtitle"><span class="status-badge ${theme.badgeClass}" style="padding: 1px 6px; font-size: 0.7rem;">${theme.typeName}</span></div>
          </div>
        </div>
      </td>
      <td><strong>${ag.tutorNome}</strong></td>
      <td><span class="status-badge ${isManejo ? 'badge-adestrador' : 'badge-vet'}">${ag.tipoServico}</span></td>
      <td>${ag.profissional}</td>
      <td><span class="status-badge ${statusClass}">${ag.status}</span></td>
      <td class="text-right">
        <select class="form-control" style="width:auto; display:inline-block; font-size:0.8rem; padding:0.35rem 0.5rem; font-weight:600;" onchange="alterarStatusAgendamento(${ag.id}, this.value)">
          <option value="Agendado" ${ag.status === 'Agendado' ? 'selected' : ''}>Agendado</option>
          <option value="Aguardando" ${ag.status === 'Aguardando' ? 'selected' : ''}>Aguardando</option>
          <option value="Em Atendimento" ${ag.status === 'Em Atendimento' ? 'selected' : ''}>Em Atendimento</option>
          <option value="Finalizado" ${ag.status === 'Finalizado' ? 'selected' : ''}>Finalizado</option>
          <option value="Cancelado" ${ag.status === 'Cancelado' ? 'selected' : ''}>Cancelado</option>
        </select>
        ${isManejo ? `
          <button class="btn btn-outline btn-sm" style="margin-left: 4px;" onclick="iniciarManejoDireto(${ag.petId})" title="Abrir Guia de Manejo Silvestre">
            <i class="fa-solid fa-leaf"></i> Manejo
          </button>
        ` : `
          <button class="btn btn-primary btn-sm" style="margin-left: 4px;" onclick="iniciarAtendimentoVetDireto(${ag.petId})" title="Abrir Prontuário Médico">
            <i class="fa-solid fa-stethoscope"></i> Vet
          </button>
        `}
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

function iniciarAtendimentoVetDireto(petId) {
  switchTab('prontuario-clinico');
  const petSelect = document.getElementById('vet-pet-select');
  if (petSelect) {
    petSelect.value = petId;
    onSelectPetVet(petId);
  }
}

function iniciarManejoDireto(petId) {
  switchTab('guia-manejo');
  const petSelect = document.getElementById('manejo-pet-select');
  if (petSelect) {
    petSelect.value = petId;
    onSelectPetManejo(petId);
  }
}

// 2. Cadastro & Edição de Funcionários
function abrirModalNovoFuncionario() {
  document.getElementById('form-novo-funcionario').reset();
  document.getElementById('func-id').value = '';
  document.getElementById('modal-funcionario-title').innerText = 'Cadastrar Membro da Equipe';
  document.getElementById('btn-salvar-funcionario').innerText = 'Salvar Membro';
  toggleCRMVField('Veterinário');
  openModal('modal-novo-funcionario');
}

function editarFuncionario(id) {
  const f = state.funcionarios.find(item => item.id === id);
  if (!f) return;

  document.getElementById('func-id').value = f.id;
  document.getElementById('func-nome').value = f.nome;
  document.getElementById('func-cpf').value = f.cpf;
  document.getElementById('func-telefone').value = f.telefone;
  document.getElementById('func-email').value = f.email;
  document.getElementById('func-cargo').value = f.cargo;
  document.getElementById('func-crmv').value = f.crmv || '';

  document.getElementById('modal-funcionario-title').innerText = 'Editar Membro da Equipe';
  document.getElementById('btn-salvar-funcionario').innerText = 'Salvar Alterações';

  toggleCRMVField(f.cargo);
  openModal('modal-novo-funcionario');
}

function renderFuncionarios(filter = '') {
  const tbody = document.getElementById('funcionarios-tbody');
  tbody.innerHTML = '';

  const query = filter.toLowerCase().trim();
  const items = state.funcionarios.filter(f => f.nome.toLowerCase().includes(query) || f.cargo.toLowerCase().includes(query) || f.cpf.includes(query));

  if (items.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" style="text-align:center; color:var(--text-muted); padding: 2rem;">Nenhum profissional encontrado.</td></tr>`;
    return;
  }

  items.forEach(f => {
    let badgeClass = 'badge-atendente';
    if (f.cargo.includes('Veterinário')) badgeClass = 'badge-vet';
    if (f.cargo.includes('Adestrador') || f.cargo.includes('Silvestres')) badgeClass = 'badge-adestrador';

    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td><strong>${f.nome}</strong><br><small style="color:var(--text-muted);">${f.email}</small></td>
      <td>${f.cpf}<br><small><i class="fa-brands fa-whatsapp" style="color:#25D366;"></i> ${f.telefone}</small></td>
      <td><span class="status-badge ${badgeClass}">${f.cargo}</span></td>
      <td>${f.crmv ? `<strong style="color:var(--primary-dark);"><i class="fa-solid fa-certificate"></i> ${f.crmv}</strong>` : '<span style="color:var(--accent-amber-hover);">Manejo Comportamental & Silvestres</span>'}</td>
      <td class="text-right">
        <button class="btn btn-outline btn-sm" style="margin-right: 4px;" onclick="editarFuncionario(${f.id})" title="Editar Profissional">
          <i class="fa-solid fa-pen-to-square"></i> Editar
        </button>
        <button class="btn btn-secondary btn-sm" onclick="removerFuncionario(${f.id})" title="Remover Profissional">
          <i class="fa-solid fa-trash"></i>
        </button>
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
  const idStr = document.getElementById('func-id').value;
  const nome = document.getElementById('func-nome').value.trim();
  const cpf = document.getElementById('func-cpf').value.trim();
  const telefone = document.getElementById('func-telefone').value.trim();
  const email = document.getElementById('func-email').value.trim();
  const cargo = document.getElementById('func-cargo').value;
  const crmv = document.getElementById('func-crmv').value.trim();

  if (idStr) {
    // Editar funcionário existente
    const id = parseInt(idStr) || Number(idStr);
    const index = state.funcionarios.findIndex(f => f.id === id);
    if (index !== -1) {
      const nomeAntigo = state.funcionarios[index].nome;
      state.funcionarios[index] = {
        ...state.funcionarios[index],
        nome,
        cpf,
        telefone,
        email,
        cargo,
        crmv: cargo === 'Veterinário' ? (crmv || 'CRMV/SP 00000') : ''
      };

      // Atualiza referências em agendamentos futuros
      state.agendamentos.forEach(ag => {
        if (ag.profissional && ag.profissional.includes(nomeAntigo)) {
          ag.profissional = `${nome} (${cargo === 'Veterinário' ? 'Vet' : 'Adestrador'})`;
        }
      });

      saveState();
      renderAll();
      closeModal('modal-novo-funcionario');
      alert('Dados do profissional atualizados com sucesso!');
      return;
    }
  }

  // Novo profissional
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
  alert('Profissional cadastrado com sucesso!');
}

function removerFuncionario(id) {
  if (confirm('Deseja realmente remover este profissional da equipe?')) {
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

// 3. Cadastros & Edição de Tutores & Pacientes (Pets)
function abrirModalNovoTutor() {
  document.getElementById('form-novo-tutor').reset();
  document.getElementById('tutor-id').value = '';
  document.getElementById('tutor-pet-id').value = '';
  document.getElementById('modal-tutor-title').innerText = 'Cadastrar Tutor e Paciente (Doméstico ou Silvestre)';
  document.getElementById('btn-salvar-tutor').innerText = 'Concluir Cadastro';

  openModal('modal-novo-tutor');
}

function editarTutor(id) {
  const tutor = state.tutores.find(t => t.id === id);
  if (!tutor) return;

  document.getElementById('tutor-id').value = tutor.id;
  document.getElementById('tutor-nome').value = tutor.nome;
  document.getElementById('tutor-cpf').value = tutor.cpf;
  document.getElementById('tutor-telefone').value = tutor.telefone;
  document.getElementById('tutor-endereco').value = tutor.endereco || '';

  if (tutor.pets && tutor.pets.length > 0) {
    const pet = tutor.pets[0];
    document.getElementById('tutor-pet-id').value = pet.id;
    document.getElementById('pet-nome').value = pet.nome;
    document.getElementById('pet-categoria').value = pet.categoria;
    document.getElementById('pet-especie-raca').value = pet.especieRaca;
    document.getElementById('pet-sexo').value = pet.sexo || 'Macho';
    document.getElementById('pet-idade').value = pet.idade || '';
    document.getElementById('pet-peso').value = pet.peso || '';
    document.getElementById('pet-habitat').value = pet.habitat || '';
  }

  document.getElementById('modal-tutor-title').innerText = 'Editar Dados do Tutor e Paciente';
  document.getElementById('btn-salvar-tutor').innerText = 'Salvar Alterações';

  openModal('modal-novo-tutor');
}

function editarPet(tutorId, petId) {
  const tutor = state.tutores.find(t => t.id === tutorId);
  if (!tutor) return;
  const pet = tutor.pets.find(p => p.id === petId);
  if (!pet) return;

  document.getElementById('edit-pet-tutor-id').value = tutorId;
  document.getElementById('edit-pet-id').value = petId;
  document.getElementById('edit-pet-nome').value = pet.nome;
  document.getElementById('edit-pet-categoria').value = pet.categoria;
  document.getElementById('edit-pet-especie-raca').value = pet.especieRaca;
  document.getElementById('edit-pet-sexo').value = pet.sexo || 'Macho';
  document.getElementById('edit-pet-idade').value = pet.idade || '';
  document.getElementById('edit-pet-peso').value = pet.peso || '';
  document.getElementById('edit-pet-habitat').value = pet.habitat || '';

  openModal('modal-editar-pet');
}

function salvarEdicaoPet(e) {
  e.preventDefault();
  const tutorId = parseInt(document.getElementById('edit-pet-tutor-id').value);
  const petId = parseInt(document.getElementById('edit-pet-id').value);

  const tutor = state.tutores.find(t => t.id === tutorId);
  if (!tutor) return;

  const petIndex = tutor.pets.findIndex(p => p.id === petId);
  if (petIndex === -1) return;

  const nome = document.getElementById('edit-pet-nome').value.trim();
  const categoria = document.getElementById('edit-pet-categoria').value;
  const especieRaca = document.getElementById('edit-pet-especie-raca').value.trim();
  const sexo = document.getElementById('edit-pet-sexo').value;
  const idade = document.getElementById('edit-pet-idade').value.trim();
  const peso = document.getElementById('edit-pet-peso').value.trim();
  const habitat = document.getElementById('edit-pet-habitat').value.trim();

  tutor.pets[petIndex] = {
    ...tutor.pets[petIndex],
    nome,
    categoria,
    especieRaca,
    sexo,
    idade,
    peso,
    habitat
  };

  // Atualiza referências em agendamentos
  state.agendamentos.forEach(ag => {
    if (ag.petId === petId) {
      ag.petNome = `${nome} (${especieRaca})`;
      ag.tutorNome = tutor.nome;
    }
  });

  saveState();
  renderAll();
  closeModal('modal-editar-pet');
  alert(`Dados do paciente ${nome} atualizados com sucesso!`);
}

function renderTutores(filter = '') {
  const tbody = document.getElementById('tutores-tbody');
  tbody.innerHTML = '';

  const query = filter.toLowerCase().trim();

  const filtered = state.tutores.filter(t => {
    const matchTutor = t.nome.toLowerCase().includes(query) || t.cpf.includes(query);
    const matchPet = t.pets.some(p => p.nome.toLowerCase().includes(query) || p.especieRaca.toLowerCase().includes(query) || p.categoria.toLowerCase().includes(query));
    return matchTutor || matchPet;
  });

  if (filtered.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" style="text-align:center; color:var(--text-muted); padding: 2rem;">Nenhum tutor ou paciente encontrado.</td></tr>`;
    return;
  }

  filtered.forEach(tutor => {
    const petsHtml = tutor.pets.map(p => {
      const theme = getPetTheme(p);
      const qtdVet = (state.prontuariosClinicos[p.id] || []).length;
      const qtdManejo = (state.guiasManejo[p.id] || []).length;
      return `
        <div style="margin-bottom:8px; display:flex; align-items:center; justify-content:space-between; flex-wrap:wrap; gap:6px; background-color:var(--surface); padding:6px 10px; border-radius:var(--radius-sm); border:1px solid var(--border-subtle);">
          <div>
            <span class="status-badge ${theme.badgeClass}" style="margin-right:5px;">
              ${theme.icon} <strong>${p.nome}</strong> (${p.especieRaca})
            </span>
            <small style="color:var(--text-muted);">[🩺 ${qtdVet} Clínicos | 🌿 ${qtdManejo} Manejos]</small>
          </div>
          <div>
            <button class="btn btn-outline btn-sm" style="padding:2px 8px; font-size:0.75rem;" onclick="editarPet(${tutor.id}, ${p.id})" title="Editar Paciente">
              <i class="fa-solid fa-pen"></i> Editar Pet
            </button>
          </div>
        </div>
      `;
    }).join('');

    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td><strong>${tutor.nome}</strong><br><small style="color:var(--text-muted);">${tutor.endereco || 'Endereço não informado'}</small></td>
      <td>${tutor.cpf}</td>
      <td><i class="fa-brands fa-whatsapp" style="color:#25D366;"></i> ${tutor.telefone}</td>
      <td>${petsHtml}</td>
      <td class="text-right">
        <button class="btn btn-outline btn-sm" style="margin-right:4px;" onclick="editarTutor(${tutor.id})" title="Editar Tutor e Paciente">
          <i class="fa-solid fa-user-pen"></i> Editar Tutor
        </button>
        <button class="btn btn-secondary btn-sm" onclick="agendarParaTutor(${tutor.id})" title="Agendar Consulta">
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

function agendarParaTutor(tutorId) {
  const tutor = state.tutores.find(t => t.id === tutorId);
  if (tutor && tutor.pets.length > 0) {
    openModal('modal-novo-agendamento');
    const select = document.getElementById('agendamento-pet-select');
    if (select) {
      select.value = tutor.pets[0].id;
    }
  }
}

// 4. Agenda Integrada
function renderAgendaTable(dateVal) {
  const tbody = document.getElementById('agenda-tbody');
  tbody.innerHTML = '';

  const agendamentos = state.agendamentos.filter(a => a.data === dateVal);

  if (agendamentos.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; color:var(--text-muted); padding: 2rem;">Nenhum atendimento agendado para esta data (${dateVal}).</td></tr>`;
    return;
  }

  agendamentos.forEach(ag => {
    let petObj = null;
    for (const t of state.tutores) {
      const p = t.pets.find(pet => pet.id === ag.petId);
      if (p) { petObj = p; break; }
    }
    const theme = petObj ? getPetTheme(petObj) : { icon: '🐾', badgeClass: 'badge-domestico' };
    const isManejo = ag.tipoServico.includes('Manejo') || ag.tipoServico.includes('Adestramento') || ag.tipoServico.includes('Recinto');

    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td><strong>${ag.data} às ${ag.hora}</strong></td>
      <td><span class="status-badge ${theme.badgeClass}">${theme.icon} ${ag.petNome}</span></td>
      <td>${ag.tutorNome}</td>
      <td><span class="status-badge ${isManejo ? 'badge-adestrador' : 'badge-vet'}">${ag.tipoServico}</span></td>
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

function populatePetSelects() {
  const selectVet = document.getElementById('vet-pet-select');
  const selectManejo = document.getElementById('manejo-pet-select');
  const selectAgendamento = document.getElementById('agendamento-pet-select');

  if (selectVet) {
    selectVet.innerHTML = '<option value="">-- Selecione um paciente --</option>';
    state.tutores.forEach(t => {
      t.pets.forEach(p => {
        const theme = getPetTheme(p);
        selectVet.innerHTML += `<option value="${p.id}">${theme.icon} ${p.nome} (${p.especieRaca}) - Tutor: ${t.nome}</option>`;
      });
    });
  }

  if (selectManejo) {
    selectManejo.innerHTML = '<option value="">-- Selecione um paciente --</option>';
    state.tutores.forEach(t => {
      t.pets.forEach(p => {
        const theme = getPetTheme(p);
        selectManejo.innerHTML += `<option value="${p.id}">${theme.icon} ${p.nome} [${p.categoria}] - Tutor: ${t.nome}</option>`;
      });
    });
  }

  if (selectAgendamento) {
    selectAgendamento.innerHTML = '<option value="">-- Selecione o Paciente --</option>';
    state.tutores.forEach(t => {
      t.pets.forEach(p => {
        const theme = getPetTheme(p);
        selectAgendamento.innerHTML += `<option value="${p.id}">${theme.icon} ${p.nome} [${p.categoria}] - Tutor: ${t.nome}</option>`;
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

// ==========================================================================
// 5. MÓDULO CLÍNICO VETERINÁRIO (Médico Veterinário)
// ==========================================================================

let activeVetPet = null;

function onSelectPetVet(petId) {
  petId = parseInt(petId);
  if (!petId) {
    document.getElementById('vet-selected-pet-card').style.display = 'none';
    document.getElementById('vet-timeline-container').innerHTML = '<p style="font-size:0.85rem; color:var(--text-muted); padding-top:0.5rem;">Nenhum paciente selecionado.</p>';
    document.getElementById('vet-vaccines-list').innerText = 'Selecione um paciente para ver o histórico.';
    activeVetPet = null;
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
    activeVetPet = { pet: foundPet, tutor: foundTutor };
    const theme = getPetTheme(foundPet);
    
    document.getElementById('vet-selected-pet-card').style.display = 'block';
    
    const avatarEl = document.getElementById('vet-pet-avatar');
    avatarEl.className = `pet-avatar ${theme.avatarClass}`;
    avatarEl.innerText = theme.icon;

    document.getElementById('vet-pet-name').innerText = foundPet.nome;
    document.getElementById('vet-pet-species').innerText = `${foundPet.categoria} • ${foundPet.especieRaca} (${foundPet.sexo})`;
    document.getElementById('vet-pet-tutor').innerText = foundTutor.nome;
    document.getElementById('vet-pet-age-weight').innerText = `${foundPet.idade || 'N/I'} | ${foundPet.peso || '--'}`;

    renderProntuarioVetTimeline(petId);
    renderPetVaccines(petId);
  }
}

function renderProntuarioVetTimeline(petId) {
  const container = document.getElementById('vet-timeline-container');
  container.innerHTML = '';

  const historico = state.prontuariosClinicos[petId] || [];

  if (historico.length === 0) {
    container.innerHTML = '<p style="font-size:0.85rem; color:var(--text-muted); padding-top:0.5rem;">Primeira consulta clínica deste paciente na clínica.</p>';
    return;
  }

  historico.forEach(item => {
    const div = document.createElement('div');
    div.className = 'timeline-item';
    div.innerHTML = `
      <div class="timeline-dot"></div>
      <div class="timeline-card">
        <div class="timeline-date" style="font-weight:700; color:var(--primary-dark); font-size:0.85rem;">
          <i class="fa-solid fa-stethoscope"></i> ${item.data} — ${item.profissional}
        </div>
        <div style="margin-top:6px; font-size:0.85rem; line-height:1.5;">
          <strong>Queixa Clínica:</strong> ${item.queixa}<br>
          ${item.sinaisVitais ? `<strong>Sinais Vitais / Exame:</strong> ${item.sinaisVitais} | ${item.mucosas || ''}<br>` : ''}
          <strong>Diagnóstico Clínico:</strong> ${item.diagnostico}<br>
          <strong style="color:var(--primary-hover);">Conduta & Prescrição:</strong> ${item.conduta}
        </div>
      </div>
    `;
    container.appendChild(div);
  });
}

function renderPetVaccines(petId) {
  const container = document.getElementById('vet-vaccines-list');
  const vacinas = state.vacinas[petId] || [];

  if (vacinas.length === 0) {
    container.innerHTML = '<em>Nenhuma vacina ou imunização registrada.</em>';
    return;
  }

  container.innerHTML = vacinas.map(v => `
    <div style="padding:0.5rem 0; border-bottom:1px solid var(--border-subtle); display:flex; justify-content:space-between; align-items:center;">
      <div><strong>${v.nome}</strong><br><small style="color:var(--text-muted);">Aplicado: ${v.dataAplicacao}</small></div>
      <div class="text-right" style="color:var(--primary-hover);"><strong>Próxima Dose:</strong><br><small style="font-weight:600;">${v.proximaDose}</small></div>
    </div>
  `).join('');
}

function salvarProntuarioVet(e) {
  e.preventDefault();

  if (!activeVetPet) {
    alert('Por favor, selecione um paciente para consulta clínica!');
    return;
  }

  const queixa = document.getElementById('vet-queixa').value;
  const sinaisVitais = document.getElementById('vet-sinais-vitais').value;
  const mucosas = document.getElementById('vet-mucosas').value;
  const exameFisico = document.getElementById('vet-exame-fisico').value;
  const diagnostico = document.getElementById('vet-diagnostico').value;
  const conduta = document.getElementById('vet-conduta').value;

  const petId = activeVetPet.pet.id;
  if (!state.prontuariosClinicos[petId]) state.prontuariosClinicos[petId] = [];

  const currentUser = USERS[currentUserRole];

  const novoRegistro = {
    data: new Date().toLocaleDateString('pt-BR'),
    profissional: `${currentUser.name} (${currentUser.crmv || 'Médico Veterinário'})`,
    queixa,
    sinaisVitais,
    mucosas,
    exameFisico,
    diagnostico,
    conduta
  };

  state.prontuariosClinicos[petId].unshift(novoRegistro);

  const ag = state.agendamentos.find(a => a.petId === petId && a.data === CURRENT_DATE);
  if (ag) ag.status = 'Finalizado';

  saveState();
  renderAll();

  document.getElementById('form-prontuario-vet').reset();
  renderProntuarioVetTimeline(petId);
  alert('Prontuário Clínico Veterinário salvo com sucesso e consulta finalizada!');
}

function abrirModalReceitaVet() {
  if (!activeVetPet) {
    alert('Selecione um paciente em consulta clínica primeiro!');
    return;
  }
  openModal('modal-receita');
}

function gerarVisualizacaoReceita(e) {
  e.preventDefault();
  
  if (!activeVetPet) {
    alert('Selecione um paciente antes de emitir a receita!');
    return;
  }

  const texto = document.getElementById('receita-texto').value;
  const user = USERS[currentUserRole];

  document.getElementById('recipe-preview-container').style.display = 'block';
  document.getElementById('recipe-vet-info').innerText = `${user.name} — ${user.crmv || 'CRMV/SP 12345'}`;
  document.getElementById('recipe-patient-name').innerText = `${activeVetPet.pet.nome} (${activeVetPet.pet.especieRaca})`;
  document.getElementById('recipe-doc-type').innerText = 'Receituário Clínico Veterinário';
  document.getElementById('recipe-tutor-name').innerText = activeVetPet.tutor.nome;
  document.getElementById('recipe-date-display').innerText = new Date().toLocaleDateString('pt-BR');
  document.getElementById('recipe-meds-list').innerText = texto;
  
  document.getElementById('recipe-vet-signature').innerText = user.name;
  document.getElementById('recipe-crmv-signature').innerText = `Médico Veterinário — ${user.crmv || 'CRMV/SP 12345'}`;
}

function imprimirReceita() {
  const content = document.getElementById('recipe-preview-container');
  if (!content || content.style.display === 'none') {
    alert('Por favor, gere a visualização da receita antes de imprimir!');
    return;
  }

  const printArea = document.getElementById('print-recipe-area');
  printArea.innerHTML = content.outerHTML;
  window.print();
}

// ==========================================================================
// 6. MÓDULO DE GUIA DE MANEJO SILVESTRE (Adestrador / Esp. Silvestres)
// ==========================================================================

let activeManejoPet = null;

function onSelectPetManejo(petId) {
  petId = parseInt(petId);
  if (!petId) {
    document.getElementById('manejo-selected-pet-card').style.display = 'none';
    document.getElementById('manejo-timeline-container').innerHTML = '<p style="font-size:0.85rem; color:var(--text-muted); padding-top:0.5rem;">Nenhum paciente selecionado.</p>';
    activeManejoPet = null;
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
    activeManejoPet = { pet: foundPet, tutor: foundTutor };
    const theme = getPetTheme(foundPet);
    
    document.getElementById('manejo-selected-pet-card').style.display = 'block';
    
    const avatarEl = document.getElementById('manejo-pet-avatar');
    avatarEl.className = `pet-avatar ${theme.avatarClass}`;
    avatarEl.innerText = theme.icon;

    document.getElementById('manejo-pet-name').innerText = foundPet.nome;
    document.getElementById('manejo-pet-species').innerText = `${foundPet.categoria} • ${foundPet.especieRaca} (${foundPet.sexo})`;
    document.getElementById('manejo-pet-tutor').innerText = foundTutor.nome;
    document.getElementById('manejo-pet-age-weight').innerText = `${foundPet.idade || 'N/I'} | ${foundPet.peso || '--'}`;
    document.getElementById('manejo-pet-habitat').innerText = foundPet.habitat || 'Sem observações registradas.';

    renderGuiaManejoTimeline(petId);
  }
}

function renderGuiaManejoTimeline(petId) {
  const container = document.getElementById('manejo-timeline-container');
  container.innerHTML = '';

  const historico = state.guiasManejo[petId] || [];

  if (historico.length === 0) {
    container.innerHTML = '<p style="font-size:0.85rem; color:var(--text-muted); padding-top:0.5rem;">Nenhuma consultoria de manejo registrada anteriormente para este animal.</p>';
    return;
  }

  historico.forEach(item => {
    const div = document.createElement('div');
    div.className = 'timeline-item';
    div.innerHTML = `
      <div class="timeline-dot manejo"></div>
      <div class="timeline-card">
        <div class="timeline-date" style="font-weight:700; color:var(--accent-amber-hover); font-size:0.85rem;">
          <i class="fa-solid fa-leaf"></i> ${item.data} — ${item.profissional}
        </div>
        <div style="margin-top:6px; font-size:0.85rem; line-height:1.5;">
          <strong>Queixa / Estresse:</strong> ${item.queixa}<br>
          <strong>Recinto & Iluminação:</strong> ${item.dimensoes || 'Padrão'} | ${item.iluminacao || ''} | ${item.temperatura || ''}<br>
          <strong>Enriquecimento:</strong> ${item.enriquecimento}<br>
          <strong style="color:var(--accent-amber-hover);">Plano de Treinamento:</strong> ${item.treinamento}
        </div>
      </div>
    `;
    container.appendChild(div);
  });
}

function salvarGuiaManejo(e) {
  e.preventDefault();

  if (!activeManejoPet) {
    alert('Por favor, selecione um paciente para consultoria de manejo!');
    return;
  }

  const queixa = document.getElementById('manejo-queixa').value;
  const dimensoes = document.getElementById('manejo-dimensoes').value;
  const iluminacao = document.getElementById('manejo-iluminacao').value;
  const temperatura = document.getElementById('manejo-temperatura').value;
  const substrato = document.getElementById('manejo-substrato').value;
  const enriquecimento = document.getElementById('manejo-alimentar').value + (document.getElementById('manejo-cognitivo').value ? '\n' + document.getElementById('manejo-cognitivo').value : '');
  const treinamento = document.getElementById('manejo-treinamento').value;

  const petId = activeManejoPet.pet.id;
  if (!state.guiasManejo[petId]) state.guiasManejo[petId] = [];

  const currentUser = USERS[currentUserRole];

  const novoRegistro = {
    data: new Date().toLocaleDateString('pt-BR'),
    profissional: `${currentUser.name} (Especialista em Silvestres / Adestrador)`,
    queixa,
    dimensoes,
    iluminacao,
    temperatura,
    substrato,
    enriquecimento,
    treinamento
  };

  state.guiasManejo[petId].unshift(novoRegistro);

  const ag = state.agendamentos.find(a => a.petId === petId && a.data === CURRENT_DATE);
  if (ag) ag.status = 'Finalizado';

  saveState();
  renderAll();

  document.getElementById('form-guia-manejo').reset();
  renderGuiaManejoTimeline(petId);
  alert('Guia de Manejo Silvestre & Consultoria salvo com sucesso!');
}

function abrirModalGuiaManejo() {
  if (!activeManejoPet) {
    alert('Selecione um paciente em consultoria de manejo primeiro!');
    return;
  }
  openModal('modal-guia-manejo');
}

function gerarVisualizacaoGuia(e) {
  e.preventDefault();

  if (!activeManejoPet) {
    alert('Selecione um paciente antes de emitir o guia!');
    return;
  }

  const user = USERS[currentUserRole];
  const dimensoes = document.getElementById('manejo-dimensoes').value || 'Adequado para a espécie';
  const iluminacao = document.getElementById('manejo-iluminacao').value || 'UVB 5.0 (10-12h de luz)';
  const temperatura = document.getElementById('manejo-temperatura').value || 'Gradiente térmico controlado';
  const enriquecimento = document.getElementById('manejo-alimentar').value || 'Rotinas de forrageamento recomendadas';
  const treinamento = document.getElementById('manejo-treinamento').value || 'Treinamento de alvos com reforço positivo';

  document.getElementById('guia-preview-container').style.display = 'block';
  document.getElementById('guia-consultor-info').innerText = `${user.name} — Consultor em Manejo Silvestre`;
  document.getElementById('guia-patient-name').innerText = `${activeManejoPet.pet.nome} (${activeManejoPet.pet.especieRaca})`;
  document.getElementById('guia-tutor-name').innerText = activeManejoPet.tutor.nome;
  document.getElementById('guia-date-display').innerText = new Date().toLocaleDateString('pt-BR');

  document.getElementById('guia-recinto-dim').innerText = dimensoes;
  document.getElementById('guia-recinto-luz').innerText = iluminacao;
  document.getElementById('guia-recinto-temp').innerText = temperatura;

  document.getElementById('guia-enrichment-text').innerText = enriquecimento;
  document.getElementById('guia-training-text').innerText = treinamento;

  document.getElementById('guia-consultor-signature').innerText = user.name;
}

function imprimirGuiaManejo() {
  const content = document.getElementById('guia-preview-container');
  if (!content || content.style.display === 'none') {
    alert('Por favor, gere a visualização do guia antes de imprimir!');
    return;
  }

  const printArea = document.getElementById('print-guia-area');
  printArea.innerHTML = content.outerHTML;
  window.print();
}

// 7. Funções de Apoio (Tutores, Agendamentos, Vacinas, Modais)
function salvarTutorEPet(e) {
  e.preventDefault();
  const tutorIdStr = document.getElementById('tutor-id').value;
  const tutorPetIdStr = document.getElementById('tutor-pet-id').value;

  const nomeTutor = document.getElementById('tutor-nome').value.trim();
  const cpf = document.getElementById('tutor-cpf').value.trim();
  const telefone = document.getElementById('tutor-telefone').value.trim();
  const endereco = document.getElementById('tutor-endereco').value.trim();

  const nomePet = document.getElementById('pet-nome').value.trim();
  const categoria = document.getElementById('pet-categoria').value;
  const especieRaca = document.getElementById('pet-especie-raca').value.trim();
  const sexo = document.getElementById('pet-sexo').value;
  const idade = document.getElementById('pet-idade').value.trim();
  const peso = document.getElementById('pet-peso').value.trim();
  const habitat = document.getElementById('pet-habitat').value.trim();

  if (tutorIdStr) {
    // Edição de tutor existente
    const tutorId = parseInt(tutorIdStr) || Number(tutorIdStr);
    const tIndex = state.tutores.findIndex(t => t.id === tutorId);
    if (tIndex !== -1) {
      const tutorAntigo = state.tutores[tIndex];
      tutorAntigo.nome = nomeTutor;
      tutorAntigo.cpf = cpf;
      tutorAntigo.telefone = telefone;
      tutorAntigo.endereco = endereco;

      if (tutorPetIdStr && tutorAntigo.pets) {
        const petId = parseInt(tutorPetIdStr) || Number(tutorPetIdStr);
        const pIndex = tutorAntigo.pets.findIndex(p => p.id === petId);
        if (pIndex !== -1) {
          tutorAntigo.pets[pIndex] = {
            ...tutorAntigo.pets[pIndex],
            nome: nomePet,
            categoria,
            especieRaca,
            sexo,
            idade,
            peso,
            habitat
          };
        }
      }

      // Atualiza referências em agendamentos
      state.agendamentos.forEach(ag => {
        const p = tutorAntigo.pets.find(item => item.id === ag.petId);
        if (p) {
          ag.petNome = `${p.nome} (${p.especieRaca})`;
          ag.tutorNome = tutorAntigo.nome;
        }
      });

      saveState();
      renderAll();
      closeModal('modal-novo-tutor');
      alert('Dados do tutor e paciente atualizados com sucesso!');
      return;
    }
  }

  // Novo Cadastro
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
  alert('Tutor e Paciente cadastrados com sucesso!');
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
  alert('Atendimento agendado com sucesso!');
}

function salvarVacina(e) {
  e.preventDefault();
  if (!activeVetPet) {
    alert('Selecione um paciente no Prontuário Clínico primeiro!');
    return;
  }

  const petId = activeVetPet.pet.id;
  const nome = document.getElementById('vacina-nome').value;
  const dataAplicacao = document.getElementById('vacina-data-aplicacao').value;
  const proximaDose = document.getElementById('vacina-data-proxima').value;

  if (!state.vacinas[petId]) state.vacinas[petId] = [];

  state.vacinas[petId].unshift({ nome, dataAplicacao, proximaDose });
  saveState();
  renderPetVaccines(petId);
  closeModal('modal-nova-vacina');
  document.getElementById('form-nova-vacina').reset();
  alert('Imunização / Tratamento registrado com sucesso!');
}

function openModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) modal.classList.add('active');
}

function closeModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) modal.classList.remove('active');
}

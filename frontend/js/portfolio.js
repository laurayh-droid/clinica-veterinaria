/* ==========================================================================
   ZooMed Fauna & Domésticos - JavaScript do Portfólio Showcase
   Interatividade: Tour de Telas, Simulador de Perfis & Calculadora de Impacto
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  initNavbarScroll();
  initTourTabs();
  initRoleSimulator();
  initRoiCalculator();
  initMobileMenu();
});

/* --------------------------------------------------------------------------
   1. EFEITO DE SCROLL NA NAVBAR
   -------------------------------------------------------------------------- */
function initNavbarScroll() {
  const header = document.querySelector('.showcase-header');
  if (!header) return;

  window.addEventListener('scroll', () => {
    if (window.scrollY > 40) {
      header.classList.add('scrolled');
    } else {
      header.classList.remove('scrolled');
    }
  });
}

/* --------------------------------------------------------------------------
   2. TOUR INTERATIVO DAS TELAS (TABS)
   -------------------------------------------------------------------------- */
function initTourTabs() {
  const tabButtons = document.querySelectorAll('.tour-tab-btn');
  const panels = document.querySelectorAll('.tour-content-panel');

  tabButtons.forEach(btn => {
    btn.addEventListener('click', () => {
      const targetId = btn.getAttribute('data-target');

      // Atualiza botões
      tabButtons.forEach(b => b.classList.remove('active'));
      btn.classList.add('active');

      // Atualiza painéis
      panels.forEach(p => {
        p.classList.remove('active');
        if (p.id === targetId) {
          p.classList.add('active');
        }
      });
    });
  });
}

/* --------------------------------------------------------------------------
   3. SIMULADOR POR PERFIL DE USUÁRIO
   -------------------------------------------------------------------------- */
const ROLES_DATA = {
  vet: {
    name: 'Dr. Carlos Eduardo',
    title: 'Médico Veterinário Clínico',
    crmv: 'CRMV/SP 12345',
    badge: 'Módulo Clínico & Prescrição',
    colorClass: 'blue',
    icon: 'fa-user-doctor',
    email: 'carlos.vet@zoomed.com',
    pwd: 'vet123',
    benefits: [
      {
        title: 'Prescrição com CRMV Automático',
        desc: 'Emissão ágil de receitas oficiais formatadas, com CRMV impresso por exigência do conselho, dosagens e orientações posológicas claras.'
      },
      {
        title: 'Prontuário Médico Bio-Harmônico',
        desc: 'Registro completo de queixa, anamnese e parâmetros vitais (temperatura, FC, FR, TPC e peso) com histórico unificado do paciente.'
      },
      {
        title: 'Linha do Tempo Vacinal',
        desc: 'Controle preciso de vacinas, vermífugos e tratamentos profiláticos com agendamento da próxima dose.'
      },
      {
        title: 'Segurança & Segregação Ética',
        desc: 'Garantia de que apenas o médico veterinário habilitado assina o receituário e responde pelos procedimentos clínicos.'
      }
    ]
  },
  adestrador: {
    name: 'Lucas Mendes',
    title: 'Biólogo & Esp. Silvestres e Exóticos',
    crmv: 'CRBio 45982',
    badge: 'Manejo Silvestre & Bem-Estar',
    colorClass: 'amber',
    icon: 'fa-feather',
    email: 'lucas.adestrador@zoomed.com',
    pwd: 'adestrador123',
    benefits: [
      {
        title: 'Guia Oficial de Manejo Silvestre',
        desc: 'Emissão de documento técnico padronizado com parâmetros ambientais recomendados para aves, répteis e pequenos mamíferos.'
      },
      {
        title: 'Auditoria de Recintos & Iluminação UVB',
        desc: 'Registro de dimensões, lâmpadas com espectro UVB específico, gradiente térmico (zona quente/fria), fotoperíodo e umidade ideal.'
      },
      {
        title: 'Enriquecimento Ambiental 5D',
        desc: 'Prescrição de rotinas de enriquecimento sensorial, cognitivo, alimentar, físico e social para extinguir estereotipias.'
      },
      {
        title: 'Conformidade e Suporte ao Tutor',
        desc: 'O tutor sai com diretrizes impressas passo a passo, reduzindo em 85% as doenças decorrentes de erros de manejo doméstico.'
      }
    ]
  },
  recepcao: {
    name: 'Ana Silva',
    title: 'Recepcionista & Triagem de Atendimentos',
    crmv: 'Atendimento ao Cliente',
    badge: 'Recepção & Fila Inteligente',
    colorClass: 'green',
    icon: 'fa-headset',
    email: 'ana.recepcao@zoomed.com',
    pwd: 'atendente123',
    benefits: [
      {
        title: 'Fila do Dia com Status em Tempo Real',
        desc: 'Visão instantânea de quem está aguardando na recepção, em atendimento no consultório ou finalizado, sem pranchetas.'
      },
      {
        title: 'Cadastro Unificado Tutor + Animal',
        desc: 'Cadastro simplificado com identificação imediata da espécie: cão, gato, papagaio, jabuti, iguana, coelho ou furão.'
      },
      {
        title: 'Agendamento Direcionado',
        desc: 'Direcionamento automático do agendamento para a especialidade correta (consulta médica vs consultoria de recinto e comportamento).'
      },
      {
        title: 'Atendimento Rápido em Menos de 2 Minutos',
        desc: 'Recepção fluida que impressiona o tutor logo na chegada, com busca rápida por CPF ou nome do animal.'
      }
    ]
  },
  admin: {
    name: 'Mariana Oliveira',
    title: 'Gestora & Administradora Geral',
    crmv: 'Gestão de Saúde Animal',
    badge: 'Painel Executivo & Métricas',
    colorClass: 'purple',
    icon: 'fa-shield-halved',
    email: 'mariana.admin@zoomed.com',
    pwd: 'admin123',
    benefits: [
      {
        title: 'Painel de Indicadores & Métricas em Tempo Real',
        desc: 'Acompanhamento do volume diário de atendimentos, proporção de silvestres vs domésticos e tempo de giro dos consultórios.'
      },
      {
        title: 'Gestão da Equipe Multidisciplinar',
        desc: 'Controle de acessos, cadastro de veterinários, biólogos e recepcionistas com permissões rígidas baseadas em função.'
      },
      {
        title: 'Confiabilidade e Auditoria',
        desc: 'Histórico auditável de prontuários e receitas, resguardando a clínica jurídica e eticamente em todos os atendimentos.'
      },
      {
        title: 'Diferencial Competitivo Único',
        desc: 'A clínica se posiciona como referência regional tanto para tutores tradicionais quanto para o mercado aquecido de animais não convencionais.'
      }
    ]
  }
};

function initRoleSimulator() {
  const roleCards = document.querySelectorAll('.role-select-card');
  const userTitle = document.getElementById('sim-user-title');
  const userRole = document.getElementById('sim-user-role');
  const userBadge = document.getElementById('sim-user-badge');
  const userAvatar = document.getElementById('sim-user-avatar');
  const benefitsContainer = document.getElementById('sim-benefits-grid');

  roleCards.forEach(card => {
    card.addEventListener('click', () => {
      const roleKey = card.getAttribute('data-role');
      const data = ROLES_DATA[roleKey];
      if (!data) return;

      // Atualiza seleção visual dos cards laterais
      roleCards.forEach(c => c.classList.remove('active'));
      card.classList.add('active');

      // Atualiza cabeçalho do simulador
      if (userTitle) userTitle.textContent = data.name;
      if (userRole) userRole.textContent = data.title + ' • ' + data.crmv;
      if (userBadge) userBadge.textContent = data.badge;
      if (userAvatar) {
        userAvatar.className = `role-card-avatar ${data.colorClass}`;
        userAvatar.innerHTML = `<i class="fa-solid ${data.icon}"></i>`;
      }

      // Atualiza grid de benefícios
      if (benefitsContainer) {
        benefitsContainer.innerHTML = data.benefits.map(b => `
          <div class="benefit-box">
            <h5><i class="fa-solid fa-circle-check" style="color:var(--p-emerald);"></i> ${b.title}</h5>
            <p>${b.desc}</p>
          </div>
        `).join('');
      }
    });
  });
}

/* --------------------------------------------------------------------------
   4. CALCULADORA DE IMPACTO / ROI CLÍNICO
   -------------------------------------------------------------------------- */
function initRoiCalculator() {
  const sliderConsultas = document.getElementById('slider-consultas');
  const sliderSilvestres = document.getElementById('slider-silvestres');
  const valConsultas = document.getElementById('val-consultas');
  const valSilvestres = document.getElementById('val-silvestres');

  const resHoras = document.getElementById('res-horas-semana');
  const resEspera = document.getElementById('res-reducao-espera');
  const resFidelizacao = document.getElementById('res-fidelizacao');

  function calculate() {
    const consultas = parseInt(sliderConsultas ? sliderConsultas.value : 18, 10);
    const silvestres = parseInt(sliderSilvestres ? sliderSilvestres.value : 35, 10);

    if (valConsultas) valConsultas.textContent = `${consultas} atendimentos/dia`;
    if (valSilvestres) valSilvestres.textContent = `${silvestres}% do total`;

    // Cálculo estimado de economia
    // 15 minutos economizados por consulta em registro, prontuário, receita e guia
    const minutosDia = consultas * 14;
    const horasSemana = Math.round((minutosDia * 6) / 60);

    // Redução do tempo de espera na recepção
    const reducaoEspera = Math.min(58, Math.round(28 + (consultas * 0.7)));

    // Índice de retenção e valor percebido pelo tutor
    const aumentoRetencao = Math.min(95, Math.round(45 + (silvestres * 0.8)));

    if (resHoras) resHoras.textContent = `~${horasSemana}h`;
    if (resEspera) resEspera.textContent = `-${reducaoEspera}%`;
    if (resFidelizacao) resFidelizacao.textContent = `+${aumentoRetencao}%`;
  }

  if (sliderConsultas) sliderConsultas.addEventListener('input', calculate);
  if (sliderSilvestres) sliderSilvestres.addEventListener('input', calculate);

  calculate();
}

/* --------------------------------------------------------------------------
   5. MENU MOBILE RESPONSIVO DO SHOWCASE
   -------------------------------------------------------------------------- */
function togglePortfolioMenu(force) {
  const nav = document.getElementById('portfolio-nav-links');
  const toggleBtn = document.getElementById('portfolio-mobile-toggle');
  if (!nav) return;

  const shouldOpen = force !== undefined ? force : !nav.classList.contains('mobile-open');
  if (shouldOpen) {
    nav.classList.add('mobile-open');
    if (toggleBtn) toggleBtn.innerHTML = '<i class="fa-solid fa-xmark"></i>';
  } else {
    nav.classList.remove('mobile-open');
    if (toggleBtn) toggleBtn.innerHTML = '<i class="fa-solid fa-bars"></i>';
  }
}

function initMobileMenu() {
  const links = document.querySelectorAll('#portfolio-nav-links a');
  links.forEach(link => {
    link.addEventListener('click', () => {
      togglePortfolioMenu(false);
    });
  });

  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
      togglePortfolioMenu(false);
    }
  });

  document.addEventListener('click', (e) => {
    const nav = document.getElementById('portfolio-nav-links');
    const toggleBtn = document.getElementById('portfolio-mobile-toggle');
    if (!nav || !toggleBtn) return;
    if (nav.classList.contains('mobile-open') && !nav.contains(e.target) && !toggleBtn.contains(e.target)) {
      togglePortfolioMenu(false);
    }
  });
}

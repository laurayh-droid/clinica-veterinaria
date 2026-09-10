# Levantamento de Requisitos: Sistema para Clínica e Consultoria Veterinária (Pets Domésticos e Silvestres)

---

## 1. Visão Geral
Sistema simplificado e direto para gerenciar as rotinas da clínica veterinária com foco no atendimento clínico e suporte especializado a tutores de **pets domésticos e fauna silvestre/exótica**: cadastro de funcionários (atendentes, veterinários e adestradores/especialistas em silvestres), cadastro de tutores e pets, agendamento de consultas/consultorias, prontuário clínico médico, guia de manejo silvestre e fila de atendimento do dia.

*Obs: Este sistema não possui módulos de venda de produtos ou controle de estoque.*

---

## 2. Perfis de Usuários (Atores)

| Perfil | Responsabilidades |
| :--- | :--- |
| **Administrador** | Gestão geral e cadastro de funcionários (atendentes, veterinários e adestradores). |
| **Médico(a) Veterinário(a)** | Atendimentos clínicos, exame físico, diagnóstico médico, preenchimento do prontuário veterinário, vacinas e emissão de receitas com CRMV. |
| **Adestrador(a) / Esp. Silvestres** | Consultoria comportamental, análise e adequação de recinto (iluminação, temperatura, enriquecimento), plano de adestramento e emissão do **Guia Oficial de Manejo Silvestre**. |
| **Recepcionista / Atendente** | Cadastro de tutores e pets, agendamento de atendimentos (médicos e de manejo) e controle da fila do dia. |

---

## 3. Requisitos Funcionais (RF)

### 3.1. Cadastros Básicos
* **RF01 - Cadastro de Funcionários:** Registro de funcionários da clínica (Nome, CPF, E-mail, Telefone, Perfil: *Atendente, Veterinário (com CRMV)* ou *Adestrador/Especialista em Silvestres*).
* **RF02 - Cadastro de Tutores:** Registro de nome, CPF, telefone (WhatsApp) e endereço.
* **RF03 - Cadastro de Pets (Domésticos e Silvestres):** Registro do animal vinculado ao tutor (nome, espécie, raça/categoria, sexo, idade, peso e observações de manejo/hábito silvestre).

### 3.2. Agenda e Recepção
* **RF04 - Agendamento de Atendimentos:** Marcação de consultas médicas ou sessões de consultoria de manejo com data, horário, pet, tutor e profissional responsável (Veterinário ou Adestrador).
* **RF05 - Fila do Dia (Recepção):** Visualização dos atendimentos agendados para o dia e status (*Agendado, Aguardando, Em Atendimento, Finalizado, Cancelado*).

### 3.3. Módulo de Atendimento Clínico & Prontuário Médico (Veterinário)
* **RF06 - Registro de Prontuário Médico:** Registro de queixa clínica principal, anamnese, exame físico/sinais vitais, hipótese diagnóstica e conduta terapêutica prescrita pelo Médico Veterinário.
* **RF07 - Histórico Clínico do Paciente:** Consulta cronológica aos atendimentos clínicos anteriores e evolução médica do pet.
* **RF08 - Emissão de Receita Médica Oficial:** Geração e impressão de receituário médico com nome do médico, número do CRMV obrigatório, posologia e instruções medicamentosas.
* **RF09 - Carteira de Vacinas & Vermifugação:** Registro das vacinas e tratamentos aplicados com controle de datas e próximas doses.

### 3.4. Módulo de Manejo Silvestre & Consultoria Comportamental (Adestrador / Esp. Silvestres)
* **RF10 - Registro de Sessão de Manejo & Bem-Estar:** Registro de queixas comportamentais (arrancamento de penas, estereotipias, agressividade), avaliação técnica do recinto (dimensões, fotoperíodo, lâmpada UVB/aquecimento, umidade, substrato) e estratégias de enriquecimento ambiental (cognitivo, alimentar, sensorial, físico).
* **RF11 - Histórico de Sessões de Manejo:** Consulta cronológica às consultorias e relatórios de adequação comportamental do animal.
* **RF12 - Emissão do Guia Oficial de Manejo Silvestre:** Geração e impressão de documento técnico contendo as diretrizes de recinto, dieta recomendada, metas de condicionamento e rotinas de enriquecimento para o tutor.

---

## 4. Requisitos Não Funcionais (RNF)

* **RNF01 - Usabilidade:** Interface simples, intuitiva e direta, com abas dedicadas e distintas para o consultório médico e para a consultoria de manejo silvestre.
* **RNF02 - Autenticação e Segurança:** Controle de acesso e validação por perfil de usuário.
* **RNF03 - Desempenho:** Busca rápida de funcionários, tutores e pets em menos de 2 segundos.
* **RNF04 - Compatibilidade:** Acesso via navegadores modernos (Chrome, Edge, Firefox).

---

## 5. Regras de Negócio Básicas (RN)

1. **RN01 - Vínculo Obrigatório:** Todo pet deve estar obrigatoriamente associado a um tutor cadastrado.
2. **RN02 - Separação Rigorosa de Competências:** 
   - Apenas o usuário com perfil de **Médico Veterinário** pode preencher o prontuário clínico e emitir receitas médicas oficiais com CRMV.
   - O perfil de **Adestrador / Especialista em Silvestres** atua na consultoria comportamental, avaliação de recintos e emissão do Guia de Manejo Silvestre.
3. **RN03 - CRMV no Documento:** Toda receita médica emitida deve conter automaticamente o nome completo e o CRMV válido do profissional responsável.
4. **RN04 - Parâmetros do Guia de Manejo:** O guia de manejo para animais silvestres e exóticos deve registrar obrigatoriamente parâmetros ambientais essenciais (iluminação UVB, fotoperíodo, temperatura e enriquecimento).
5. **RN05 - Histórico Multidisciplinar Integrado:** A ficha do paciente permite visualizar tanto o histórico clínico quanto o histórico de manejo de forma organizada e independente.

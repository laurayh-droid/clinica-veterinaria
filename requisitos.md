# Levantamento de Requisitos: Sistema para Clínica e Consultoria Veterinária (Pets Domésticos e Silvestres)

---

## 1. Visão Geral
Sistema simplificado e direto para gerenciar as rotinas da clínica veterinária com foco no atendimento clínico e suporte a tutores de **pets domésticos e silvestres/exóticos**: cadastro de funcionários (atendentes, veterinários e adestradores/especialistas em silvestres), cadastro de tutores e pets, agendamento de consultas/consultorias, prontuário clínico e fila de atendimento do dia.

*Obs: Este sistema não possui módulos de venda de produtos ou controle de estoque.*

---

## 2. Perfis de Usuários (Atores)

| Perfil | Responsabilidades |
| :--- | :--- |
| **Administrador** | Gestão geral e cadastro de funcionários (atendentes, veterinários e adestradores). |
| **Médico(a) Veterinário(a)** | Atendimentos clínicos, preenchimento do prontuário médico, exames e emissão de receitas. |
| **Adestrador(a) / Esp. Silvestres** | Consultoria comportamental, orientações de manejo, recinto e adestramento para tutores de pets silvestres e exóticos. |
| **Recepcionista / Atendente** | Cadastro de tutores e pets, agendamento de atendimentos e controle da fila do dia. |

---

## 3. Requisitos Funcionais (RF)

### 3.1. Cadastros Básicos
* **RF01 - Cadastro de Funcionários:** Registro de funcionários da clínica (Nome, CPF, E-mail, Telefone, Perfil: *Atendente, Veterinário (com CRMV)* ou *Adestrador/Especialista em Silvestres*).
* **RF02 - Cadastro de Tutores:** Registro de nome, CPF, telefone (WhatsApp) e endereço.
* **RF03 - Cadastro de Pets (Domésticos e Silvestres):** Registro do animal vinculado ao tutor (nome, espécie, raça/categoria, sexo, idade, peso e observações de manejo/hábito silvestre).

### 3.2. Agenda e Recepção
* **RF04 - Agendamento de Atendimentos:** Marcação de consultas médicas ou sessões de adestramento/manejo com data, horário, pet, tutor e profissional responsável (Veterinário ou Adestrador).
* **RF05 - Fila do Dia (Recepção):** Visualização dos atendimentos agendados para o dia e status (*Agendado, Aguardando, Em Atendimento, Finalizado, Cancelado*).

### 3.3. Atendimento e Prontuário (Clínico e Manejo Silvestre)
* **RF06 - Registro de Prontuário / Sessão:** Registro de queixa principal, sintomas ou dificuldades de criação/comportamento, diagnóstico/avaliação e orientações de manejo/adestramento.
* **RF07 - Histórico do Paciente:** Consulta rápida ao histórico de atendimentos clínicos e sessões de adestramento anteriores do pet.
* **RF08 - Emissão de Receita / Guia de Manejo:** Geração e impressão de receita médica (para veterinários) ou guia de orientações de manejo silvestre (para adestradores).
* **RF09 - Carteira de Vacinas & Vermifugação:** Registro das vacinas e tratamentos aplicados com próximas doses.

---

## 4. Requisitos Não Funcionais (RNF)

* **RNF01 - Usabilidade:** Interface simples, intuitiva e direta, adequada para recepção, consultório e sessões de orientação.
* **RNF02 - Autenticação e Segurança:** Controle de acesso por perfil de usuário.
* **RNF03 - Desempenho:** Busca rápida de funcionários, tutores e pets em menos de 2 segundos.
* **RNF04 - Compatibilidade:** Acesso via navegadores modernos (Chrome, Edge, Firefox).

---

## 5. Regras de Negócio Básicas (RN)

1. **RN01 - Vínculo Obrigatório:** Todo pet deve estar obrigatoriamente associado a um tutor cadastrado.
2. **RN02 - Permissão por Perfil:** 
   - Apenas o usuário com perfil de **Médico Veterinário** pode emitir receitas médicas com CRMV.
   - O perfil de **Adestrador** registra sessões de orientação comportamental e guia de manejo para animais silvestres.
3. **RN03 - CRMV no Documento:** Toda receita médica emitida por veterinário deve conter automaticamente o nome completo e o CRMV.
4. **RN04 - Apoio a Pets Silvestres:** O cadastro e atendimento de pets deve permitir especificar necessidades de recinto, alimentação e comportamento de espécies silvestres/exóticas (ex: jabutis, jiboias, papagaios, ferrets, hamsters).

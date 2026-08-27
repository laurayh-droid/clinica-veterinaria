# Levantamento de Requisitos: Sistema Básico para Clínica Veterinária

---

## 1. Visão Geral
Sistema simplificado e direto para gerenciar as rotinas essenciais de uma clínica veterinária de pequeno ou médio porte: cadastro de clientes e pacientes, agendamento, prontuário clínico simplificado, controle básico de estoque e registro de pagamentos (caixa).

---

## 2. Perfis de Usuários (Atores)

| Perfil | Responsabilidades |
| :--- | :--- |
| **Administrador** | Cadastro de usuários, controle do financeiro básico e relatórios simples. |
| **Médico(a) Veterinário(a)** | Realização de consultas, preenchimento do prontuário, emissão de receitas e registro de vacinas. |
| **Recepcionista / Atendente** | Cadastro de tutores e pets, agendamento de consultas e recebimento de pagamentos no caixa. |

---

## 3. Requisitos Funcionais (RF)

### 3.1. Cadastros Básicos
* **RF01 - Cadastro de Tutores:** Registro de nome, CPF, telefone (WhatsApp) e endereço.
* **RF02 - Cadastro de Pets:** Registro do animal vinculado ao tutor (nome, espécie, raça, sexo, data de nascimento/idade e peso).
* **RF03 - Cadastro de Usuários:** Cadastro de login, senha e perfil de acesso (Administrador, Veterinário com CRMV, Recepcionista).

### 3.2. Agenda e Recepção
* **RF04 - Agendamento de Consultas:** Marcação de consultas e retornos com data, horário, pet, tutor e veterinário responsável.
* **RF05 - Fila do Dia (Recepção):** Visualização dos atendimentos agendados para o dia e status (*Agendado, Aguardando, Em Atendimento, Finalizado, Cancelado*).

### 3.3. Atendimento Clínico (Prontuário Simples)
* **RF06 - Registro de Consulta:** Registro de queixa principal, sintomas observados, diagnóstico e orientações do veterinário.
* **RF07 - Histórico do Paciente:** Consulta rápida ao histórico de atendimentos e consultas anteriores do pet.
* **RF08 - Emissão de Receita Simples:** Geração e impressão de receita médica contendo os medicamentos prescritos e instruções de uso.
* **RF09 - Controle de Vacinas:** Registro das vacinas aplicadas (nome da vacina, data de aplicação e data prevista para a próxima dose).

### 3.4. Estoque Básico
* **RF10 - Cadastro de Produtos e Medicamentos:** Cadastro do item com nome, preço de venda e quantidade atual em estoque.
* **RF11 - Baixa de Estoque:** Atualização da quantidade disponível ao utilizar o produto em atendimento ou realizar uma venda direta.

### 3.5. Caixa e Pagamentos
* **RF12 - Registro de Pagamento:** Lançamento do valor cobrado por consultas, procedimentos ou produtos, selecionando a forma de pagamento (*Dinheiro, Cartão de Crédito/Débito ou Pix*).
* **RF13 - Fechamento de Caixa Diário:** Relatório simples com o total de entradas e faturamento do dia por forma de pagamento.

---

## 4. Requisitos Não Funcionais (RNF)

* **RNF01 - Usabilidade:** Interface simples, intuitiva e direta, que permita realizar cadastros e atendimentos com poucos cliques.
* **RNF02 - Autenticação e Segurança:** Acesso protegido por login e senha, garantindo que apenas usuários autorizados acessem o sistema.
* **RNF03 - Desempenho:** Busca rápida de tutores e pets pelo nome, telefone ou CPF em menos de 2 segundos.
* **RNF04 - Compatibilidade:** Sistema acessível via navegador web moderno (Google Chrome, Edge, Firefox) em computadores da clínica.
* **RNF05 - Backup de Dados:** Rotina automática de backup dos dados para evitar perda de cadastros e históricos de consultas.

---

## 5. Regras de Negócio Básicas (RN)

1. **RN01 - Vínculo Obrigatório:** Todo pet deve estar obrigatoriamente associado a um tutor cadastrado.
2. **RN02 - Permissão Clínica:** Apenas o usuário com perfil de **Médico Veterinário** pode preencher o prontuário, registrar o diagnóstico e emitir receitas.
3. **RN03 - CRMV no Documento:** Toda receita médica emitida pelo sistema deve conter automaticamente o nome completo e o número de CRMV do veterinário que realizou o atendimento.
4. **RN04 - Alerta de Estoque:** O sistema deve alertar quando a quantidade de um produto atingir zero unidades.

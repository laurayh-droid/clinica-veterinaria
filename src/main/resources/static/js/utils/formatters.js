/**
 * Utilitários de Formatação e Validação da Clínica Veterinária.
 */

export const Formatters = {
  /**
   * Formata CPF no padrão 000.000.000-00
   */
  formatCPF(value) {
    if (!value) return '';
    return value
      .replace(/\D/g, '')
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})(\d{1,2})$/, '$1-$2')
      .substring(0, 14);
  },

  /**
   * Formata telefone celular (00) 00000-0000
   */
  formatPhone(value) {
    if (!value) return '';
    return value
      .replace(/\D/g, '')
      .replace(/^(\d{2})(\d)/g, '($1) $2')
      .replace(/(\d{5})(\d)/, '$1-$2')
      .substring(0, 15);
  },

  /**
   * Formata data para o padrão pt-BR (DD/MM/AAAA)
   */
  formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return isNaN(date.getTime()) ? dateString : date.toLocaleDateString('pt-BR');
  },

  /**
   * Formata data e hora para o padrão pt-BR (DD/MM/AAAA às HH:mm)
   */
  formatDateTime(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return isNaN(date.getTime())
      ? dateString
      : `${date.toLocaleDateString('pt-BR')} às ${date.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })}`;
  }
};

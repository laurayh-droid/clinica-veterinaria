# 🎨 Estrutura do Template Frontend - VetCare Fauna & Domésticos

Este diretório contém os componentes, folhas de estilo, scripts e recursos estáticos organizados no padrão de **Template Web Monolítico**.

---

## 📁 Estrutura de Pastas

```text
frontend/ (e src/main/resources/static/)
├── index.html                    # Página Principal da SPA / Aplicação
├── assets/                       # Recursos visuais estáticos
│   ├── images/                   # Imagens de fundo, texturas e banners
│   │   ├── banner-texture.jpg    # Textura dos banners de boas-vindas
│   │   ├── download.jfif         # Textura da sidebar
│   │   ├── forest-bg.jpg         # Fundo florestal da tela de login
│   │   └── sidebar-bg.jfif       # Variação de textura para o menu
│   └── icons/                    # Ícones SVG e favicons da marca
├── css/                          # Folhas de estilo (CSS)
│   └── style.css                 # Folha de estilo principal com paleta bio-harmônica
├── js/                           # Scripts e módulos JavaScript
│   ├── app.js                    # Script principal de interação e regras de UI
│   ├── services/                 # Serviços de integração com a API REST Spring Boot
│   │   └── api.js                # Cliente HTTP padronizado (Fetch API / JWT)
│   └── utils/                    # Funções utilitárias e formatadores
│       └── formatters.js         # Formatadores de CPF, telefone, data/hora
└── templates/                    # Componentes e templates modulares
    └── components/               # Fragmentos e componentes reutilizáveis
```

---

## 🔄 Integração com o Spring Boot

Em aplicações Monolíticas Spring Boot:
- Os arquivos deste template são servidos automaticamente a partir de `src/main/resources/static/`.
- Ao acessar `http://localhost:8080/api/` (ou `http://localhost:8080/index.html`), o Spring Boot entrega a interface já integrada.
- As chamadas assíncronas do `api.js` comunicam-se com os controladores `@RestController` mapeados no backend.

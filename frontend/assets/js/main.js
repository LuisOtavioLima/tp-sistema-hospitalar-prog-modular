import { initRouter, loadComponent } from "./utils.js";

async function init() {
    // carrega o cabeçalho
    await loadComponent("header");
    await loadComponent("sidebar");
    await loadComponent("footer");
    
    // se entrar sem rota específica, manda para a dashboard
    window.location.hash = window.location.hash || "#dashboard";
    
    // vigia os cliques do menu para a troca de páginas
    initRouter();
}

// Inicia a aplicação
init();
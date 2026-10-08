// destaca o link da página atual com base no hash da URL
function marcarLinkAtivo() {
    const rotaAtual = window.location.hash.substring(1);

    document.querySelectorAll(".sidebar-link").forEach((link) => {
        const ativo = link.dataset.rota === rotaAtual;
        link.classList.toggle("ativo", ativo);

        if (ativo) {
            link.setAttribute("aria-current", "page");
        } else {
            link.removeAttribute("aria-current");
        }
    });
}

export function initSidebar() {
    marcarLinkAtivo();

    // toda vez que a página mudar, atualiza o destaque
    window.addEventListener("hashchange", marcarLinkAtivo);
}

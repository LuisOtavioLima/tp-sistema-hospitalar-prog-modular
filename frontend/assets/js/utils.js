export async function loadComponent(componentName) {
    try {
        // busca o componente seguindo a estrutura de pastas
        const response = await fetch(`./components/${componentName}/${componentName}.html`);
        const html = await response.text();
        document.getElementById(componentName).innerHTML = html;
    } catch (error) {
        console.error(`Erro ao carregar o componente ${componentName}:`, error);
    }
}

export function initRouter() {
    // função descobre qual página carregar baseada na URL
    const renderPage = async () => {
        // pega o que está depois do '#' na URL: '#pacientes' -> 'pacientes')
        let route = window.location.hash.substring(1); 
        
        try {
            // vai na pasta pages buscar o HTML correspondente
            const response = await fetch(`./pages/${route}/${route}.html`);
            
            if (response.ok) {
                const html = await response.text();
                document.getElementById('content').innerHTML = html;
            } else {
                document.getElementById('content').innerHTML = "<h2>Erro 404: Página não encontrada</h2>";
            }
        } catch (error) {
            console.error("Erro ao carregar a página:", error);
        }
    };

    // vigia a URL: toda vez que o hash mudar, roda a função
    window.addEventListener('hashchange', renderPage);
    
    // roda a função uma vez assim que o site abre
    renderPage();
}
async function fetchConfiguration() {
    try {
        const response = await fetch("http://localhost:8080/api/configuration/all");
        console.log("🚀 ~ fetchConfiguration ~ response:", response)
        if (!response.ok) throw new Error("Yapılandırma alınamadı");
        
        const config = await response.json();
        console.log("🚀 ~ fetchConfiguration ~ config:", config)

        config.forEach(arr => {
            applyConfigurations(arr)
        });
        
    } catch (error) {
        console.error("Hata:", error);
    }
}

function applyConfigurations(config) {
    config.actions.forEach(action => {
        switch(action.type) {
            case "remove":
                document.querySelectorAll(action.selector).forEach(el => el.remove());
                break;
            case "replace":
                document.querySelectorAll(action.selector).forEach(el => {
                    const newElement = document.createElement('div');
                    newElement.innerHTML = action.newElement;
                    el.replaceWith(newElement.firstChild);
                });
                break;
            case "insert":
                const target = document.querySelector(action.target);
                const elementToInsert = document.createElement('div');
                elementToInsert.innerHTML = action.element;
                if (action.position === "after" && target) {
                    target.insertAdjacentElement("afterend", elementToInsert.firstChild);
                } else if (action.position === "before" && target) {
                    target.insertAdjacentElement("beforebegin", elementToInsert.firstChild);
                }
                break;
            case "alter":
                document.body.innerHTML = document.body.innerHTML.replace(new RegExp(action.oldValue, 'g'), action.newValue);
                break;
            default:
                console.warn("Desteklenmeyen işlem tipi:", action.type);
        }
    });
}

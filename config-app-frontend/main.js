async function fetchConfiguration() {
    try {
        const response = await fetch("http://localhost:8080/api/configuration/configFile");
        if (!response.ok) throw new Error("cannot fetch configuration");
        
        const config = await response.json();

        applyConfigurations(config);

    } catch (error) {
        console.error("Error:", error);
    }
}

function applyConfigurations(configs) {
    const appliedElements = {}; //for conflict resolution
    const sortedConfigs = sortConfigurationsByPriority(configs.actions);

    sortedConfigs.forEach(config => {
        const { type, selector, newElement, position, target, element, oldValue, newValue } = config;
        
        if (appliedElements[selector] && type !== 'insert' && type !== 'alter') { //because insert and alter doesnt have selector
            return; 
        }

        switch (type) {
            case 'replace':
                replaceElement(selector, newElement);
                break;
            case 'remove':
                removeElement(selector);
                break;
            case 'insert':
                insertElement(position, target, element);
                break;
            case 'alter':
                alterText(oldValue, newValue);
                break;
            default:
                console.log('unsupported type: ' + type);
        }

        appliedElements[selector] = true;
    });
}

function removeElement(selector) {
    const elements = document.querySelectorAll(selector);
    elements.forEach(element => element.remove());
}

function replaceElement(selector, newElementHTML) {
    const element = document.querySelector(selector);
    if (element) {
      element.outerHTML = newElementHTML;
    }
}
  
function insertElement(position, target, elementHTML) {
    const targetElement = document.querySelector(target);
    if (targetElement) {
        const newElement = document.createElement('div');
        newElement.innerHTML = elementHTML;

        if (position === 'before') {
            targetElement.parentNode.insertBefore(newElement, targetElement);
        } else if (position === 'after') {
            targetElement.parentNode.insertBefore(newElement, targetElement.nextSibling);
        }
    }
}

function alterText(oldValue, newValue) {
    const walk = document.createTreeWalker(
        document.body, 
        NodeFilter.SHOW_TEXT, 
        null, 
        false
    ); //scan all text nodes

    let node;
    while (node = walk.nextNode()) {
        if (node.nodeValue.includes(oldValue)) {
            node.nodeValue = node.nodeValue.replace(new RegExp(oldValue, 'g'), newValue);
        }
    }
}

function sortConfigurationsByPriority(configurations) {
    return configurations.sort((a, b) => a.priority - b.priority);
}

async function fetchSpesificConfig(host, url) {
    try {
        const response = await fetch(`http://localhost:8080/api/configuration/specific?host=${host}&url=${url}`);
        const configData = await response.json();
        //for host:
        fetchByConfigFileName(extractFileName(configData.hostConfigFile));
        //for url:
        fetchByConfigFileName(extractFileName(configData.urlConfigFile));
    } catch (error) {
        console.error('Error fetching configuration:', error);
    }
}

function fetchByConfigFileName(fileName) {
    fetch(`http://localhost:8080/api/configuration/byFileName/${fileName}`)
      .then(response => response.json())
      .then(data => {
          console.log("🚀 ~ fetchByConfigFileName ~ data:", data)
        applyConfigurations(data);
      })
      .catch(error => console.error('Config fetch failed:', error));
}

function extractFileName(filename) {
    return filename.includes('.') ? filename.split('.')[0] : filename;
}
function updatePageInfo() {
    const contentHeight = document.body.scrollHeight;
    const viewportHeight = window.innerHeight;
    const scrollTop = window.scrollY;
    const totalPages = Math.ceil(contentHeight / viewportHeight);
    const currentPage = Math.ceil((scrollTop + viewportHeight) / viewportHeight);
    document.getElementById('page-info').innerText = `Page ${currentPage} of ${totalPages}`;
}

function handleTranslationResultById(elementId, translatedText) {
    const p = document.getElementById(elementId);
    if (p) {
        const loadingTranslation = p.querySelector('.translation')
        loadingTranslation.innerText = translatedText;
    }
}

function handleTranslationError(elementId, message) {
    const p = document.getElementById(elementId);
    if (p) {
        const loadingTranslation = p.querySelector('.translation')
        if (loadingTranslation) {
            loadingTranslation.remove();
        }
    }
    showToast(message);
}

function showToast(message) {
    const toast = document.createElement('div');
    toast.classList.add('toast');
    toast.innerText = message;
    document.body.appendChild(toast);

    // Remove toast after a few seconds
    setTimeout(() => {
        toast.remove();
    }, 3000);
}

function navigateToPage(pageNumber) {
    const contentHeight = document.body.scrollHeight;
    const viewportHeight = window.innerHeight;
    const totalPages = Math.ceil(contentHeight / viewportHeight);

    if (pageNumber > 0 && pageNumber <= totalPages) {
        const scrollToY = (pageNumber - 1) * viewportHeight;
        window.scrollTo({ top: scrollToY, behavior: 'smooth' });
    } else {
        showToast("Page number out of range!");
    }
}

function initialize() {
    window.addEventListener('scroll', updatePageInfo);
    window.addEventListener('resize', updatePageInfo);
    window.addEventListener('load', updatePageInfo);

    document.querySelectorAll('p').forEach((p, index) => {
        const aTag = p.querySelector('a');
        const imgTag = p.querySelector('img');
        const brTag = p.querySelector('br')
        const valid = imgTag == null && aTag == null && brTag == null;

        if (valid && !p.hasAttribute('id')) {
            p.setAttribute('id', 'p-' + index);
        }

        if (valid) {
            p.addEventListener('click', () => {
                const existingTranslation = p.querySelector('.translation');
                if (existingTranslation) {
                    if (existingTranslation.innerText !== "Wait for translator...") {
                        existingTranslation.remove();
                    } else {
                        showToast("Please way for async process to complete!");
                    }
                } else {
                    if (p.innerText !== "" && p.hasAttribute("id")) {
                        const originalText = p.innerText;
                        const elementId = p.id;
                        window.app.getTranslationAsync(originalText, elementId);
                        const translationElement = document.createElement('div');
                        translationElement.classList.add('translation');
                        translationElement.innerText = "Wait for translator...";
                        p.appendChild(translationElement);
                    }
                }
            });
        }
    });

    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            window.alert(this.getAttribute('href'));
        });
    });
}

